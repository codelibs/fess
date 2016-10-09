/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.codelibs.fess.ds.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.io.FilenameUtils;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.elasticsearch.runner.net.Curl;
import org.codelibs.elasticsearch.runner.net.CurlResponse;
import org.codelibs.fess.crawler.client.CrawlerClientFactory;
import org.codelibs.fess.crawler.client.http.HcHttpClient;
import org.codelibs.fess.crawler.client.http.RequestHeader;
import org.codelibs.fess.ds.IndexUpdateCallback;
import org.codelibs.fess.es.config.exentity.CrawlingConfig;
import org.codelibs.fess.es.config.exentity.CrawlingConfigWrapper;
import org.codelibs.fess.es.config.exentity.DataConfig;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Keiichi Watanabe
 */
public class GitBucketDataStoreImpl extends AbstractDataStoreImpl {
    private static final Logger logger = LoggerFactory.getLogger(CsvDataStoreImpl.class);

    private static final int MAX_DEPTH = 20;

    protected static final String TOKEN_PARAM = "token";
    protected static final String GITBUCKET_URL_PARAM = "url";

    @Override
    protected void storeData(final DataConfig dataConfig, final IndexUpdateCallback callback, final Map<String, String> paramMap,
            final Map<String, String> scriptMap, final Map<String, Object> defaultDataMap) {

        final String rootURL = getRootURL(paramMap);
        final String authToken = getAuthToken(paramMap);
        final long readInterval = getReadInterval(paramMap);

        if (rootURL.isEmpty() || authToken.isEmpty()) {
            logger.warn("parameter \"" + TOKEN_PARAM + "\" and \"" + GITBUCKET_URL_PARAM + "\" are required");
            return;
        }

        final List<Map<String, Object>> repositoryList = getRepositoryList(rootURL, authToken);
        if (repositoryList.isEmpty()) {
            logger.warn("Token is invalid or no Repository");
            return;
        }

        final CrawlingConfig crawlingConfig = new CrawlingConfigWrapper(dataConfig) {
            @Override
            public Map<String, Object> initializeClientFactory(CrawlerClientFactory crawlerClientFactory) {
                final Map<String, Object> paramMap = super.initializeClientFactory(crawlerClientFactory);
                List<RequestHeader> headerList = new ArrayList<>();
                RequestHeader[] headers = (RequestHeader[]) paramMap.get(HcHttpClient.REQUERT_HEADERS_PROPERTY);
                if (headers != null) {
                    for (RequestHeader header : headers) {
                        headerList.add(header);
                    }
                }
                headerList.add(new RequestHeader("Authorization", "token " + authToken));
                paramMap.put(HcHttpClient.REQUERT_HEADERS_PROPERTY, headerList.toArray(new RequestHeader[headerList.size()]));
                return paramMap;
            }
        };
        for (final Map<String, Object> repository : repositoryList) {
            try {
                final String name = (String) repository.get("name");
                final String owner = (String) repository.get("owner");
                repository.get("is_private");

                collectFileNames(rootURL, authToken, owner, name, StringUtil.EMPTY, 0, readInterval, path -> {
                    storeFileContent(rootURL, authToken, owner, name, path, crawlingConfig, callback, paramMap, scriptMap, defaultDataMap);
                    if (readInterval > 0) {
                        sleep(readInterval);
                    }

                });
            } catch (final Exception e) {
                logger.warn("Failed to access to " + repository, e);
            }
        }

    }

    protected String getRootURL(final Map<String, String> paramMap) {
        if (paramMap.containsKey(GITBUCKET_URL_PARAM)) {
            final String url = paramMap.get(GITBUCKET_URL_PARAM);
            if (!url.endsWith("/")) {
                return url + "/";
            }
            return url;
        }
        return StringUtil.EMPTY;
    }

    protected String getAuthToken(final Map<String, String> paramMap) {
        if (paramMap.containsKey(TOKEN_PARAM)) {
            return paramMap.get(TOKEN_PARAM);
        }
        return StringUtil.EMPTY;
    }

