/*
 * Copyright 2012-2018 CodeLibs Project and the Others.
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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Keiichi Watanabe
 */
public class GitBucketDataStoreImpl extends AbstractDataStoreImpl {
    private static final Logger logger = LoggerFactory.getLogger(GitBucketDataStoreImpl.class);

    private static final int MAX_DEPTH = 20;

    protected static final String TOKEN_PARAM = "token";
    protected static final String GITBUCKET_URL_PARAM = "url";
    protected static final String PRIVATE_REPOSITORY_PARAM = "is_private";
    protected static final String COLLABORATORS_PARAM = "collaborators";

    @Override
    protected void storeData(final DataConfig dataConfig, final IndexUpdateCallback callback, final Map<String, String> paramMap,
            final Map<String, String> scriptMap, final Map<String, Object> defaultDataMap) {

        final String rootURL = getRootURL(paramMap);
        final String authToken = getAuthToken(paramMap);
        final long readInterval = getReadInterval(paramMap);

        // Non-emptiness Check for URL and Token
        if (rootURL.isEmpty() || authToken.isEmpty()) {
            logger.warn("parameter \"" + TOKEN_PARAM + "\" and \"" + GITBUCKET_URL_PARAM + "\" are required");
            return;
        }

        // Get List of Repositories
        final List<Map<String, Object>> repositoryList = getRepositoryList(rootURL, authToken);
        if (repositoryList.isEmpty()) {
            logger.warn("Token is invalid or no Repository");
            return;
        }

        // Get Labels
        final Map<String, String> pluginInfo = getFessPluginInfo(rootURL, authToken);
        final String sourceLabel = pluginInfo.get("source_label");
        final String issueLabel = pluginInfo.get("issue_label");
        final String wikiLabel = pluginInfo.get("wiki_label");

        final CrawlingConfig crawlingConfig = new CrawlingConfigWrapper(dataConfig) {
            @Override
            public Map<String, Object> initializeClientFactory(final CrawlerClientFactory crawlerClientFactory) {
                final Map<String, Object> paramMap = super.initializeClientFactory(crawlerClientFactory);
                final List<RequestHeader> headerList = new ArrayList<>();
                final RequestHeader[] headers = (RequestHeader[]) paramMap.get(HcHttpClient.REQUERT_HEADERS_PROPERTY);
                if (headers != null) {
                    for (final RequestHeader header : headers) {
                        headerList.add(header);
                    }
                }
                headerList.add(new RequestHeader("Authorization", "token " + authToken));
                headerList.add(new RequestHeader("Accept", "application/vnd.github.v3.raw"));
                paramMap.put(HcHttpClient.REQUERT_HEADERS_PROPERTY, headerList.toArray(new RequestHeader[headerList.size()]));
                return paramMap;
            }
        };

        // Crawl each repository
        for (final Map<String, Object> repository : repositoryList) {
            try {
                final String owner = (String) repository.get("owner");
                final String name = (String) repository.get("name");
                // Since old gitbucket-fess-plugin does not return "branch", it refers instead of "master".
                final String branch = (String) repository.getOrDefault("branch", "master");
                final int issueCount = (int) repository.get("issue_count");
                final int pullCount = (int) repository.get("pull_count");
                final List<String> roleList = createRoleList(owner, repository);

                // branch is empty when git repository is empty.
                if (StringUtil.isNotEmpty(branch)) {
                    final String refStr = getGitRef(rootURL, authToken, owner, name, branch);
                    logger.info("Crawl " + owner + "/" + name);
                    // crawl and store file contents recursively
                    crawlFileContents(
                            rootURL,
                            authToken,
                            owner,
                            name,
                            refStr,
                            StringUtil.EMPTY,
                            0,
                            readInterval,
                            path -> {
                                storeFileContent(rootURL, authToken, sourceLabel, owner, name, refStr, roleList, path, crawlingConfig,
                                        callback, paramMap, scriptMap, defaultDataMap);
                                if (readInterval > 0) {
                                    sleep(readInterval);
                                }
                            });
                }

                logger.info("Crawl issues in " + owner + "/" + name);
                // store issues
                for (int issueId = 1; issueId <= issueCount + pullCount; issueId++) {
                    storeIssueById(rootURL, authToken, issueLabel, owner, name, new Integer(issueId), roleList, crawlingConfig, callback,
                            paramMap, scriptMap, defaultDataMap);

                    if (readInterval > 0) {
                        sleep(readInterval);
                    }
                }

                logger.info("Crawl Wiki in " + owner + "/" + name);
                // crawl Wiki
                storeWikiContents(rootURL, authToken, wikiLabel, owner, name, roleList, crawlingConfig, callback, paramMap, scriptMap,
                        defaultDataMap, readInterval);

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

    protected Map<String, String> getFessPluginInfo(final String rootURL, final String authToken) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String url = rootURL + "api/v3/fess/info";
        try (CurlResponse curlResponse =
                Curl.get(url).proxy(fessConfig.getHttpProxy()).header("Authorization", "token " + authToken).execute()) {
            @SuppressWarnings({ "rawtypes", "unchecked" })
            final Map<String, String> map = (Map) curlResponse.getContentAsMap();
            assert (map.containsKey("version"));
            assert (map.containsKey("source_label") && map.containsKey("wiki_label") && map.containsKey("issue_label"));
            return map;

        } catch (final Exception e) {
            logger.warn("Failed to access to " + url, e);
            return Collections.emptyMap();
        }
    }

    protected List<Map<String, Object>> getRepositoryList(final String rootURL, final String authToken) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String url = rootURL + "api/v3/fess/repos";
        int totalCount = -1; // initialize with dummy value
        final List<Map<String, Object>> repoList = new ArrayList<>();

        do {
            final String urlWithOffset = url + "?offset=" + repoList.size();

            try (CurlResponse curlResponse =
                    Curl.get(urlWithOffset).proxy(fessConfig.getHttpProxy()).header("Authorization", "token " + authToken).execute()) {
                final Map<String, Object> map = curlResponse.getContentAsMap();

                assert (map.containsKey("total_count"));
                assert (map.containsKey("response_count"));
                assert (map.containsKey("repositories"));

                totalCount = (int) map.get("total_count");
                final int responseCount = (int) map.get("response_count");
                if (responseCount == 0) {
                    break;
                }

                @SuppressWarnings("unchecked")
                final List<Map<String, Object>> repos = (ArrayList<Map<String, Object>>) map.get("repositories");
                repoList.addAll(repos);
            } catch (final Exception e) {
                logger.warn("Failed to access to " + urlWithOffset, e);
                break;
            }
        } while (repoList.size() < totalCount);

        logger.info("There exist " + repoList.size() + " repositories");
        return repoList;
    }

    protected String getGitRef(final String rootURL, final String authToken, final String owner, final String name, final String branch) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String url = encode(rootURL, "api/v3/repos/" + owner + "/" + name + "/git/refs/heads/" + branch, null);

        try (CurlResponse curlResponse =
                Curl.get(url).proxy(fessConfig.getHttpProxy()).header("Authorization", "token " + authToken).execute()) {
            final Map<String, Object> map = curlResponse.getContentAsMap();
            assert (map.containsKey("object"));
            @SuppressWarnings("unchecked")
            final Map<String, String> objmap = (Map<String, String>) map.get("object");
            assert (objmap.containsKey("sha"));
            return objmap.get("sha");
        } catch (final Exception e) {
            logger.warn("Failed to access to " + url, e);
            return branch;
        }
    }

