/*
 * Copyright 2009-2013 the Fess Project and the Others.
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

package jp.sf.fess.ds.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.sf.fess.Constants;
import jp.sf.fess.ds.DataStoreException;
import jp.sf.fess.ds.IndexUpdateCallback;
import jp.sf.fess.helper.CrawlingSessionHelper;
import jp.sf.orangesignal.csv.CsvConfig;

import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.auth.DigestScheme;
import org.apache.http.impl.auth.NTLMScheme;
import org.codelibs.solr.lib.SolrGroup;
import org.seasar.framework.container.SingletonS2Container;
import org.seasar.framework.util.SerializeUtil;
import org.seasar.framework.util.StringUtil;
import org.seasar.robot.RobotSystemException;
import org.seasar.robot.client.S2RobotClient;
import org.seasar.robot.client.S2RobotClientFactory;
import org.seasar.robot.client.http.Authentication;
import org.seasar.robot.client.http.HcHttpClient;
import org.seasar.robot.client.http.impl.AuthenticationImpl;
import org.seasar.robot.client.http.ntlm.JcifsEngine;
import org.seasar.robot.client.smb.SmbAuthentication;
import org.seasar.robot.client.smb.SmbClient;
import org.seasar.robot.entity.ResponseData;
import org.seasar.robot.entity.ResultData;
import org.seasar.robot.processor.ResponseProcessor;
import org.seasar.robot.processor.impl.DefaultResponseProcessor;
import org.seasar.robot.rule.Rule;
import org.seasar.robot.rule.RuleManager;
import org.seasar.robot.transformer.Transformer;
import org.seasar.robot.util.LruHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileListDataStoreImpl extends CsvDataStoreImpl {

    private static final String S2ROBOT_WEB_HEADER_PREFIX = "s2robot.web.header.";

    private static final String S2ROBOT_WEB_AUTH = "s2robot.web.auth";

    private static final String S2ROBOT_USERAGENT = "s2robot.useragent";

    private static final String S2ROBOT_PARAM_PREFIX = "s2robot.param.";

    private static final Object S2ROBOT_FILE_AUTH = "s2robot.file.auth";

    private static final Logger logger = LoggerFactory
            .getLogger(FileListDataStoreImpl.class);

    public boolean deleteProcessedFile = true;

    public long csvFileTimestampMargin = 60 * 1000;// 1min

    public boolean ignoreDataStoreException = true;

    public String createEventName = "create";

    public String modifyEventName = "modify";

    public String deleteEventName = "delete";

    public String eventTypeField = "event_type";

    public String urlField = "url";

    public int maxDeleteDocumentCacheSize = 100;

    protected S2RobotClientFactory robotClientFactory;

    protected CrawlingSessionHelper crawlingSessionHelper;

    public Map<String, String> parentEncodingMap = Collections
            .synchronizedMap(new LruHashMap<String, String>(1000));

    public String[] ignoreFieldNames = new String[] {
            Constants.INDEXING_TARGET, Constants.SESSION_ID };

    @Override
    protected boolean isCsvFile(final File parentFile, final String filename) {
        if (super.isCsvFile(parentFile, filename)) {
            final File file = new File(parentFile, filename);
            final long now = System.currentTimeMillis();
            return now - file.lastModified() > csvFileTimestampMargin;
        }
        return false;
    }

    @Override
    protected void storeData(final IndexUpdateCallback callback,
            final Map<String, String> paramMap,
            final Map<String, String> scriptMap,
            final Map<String, Object> defaultDataMap) {
        robotClientFactory = SingletonS2Container
                .getComponent(S2RobotClientFactory.class);

        final Map<String, Object> initParamMap = new HashMap<String, Object>();
        robotClientFactory.setInitParameterMap(initParamMap);

        // parameters
        for (final Map.Entry<String, String> entry : paramMap.entrySet()) {
            final String key = entry.getKey();
            if (key.startsWith(S2ROBOT_PARAM_PREFIX)) {
                initParamMap.put(key.substring(S2ROBOT_PARAM_PREFIX.length()),
                        entry.getValue());
            }
        }

        // user agent
        final String userAgent = paramMap.get(S2ROBOT_USERAGENT);
        if (StringUtil.isNotBlank(userAgent)) {
            initParamMap.put(HcHttpClient.USER_AGENT_PROPERTY, userAgent);
        }

        // web auth
        final String webAuthStr = paramMap.get(S2ROBOT_WEB_AUTH);
        if (StringUtil.isNotBlank(webAuthStr)) {
            final String[] webAuthNames = webAuthStr.split(",");
            final List<Authentication> basicAuthList = new ArrayList<Authentication>();
            for (final String webAuthName : webAuthNames) {
                final String scheme = paramMap.get(S2ROBOT_WEB_AUTH + "."
                        + webAuthName + ".scheme");
                final String hostname = paramMap.get(S2ROBOT_WEB_AUTH + "."
                        + webAuthName + ".host");
                final String port = paramMap.get(S2ROBOT_WEB_AUTH + "."
                        + webAuthName + ".port");
                final String realm = paramMap.get(S2ROBOT_WEB_AUTH + "."
                        + webAuthName + ".realm");
                final String username = paramMap.get(S2ROBOT_WEB_AUTH + "."
                        + webAuthName + ".username");
                final String password = paramMap.get(S2ROBOT_WEB_AUTH + "."
                        + webAuthName + ".password");

                if (StringUtil.isEmpty(username)) {
                    logger.warn("username is empty. webAuth:" + webAuthName);
                    continue;
                }

                AuthScheme authScheme = null;
                if (Constants.BASIC.equals(scheme)) {
                    authScheme = new BasicScheme();
                } else if (Constants.DIGEST.equals(scheme)) {
                    authScheme = new DigestScheme();
                } else if (Constants.NTLM.equals(scheme)) {
                    authScheme = new NTLMScheme(new JcifsEngine());
                }

                AuthScope authScope;
                if (StringUtil.isBlank(hostname)) {
                    authScope = AuthScope.ANY;
                } else {
                    int p = AuthScope.ANY_PORT;
                    if (StringUtil.isNotBlank(port)) {
                        try {
                            p = Integer.parseInt(port);
                        } catch (final NumberFormatException e) {
                            logger.warn("Failed to parse " + port, e);
                        }
                    }

                    String r = realm;
                    if (StringUtil.isBlank(realm)) {
                        r = AuthScope.ANY_REALM;
                    }

                    String s = scheme;
                    if (StringUtil.isBlank(scheme)
                            || Constants.NTLM.equals(scheme)) {
                        s = AuthScope.ANY_SCHEME;
                    }
                    authScope = new AuthScope(hostname, p, r, s);
                }

                Credentials credentials;
                if (Constants.NTLM.equals(scheme)) {
                    final String workstation = paramMap.get(S2ROBOT_WEB_AUTH
                            + "." + webAuthName + ".workstation");
                    final String domain = paramMap.get(S2ROBOT_WEB_AUTH + "."
                            + webAuthName + ".domain");
                    credentials = new NTCredentials(username,
                            password == null ? "" : password,
                            workstation == null ? "" : workstation,
                            domain == null ? "" : domain);
                } else {
                    credentials = new UsernamePasswordCredentials(username,
                            password == null ? "" : password);
                }

                basicAuthList.add(new AuthenticationImpl(authScope,
                        credentials, authScheme));
            }
            initParamMap.put(HcHttpClient.BASIC_AUTHENTICATIONS_PROPERTY,
                    basicAuthList.toArray(new Authentication[basicAuthList
                            .size()]));
        }

        // request header
        final List<org.seasar.robot.client.http.RequestHeader> rhList = new ArrayList<org.seasar.robot.client.http.RequestHeader>();
        int count = 1;
        String headerName = paramMap.get(S2ROBOT_WEB_HEADER_PREFIX + count
                + ".name");
        while (StringUtil.isNotBlank(headerName)) {
            final String headerValue = paramMap.get(S2ROBOT_WEB_HEADER_PREFIX
                    + count + ".value");
            rhList.add(new org.seasar.robot.client.http.RequestHeader(
                    headerName, headerValue));
            count++;
            headerName = paramMap.get(S2ROBOT_WEB_HEADER_PREFIX + count
                    + ".name");
        }
        if (!rhList.isEmpty()) {
            initParamMap
                    .put(HcHttpClient.REQUERT_HEADERS_PROPERTY,
                            rhList.toArray(new org.seasar.robot.client.http.RequestHeader[rhList
                                    .size()]));
        }

        // file auth
        final String fileAuthStr = paramMap.get(S2ROBOT_FILE_AUTH);
        if (StringUtil.isNotBlank(fileAuthStr)) {
            final String[] fileAuthNames = fileAuthStr.split(",");
            final List<SmbAuthentication> smbAuthList = new ArrayList<SmbAuthentication>();
            for (final String fileAuthName : fileAuthNames) {
                final String scheme = paramMap.get(S2ROBOT_FILE_AUTH + "."
                        + fileAuthName + ".scheme");
                if (Constants.SAMBA.equals(scheme)) {
                    final String domain = paramMap.get(S2ROBOT_FILE_AUTH + "."
                            + fileAuthName + ".domain");
                    final String hostname = paramMap.get(S2ROBOT_FILE_AUTH
                            + "." + fileAuthName + ".host");
                    final String port = paramMap.get(S2ROBOT_FILE_AUTH + "."
                            + fileAuthName + ".port");
                    final String username = paramMap.get(S2ROBOT_FILE_AUTH
                            + "." + fileAuthName + ".username");
                    final String password = paramMap.get(S2ROBOT_FILE_AUTH
                            + "." + fileAuthName + ".password");

                    if (StringUtil.isEmpty(username)) {
                        logger.warn("username is empty. fileAuth:"
                                + fileAuthName);
                        continue;
                    }

                    final SmbAuthentication smbAuth = new SmbAuthentication();
                    smbAuth.setDomain(domain == null ? "" : domain);
                    smbAuth.setServer(hostname);
                    try {
                        smbAuth.setPort(Integer.parseInt(port));
                    } catch (final NumberFormatException e) {
                        logger.warn("Failed to parse " + port, e);
                    }
                    smbAuth.setUsername(username);
                    smbAuth.setPassword(password == null ? "" : password);
                    smbAuthList.add(smbAuth);
                }
            }
            if (!smbAuthList.isEmpty()) {
                initParamMap.put(SmbClient.SMB_AUTHENTICATIONS_PROPERTY,
                        smbAuthList.toArray(new SmbAuthentication[smbAuthList
                                .size()]));
            }
        }

        crawlingSessionHelper = SingletonS2Container
                .getComponent(CrawlingSessionHelper.class);

        super.storeData(new FileListIndexUpdateCallback(callback), paramMap,
                scriptMap, defaultDataMap);
    }

    @Override
    protected void processCsv(final IndexUpdateCallback callback,
            final Map<String, String> paramMap,
            final Map<String, String> scriptMap,
            final Map<String, Object> defaultDataMap,
            final CsvConfig csvConfig, final File csvFile,
            final long readInterval, final String csvFileEncoding,
            final boolean hasHeaderLine) {
        try {
            super.processCsv(callback, paramMap, scriptMap, defaultDataMap,
                    csvConfig, csvFile, readInterval, csvFileEncoding,
                    hasHeaderLine);

            // delete csv file
            if (deleteProcessedFile && !csvFile.delete()) {
                logger.warn("Failed to delete {}", csvFile.getAbsolutePath());
            }
        } catch (final DataStoreException e) {
            if (ignoreDataStoreException) {
                logger.error("Failed to process " + csvFile.getAbsolutePath(),
                        e);
                // rename csv file, or delete it if failed
                if (!csvFile.renameTo(new File(csvFile.getParent(), csvFile
                        .getName() + ".txt"))
                        && !csvFile.delete()) {
                    logger.warn("Failed to delete {}",
                            csvFile.getAbsolutePath());
                }
            } else {
                throw e;
            }
        }
    }

    protected class FileListIndexUpdateCallback implements IndexUpdateCallback {
        protected IndexUpdateCallback indexUpdateCallback;

        protected List<String> deleteIdList = new ArrayList<String>();

        protected FileListIndexUpdateCallback(
                final IndexUpdateCallback indexUpdateCallback) {
            this.indexUpdateCallback = indexUpdateCallback;

        }

        @Override
        public boolean store(final Map<String, Object> dataMap) {
            final Object eventType = dataMap.remove(eventTypeField);

            if (createEventName.equals(eventType)
                    || modifyEventName.equals(eventType)) {
                // updated file
                return addDocument(dataMap);
            } else if (deleteEventName.equals(eventType)) {
                // deleted file
                return deleteDocument(dataMap);
            }

            logger.warn("unknown event: " + eventType + ", data: " + dataMap);
            return false;
        }

        protected boolean addDocument(final Map<String, Object> dataMap) {
            synchronized (indexUpdateCallback) {

                //   required check
                if (!dataMap.containsKey(urlField)
                        || dataMap.get(urlField) == null) {
                    logger.warn("Could not add a doc. Invalid data: " + dataMap);
                    return false;
                }

                final String url = dataMap.get(urlField).toString();
                final S2RobotClient client = robotClientFactory.getClient(url);
                if (client == null) {
                    logger.warn("S2RobotClient is null. Data: " + dataMap);
                    return false;
                }

                final long startTime = System.currentTimeMillis();
                final ResponseData responseData = client.doGet(url);
                responseData.setExecutionTime(System.currentTimeMillis()
                        - startTime);
                responseData.setSessionId((String) dataMap
                        .get(Constants.SESSION_ID));

                final RuleManager ruleManager = SingletonS2Container
                        .getComponent(RuleManager.class);
                final Rule rule = ruleManager.getRule(responseData);
                if (rule == null) {
                    logger.warn("No url rule. Data: " + dataMap);
                    return false;
                } else {
                    responseData.setRuleId(rule.getRuleId());
                    final ResponseProcessor responseProcessor = rule
                            .getResponseProcessor();
                    if (responseProcessor instanceof DefaultResponseProcessor) {
                        final Transformer transformer = ((DefaultResponseProcessor) responseProcessor)
                                .getTransformer();
                        final ResultData resultData = transformer
                                .transform(responseData);
                        final byte[] data = resultData.getData();
                        if (data != null) {
                            try {
                                @SuppressWarnings("unchecked")
                                final Map<String, Object> responseDataMap = (Map<String, Object>) SerializeUtil
                                        .fromBinaryToObject(data);
                                dataMap.putAll(responseDataMap);
                            } catch (final Exception e) {
                                throw new RobotSystemException(
                                        "Could not create an instanced from bytes.",
                                        e);
                            }
                        }

                        // remove
                        for (final String fieldName : ignoreFieldNames) {
                            dataMap.remove(fieldName);
                        }

                        return indexUpdateCallback.store(dataMap);
                    } else {
                        logger.warn("The response processor is not DefaultResponseProcessor. responseProcessor: "
                                + responseProcessor + ", Data: " + dataMap);
                        return false;
                    }
                }
            }
        }

        protected boolean deleteDocument(final Map<String, Object> dataMap) {

            if (logger.isDebugEnabled()) {
                logger.debug("Deleting " + dataMap);
            }

            //   required check
            if (!dataMap.containsKey(urlField) || dataMap.get(urlField) == null) {
                logger.warn("Could not delete a doc. Invalid data: " + dataMap);
                return false;
            }

            synchronized (indexUpdateCallback) {
                deleteIdList.add(crawlingSessionHelper.generateId(dataMap));

                if (deleteIdList.size() >= maxDeleteDocumentCacheSize) {
                    indexUpdateCallback.getSolrGroup().deleteById(deleteIdList);
                    if (logger.isDebugEnabled()) {
                        logger.debug("Deleted " + deleteIdList);
                    }
                    deleteIdList.clear();
                }

            }
            return true;
        }

        @Override
        public void setSolrGroup(final SolrGroup solrGroup) {
            indexUpdateCallback.setSolrGroup(solrGroup);
        }

        @Override
        public void setCommitPerCount(final long commitPerCount) {
            indexUpdateCallback.setCommitPerCount(commitPerCount);
        }

        @Override
        public long getDocumentSize() {
            return indexUpdateCallback.getDocumentSize();
        }

        @Override
        public long getExecuteTime() {
            return indexUpdateCallback.getExecuteTime();
        }

        @Override
        public void commit() {
            if (!deleteIdList.isEmpty()) {
                indexUpdateCallback.getSolrGroup().deleteById(deleteIdList);
                if (logger.isDebugEnabled()) {
                    logger.debug("Deleted " + deleteIdList);
                }
            }
            indexUpdateCallback.commit();
        }

        @Override
        public SolrGroup getSolrGroup() {
            return indexUpdateCallback.getSolrGroup();
        }
    }
}