    protected List<Map<String, Object>> getRepositoryList(final String rootURL, final String authToken) {
        final String url = rootURL + "api/v3/fess/repos";
        try (CurlResponse curlResponse = Curl.get(url).header("Authorization", "token " + authToken).execute()) {
            final Map<String, Object> map = curlResponse.getContentAsMap();
            assert (map.containsKey("repositories"));
            @SuppressWarnings("unchecked")
            final List<Map<String, Object>> repoList = (List<Map<String, Object>>) map.get("repositories");
            return repoList;
        } catch (final Exception e) {
            logger.warn("Failed to access to " + rootURL, e);
            return Collections.emptyList();
        }
    }

    private List<Object> parseList(final InputStream is) { // TODO This function should be moved to CurlResponse
        try {
            return JsonXContent.jsonXContent.createParser(is).list();
        } catch (final Exception e) {
            logger.warn("Failed to parse a list.", e);
            return Collections.emptyList();
        }
    }

    private void storeFileContent(final String rootURL, final String authToken, final String owner, final String name, final String path,
            final CrawlingConfig crawlingConfig, final IndexUpdateCallback callback, final Map<String, String> paramMap,
            final Map<String, String> scriptMap, final Map<String, Object> defaultDataMap) {
        final String url = rootURL + "api/v3/repos/" + owner + "/" + name + "/contents/" + path;

        if (logger.isInfoEnabled()) {
            logger.info("Get a content from " + url);
        }
        final Map<String, Object> dataMap = new HashMap<>();
        dataMap.putAll(defaultDataMap);
        // FIXME Use DocumentHelper
        // dataMap.putAll(ComponentUtil.getDocumentHelper().processRequest(crawlingConfig, paramMap.get("crawlingInfoId"), url));
        dataMap.putAll(processContentRequest(authToken, url));
        
        // TODO scriptMap

        callback.store(paramMap, dataMap);

        return;
    }
    
    private Map<String, String> processContentRequest(final String authToken, final String url) { // FIXME should be replaced by DocumentHelper
        final  Map<String, String> dataMap = new HashMap<>();
        try (CurlResponse curlResponse = Curl.get(url).header("Authorization", "token " + authToken).execute()) {
            final Map<String, Object> map = curlResponse.getContentAsMap();
            String content = StringUtil.EMPTY;;
            if (map.containsKey("content")) {
                content = (String) map.get("content");
            }
            
            if (map.containsKey("encoding") && map.get("encoding").equals("base64")) {
                content = new String(Base64.getDecoder().decode(content));
            }
            
            dataMap.put("title", FilenameUtils.getName(url));
            dataMap.put("url", url);
            dataMap.put("content", content);
            
            return dataMap;
        } catch (final Exception e) {
            logger.warn("Failed to get " + url, e);
            return Collections.emptyMap();
        }
    }

    protected void collectFileNames(final String rootURL, final String authToken, final String owner, final String name, final String path,
            final int depth, final long readInterval, Consumer<String> consumer) {

        if (MAX_DEPTH <= depth) {
            return;
        }

        final String url = rootURL + "api/v3/repos/" + owner + "/" + name + "/contents/" + path;

        try (CurlResponse curlResponse = Curl.get(url).header("Authorization", "token " + authToken).execute()) {
            final InputStream iStream = curlResponse.getContentAsStream();
            final List<Object> fileList = parseList(iStream);

            for (int i = 0; i < fileList.size(); ++i) {
                @SuppressWarnings("unchecked")
                final Map<String, String> file = (Map<String, String>) fileList.get(i);
                final String newPath = path.isEmpty() ? file.get("name") : path + "/" + file.get("name");
                switch (file.get("type")) {
                case "file":
                    consumer.accept(newPath);
                    break;
                case "dir":
                    if (readInterval > 0) {
                        sleep(readInterval);
                    }
                    collectFileNames(rootURL, authToken, owner, name, newPath, depth + 1, readInterval, consumer);
                    break;
                }
            }
        } catch (final Exception e) {
            logger.warn("Failed to access to " + url, e);
        }
    }

}