    private List<String> createRoleList(final String owner, final Map<String, Object> repository) {
        Boolean isPrivate = true;
        if (repository.containsKey(PRIVATE_REPOSITORY_PARAM)) {
            isPrivate = (Boolean) repository.get(PRIVATE_REPOSITORY_PARAM);
        }
        if (!isPrivate) {
            return Collections.singletonList("Rguest");
        }

        @SuppressWarnings("unchecked")
        final List<String> collaboratorList = (List<String>) repository.get(COLLABORATORS_PARAM);
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        collaboratorList.add(owner);
        return collaboratorList.stream().map(user -> systemHelper.getSearchRoleByUser(user)).collect(Collectors.toList());
    }

    private List<Object> parseList(final InputStream is) { // TODO This function should be moved to CurlResponse
        try {
            return JsonXContent.jsonXContent.createParser(NamedXContentRegistry.EMPTY, is).list();
        } catch (final Exception e) {
            logger.warn("Failed to parse a list.", e);
            return Collections.emptyList();
        }
    }

    private void storeFileContent(final String rootURL, final String authToken, final String sourceLabel, final String owner,
            final String name, final String refStr, final List<String> roleList, final String path, final CrawlingConfig crawlingConfig,
            final IndexUpdateCallback callback, final Map<String, String> paramMap, final Map<String, String> scriptMap,
            final Map<String, Object> defaultDataMap) {
        final String apiUrl = encode(rootURL, "api/v3/repos/" + owner + "/" + name + "/contents/" + path, null);
        final String viewUrl = encode(rootURL, owner + "/" + name + "/blob/" + refStr + "/" + path, null);

        if (logger.isInfoEnabled()) {
            logger.info("Get a content from " + apiUrl);
        }
        final Map<String, Object> dataMap = new HashMap<>();
        dataMap.putAll(defaultDataMap);
        dataMap.putAll(ComponentUtil.getDocumentHelper().processRequest(crawlingConfig, paramMap.get("crawlingInfoId"),
                apiUrl + "?ref=" + refStr + "&large_file=true"));

        dataMap.put("url", viewUrl);
        dataMap.put("role", roleList);
        dataMap.put("label", Collections.singletonList(sourceLabel));

        // TODO scriptMap

        callback.store(paramMap, dataMap);

        return;
    }

