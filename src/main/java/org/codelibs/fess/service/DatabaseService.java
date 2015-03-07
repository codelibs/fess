/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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

package org.codelibs.fess.service;

import java.beans.PersistenceDelegate;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.transaction.UserTransaction;

import org.codelibs.core.util.DynamicProperties;
import org.codelibs.core.util.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.FessSystemException;
import org.codelibs.fess.db.exbhv.BoostDocumentRuleBhv;
import org.codelibs.fess.db.exbhv.CrawlingSessionBhv;
import org.codelibs.fess.db.exbhv.CrawlingSessionInfoBhv;
import org.codelibs.fess.db.exbhv.DataConfigToLabelTypeMappingBhv;
import org.codelibs.fess.db.exbhv.DataConfigToRoleTypeMappingBhv;
import org.codelibs.fess.db.exbhv.DataCrawlingConfigBhv;
import org.codelibs.fess.db.exbhv.FileAuthenticationBhv;
import org.codelibs.fess.db.exbhv.FileConfigToLabelTypeMappingBhv;
import org.codelibs.fess.db.exbhv.FileConfigToRoleTypeMappingBhv;
import org.codelibs.fess.db.exbhv.FileCrawlingConfigBhv;
import org.codelibs.fess.db.exbhv.KeyMatchBhv;
import org.codelibs.fess.db.exbhv.LabelTypeBhv;
import org.codelibs.fess.db.exbhv.LabelTypeToRoleTypeMappingBhv;
import org.codelibs.fess.db.exbhv.OverlappingHostBhv;
import org.codelibs.fess.db.exbhv.PathMappingBhv;
import org.codelibs.fess.db.exbhv.RequestHeaderBhv;
import org.codelibs.fess.db.exbhv.RoleTypeBhv;
import org.codelibs.fess.db.exbhv.ScheduledJobBhv;
import org.codelibs.fess.db.exbhv.SuggestBadWordBhv;
import org.codelibs.fess.db.exbhv.SuggestElevateWordBhv;
import org.codelibs.fess.db.exbhv.WebAuthenticationBhv;
import org.codelibs.fess.db.exbhv.WebConfigToLabelTypeMappingBhv;
import org.codelibs.fess.db.exbhv.WebConfigToRoleTypeMappingBhv;
import org.codelibs.fess.db.exbhv.WebCrawlingConfigBhv;
import org.codelibs.fess.db.exentity.BoostDocumentRule;
import org.codelibs.fess.db.exentity.CrawlingSession;
import org.codelibs.fess.db.exentity.CrawlingSessionInfo;
import org.codelibs.fess.db.exentity.DataConfigToLabelTypeMapping;
import org.codelibs.fess.db.exentity.DataConfigToRoleTypeMapping;
import org.codelibs.fess.db.exentity.DataCrawlingConfig;
import org.codelibs.fess.db.exentity.FileAuthentication;
import org.codelibs.fess.db.exentity.FileConfigToLabelTypeMapping;
import org.codelibs.fess.db.exentity.FileConfigToRoleTypeMapping;
import org.codelibs.fess.db.exentity.FileCrawlingConfig;
import org.codelibs.fess.db.exentity.KeyMatch;
import org.codelibs.fess.db.exentity.LabelType;
import org.codelibs.fess.db.exentity.LabelTypeToRoleTypeMapping;
import org.codelibs.fess.db.exentity.OverlappingHost;
import org.codelibs.fess.db.exentity.PathMapping;
import org.codelibs.fess.db.exentity.RequestHeader;
import org.codelibs.fess.db.exentity.RoleType;
import org.codelibs.fess.db.exentity.ScheduledJob;
import org.codelibs.fess.db.exentity.SuggestBadWord;
import org.codelibs.fess.db.exentity.SuggestElevateWord;
import org.codelibs.fess.db.exentity.WebAuthentication;
import org.codelibs.fess.db.exentity.WebConfigToLabelTypeMapping;
import org.codelibs.fess.db.exentity.WebConfigToRoleTypeMapping;
import org.codelibs.fess.db.exentity.WebCrawlingConfig;
import org.codelibs.fess.helper.LabelTypeHelper;
import org.codelibs.fess.util.ComponentUtil;
import org.seasar.framework.beans.util.Beans;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseService {

    private static final String LIST_SUFFIX = "List";

    private static final Logger logger = LoggerFactory.getLogger(DatabaseService.class);

    private static final String VERSION_KEY = "version";

    private static final String CRAWLER_PROPERTIES_KEY = "crawlerProperties";

    private static final String SCHEDULED_JOB_KEY = "scheduledJob";

    private static final String LABEL_TYPE_KEY = "labelType";

    private static final String ROLE_TYPE_KEY = "roleType";

    private static final String CRAWLING_SESSION_KEY = "crawlingSession";

    private static final String CRAWLING_SESSION_INFO_KEY = "crawlingSessionInfo";

    private static final String FILE_CRAWLING_CONFIG_KEY = "fileCrawlingConfig";

    private static final String DATA_CRAWLING_CONFIG_KEY = "dataCrawlingConfig";

    private static final String PATH_MAPPING_KEY = "pathMapping";

    private static final String WEB_CRAWLING_CONFIG_KEY = "webCrawlingConfig";

    private static final String FILE_CONFIG_TO_LABEL_TYPE_MAPPING_KEY = "fileConfigToLabelTypeMapping";

    private static final String DATA_CONFIG_TO_LABEL_TYPE_MAPPING_KEY = "dataConfigToLabelTypeMapping";

    private static final String WEB_CONFIG_TO_LABEL_TYPE_MAPPING_KEY = "webConfigToLabelTypeMapping";

    private static final String FILE_CONFIG_TO_ROLE_TYPE_MAPPING_KEY = "fileConfigToRoleTypeMapping";

    private static final String DATA_CONFIG_TO_ROLE_TYPE_MAPPING_KEY = "dataConfigToRoleTypeMapping";

    private static final String WEB_CONFIG_TO_ROLE_TYPE_MAPPING_KEY = "webConfigToRoleTypeMapping";

    private static final String LABEL_TYPE_TO_ROLE_TYPE_MAPPING_KEY = "labelTypeToRoleTypeMapping";

    private static final String WEB_AUTHENTICATION_KEY = "webAuthentication";

    private static final String FILE_AUTHENTICATION_KEY = "fileAuthentication";

    private static final String REQUEST_HEADER_KEY = "requestHeader";

    private static final String KEY_MATCH_KEY = "keyMatch";

    private static final String BOOST_DOCUMENT_RULE_KEY = "boostDocumentRule";

    private static final String SUGGEST_ELEVATE_WORD_KEY = "suggestElevateWord";

    private static final String SUGGEST_BAD_WORD_KEY = "suggestBadWord";

    private static final String OVERLAPPING_HOST_KEY = "overlappingHost";

    @Resource
    protected ScheduledJobBhv scheduledJobBhv;

    @Resource
    protected LabelTypeBhv labelTypeBhv;

    @Resource
    protected RoleTypeBhv roleTypeBhv;

    @Resource
    protected CrawlingSessionBhv crawlingSessionBhv;

    @Resource
    protected CrawlingSessionInfoBhv crawlingSessionInfoBhv;

    @Resource
    protected FileCrawlingConfigBhv fileCrawlingConfigBhv;

    @Resource
    protected DataCrawlingConfigBhv dataCrawlingConfigBhv;

    @Resource
    protected PathMappingBhv pathMappingBhv;

    @Resource
    protected WebCrawlingConfigBhv webCrawlingConfigBhv;

    @Resource
    protected FileConfigToLabelTypeMappingBhv fileConfigToLabelTypeMappingBhv;

    @Resource
    protected DataConfigToLabelTypeMappingBhv dataConfigToLabelTypeMappingBhv;

    @Resource
    protected WebConfigToLabelTypeMappingBhv webConfigToLabelTypeMappingBhv;

    @Resource
    protected FileConfigToRoleTypeMappingBhv fileConfigToRoleTypeMappingBhv;

    @Resource
    protected DataConfigToRoleTypeMappingBhv dataConfigToRoleTypeMappingBhv;

    @Resource
    protected WebConfigToRoleTypeMappingBhv webConfigToRoleTypeMappingBhv;

    @Resource
    protected LabelTypeToRoleTypeMappingBhv labelTypeToRoleTypeMappingBhv;

    @Resource
    protected WebAuthenticationBhv webAuthenticationBhv;

    @Resource
    protected FileAuthenticationBhv fileAuthenticationBhv;

    @Resource
    protected OverlappingHostBhv overlappingHostBhv;

    @Resource
    protected RequestHeaderBhv requestHeaderBhv;

    @Resource
    protected KeyMatchBhv keyMatchBhv;

    @Resource
    protected BoostDocumentRuleBhv boostDocumentRuleBhv;

    @Resource
    protected SuggestElevateWordBhv suggestElevateWordBhv;

    @Resource
    protected SuggestBadWordBhv suggestBadWordBhv;

    @Resource
    protected DynamicProperties crawlerProperties;

    @Resource
    public UserTransaction userTransaction;

    public static class DataSet implements Serializable {
        private static final long serialVersionUID = 1L;

        private final Map<String, Object> dataMap = new HashMap<String, Object>();

        public void put(final String key, final Object value) {
            dataMap.put(key, value);
        }
    }

    public void exportData(final OutputStream out) {
        if (out == null) {
            throw new FessSystemException("The output stream is null.");
        }

        final Map<String, Object> dataSet = new HashMap<String, Object>();

        // version
        dataSet.put(VERSION_KEY, Constants.FESS_VERSION);

        // scheduledJob
        dataSet.put(SCHEDULED_JOB_KEY + LIST_SUFFIX, scheduledJobBhv.selectList(scheduledJobCB -> {
            scheduledJobCB.query().setDeletedBy_IsNull();
        }));
        // labelType
        dataSet.put(LABEL_TYPE_KEY + LIST_SUFFIX, labelTypeBhv.selectList(labelTypeCB -> {
            labelTypeCB.query().setDeletedBy_IsNull();
        }));
        // roleType
        dataSet.put(ROLE_TYPE_KEY + LIST_SUFFIX, roleTypeBhv.selectList(roleTypeCB -> {
            roleTypeCB.query().setDeletedBy_IsNull();
        }));
        // fileCrawlingConfig
        dataSet.put(FILE_CRAWLING_CONFIG_KEY + LIST_SUFFIX, fileCrawlingConfigBhv.selectList(fileCrawlingConfigCB -> {
            fileCrawlingConfigCB.query().setDeletedBy_IsNull();
        }));
        // dataCrawlingConfig
        dataSet.put(DATA_CRAWLING_CONFIG_KEY + LIST_SUFFIX, dataCrawlingConfigBhv.selectList(dataCrawlingConfigCB -> {
            dataCrawlingConfigCB.query().setDeletedBy_IsNull();
        }));
        // pathMapping
        dataSet.put(PATH_MAPPING_KEY + LIST_SUFFIX, pathMappingBhv.selectList(pathMappingCB -> {
            pathMappingCB.query().setDeletedBy_IsNull();
        }));
        // overlappingHost
        dataSet.put(OVERLAPPING_HOST_KEY + LIST_SUFFIX, overlappingHostBhv.selectList(overlappingHostCB -> {
            overlappingHostCB.query().setDeletedBy_IsNull();
        }));
        // webCrawlingConfig
        dataSet.put(WEB_CRAWLING_CONFIG_KEY + LIST_SUFFIX, webCrawlingConfigBhv.selectList(webCrawlingConfigCB -> {
            webCrawlingConfigCB.query().setDeletedBy_IsNull();
        }));
        // fileConfigToLabelTypeMapping
        dataSet.put(FILE_CONFIG_TO_LABEL_TYPE_MAPPING_KEY + LIST_SUFFIX,
                fileConfigToLabelTypeMappingBhv.selectList(fileConfigToLabelTypeMappingCB -> {}));
        // dataConfigToLabelTypeMapping
        dataSet.put(DATA_CONFIG_TO_LABEL_TYPE_MAPPING_KEY + LIST_SUFFIX,
                dataConfigToLabelTypeMappingBhv.selectList(dataConfigToLabelTypeMappingCB -> {}));
        // webConfigToLabelTypeMapping
        dataSet.put(WEB_CONFIG_TO_LABEL_TYPE_MAPPING_KEY + LIST_SUFFIX,
                webConfigToLabelTypeMappingBhv.selectList(webConfigToLabelTypeMappingCB -> {}));
        // fileConfigToRoleTypeMapping
        dataSet.put(FILE_CONFIG_TO_ROLE_TYPE_MAPPING_KEY + LIST_SUFFIX,
                fileConfigToRoleTypeMappingBhv.selectList(fileConfigToRoleTypeMappingCB -> {}));
        // dataConfigToRoleTypeMapping
        dataSet.put(DATA_CONFIG_TO_ROLE_TYPE_MAPPING_KEY + LIST_SUFFIX,
                dataConfigToRoleTypeMappingBhv.selectList(dataConfigToRoleTypeMappingCB -> {}));
        // webConfigToRoleTypeMapping
        dataSet.put(WEB_CONFIG_TO_ROLE_TYPE_MAPPING_KEY + LIST_SUFFIX,
                webConfigToRoleTypeMappingBhv.selectList(webConfigToRoleTypeMappingCB -> {}));
        // webConfigToRoleTypeMapping
        dataSet.put(LABEL_TYPE_TO_ROLE_TYPE_MAPPING_KEY + LIST_SUFFIX,
                labelTypeToRoleTypeMappingBhv.selectList(labelTypeToRoleTypeMappingCB -> {}));
        // webAuthentication
        dataSet.put(WEB_AUTHENTICATION_KEY + LIST_SUFFIX, webAuthenticationBhv.selectList(webAuthenticationCB -> {
            webAuthenticationCB.query().setDeletedBy_IsNull();
        }));
        // fileAuthentication
        dataSet.put(FILE_AUTHENTICATION_KEY + LIST_SUFFIX, fileAuthenticationBhv.selectList(fileAuthenticationCB -> {
            fileAuthenticationCB.query().setDeletedBy_IsNull();
        }));
        // requestHeader
        dataSet.put(REQUEST_HEADER_KEY + LIST_SUFFIX, requestHeaderBhv.selectList(requestHeaderCB -> {
            requestHeaderCB.query().setDeletedBy_IsNull();
        }));
        // keyMatch
        dataSet.put(KEY_MATCH_KEY + LIST_SUFFIX, keyMatchBhv.selectList(keyMatchCB -> {
            keyMatchCB.query().setDeletedBy_IsNull();
        }));
        // boostDocumentRule
        dataSet.put(BOOST_DOCUMENT_RULE_KEY + LIST_SUFFIX, boostDocumentRuleBhv.selectList(boostDocumentRuleCB -> {
            boostDocumentRuleCB.query().setDeletedBy_IsNull();
        }));
        // suggestElevateWord
        dataSet.put(SUGGEST_ELEVATE_WORD_KEY + LIST_SUFFIX, suggestElevateWordBhv.selectList(suggestElevateWordCB -> {
            suggestElevateWordCB.query().setDeletedBy_IsNull();
        }));
        // suggestBadWord
        dataSet.put(SUGGEST_BAD_WORD_KEY + LIST_SUFFIX, suggestBadWordBhv.selectList(suggestBadWordCB -> {
            suggestBadWordCB.query().setDeletedBy_IsNull();
        }));

        // crawlerProperties
        final Map<String, String> crawlerPropertyMap = new HashMap<String, String>();
        for (final Map.Entry<Object, Object> entry : crawlerProperties.entrySet()) {
            try {
                crawlerPropertyMap.put(entry.getKey().toString(), entry.getValue().toString());
            } catch (final Exception e) {
                logger.warn("Invalid data. key: " + entry.getKey() + ", value: " + entry.getValue(), e);
            }
        }
        dataSet.put(CRAWLER_PROPERTIES_KEY, crawlerPropertyMap);

        try {
            final XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(out));
            final PersistenceDelegate pd = encoder.getPersistenceDelegate(Date.class);
            encoder.setPersistenceDelegate(Timestamp.class, pd);
            encoder.writeObject(dataSet);
            encoder.close();
        } catch (final Exception e) {
            throw new FessSystemException("Could not write a data set.", e);
        }
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void importData(final InputStream in, final boolean overwrite) {
        if (in == null) {
            throw new FessSystemException("The input stream is null.");
        }

        Map<String, Object> dataSet;
        try {
            final XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(in));
            dataSet = (Map<String, Object>) decoder.readObject();
        } catch (final Exception e) {
            throw new FessSystemException("Could not read a data set.", e);
        }

        if (dataSet == null) {
            throw new FessSystemException("The object is null.");
        }

        // TODO check version

        new Thread(new DataImporter(dataSet, overwrite)).start();

    }

    protected class DataImporter implements Runnable {

        protected boolean overwrite;

        protected Map<String, Object> dataSet;

        protected DataImporter(final Map<String, Object> dataSet, final boolean overwrite) {
            this.dataSet = dataSet;
            this.overwrite = overwrite;
        }

        @Override
        public void run() {
            final Map<String, Long> idMap = new HashMap<String, Long>();

            // scheduledJob
            try {
                userTransaction.begin();

                final List<ScheduledJob> scheduledJobList = (List<ScheduledJob>) dataSet.get(SCHEDULED_JOB_KEY + LIST_SUFFIX);
                if (scheduledJobList != null) {
                    for (ScheduledJob scheduledJob : scheduledJobList) {
                        final Long id = scheduledJob.getId();

                        final String name = scheduledJob.getName();
                        final ScheduledJob entity = scheduledJobBhv.selectEntity(cb -> {
                            cb.query().setName_Equal(name);
                            cb.query().setDeletedBy_IsNull();
                        }).orElse(null);//TODO
                        scheduledJob.setId(null);
                        if (entity == null) {
                            scheduledJobBhv.insert(scheduledJob);
                        } else {
                            if (overwrite) {
                                scheduledJob.setVersionNo(null);
                                Beans.copy(scheduledJob, entity).excludesNull().execute();
                                scheduledJob = entity;
                                scheduledJobBhv.update(scheduledJob);
                            } else {
                                scheduledJobBhv.insert(scheduledJob);
                            }
                        }
                        idMap.put(SCHEDULED_JOB_KEY + ":" + id.toString(), scheduledJob.getId());
                    }
                }
                userTransaction.commit();
            } catch (final Exception e) {
                rollback(SCHEDULED_JOB_KEY, e);
            }
            // labelType
            try {
                userTransaction.begin();

                final List<LabelType> labelTypeList = (List<LabelType>) dataSet.get(LABEL_TYPE_KEY + LIST_SUFFIX);
                if (labelTypeList != null) {
                    for (LabelType labelType : labelTypeList) {
                        final Long id = labelType.getId();

                        final String value = labelType.getValue();
                        final LabelType entity = labelTypeBhv.selectEntity(cb -> {
                            cb.query().setValue_Equal(value);
                            cb.query().setDeletedBy_IsNull();
                        }).orElse(null);//TODO
                        labelType.setId(null);
                        if (entity == null) {
                            labelTypeBhv.insert(labelType);
                        } else {
                            // always overwrite
                            // if (overwrite) {
                            labelType.setVersionNo(null);
                            Beans.copy(labelType, entity).excludesNull().execute();
                            labelType = entity;
                            labelTypeBhv.update(labelType);
                            // } else {
                            // labelTypeBhv.insert(labelType);
                            // }
                        }
                        idMap.put(LABEL_TYPE_KEY + ":" + id.toString(), labelType.getId());
                    }
                }
                // restore labels
                final LabelTypeHelper labelTypeHelper = ComponentUtil.getLabelTypeHelper();
                if (labelTypeHelper != null) {
                    labelTypeHelper.init();
                }
                userTransaction.commit();
            } catch (final Exception e) {
                rollback(LABEL_TYPE_KEY, e);
            }
            // roleType
            try {
                userTransaction.begin();

                final List<RoleType> roleTypeList = (List<RoleType>) dataSet.get(ROLE_TYPE_KEY + LIST_SUFFIX);
                if (roleTypeList != null) {
                    for (RoleType roleType : roleTypeList) {
                        final Long id = roleType.getId();

                        final String value = roleType.getValue();
                        final RoleType entity = roleTypeBhv.selectEntity(cb -> {
                            cb.query().setValue_Equal(value);
                            cb.query().setDeletedBy_IsNull();
                        }).orElse(null);//TODO
                        roleType.setId(null);
                        if (entity == null) {
                            roleTypeBhv.insert(roleType);
                        } else {
                            // always overwrite
                            // if (overwrite) {
                            roleType.setVersionNo(null);
                            Beans.copy(roleType, entity).excludesNull().execute();
                            roleType = entity;
                            roleTypeBhv.update(roleType);
                            // } else {
                            // roleTypeBhv.insert(roleType);
                            // }
                        }
                        idMap.put(ROLE_TYPE_KEY + ":" + id.toString(), roleType.getId());
                    }
                }
                userTransaction.commit();
            } catch (final Exception e) {
                rollback(ROLE_TYPE_KEY, e);
            }
            // crawlingSession
            try {
                userTransaction.begin();

                final List<CrawlingSession> crawlingSessionList = (List<CrawlingSession>) dataSet.get(CRAWLING_SESSION_KEY + LIST_SUFFIX);
                if (crawlingSessionList != null) {
                    for (CrawlingSession crawlingSession : crawlingSessionList) {
                        final Long id = crawlingSession.getId();

                        final String sessionId = crawlingSession.getSessionId();
                        final CrawlingSession entity = crawlingSessionBhv.selectEntity(cb -> {
                            cb.query().setSessionId_Equal(sessionId);
                        }).orElse(null);//TODO
                        crawlingSession.setId(null);
                        if (entity == null) {
                            crawlingSessionBhv.insert(crawlingSession);
                        } else {
                            Beans.copy(crawlingSession, entity).excludesNull().execute();
                            crawlingSession = entity;
                            crawlingSessionBhv.update(crawlingSession);
                            final Long crawlingSessionId = crawlingSession.getId();
                            // delete info
                            crawlingSessionInfoBhv.varyingQueryDelete(cb2 -> {
                                cb2.query().setCrawlingSessionId_Equal(crawlingSessionId);
                            }, op -> op.allowNonQueryDelete());
                        }
                        idMap.put(CRAWLING_SESSION_KEY + ":" + id.toString(), crawlingSession.getId());
                    }
                }
                userTransaction.commit();
            } catch (final Exception e) {
                rollback(CRAWLING_SESSION_KEY, e);
            }
            // crawlingSessionInfo
            try {
                userTransaction.begin();

                final List<CrawlingSessionInfo> crawlingSessionInfoList =
                        (List<CrawlingSessionInfo>) dataSet.get(CRAWLING_SESSION_INFO_KEY + LIST_SUFFIX);
                if (crawlingSessionInfoList != null) {
                    for (final CrawlingSessionInfo crawlingSessionInfo : crawlingSessionInfoList) {
                        final Long id = crawlingSessionInfo.getId();
                        // relations
                        crawlingSessionInfo.setCrawlingSessionId(idMap.get(CRAWLING_SESSION_KEY + ":"
                                + crawlingSessionInfo.getCrawlingSessionId()));
                        crawlingSessionInfo.setId(null);
                        crawlingSessionInfoBhv.insert(crawlingSessionInfo);
                        idMap.put(CRAWLING_SESSION_INFO_KEY + ":" + id.toString(), crawlingSessionInfo.getId());
                    }
                }
                userTransaction.commit();
            } catch (final Exception e) {
                rollback(CRAWLING_SESSION_INFO_KEY, e);
            }
            // fileCrawlingConfig
            try {
                userTransaction.begin();

                final List<FileCrawlingConfig> fileCrawlingConfigList =
                        (List<FileCrawlingConfig>) dataSet.get(FILE_CRAWLING_CONFIG_KEY + LIST_SUFFIX);
                if (fileCrawlingConfigList != null) {
                    for (FileCrawlingConfig fileCrawlingConfig : fileCrawlingConfigList) {
                        final Long id = fileCrawlingConfig.getId();

                        final String name = fileCrawlingConfig.getName();
                        final FileCrawlingConfig entity = fileCrawlingConfigBhv.selectEntity(cb -> {
                            cb.query().setName_Equal(name);
                            cb.query().setDeletedBy_IsNull();
                        }).orElse(null);//TODO
                        fileCrawlingConfig.setId(null);
                        if (entity == null) {
                            fileCrawlingConfigBhv.insert(fileCrawlingConfig);
                        } else {
                            if (overwrite) {
                                fileCrawlingConfig.setVersionNo(null);
                                Beans.copy(fileCrawlingConfig, entity).excludesNull().execute();
                                fileCrawlingConfig = entity;
                                fileCrawlingConfigBhv.update(fileCrawlingConfig);
                            } else {
                                fileCrawlingConfigBhv.insert(fileCrawlingConfig);
                            }
                        }
                        idMap.put(FILE_CRAWLING_CONFIG_KEY + ":" + id.toString(), fileCrawlingConfig.getId());
                    }
                }
                userTransaction.commit();
            } catch (final Exception e) {
                rollback(FILE_CRAWLING_CONFIG_KEY, e);
            }
            // dataCrawlingConfig
            try {
                userTransaction.begin();

                final List<DataCrawlingConfig> dataCrawlingConfigList =
                        (List<DataCrawlingConfig>) dataSet.get(DATA_CRAWLING_CONFIG_KEY + LIST_SUFFIX);
                if (dataCrawlingConfigList != null) {
                    for (DataCrawlingConfig dataCrawlingConfig : dataCrawlingConfigList) {
                        final Long id = dataCrawlingConfig.getId();

                        final String name = dataCrawlingConfig.getName();
                        final DataCrawlingConfig entity = dataCrawlingConfigBhv.selectEntity(cb -> {
                            cb.query().setName_Equal(name);
                            cb.query().setDeletedBy_IsNull();
                        }).orElse(null);//TODO
                        dataCrawlingConfig.setId(null);
                        if (entity == null) {
                            dataCrawlingConfigBhv.insert(dataCrawlingConfig);
                        } else {
                            if (overwrite) {
                                dataCrawlingConfig.setVersionNo(null);
                                Beans.copy(dataCrawlingConfig, entity).excludesNull().execute();
                                dataCrawlingConfig = entity;
                                dataCrawlingConfigBhv.update(dataCrawlingConfig);
                            } else {
                                dataCrawlingConfigBhv.insert(dataCrawlingConfig);
                            }
                        }
                        idMap.put(DATA_CRAWLING_CONFIG_KEY + ":" + id.toString(), dataCrawlingConfig.getId());
                    }
                }
                userTransaction.commit();
            } catch (final Exception e) {
                rollback(DATA_CRAWLING_CONFIG_KEY, e);
            }
            // pathMapping
            try {
                userTransaction.begin();

                final List<PathMapping> pathMappingList = (List<PathMapping>) dataSet.get(PATH_MAPPING_KEY + LIST_SUFFIX);
                if (pathMappingList != null) {
                    for (PathMapping pathMapping : pathMappingList) {
                        final Long id = pathMapping.getId();

                        final String regex = pathMapping.getRegex();
                        final PathMapping entity = pathMappingBhv.selectEntity(cb -> {
                            cb.query().setRegex_Equal(regex);
                            cb.query().setDeletedBy_IsNull();
                        }).orElse(null);//TODO
                        pathMapping.setId(null);
                        if (pathMapping.getProcessType() == null) {
                            pathMapping.setProcessType_Crawling();
                        }
                        if (entity == null) {
                            pathMappingBhv.insert(pathMapping);
                        } else {
                            if (overwrite) {
                                pathMapping.setVersionNo(null);
                                Beans.copy(pathMapping, entity).excludesNull().execute();
                                pathMapping = entity;
                                pathMappingBhv.update(pathMapping);
                            } else {
                                pathMappingBhv.insert(pathMapping);
                            }
                        }
                        idMap.put(PATH_MAPPING_KEY + ":" + id.toString(), pathMapping.getId());
                    }
                }
                userTransaction.commit();
            } catch (final Exception e) {
                rollback(PATH_MAPPING_KEY, e);
            }
            // overlappingHost
            try {
                userTransaction.begin();

                final List<OverlappingHost> overlappingHostList = (List<OverlappingHost>) dataSet.get(OVERLAPPING_HOST_KEY + LIST_SUFFIX);
                if (overlappingHostList != null) {
                    for (OverlappingHost overlappingHost : overlappingHostList) {
                        final Long id = overlappingHost.getId();

                        final String regularName = overlappingHost.getRegularName();
                        final String overlappingName = overlappingHost.getOverlappingName();
                        final OverlappingHost entity = overlappingHostBhv.selectEntity(cb -> {
                            cb.query().setRegularName_Equal(regularName);
                            cb.query().setOverlappingName_Equal(overlappingName);
                            cb.query().setDeletedBy_IsNull();
                        }).orElse(null);//TODO
                        overlappingHost.setId(null);
                        if (entity == null) {
                            overlappingHostBhv.insert(overlappingHost);
                        } else {
                            if (overwrite) {
                                overlappingHost.setVersionNo(null);
                                Beans.copy(overlappingHost, entity).excludesNull().execute();
                                overlappingHost = entity;
                                overlappingHostBhv.update(overlappingHost);
                            } else {
                                overlappingHostBhv.insert(overlappingHost);
                            }
                        }
                        idMap.put(OVERLAPPING_HOST_KEY + ":" + id.toString(), overlappingHost.getId());
                    }
                }
                userTransaction.commit();
            } catch (final Exception e) {
                rollback(OVERLAPPING_HOST_KEY, e);
            }
            // webCrawlingConfig
            try {
                userTransaction.begin();

                final List<WebCrawlingConfig> webCrawlingConfigList =
                        (List<WebCrawlingConfig>) dataSet.get(WEB_CRAWLING_CONFIG_KEY + LIST_SUFFIX);
                if (webCrawlingConfigList != null) {
                    for (WebCrawlingConfig webCrawlingConfig : webCrawlingConfigList) {
                        final Long id = webCrawlingConfig.getId();

                        final String name = webCrawlingConfig.getName();
                        final WebCrawlingConfig entity = webCrawlingConfigBhv.selectEntity(cb -> {
                            cb.query().setName_Equal(name);
                            cb.query().setDeletedBy_IsNull();
                        }).orElse(null);//TODO
                        webCrawlingConfig.setId(null);
                        if (entity == null) {
                            webCrawlingConfigBhv.insert(webCrawlingConfig);
                        } else {
                            if (overwrite) {
                                webCrawlingConfig.setVersionNo(null);
                                Beans.copy(webCrawlingConfig, entity).excludesNull().execute();
                                webCrawlingConfig = entity;
                                webCrawlingConfigBhv.update(webCrawlingConfig);
                            } else {
                                webCrawlingConfigBhv.insert(webCrawlingConfig);
                            }
                        }
                        idMap.put(WEB_CRAWLING_CONFIG_KEY + ":" + id.toString(), webCrawlingConfig.getId());
                    }
                }
                userTransaction.commit();
            } catch (final Exception e) {
                rollback(WEB_CRAWLING_CONFIG_KEY, e);
            }
            // fileConfigToLabelTypeMapping
            try {
                userTransaction.begin();

                final List<FileConfigToLabelTypeMapping> fileConfigToLabelTypeMappingList =
                        (List<FileConfigToLabelTypeMapping>) dataSet.get(FILE_CONFIG_TO_LABEL_TYPE_MAPPING_KEY + LIST_SUFFIX);
                if (fileConfigToLabelTypeMappingList != null) {
                    for (FileConfigToLabelTypeMapping fileConfigToLabelTypeMapping : fileConfigToLabelTypeMappingList) {
                        final Long id = fileConfigToLabelTypeMapping.getId();

                        final Long labelTypeId = idMap.get(LABEL_TYPE_KEY + ":" + fileConfigToLabelTypeMapping.getLabelTypeId());
                        final Long fileConfigId =
                                idMap.get(FILE_CRAWLING_CONFIG_KEY + ":" + fileConfigToLabelTypeMapping.getFileConfigId());
                        if (labelTypeId == null || fileConfigId == null) {
                            // skip
                            continue;
                        }

                        final FileConfigToLabelTypeMapping entity = fileConfigToLabelTypeMappingBhv.selectEntity(cb -> {
                            cb.query().setLabelTypeId_Equal(labelTypeId);
                            cb.query().setFileConfigId_Equal(fileConfigId);
                        }).orElse(null);//TODO
                        if (entity == null) {
                            fileConfigToLabelTypeMapping = new FileConfigToLabelTypeMapping();
                            fileConfigToLabelTypeMapping.setLabelTypeId(labelTypeId);
                            fileConfigToLabelTypeMapping.setFileConfigId(fileConfigId);
                            fileConfigToLabelTypeMappingBhv.insert(fileConfigToLabelTypeMapping);
                        }
                        idMap.put(FILE_CONFIG_TO_LABEL_TYPE_MAPPING_KEY + ":" + id.toString(), fileConfigToLabelTypeMapping.getId());
                    }
                }
                userTransaction.commit();
            } catch (final Exception e) {
                rollback(FILE_CONFIG_TO_LABEL_TYPE_MAPPING_KEY, e);
            }
            // dataConfigToLabelTypeMapping
            try {
                userTransaction.begin();

                final List<DataConfigToLabelTypeMapping> dataConfigToLabelTypeMappingList =
                        (List<DataConfigToLabelTypeMapping>) dataSet.get(DATA_CONFIG_TO_LABEL_TYPE_MAPPING_KEY + LIST_SUFFIX);
                if (dataConfigToLabelTypeMappingList != null) {
                    for (DataConfigToLabelTypeMapping dataConfigToLabelTypeMapping : dataConfigToLabelTypeMappingList) {
                        final Long id = dataConfigToLabelTypeMapping.getId();

                        final Long labelTypeId = idMap.get(LABEL_TYPE_KEY + ":" + dataConfigToLabelTypeMapping.getLabelTypeId());
                        final Long dataConfigId =
                                idMap.get(DATA_CRAWLING_CONFIG_KEY + ":" + dataConfigToLabelTypeMapping.getDataConfigId());
                        if (labelTypeId == null || dataConfigId == null) {
                            // skip
                            continue;
                        }

                        final DataConfigToLabelTypeMapping entity = dataConfigToLabelTypeMappingBhv.selectEntity(cb -> {
                            cb.query().setLabelTypeId_Equal(labelTypeId);
                            cb.query().setDataConfigId_Equal(dataConfigId);
                        }).orElse(null);//TODO
                        if (entity == null) {
                            dataConfigToLabelTypeMapping = new DataConfigToLabelTypeMapping();
                            dataConfigToLabelTypeMapping.setLabelTypeId(labelTypeId);
                            dataConfigToLabelTypeMapping.setDataConfigId(dataConfigId);
                            dataConfigToLabelTypeMappingBhv.insert(dataConfigToLabelTypeMapping);
                        }
                        idMap.put(DATA_CONFIG_TO_LABEL_TYPE_MAPPING_KEY + ":" + id.toString(), dataConfigToLabelTypeMapping.getId());
                    }
                }
                userTransaction.commit();
            } catch (final Exception e) {
                rollback(DATA_CONFIG_TO_LABEL_TYPE_MAPPING_KEY, e);
            }
            // webConfigToLabelTypeMapping
            try {
                userTransaction.begin();

                final List<WebConfigToLabelTypeMapping> webConfigToLabelTypeMappingList =
                        (List<WebConfigToLabelTypeMapping>) dataSet.get(WEB_CONFIG_TO_LABEL_TYPE_MAPPING_KEY + LIST_SUFFIX);
                if (webConfigToLabelTypeMappingList != null) {
                    for (WebConfigToLabelTypeMapping webConfigToLabelTypeMapping : webConfigToLabelTypeMappingList) {
                        final Long id = webConfigToLabelTypeMapping.getId();

                        final Long labelTypeId = idMap.get(LABEL_TYPE_KEY + ":" + webConfigToLabelTypeMapping.getLabelTypeId());
                        final Long webConfigId = idMap.get(WEB_CRAWLING_CONFIG_KEY + ":" + webConfigToLabelTypeMapping.getWebConfigId());
                        if (labelTypeId == null || webConfigId == null) {
                            // skip
                            continue;
                        }

                        final WebConfigToLabelTypeMapping entity = webConfigToLabelTypeMappingBhv.selectEntity(cb -> {
                            cb.query().setLabelTypeId_Equal(labelTypeId);
                            cb.query().setWebConfigId_Equal(webConfigId);
                        }).orElse(null);//TODO
                        if (entity == null) {
                            webConfigToLabelTypeMapping = new WebConfigToLabelTypeMapping();
                            webConfigToLabelTypeMapping.setLabelTypeId(labelTypeId);
                            webConfigToLabelTypeMapping.setWebConfigId(webConfigId);
                            webConfigToLabelTypeMappingBhv.insert(webConfigToLabelTypeMapping);
                        }
                        idMap.put(WEB_CONFIG_TO_LABEL_TYPE_MAPPING_KEY + ":" + id.toString(), webConfigToLabelTypeMapping.getId());
                    }
                }
                userTransaction.commit();
            } catch (final Exception e) {
                rollback(WEB_CONFIG_TO_LABEL_TYPE_MAPPING_KEY, e);
            }
            // fileConfigToRoleTypeMapping
            try {
                userTransaction.begin();

                final List<FileConfigToRoleTypeMapping> fileConfigToRoleTypeMappingList =
                        (List<FileConfigToRoleTypeMapping>) dataSet.get(FILE_CONFIG_TO_ROLE_TYPE_MAPPING_KEY + LIST_SUFFIX);
                if (fileConfigToRoleTypeMappingList != null) {
                    for (FileConfigToRoleTypeMapping fileConfigToRoleTypeMapping : fileConfigToRoleTypeMappingList) {
                        final Long id = fileConfigToRoleTypeMapping.getId();

                        final Long roleTypeId = idMap.get(ROLE_TYPE_KEY + ":" + fileConfigToRoleTypeMapping.getRoleTypeId());
                        final Long fileConfigId = idMap.get(FILE_CRAWLING_CONFIG_KEY + ":" + fileConfigToRoleTypeMapping.getFileConfigId());
                        if (roleTypeId == null || fileConfigId == null) {
                            // skip
                            continue;
                        }

                        final FileConfigToRoleTypeMapping entity = fileConfigToRoleTypeMappingBhv.selectEntity(cb -> {
                            cb.query().setRoleTypeId_Equal(roleTypeId);
                            cb.query().setFileConfigId_Equal(fileConfigId);
                        }).orElse(null);//TODO
                        if (entity == null) {
                            fileConfigToRoleTypeMapping = new FileConfigToRoleTypeMapping();
                            fileConfigToRoleTypeMapping.setRoleTypeId(roleTypeId);
                            fileConfigToRoleTypeMapping.setFileConfigId(fileConfigId);
                            fileConfigToRoleTypeMappingBhv.insert(fileConfigToRoleTypeMapping);
                        }
                        idMap.put(FILE_CONFIG_TO_ROLE_TYPE_MAPPING_KEY + ":" + id.toString(), fileConfigToRoleTypeMapping.getId());
                    }
                }
                userTransaction.commit();
            } catch (final Exception e) {
                rollback(FILE_CONFIG_TO_ROLE_TYPE_MAPPING_KEY, e);
            }
            // dataConfigToRoleTypeMapping
            try {
                userTransaction.begin();

                final List<DataConfigToRoleTypeMapping> dataConfigToRoleTypeMappingList =
                        (List<DataConfigToRoleTypeMapping>) dataSet.get(DATA_CONFIG_TO_ROLE_TYPE_MAPPING_KEY + LIST_SUFFIX);
                if (dataConfigToRoleTypeMappingList != null) {
                    for (DataConfigToRoleTypeMapping dataConfigToRoleTypeMapping : dataConfigToRoleTypeMappingList) {
                        final Long id = dataConfigToRoleTypeMapping.getId();

                        final Long roleTypeId = idMap.get(ROLE_TYPE_KEY + ":" + dataConfigToRoleTypeMapping.getRoleTypeId());
                        final Long dataConfigId = idMap.get(DATA_CRAWLING_CONFIG_KEY + ":" + dataConfigToRoleTypeMapping.getDataConfigId());
                        if (roleTypeId == null || dataConfigId == null) {
                            // skip
                            continue;
                        }

                        final DataConfigToRoleTypeMapping entity = dataConfigToRoleTypeMappingBhv.selectEntity(cb -> {
                            cb.query().setRoleTypeId_Equal(roleTypeId);
                            cb.query().setDataConfigId_Equal(dataConfigId);
                        }).orElse(null);//TODO
                        if (entity == null) {
                            dataConfigToRoleTypeMapping = new DataConfigToRoleTypeMapping();
                            dataConfigToRoleTypeMapping.setRoleTypeId(roleTypeId);
                            dataConfigToRoleTypeMapping.setDataConfigId(dataConfigId);
                            dataConfigToRoleTypeMappingBhv.insert(dataConfigToRoleTypeMapping);
                        }
                        idMap.put(DATA_CONFIG_TO_ROLE_TYPE_MAPPING_KEY + ":" + id.toString(), dataConfigToRoleTypeMapping.getId());
                    }
                }
                userTransaction.commit();
            } catch (final Exception e) {
                rollback(DATA_CONFIG_TO_ROLE_TYPE_MAPPING_KEY, e);
            }
            // webConfigToRoleTypeMapping
            try {
                userTransaction.begin();

                final List<WebConfigToRoleTypeMapping> webConfigToRoleTypeMappingList =
                        (List<WebConfigToRoleTypeMapping>) dataSet.get(WEB_CONFIG_TO_ROLE_TYPE_MAPPING_KEY + LIST_SUFFIX);
                if (webConfigToRoleTypeMappingList != null) {
                    for (WebConfigToRoleTypeMapping webConfigToRoleTypeMapping : webConfigToRoleTypeMappingList) {
                        final Long id = webConfigToRoleTypeMapping.getId();

                        final Long roleTypeId = idMap.get(ROLE_TYPE_KEY + ":" + webConfigToRoleTypeMapping.getRoleTypeId());
                        final Long webConfigId = idMap.get(WEB_CRAWLING_CONFIG_KEY + ":" + webConfigToRoleTypeMapping.getWebConfigId());
                        if (roleTypeId == null || webConfigId == null) {
                            // skip
                            continue;
                        }

                        final WebConfigToRoleTypeMapping entity = webConfigToRoleTypeMappingBhv.selectEntity(cb -> {
                            cb.query().setRoleTypeId_Equal(roleTypeId);
                            cb.query().setWebConfigId_Equal(webConfigId);
                        }).orElse(null);//TODO
                        if (entity == null) {
                            webConfigToRoleTypeMapping = new WebConfigToRoleTypeMapping();
                            webConfigToRoleTypeMapping.setRoleTypeId(roleTypeId);
                            webConfigToRoleTypeMapping.setWebConfigId(webConfigId);
                            webConfigToRoleTypeMappingBhv.insert(webConfigToRoleTypeMapping);
                        }
                        idMap.put(WEB_CONFIG_TO_ROLE_TYPE_MAPPING_KEY + ":" + id.toString(), webConfigToRoleTypeMapping.getId());
                    }
                }
                userTransaction.commit();
            } catch (final Exception e) {
                rollback(WEB_CONFIG_TO_ROLE_TYPE_MAPPING_KEY, e);
            }
            // labelTypeToRoleTypeMapping
            try {
                userTransaction.begin();

                final List<LabelTypeToRoleTypeMapping> labelTypeToRoleTypeMappingList =
                        (List<LabelTypeToRoleTypeMapping>) dataSet.get(LABEL_TYPE_TO_ROLE_TYPE_MAPPING_KEY + LIST_SUFFIX);
                if (labelTypeToRoleTypeMappingList != null) {
                    for (LabelTypeToRoleTypeMapping labelTypeToRoleTypeMapping : labelTypeToRoleTypeMappingList) {
                        final Long id = labelTypeToRoleTypeMapping.getId();

                        final Long roleTypeId = idMap.get(ROLE_TYPE_KEY + ":" + labelTypeToRoleTypeMapping.getRoleTypeId());
                        final Long labelTypeId = idMap.get(LABEL_TYPE_KEY + ":" + labelTypeToRoleTypeMapping.getLabelTypeId());
                        if (roleTypeId == null || labelTypeId == null) {
                            // skip
                            continue;
                        }

                        final LabelTypeToRoleTypeMapping entity = labelTypeToRoleTypeMappingBhv.selectEntity(cb -> {
                            cb.query().setRoleTypeId_Equal(roleTypeId);
                            cb.query().setLabelTypeId_Equal(labelTypeId);
                        }).orElse(null);//TODO
                        if (entity == null) {
                            labelTypeToRoleTypeMapping = new LabelTypeToRoleTypeMapping();
                            labelTypeToRoleTypeMapping.setRoleTypeId(roleTypeId);
                            labelTypeToRoleTypeMapping.setLabelTypeId(labelTypeId);
                            labelTypeToRoleTypeMappingBhv.insert(labelTypeToRoleTypeMapping);
                        }
                        idMap.put(LABEL_TYPE_TO_ROLE_TYPE_MAPPING_KEY + ":" + id.toString(), labelTypeToRoleTypeMapping.getId());
                    }
                }
                userTransaction.commit();
            } catch (final Exception e) {
                rollback(LABEL_TYPE_TO_ROLE_TYPE_MAPPING_KEY, e);
            }
            // webAuthentication
            try {
                userTransaction.begin();

                final List<WebAuthentication> webAuthenticationList =
                        (List<WebAuthentication>) dataSet.get(WEB_AUTHENTICATION_KEY + LIST_SUFFIX);
                if (webAuthenticationList != null) {
                    for (WebAuthentication webAuthentication : webAuthenticationList) {
                        final Long id = webAuthentication.getId();

                        final Long webConfigId = idMap.get(WEB_CRAWLING_CONFIG_KEY + ":" + webAuthentication.getWebCrawlingConfigId());
                        if (webConfigId == null) {
                            // skip
                            continue;
                        }

                        WebAuthentication entity = null;
                        final List<WebAuthentication> list = webAuthenticationBhv.selectList(cb -> {
                            cb.query().setWebCrawlingConfigId_Equal(webConfigId);
                            cb.query().setDeletedBy_IsNull();
                        });
                        for (final WebAuthentication e : list) {
                            if (StringUtil.equals(webAuthentication.getAuthRealm(), e.getAuthRealm())
                                    && StringUtil.equals(webAuthentication.getHostname(), e.getHostname())
                                    && StringUtil.equals(webAuthentication.getProtocolScheme(), e.getProtocolScheme())
                                    && equalusNumber(webAuthentication.getPort(), e.getPort())) {
                                entity = e;
                            }
                        }
                        webAuthentication.setId(null);
                        webAuthentication.setWebCrawlingConfigId(webConfigId);
                        if (entity == null) {
                            webAuthenticationBhv.insert(webAuthentication);
                        } else {
                            if (overwrite) {
                                webAuthentication.setVersionNo(null);
                                Beans.copy(webAuthentication, entity).excludesNull().execute();
                                webAuthentication = entity;
                                webAuthenticationBhv.update(webAuthentication);
                            } else {
                                webAuthenticationBhv.insert(webAuthentication);
                            }
                        }
                        idMap.put(WEB_AUTHENTICATION_KEY + ":" + id.toString(), webAuthentication.getId());
                    }
                }
                userTransaction.commit();
            } catch (final Exception e) {
                rollback(WEB_AUTHENTICATION_KEY, e);
            }
            // fileAuthentication
            try {
                userTransaction.begin();

                final List<FileAuthentication> fileAuthenticationList =
                        (List<FileAuthentication>) dataSet.get(FILE_AUTHENTICATION_KEY + LIST_SUFFIX);
                if (fileAuthenticationList != null) {
                    for (FileAuthentication fileAuthentication : fileAuthenticationList) {
                        final Long id = fileAuthentication.getId();

                        final Long fileConfigId = idMap.get(FILE_CRAWLING_CONFIG_KEY + ":" + fileAuthentication.getFileCrawlingConfigId());
                        if (fileConfigId == null) {
                            // skip
                            continue;
                        }

                        FileAuthentication entity = null;
                        final List<FileAuthentication> list = fileAuthenticationBhv.selectList(cb -> {
                            cb.query().setFileCrawlingConfigId_Equal(fileConfigId);
                            cb.query().setDeletedBy_IsNull();
                        });
                        for (final FileAuthentication e : list) {
                            if (StringUtil.equals(fileAuthentication.getHostname(), e.getHostname())
                                    && StringUtil.equals(fileAuthentication.getProtocolScheme(), e.getProtocolScheme())
                                    && equalusNumber(fileAuthentication.getPort(), e.getPort())) {
                                entity = e;
                            }
                        }
                        fileAuthentication.setId(null);
                        fileAuthentication.setFileCrawlingConfigId(fileConfigId);
                        if (entity == null) {
                            fileAuthenticationBhv.insert(fileAuthentication);
                        } else {
                            if (overwrite) {
                                fileAuthentication.setVersionNo(null);
                                Beans.copy(fileAuthentication, entity).excludesNull().execute();
                                fileAuthentication = entity;
                                fileAuthenticationBhv.update(fileAuthentication);
                            } else {
                                fileAuthenticationBhv.insert(fileAuthentication);
                            }
                        }
                        idMap.put(FILE_AUTHENTICATION_KEY + ":" + id.toString(), fileAuthentication.getId());
                    }
                }
                userTransaction.commit();
            } catch (final Exception e) {
                rollback(FILE_AUTHENTICATION_KEY, e);
            }
            // requestHeader
            try {
                userTransaction.begin();

                final List<RequestHeader> requestHeaderList = (List<RequestHeader>) dataSet.get(REQUEST_HEADER_KEY + LIST_SUFFIX);
                if (requestHeaderList != null) {
                    for (RequestHeader requestHeader : requestHeaderList) {
                        final Long id = requestHeader.getId();

                        final Long webConfigId = idMap.get(WEB_CRAWLING_CONFIG_KEY + ":" + requestHeader.getWebCrawlingConfigId());
                        if (webConfigId == null) {
                            // skip
                            continue;
                        }

                        RequestHeader entity = null;
                        final List<RequestHeader> list = requestHeaderBhv.selectList(cb -> {
                            cb.query().setWebCrawlingConfigId_Equal(webConfigId);
                            cb.query().setDeletedBy_IsNull();
                        });
                        for (final RequestHeader e : list) {
                            if (StringUtil.equals(requestHeader.getName(), e.getName())) {
                                entity = e;
                            }
                        }
                        requestHeader.setId(null);
                        requestHeader.setWebCrawlingConfigId(webConfigId);
                        if (entity == null) {
                            requestHeaderBhv.insert(requestHeader);
                        } else {
                            if (overwrite) {
                                requestHeader.setVersionNo(null);
                                Beans.copy(requestHeader, entity).excludesNull().execute();
                                requestHeader = entity;
                                requestHeaderBhv.update(requestHeader);
                            } else {
                                requestHeaderBhv.insert(requestHeader);
                            }
                        }
                        idMap.put(REQUEST_HEADER_KEY + ":" + id.toString(), requestHeader.getId());
                    }
                }
                userTransaction.commit();
            } catch (final Exception e) {
                rollback(REQUEST_HEADER_KEY, e);
            }
            // keyMatch
            try {
                userTransaction.begin();

                final List<KeyMatch> keyMatchList = (List<KeyMatch>) dataSet.get(KEY_MATCH_KEY + LIST_SUFFIX);
                if (keyMatchList != null) {
                    for (KeyMatch keyMatch : keyMatchList) {
                        final Long id = keyMatch.getId();

                        final String term = keyMatch.getTerm();
                        final KeyMatch entity = keyMatchBhv.selectEntity(cb -> {
                            cb.query().setTerm_Equal(term);
                        }).orElse(null);//TODO
                        keyMatch.setId(null);
                        if (entity == null) {
                            keyMatchBhv.insert(keyMatch);
                        } else {
                            if (overwrite) {
                                keyMatch.setVersionNo(null);
                                Beans.copy(keyMatch, entity).excludesNull().execute();
                                keyMatch = entity;
                                keyMatchBhv.update(keyMatch);
                            } else {
                                keyMatchBhv.insert(keyMatch);
                            }
                        }
                        idMap.put(KEY_MATCH_KEY + ":" + id.toString(), keyMatch.getId());
                    }
                }
                userTransaction.commit();
            } catch (final Exception e) {
                rollback(KEY_MATCH_KEY, e);
            }
            // boostDocumentRule
            try {
                userTransaction.begin();

                final List<BoostDocumentRule> boostDocumentRuleList =
                        (List<BoostDocumentRule>) dataSet.get(BOOST_DOCUMENT_RULE_KEY + LIST_SUFFIX);
                if (boostDocumentRuleList != null) {
                    for (BoostDocumentRule boostDocumentRule : boostDocumentRuleList) {
                        final Long id = boostDocumentRule.getId();

                        final String urlExpr = boostDocumentRule.getUrlExpr();
                        final BoostDocumentRule entity = boostDocumentRuleBhv.selectEntity(cb -> {
                            cb.query().setUrlExpr_Equal(urlExpr);
                        }).orElse(null);//TODO
                        boostDocumentRule.setId(null);
                        if (entity == null) {
                            boostDocumentRuleBhv.insert(boostDocumentRule);
                        } else {
                            if (overwrite) {
                                boostDocumentRule.setVersionNo(null);
                                Beans.copy(boostDocumentRule, entity).excludesNull().execute();
                                boostDocumentRule = entity;
                                boostDocumentRuleBhv.update(boostDocumentRule);
                            } else {
                                boostDocumentRuleBhv.insert(boostDocumentRule);
                            }
                        }
                        idMap.put(BOOST_DOCUMENT_RULE_KEY + ":" + id.toString(), boostDocumentRule.getId());
                    }
                }
                userTransaction.commit();
            } catch (final Exception e) {
                rollback(BOOST_DOCUMENT_RULE_KEY, e);
            }
            // suggestElevateWord
            try {
                userTransaction.begin();

                final List<SuggestElevateWord> suggestElevateWordList =
                        (List<SuggestElevateWord>) dataSet.get(SUGGEST_ELEVATE_WORD_KEY + LIST_SUFFIX);
                if (suggestElevateWordList != null) {
                    for (SuggestElevateWord suggestElevateWord : suggestElevateWordList) {
                        final Long id = suggestElevateWord.getId();

                        final String suggestWord = suggestElevateWord.getSuggestWord();
                        final SuggestElevateWord entity = suggestElevateWordBhv.selectEntity(cb -> {
                            cb.query().setSuggestWord_Equal(suggestWord);
                        }).orElse(null);//TODO
                        suggestElevateWord.setId(null);
                        if (entity == null) {
                            suggestElevateWordBhv.insert(suggestElevateWord);
                        } else {
                            if (overwrite) {
                                suggestElevateWord.setVersionNo(null);
                                Beans.copy(suggestElevateWord, entity).excludesNull().execute();
                                suggestElevateWord = entity;
                                suggestElevateWordBhv.update(suggestElevateWord);
                            } else {
                                suggestElevateWordBhv.insert(suggestElevateWord);
                            }
                        }
                        idMap.put(SUGGEST_ELEVATE_WORD_KEY + ":" + id.toString(), suggestElevateWord.getId());
                    }
                }
                userTransaction.commit();
            } catch (final Exception e) {
                rollback(SUGGEST_ELEVATE_WORD_KEY, e);
            }
            // suggestNGWord
            try {
                userTransaction.begin();

                final List<SuggestBadWord> suggestNGWordList = (List<SuggestBadWord>) dataSet.get(SUGGEST_BAD_WORD_KEY + LIST_SUFFIX);
                if (suggestNGWordList != null) {
                    for (SuggestBadWord suggestBadWord : suggestNGWordList) {
                        final Long id = suggestBadWord.getId();

                        final String suggestWord = suggestBadWord.getSuggestWord();
                        final SuggestBadWord entity = suggestBadWordBhv.selectEntity(cb -> {
                            cb.query().setSuggestWord_Equal(suggestWord);
                        }).orElse(null);//TODO
                        suggestBadWord.setId(null);
                        if (entity == null) {
                            suggestBadWordBhv.insert(suggestBadWord);
                        } else {
                            if (overwrite) {
                                suggestBadWord.setVersionNo(null);
                                Beans.copy(suggestBadWord, entity).excludesNull().execute();
                                suggestBadWord = entity;
                                suggestBadWordBhv.update(suggestBadWord);
                            } else {
                                suggestBadWordBhv.insert(suggestBadWord);
                            }
                        }
                        idMap.put(SUGGEST_BAD_WORD_KEY + ":" + id.toString(), suggestBadWord.getId());
                    }
                }
                userTransaction.commit();
            } catch (final Exception e) {
                rollback(SUGGEST_BAD_WORD_KEY, e);
            }

            // crawlerProperties
            try {
                final Map<String, String> crawlerPropertyMap = (Map<String, String>) dataSet.get(CRAWLER_PROPERTIES_KEY);
                for (final Map.Entry<String, String> entry : crawlerPropertyMap.entrySet()) {
                    final String value = entry.getValue();
                    if (StringUtil.isNotBlank(value)) {
                        crawlerProperties.setProperty(entry.getKey(), value);
                    }
                }
                crawlerProperties.store();
            } catch (final Exception e) {
                logger.warn("Failed to restore properties: " + CRAWLER_PROPERTIES_KEY, e);
            }

        }

        private boolean equalusNumber(final Integer port1, final Integer port2) {
            if (port1 == null) {
                return port2 == null;
            } else if (port2 == null) {
                return false;
            }
            return port1.intValue() == port2.intValue();
        }

        private void rollback(final String key, final Exception e) {
            logger.warn("Failed to restore data: " + key, e);
            try {
                userTransaction.rollback();
            } catch (final Exception e1) {
                logger.warn("Failed to rollback data.", e1);
            }
        }
    }
}