    private void storeIssueById(final String rootURL, final String authToken, final String issueLabel, final String owner,
            final String name, final Integer issueId, final List<String> roleList, final CrawlingConfig crawlingConfig,
            final IndexUpdateCallback callback, final Map<String, String> paramMap, final Map<String, String> scriptMap,
            final Map<String, Object> defaultDataMap) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();

        final String issueUrl = rootURL + "api/v3/repos/" + owner + "/" + name + "/issues/" + issueId.toString();
        final String viewUrl = rootURL + owner + "/" + name + "/issues/" + issueId.toString();

        if (logger.isInfoEnabled()) {
            logger.info("Get a content from " + issueUrl);
        }

        final Map<String, Object> dataMap = new HashMap<>();
        String contentStr = "";
        dataMap.putAll(defaultDataMap);

        // Get issue description
        // FIXME: Use `ComponentUtil.getDocumentHelper().processRequest` instead of `Curl.get`
        try (CurlResponse curlResponse =
                Curl.get(issueUrl).proxy(fessConfig.getHttpProxy()).header("Authorization", "token " + authToken).execute()) {
            final Map<String, Object> map = curlResponse.getContentAsMap();
            dataMap.put("title", map.getOrDefault("title", ""));
            contentStr = (String) map.getOrDefault("body", "");
        } catch (final Exception e) {
            logger.warn("Failed to access to " + issueUrl, e);
        }

        final String commentsStr = String.join("\n", getIssueComments(issueUrl, authToken));
        contentStr += "\n" + commentsStr;

        dataMap.put("content", contentStr);
        dataMap.put("url", viewUrl);
        dataMap.put("role", roleList);
        dataMap.put("label", Collections.singletonList(issueLabel));

        // TODO scriptMap

        callback.store(paramMap, dataMap);

        return;
    }

    private List<String> getIssueComments(final String issueUrl, final String authToken) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String commentsUrl = issueUrl + "/comments";
        final List<String> commentList = new ArrayList<>();

        try (CurlResponse curlResponse =
                Curl.get(commentsUrl).proxy(fessConfig.getHttpProxy()).header("Authorization", "token " + authToken).execute()) {
            final String commentsJson = curlResponse.getContentAsString();
            final List<Map<String, Object>> comments =
                    new ObjectMapper().readValue(commentsJson, new TypeReference<List<Map<String, Object>>>() {
                    });

            for (final Map<String, Object> comment : comments) {
                if (comment.containsKey("body")) {
                    commentList.add((String) comment.get("body"));
                }
            }
        } catch (final Exception e) {
            logger.warn("Failed to access to " + issueUrl, e);
        }

        return commentList;
    }

    @SuppressWarnings("unchecked")
    private void storeWikiContents(final String rootURL, final String authToken, final String wikiLabel, final String owner,
            final String name, final List<String> roleList, final CrawlingConfig crawlingConfig, final IndexUpdateCallback callback,
            final Map<String, String> paramMap, final Map<String, String> scriptMap, final Map<String, Object> defaultDataMap,
            final long readInterval) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String wikiUrl = rootURL + "api/v3/fess/" + owner + "/" + name + "/wiki";

        List<String> pageList = Collections.emptyList();

        // Get list of pages
        try (CurlResponse curlResponse =
                Curl.get(wikiUrl).proxy(fessConfig.getHttpProxy()).header("Authorization", "token " + authToken).execute()) {
            final Map<String, Object> map = curlResponse.getContentAsMap();
            pageList = (List<String>) map.get("pages");
        } catch (final Exception e) {
            logger.warn("Failed to access to " + wikiUrl, e);
        }

        for (final String page : pageList) {
            // FIXME: URL encoding (e.g. page name that contains spaces)
            final String pageUrl = wikiUrl + "/contents/" + page + ".md";
            final String viewUrl = rootURL + owner + "/" + name + "/wiki/" + page;

            if (logger.isInfoEnabled()) {
                logger.info("Get a content from " + pageUrl);
            }

            final Map<String, Object> dataMap = new HashMap<>();
            dataMap.putAll(defaultDataMap);
            dataMap.putAll(ComponentUtil.getDocumentHelper().processRequest(crawlingConfig, paramMap.get("crawlingInfoId"), pageUrl));

            dataMap.put("url", viewUrl);
            dataMap.put("role", roleList);
            dataMap.put("label", Collections.singletonList(wikiLabel));

            // TODO scriptMap

            callback.store(paramMap, dataMap);
            logger.info("Stored " + pageUrl);

            if (readInterval > 0) {
                sleep(readInterval);
            }
        }

    }

    protected void crawlFileContents(final String rootURL, final String authToken, final String owner, final String name,
            final String refStr, final String path, final int depth, final long readInterval, final Consumer<String> consumer) {

        if (MAX_DEPTH <= depth) {
            return;
        }

        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String url = encode(rootURL, "api/v3/repos/" + owner + "/" + name + "/contents/" + path, "ref=" + refStr);

        try (CurlResponse curlResponse =
                Curl.get(url).proxy(fessConfig.getHttpProxy()).header("Authorization", "token " + authToken).execute()) {
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
                    crawlFileContents(rootURL, authToken, owner, name, refStr, newPath, depth + 1, readInterval, consumer);
                    break;
                }
            }
        } catch (final Exception e) {
            logger.warn("Failed to access to " + url, e);
        }
    }

    private String encode(final String rootURL, final String path, final String query) {
        try {
            final URI rootURI = new URI(rootURL);
            final URI uri =
                    new URI(rootURI.getScheme(), rootURI.getUserInfo(), rootURI.getHost(), rootURI.getPort(), rootURI.getPath() + path,
                            query, null);
            return uri.toASCIIString();
        } catch (final URISyntaxException e) {
            logger.warn("Failed to parse " + rootURL + path + "?" + query, e);
            if (StringUtil.isEmpty(query)) {
                return rootURL + path;
            }
            return rootURL + path + "?" + query;
        }
    }
}
