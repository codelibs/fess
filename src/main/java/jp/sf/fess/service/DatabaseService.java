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

package jp.sf.fess.service;

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

import jp.sf.fess.Constants;
import jp.sf.fess.FessSystemException;
import jp.sf.fess.db.cbean.BrowserTypeCB;
import jp.sf.fess.db.cbean.CrawlingSessionCB;
import jp.sf.fess.db.cbean.CrawlingSessionInfoCB;
import jp.sf.fess.db.cbean.DataConfigToBrowserTypeMappingCB;
import jp.sf.fess.db.cbean.DataConfigToLabelTypeMappingCB;
import jp.sf.fess.db.cbean.DataConfigToRoleTypeMappingCB;
import jp.sf.fess.db.cbean.DataCrawlingConfigCB;
import jp.sf.fess.db.cbean.FileAuthenticationCB;
import jp.sf.fess.db.cbean.FileConfigToBrowserTypeMappingCB;
import jp.sf.fess.db.cbean.FileConfigToLabelTypeMappingCB;
import jp.sf.fess.db.cbean.FileConfigToRoleTypeMappingCB;
import jp.sf.fess.db.cbean.FileCrawlingConfigCB;
import jp.sf.fess.db.cbean.LabelTypeCB;
import jp.sf.fess.db.cbean.LabelTypeToRoleTypeMappingCB;
import jp.sf.fess.db.cbean.OverlappingHostCB;
import jp.sf.fess.db.cbean.PathMappingCB;
import jp.sf.fess.db.cbean.RequestHeaderCB;
import jp.sf.fess.db.cbean.RoleTypeCB;
import jp.sf.fess.db.cbean.ScheduledJobCB;
import jp.sf.fess.db.cbean.WebAuthenticationCB;
import jp.sf.fess.db.cbean.WebConfigToBrowserTypeMappingCB;
import jp.sf.fess.db.cbean.WebConfigToLabelTypeMappingCB;
import jp.sf.fess.db.cbean.WebConfigToRoleTypeMappingCB;
import jp.sf.fess.db.cbean.WebCrawlingConfigCB;
import jp.sf.fess.db.exbhv.BrowserTypeBhv;
import jp.sf.fess.db.exbhv.CrawlingSessionBhv;
import jp.sf.fess.db.exbhv.CrawlingSessionInfoBhv;
import jp.sf.fess.db.exbhv.DataConfigToBrowserTypeMappingBhv;
import jp.sf.fess.db.exbhv.DataConfigToLabelTypeMappingBhv;
import jp.sf.fess.db.exbhv.DataConfigToRoleTypeMappingBhv;
import jp.sf.fess.db.exbhv.DataCrawlingConfigBhv;
import jp.sf.fess.db.exbhv.FileAuthenticationBhv;
import jp.sf.fess.db.exbhv.FileConfigToBrowserTypeMappingBhv;
import jp.sf.fess.db.exbhv.FileConfigToLabelTypeMappingBhv;
import jp.sf.fess.db.exbhv.FileConfigToRoleTypeMappingBhv;
import jp.sf.fess.db.exbhv.FileCrawlingConfigBhv;
import jp.sf.fess.db.exbhv.LabelTypeBhv;
import jp.sf.fess.db.exbhv.LabelTypeToRoleTypeMappingBhv;
import jp.sf.fess.db.exbhv.OverlappingHostBhv;
import jp.sf.fess.db.exbhv.PathMappingBhv;
import jp.sf.fess.db.exbhv.RequestHeaderBhv;
import jp.sf.fess.db.exbhv.RoleTypeBhv;
import jp.sf.fess.db.exbhv.ScheduledJobBhv;
import jp.sf.fess.db.exbhv.WebAuthenticationBhv;
import jp.sf.fess.db.exbhv.WebConfigToBrowserTypeMappingBhv;
import jp.sf.fess.db.exbhv.WebConfigToLabelTypeMappingBhv;
import jp.sf.fess.db.exbhv.WebConfigToRoleTypeMappingBhv;
import jp.sf.fess.db.exbhv.WebCrawlingConfigBhv;
import jp.sf.fess.db.exentity.BrowserType;
import jp.sf.fess.db.exentity.CrawlingSession;
import jp.sf.fess.db.exentity.CrawlingSessionInfo;
import jp.sf.fess.db.exentity.DataConfigToBrowserTypeMapping;
import jp.sf.fess.db.exentity.DataConfigToLabelTypeMapping;
import jp.sf.fess.db.exentity.DataConfigToRoleTypeMapping;
import jp.sf.fess.db.exentity.DataCrawlingConfig;
import jp.sf.fess.db.exentity.FileAuthentication;
import jp.sf.fess.db.exentity.FileConfigToBrowserTypeMapping;
import jp.sf.fess.db.exentity.FileConfigToLabelTypeMapping;
import jp.sf.fess.db.exentity.FileConfigToRoleTypeMapping;
import jp.sf.fess.db.exentity.FileCrawlingConfig;
import jp.sf.fess.db.exentity.LabelType;
import jp.sf.fess.db.exentity.LabelTypeToRoleTypeMapping;
import jp.sf.fess.db.exentity.OverlappingHost;
import jp.sf.fess.db.exentity.PathMapping;
import jp.sf.fess.db.exentity.RequestHeader;
import jp.sf.fess.db.exentity.RoleType;
import jp.sf.fess.db.exentity.ScheduledJob;
import jp.sf.fess.db.exentity.WebAuthentication;
import jp.sf.fess.db.exentity.WebConfigToBrowserTypeMapping;
import jp.sf.fess.db.exentity.WebConfigToLabelTypeMapping;
import jp.sf.fess.db.exentity.WebConfigToRoleTypeMapping;
import jp.sf.fess.db.exentity.WebCrawlingConfig;
import jp.sf.fess.helper.LabelTypeHelper;

import org.codelibs.core.util.DynamicProperties;
import org.seasar.dbflute.bhv.DeleteOption;
import org.seasar.framework.beans.util.Beans;
import org.seasar.framework.container.SingletonS2Container;
import org.seasar.framework.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseService {

    private static final String LIST_SUFFIX = "List";

    private static final Logger logger = LoggerFactory
            .getLogger(DatabaseService.class);

    private static final String VERSION_KEY = "version";

    private static final String CRAWLER_PROPERTIES_KEY = "crawlerProperties";

    private static final String SCHEDULED_JOB_KEY = "scheduledJob";

    private static final String BROWSER_TYPE_KEY = "browserType";

    private static final String LABEL_TYPE_KEY = "labelType";

    private static final String ROLE_TYPE_KEY = "roleType";

    private static final String CRAWLING_SESSION_KEY = "crawlingSession";

    private static final String CRAWLING_SESSION_INFO_KEY = "crawlingSessionInfo";

    private static final String FILE_CRAWLING_CONFIG_KEY = "fileCrawlingConfig";

    private static final String DATA_CRAWLING_CONFIG_KEY = "dataCrawlingConfig";

    private static final String PATH_MAPPING_KEY = "pathMapping";

    private static final String WEB_CRAWLING_CONFIG_KEY = "webCrawlingConfig";

    private static final String FILE_CONFIG_TO_BROWSER_TYPE_MAPPING_KEY = "fileConfigToBrowserTypeMapping";

    private static final String DATA_CONFIG_TO_BROWSER_TYPE_MAPPING_KEY = "dataConfigToBrowserTypeMapping";

    private static final String WEB_CONFIG_TO_BROWSER_TYPE_MAPPING_KEY = "webConfigToBrowserTypeMapping";

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

    private static final String OVERLAPPING_HOST_KEY = "overlappingHost";

    @Resource
    protected ScheduledJobBhv scheduledJobBhv;

    @Resource
    protected BrowserTypeBhv browserTypeBhv;

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
    protected FileConfigToBrowserTypeMappingBhv fileConfigToBrowserTypeMappingBhv;

    @Resource
    protected DataConfigToBrowserTypeMappingBhv dataConfigToBrowserTypeMappingBhv;

    @Resource
    protected WebConfigToBrowserTypeMappingBhv webConfigToBrowserTypeMappingBhv;

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
        final ScheduledJobCB scheduledJobCB = new ScheduledJobCB();
        scheduledJobCB.query().setDeletedBy_IsNull();
        dataSet.put(SCHEDULED_JOB_KEY + LIST_SUFFIX,
                scheduledJobBhv.selectList(scheduledJobCB));
        // browserType
        final BrowserTypeCB browserTypeCB = new BrowserTypeCB();
        browserTypeCB.query().setDeletedBy_IsNull();
        dataSet.put(BROWSER_TYPE_KEY + LIST_SUFFIX,
                browserTypeBhv.selectList(browserTypeCB));
        // labelType
        final LabelTypeCB labelTypeCB = new LabelTypeCB();
        labelTypeCB.query().setDeletedBy_IsNull();
        dataSet.put(LABEL_TYPE_KEY + LIST_SUFFIX,
                labelTypeBhv.selectList(labelTypeCB));
        // roleType
        final RoleTypeCB roleTypeCB = new RoleTypeCB();
        roleTypeCB.query().setDeletedBy_IsNull();
        dataSet.put(ROLE_TYPE_KEY + LIST_SUFFIX,
                roleTypeBhv.selectList(roleTypeCB));
        // fileCrawlingConfig
        final FileCrawlingConfigCB fileCrawlingConfigCB = new FileCrawlingConfigCB();
        fileCrawlingConfigCB.query().setDeletedBy_IsNull();
        dataSet.put(FILE_CRAWLING_CONFIG_KEY + LIST_SUFFIX,
                fileCrawlingConfigBhv.selectList(fileCrawlingConfigCB));
        // dataCrawlingConfig
        final DataCrawlingConfigCB dataCrawlingConfigCB = new DataCrawlingConfigCB();
        dataCrawlingConfigCB.query().setDeletedBy_IsNull();
        dataSet.put(DATA_CRAWLING_CONFIG_KEY + LIST_SUFFIX,
                dataCrawlingConfigBhv.selectList(dataCrawlingConfigCB));
        // pathMapping
        final PathMappingCB pathMappingCB = new PathMappingCB();
        pathMappingCB.query().setDeletedBy_IsNull();
        dataSet.put(PATH_MAPPING_KEY + LIST_SUFFIX,
                pathMappingBhv.selectList(pathMappingCB));
        // overlappingHost
        final OverlappingHostCB overlappingHostCB = new OverlappingHostCB();
        overlappingHostCB.query().setDeletedBy_IsNull();
        dataSet.put(OVERLAPPING_HOST_KEY + LIST_SUFFIX,
                overlappingHostBhv.selectList(overlappingHostCB));
        // webCrawlingConfig
        final WebCrawlingConfigCB webCrawlingConfigCB = new WebCrawlingConfigCB();
        webCrawlingConfigCB.query().setDeletedBy_IsNull();
        dataSet.put(WEB_CRAWLING_CONFIG_KEY + LIST_SUFFIX,
                webCrawlingConfigBhv.selectList(webCrawlingConfigCB));
        // fileConfigToBrowserTypeMapping
        dataSet.put(FILE_CONFIG_TO_BROWSER_TYPE_MAPPING_KEY + LIST_SUFFIX,
                fileConfigToBrowserTypeMappingBhv
                        .selectList(new FileConfigToBrowserTypeMappingCB()));
        // dataConfigToBrowserTypeMapping
        dataSet.put(DATA_CONFIG_TO_BROWSER_TYPE_MAPPING_KEY + LIST_SUFFIX,
                dataConfigToBrowserTypeMappingBhv
                        .selectList(new DataConfigToBrowserTypeMappingCB()));
        // webConfigToBrowserTypeMapping
        dataSet.put(WEB_CONFIG_TO_BROWSER_TYPE_MAPPING_KEY + LIST_SUFFIX,
                webConfigToBrowserTypeMappingBhv
                        .selectList(new WebConfigToBrowserTypeMappingCB()));
        // fileConfigToLabelTypeMapping
        dataSet.put(FILE_CONFIG_TO_LABEL_TYPE_MAPPING_KEY + LIST_SUFFIX,
                fileConfigToLabelTypeMappingBhv
                        .selectList(new FileConfigToLabelTypeMappingCB()));
        // dataConfigToLabelTypeMapping
        dataSet.put(DATA_CONFIG_TO_LABEL_TYPE_MAPPING_KEY + LIST_SUFFIX,
                dataConfigToLabelTypeMappingBhv
                        .selectList(new DataConfigToLabelTypeMappingCB()));
        // webConfigToLabelTypeMapping
        dataSet.put(WEB_CONFIG_TO_LABEL_TYPE_MAPPING_KEY + LIST_SUFFIX,
                webConfigToLabelTypeMappingBhv
                        .selectList(new WebConfigToLabelTypeMappingCB()));
        // fileConfigToRoleTypeMapping
        dataSet.put(FILE_CONFIG_TO_ROLE_TYPE_MAPPING_KEY + LIST_SUFFIX,
                fileConfigToRoleTypeMappingBhv
                        .selectList(new FileConfigToRoleTypeMappingCB()));
        // dataConfigToRoleTypeMapping
        dataSet.put(DATA_CONFIG_TO_ROLE_TYPE_MAPPING_KEY + LIST_SUFFIX,
                dataConfigToRoleTypeMappingBhv
                        .selectList(new DataConfigToRoleTypeMappingCB()));
        // webConfigToRoleTypeMapping
        dataSet.put(WEB_CONFIG_TO_ROLE_TYPE_MAPPING_KEY + LIST_SUFFIX,
                webConfigToRoleTypeMappingBhv
                        .selectList(new WebConfigToRoleTypeMappingCB()));
        // webConfigToRoleTypeMapping
        dataSet.put(LABEL_TYPE_TO_ROLE_TYPE_MAPPING_KEY + LIST_SUFFIX,
                labelTypeToRoleTypeMappingBhv
                        .selectList(new LabelTypeToRoleTypeMappingCB()));
        // webAuthentication
        dataSet.put(WEB_AUTHENTICATION_KEY + LIST_SUFFIX,
                webAuthenticationBhv.selectList(new WebAuthenticationCB()));
        // fileAuthentication
        dataSet.put(FILE_AUTHENTICATION_KEY + LIST_SUFFIX,
                fileAuthenticationBhv.selectList(new FileAuthenticationCB()));
        // requestHeader
        dataSet.put(REQUEST_HEADER_KEY + LIST_SUFFIX,
                requestHeaderBhv.selectList(new RequestHeaderCB()));

        // crawlerProperties
        final Map<String, String> crawlerPropertyMap = new HashMap<String, String>();
        for (final Map.Entry<Object, Object> entry : crawlerProperties
                .entrySet()) {
            try {
                crawlerPropertyMap.put(entry.getKey().toString(), entry
                        .getValue().toString());
            } catch (final Exception e) {
                logger.warn("Invalid data. key: " + entry.getKey()
                        + ", value: " + entry.getValue(), e);
            }
        }
        dataSet.put(CRAWLER_PROPERTIES_KEY, crawlerPropertyMap);

        try {
            final XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(
                    out));
            final PersistenceDelegate pd = encoder
                    .getPersistenceDelegate(Date.class);
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
            final XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(
                    in));
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

        protected DataImporter(final Map<String, Object> dataSet,
                final boolean overwrite) {
            this.dataSet = dataSet;
            this.overwrite = overwrite;
        }

        @Override
        public void run() {
            final Map<String, Long> idMap = new HashMap<String, Long>();

            // scheduledJob
            try {
                userTransaction.begin();

                final List<ScheduledJob> scheduledJobList = (List<ScheduledJob>) dataSet
                        .get(SCHEDULED_JOB_KEY + LIST_SUFFIX);
                if (scheduledJobList != null) {
                    for (ScheduledJob scheduledJob : scheduledJobList) {
                        final Long id = scheduledJob.getId();

                        final ScheduledJobCB cb = new ScheduledJobCB();
                        cb.query().setName_Equal(scheduledJob.getName());
                        cb.query().setDeletedBy_IsNull();
                        final ScheduledJob entity = scheduledJobBhv
                                .selectEntity(cb);
                        scheduledJob.setId(null);
                        if (entity == null) {
                            scheduledJobBhv.insert(scheduledJob);
                        } else {
                            if (overwrite) {
                                scheduledJob.setVersionNo(null);
                                Beans.copy(scheduledJob, entity).excludesNull()
                                        .execute();
                                scheduledJob = entity;
                                scheduledJobBhv.update(scheduledJob);
                            } else {
                                scheduledJobBhv.insert(scheduledJob);
                            }
                        }
                        idMap.put(SCHEDULED_JOB_KEY + ":" + id.toString(),
                                scheduledJob.getId());
                    }
                }
                userTransaction.commit();
            } catch (final Exception e) {
                rollback(BROWSER_TYPE_KEY, e);
            }
            // browserType
            try {
                userTransaction.begin();

                final List<BrowserType> browserTypeList = (List<BrowserType>) dataSet
                        .get(BROWSER_TYPE_KEY + LIST_SUFFIX);
                if (browserTypeList != null) {
                    for (BrowserType browserType : browserTypeList) {
                        final Long id = browserType.getId();

                        final BrowserTypeCB cb = new BrowserTypeCB();
                        cb.query().setValue_Equal(browserType.getValue());
                        cb.query().setDeletedBy_IsNull();
                        final BrowserType entity = browserTypeBhv
                                .selectEntity(cb);
                        browserType.setId(null);
                        if (entity == null) {
                            browserTypeBhv.insert(browserType);
                        } else {
                            // always overwrite
                            // if (overwrite) {
                            browserType.setVersionNo(null);
                            Beans.copy(browserType, entity).excludesNull()
                                    .execute();
                            browserType = entity;
                            browserTypeBhv.update(browserType);
                            // } else {
                            // browserTypeBhv.insert(browserType);
                            // }
                        }
                        idMap.put(BROWSER_TYPE_KEY + ":" + id.toString(),
                                browserType.getId());
                    }
                }
                userTransaction.commit();
            } catch (final Exception e) {
                rollback(BROWSER_TYPE_KEY, e);
            }
            // labelType
            try {
                userTransaction.begin();

                final List<LabelType> labelTypeList = (List<LabelType>) dataSet
                        .get(LABEL_TYPE_KEY + LIST_SUFFIX);
                if (labelTypeList != null) {
                    for (LabelType labelType : labelTypeList) {
                        final Long id = labelType.getId();

                        final LabelTypeCB cb = new LabelTypeCB();
                        cb.query().setValue_Equal(labelType.getValue());
                        cb.query().setDeletedBy_IsNull();
                        final LabelType entity = labelTypeBhv.selectEntity(cb);
                        labelType.setId(null);
                        if (entity == null) {
                            labelTypeBhv.insert(labelType);
                        } else {
                            // always overwrite
                            // if (overwrite) {
                            labelType.setVersionNo(null);
                            Beans.copy(labelType, entity).excludesNull()
                                    .execute();
                            labelType = entity;
                            labelTypeBhv.update(labelType);
                            // } else {
                            // labelTypeBhv.insert(labelType);
                            // }
                        }
                        idMap.put(LABEL_TYPE_KEY + ":" + id.toString(),
                                labelType.getId());
                    }
                }
                // restore labels
                final LabelTypeHelper labelTypeHelper = SingletonS2Container
                        .getComponent("labelTypeHelper");
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

                final List<RoleType> roleTypeList = (List<RoleType>) dataSet
                        .get(ROLE_TYPE_KEY + LIST_SUFFIX);
                if (roleTypeList != null) {
                    for (RoleType roleType : roleTypeList) {
                        final Long id = roleType.getId();

                        final RoleTypeCB cb = new RoleTypeCB();
                        cb.query().setValue_Equal(roleType.getValue());
                        cb.query().setDeletedBy_IsNull();
                        final RoleType entity = roleTypeBhv.selectEntity(cb);
                        roleType.setId(null);
                        if (entity == null) {
                            roleTypeBhv.insert(roleType);
                        } else {
                            // always overwrite
                            // if (overwrite) {
                            roleType.setVersionNo(null);
                            Beans.copy(roleType, entity).excludesNull()
                                    .execute();
                            roleType = entity;
                            roleTypeBhv.update(roleType);
                            // } else {
                            // roleTypeBhv.insert(roleType);
                            // }
                        }
                        idMap.put(ROLE_TYPE_KEY + ":" + id.toString(),
                                roleType.getId());
                    }
                }
                userTransaction.commit();
            } catch (final Exception e) {
                rollback(ROLE_TYPE_KEY, e);
            }
            // crawlingSession
            try {
                userTransaction.begin();

                final List<CrawlingSession> crawlingSessionList = (List<CrawlingSession>) dataSet
                        .get(CRAWLING_SESSION_KEY + LIST_SUFFIX);
                if (crawlingSessionList != null) {
                    for (CrawlingSession crawlingSession : crawlingSessionList) {
                        final Long id = crawlingSession.getId();

                        final CrawlingSessionCB cb = new CrawlingSessionCB();
                        cb.query().setSessionId_Equal(
                                crawlingSession.getSessionId());
                        final CrawlingSession entity = crawlingSessionBhv
                                .selectEntity(cb);
                        crawlingSession.setId(null);
                        if (entity == null) {
                            crawlingSessionBhv.insert(crawlingSession);
                        } else {
                            Beans.copy(crawlingSession, entity).excludesNull()
                                    .execute();
                            crawlingSession = entity;
                            crawlingSessionBhv.update(crawlingSession);
                            // delete info
                            final CrawlingSessionInfoCB cb2 = new CrawlingSessionInfoCB();
                            cb2.query().setCrawlingSessionId_Equal(
                                    crawlingSession.getId());
                            crawlingSessionInfoBhv.varyingQueryDelete(cb2,
                                    new DeleteOption<CrawlingSessionInfoCB>()
                                            .allowNonQueryDelete());
                        }
                        idMap.put(CRAWLING_SESSION_KEY + ":" + id.toString(),
                                crawlingSession.getId());
                    }
                }
                userTransaction.commit();
            } catch (final Exception e) {
                rollback(CRAWLING_SESSION_KEY, e);
            }
            // crawlingSessionInfo
            try {
                userTransaction.begin();

                final List<CrawlingSessionInfo> crawlingSessionInfoList = (List<CrawlingSessionInfo>) dataSet
                        .get(CRAWLING_SESSION_INFO_KEY + LIST_SUFFIX);
                if (crawlingSessionInfoList != null) {
                    for (final CrawlingSessionInfo crawlingSessionInfo : crawlingSessionInfoList) {
                        final Long id = crawlingSessionInfo.getId();
                        // relations
                        crawlingSessionInfo.setCrawlingSessionId(idMap
                                .get(CRAWLING_SESSION_KEY
                                        + ":"
                                        + crawlingSessionInfo
                                                .getCrawlingSessionId()));
                        crawlingSessionInfo.setId(null);
                        crawlingSessionInfoBhv.insert(crawlingSessionInfo);
                        idMap.put(
                                CRAWLING_SESSION_INFO_KEY + ":" + id.toString(),
                                crawlingSessionInfo.getId());
                    }
                }
                userTransaction.commit();
            } catch (final Exception e) {
                rollback(CRAWLING_SESSION_INFO_KEY, e);
            }
            // fileCrawlingConfig
            try {
                userTransaction.begin();

                final List<FileCrawlingConfig> fileCrawlingConfigList = (List<FileCrawlingConfig>) dataSet
                        .get(FILE_CRAWLING_CONFIG_KEY + LIST_SUFFIX);
                if (fileCrawlingConfigList != null) {
                    for (FileCrawlingConfig fileCrawlingConfig : fileCrawlingConfigList) {
                        final Long id = fileCrawlingConfig.getId();

                        final FileCrawlingConfigCB cb = new FileCrawlingConfigCB();
                        cb.query().setName_Equal(fileCrawlingConfig.getName());
                        cb.query().setDeletedBy_IsNull();
                        final FileCrawlingConfig entity = fileCrawlingConfigBhv
                                .selectEntity(cb);
                        fileCrawlingConfig.setId(null);
                        if (entity == null) {
                            fileCrawlingConfigBhv.insert(fileCrawlingConfig);
                        } else {
                            if (overwrite) {
                                fileCrawlingConfig.setVersionNo(null);
                                Beans.copy(fileCrawlingConfig, entity)
                                        .excludesNull().execute();
                                fileCrawlingConfig = entity;
                                fileCrawlingConfigBhv
                                        .update(fileCrawlingConfig);
                            } else {
                                fileCrawlingConfigBhv
                                        .insert(fileCrawlingConfig);
                            }
                        }
                        idMap.put(
                                FILE_CRAWLING_CONFIG_KEY + ":" + id.toString(),
                                fileCrawlingConfig.getId());
                    }
                }
                userTransaction.commit();
            } catch (final Exception e) {
                rollback(FILE_CRAWLING_CONFIG_KEY, e);
            }
            // dataCrawlingConfig
            try {
                userTransaction.begin();

                final List<DataCrawlingConfig> dataCrawlingConfigList = (List<DataCrawlingConfig>) dataSet
                        .get(DATA_CRAWLING_CONFIG_KEY + LIST_SUFFIX);
                if (dataCrawlingConfigList != null) {
                    for (DataCrawlingConfig dataCrawlingConfig : dataCrawlingConfigList) {
                        final Long id = dataCrawlingConfig.getId();

                        final DataCrawlingConfigCB cb = new DataCrawlingConfigCB();
                        cb.query().setName_Equal(dataCrawlingConfig.getName());
                        cb.query().setDeletedBy_IsNull();
                        final DataCrawlingConfig entity = dataCrawlingConfigBhv
                                .selectEntity(cb);
                        dataCrawlingConfig.setId(null);
                        if (entity == null) {
                            dataCrawlingConfigBhv.insert(dataCrawlingConfig);
                        } else {
                            if (overwrite) {
                                dataCrawlingConfig.setVersionNo(null);
                                Beans.copy(dataCrawlingConfig, entity)
                                        .excludesNull().execute();
                                dataCrawlingConfig = entity;
                                dataCrawlingConfigBhv
                                        .update(dataCrawlingConfig);
                            } else {
                                dataCrawlingConfigBhv
                                        .insert(dataCrawlingConfig);
                            }
                        }
                        idMap.put(
                                DATA_CRAWLING_CONFIG_KEY + ":" + id.toString(),
                                dataCrawlingConfig.getId());
                    }
                }
                userTransaction.commit();
            } catch (final Exception e) {
                rollback(DATA_CRAWLING_CONFIG_KEY, e);
            }
            // pathMapping
            try {
                userTransaction.begin();

                final List<PathMapping> pathMappingList = (List<PathMapping>) dataSet
                        .get(PATH_MAPPING_KEY + LIST_SUFFIX);
                if (pathMappingList != null) {
                    for (PathMapping pathMapping : pathMappingList) {
                        final Long id = pathMapping.getId();

                        final PathMappingCB cb = new PathMappingCB();
                        cb.query().setRegex_Equal(pathMapping.getRegex());
                        cb.query().setDeletedBy_IsNull();
                        final PathMapping entity = pathMappingBhv
                                .selectEntity(cb);
                        pathMapping.setId(null);
                        if (pathMapping.getProcessType() == null) {
                            pathMapping.setProcessType_Crawling();
                        }
                        if (entity == null) {
                            pathMappingBhv.insert(pathMapping);
                        } else {
                            if (overwrite) {
                                pathMapping.setVersionNo(null);
                                Beans.copy(pathMapping, entity).excludesNull()
                                        .execute();
                                pathMapping = entity;
                                pathMappingBhv.update(pathMapping);
                            } else {
                                pathMappingBhv.insert(pathMapping);
                            }
                        }
                        idMap.put(PATH_MAPPING_KEY + ":" + id.toString(),
                                pathMapping.getId());
                    }
                }
                userTransaction.commit();
            } catch (final Exception e) {
                rollback(PATH_MAPPING_KEY, e);
            }
            // overlappingHost
            try {
                userTransaction.begin();

                final List<OverlappingHost> overlappingHostList = (List<OverlappingHost>) dataSet
                        .get(OVERLAPPING_HOST_KEY + LIST_SUFFIX);
                if (overlappingHostList != null) {
                    for (OverlappingHost overlappingHost : overlappingHostList) {
                        final Long id = overlappingHost.getId();

                        final OverlappingHostCB cb = new OverlappingHostCB();
                        cb.query().setRegularName_Equal(
                                overlappingHost.getRegularName());
                        cb.query().setOverlappingName_Equal(
                                overlappingHost.getOverlappingName());
                        cb.query().setDeletedBy_IsNull();
                        final OverlappingHost entity = overlappingHostBhv
                                .selectEntity(cb);
                        overlappingHost.setId(null);
                        if (entity == null) {
                            overlappingHostBhv.insert(overlappingHost);
                        } else {
                            if (overwrite) {
                                overlappingHost.setVersionNo(null);
                                Beans.copy(overlappingHost, entity)
                                        .excludesNull().execute();
                                overlappingHost = entity;
                                overlappingHostBhv.update(overlappingHost);
                            } else {
                                overlappingHostBhv.insert(overlappingHost);
                            }
                        }
                        idMap.put(OVERLAPPING_HOST_KEY + ":" + id.toString(),
                                overlappingHost.getId());
                    }
                }
                userTransaction.commit();
            } catch (final Exception e) {
                rollback(OVERLAPPING_HOST_KEY, e);
            }
            // webCrawlingConfig
            try {
                userTransaction.begin();

                final List<WebCrawlingConfig> webCrawlingConfigList = (List<WebCrawlingConfig>) dataSet
                        .get(WEB_CRAWLING_CONFIG_KEY + LIST_SUFFIX);
                if (webCrawlingConfigList != null) {
                    for (WebCrawlingConfig webCrawlingConfig : webCrawlingConfigList) {
                        final Long id = webCrawlingConfig.getId();

                        final WebCrawlingConfigCB cb = new WebCrawlingConfigCB();
                        cb.query().setName_Equal(webCrawlingConfig.getName());
                        cb.query().setDeletedBy_IsNull();
                        final WebCrawlingConfig entity = webCrawlingConfigBhv
                                .selectEntity(cb);
                        webCrawlingConfig.setId(null);
                        if (entity == null) {
                            webCrawlingConfigBhv.insert(webCrawlingConfig);
                        } else {
                            if (overwrite) {
                                webCrawlingConfig.setVersionNo(null);
                                Beans.copy(webCrawlingConfig, entity)
                                        .excludesNull().execute();
                                webCrawlingConfig = entity;
                                webCrawlingConfigBhv.update(webCrawlingConfig);
                            } else {
                                webCrawlingConfigBhv.insert(webCrawlingConfig);
                            }
                        }
                        idMap.put(
                                WEB_CRAWLING_CONFIG_KEY + ":" + id.toString(),
                                webCrawlingConfig.getId());
                    }
                }
                userTransaction.commit();
            } catch (final Exception e) {
                rollback(WEB_CRAWLING_CONFIG_KEY, e);
            }
            // fileConfigToBrowserTypeMapping
            try {
                userTransaction.begin();

                final List<FileConfigToBrowserTypeMapping> fileConfigToBrowserTypeMappingList = (List<FileConfigToBrowserTypeMapping>) dataSet
                        .get(FILE_CONFIG_TO_BROWSER_TYPE_MAPPING_KEY
                                + LIST_SUFFIX);
                if (fileConfigToBrowserTypeMappingList != null) {
                    for (FileConfigToBrowserTypeMapping fileConfigToBrowserTypeMapping : fileConfigToBrowserTypeMappingList) {
                        final Long id = fileConfigToBrowserTypeMapping.getId();

                        final Long browserTypeId = idMap.get(BROWSER_TYPE_KEY
                                + ":"
                                + fileConfigToBrowserTypeMapping
                                        .getBrowserTypeId());
                        final Long fileConfigId = idMap
                                .get(FILE_CRAWLING_CONFIG_KEY
                                        + ":"
                                        + fileConfigToBrowserTypeMapping
                                                .getFileConfigId());
                        if (browserTypeId == null || fileConfigId == null) {
                            // skip
                            continue;
                        }

                        final FileConfigToBrowserTypeMappingCB cb = new FileConfigToBrowserTypeMappingCB();
                        cb.query().setBrowserTypeId_Equal(browserTypeId);
                        cb.query().setFileConfigId_Equal(fileConfigId);
                        final FileConfigToBrowserTypeMapping entity = fileConfigToBrowserTypeMappingBhv
                                .selectEntity(cb);
                        if (entity == null) {
                            fileConfigToBrowserTypeMapping = new FileConfigToBrowserTypeMapping();
                            fileConfigToBrowserTypeMapping
                                    .setBrowserTypeId(browserTypeId);
                            fileConfigToBrowserTypeMapping
                                    .setFileConfigId(fileConfigId);
                            fileConfigToBrowserTypeMappingBhv
                                    .insert(fileConfigToBrowserTypeMapping);
                        }
                        idMap.put(FILE_CONFIG_TO_BROWSER_TYPE_MAPPING_KEY + ":"
                                + id.toString(),
                                fileConfigToBrowserTypeMapping.getId());
                    }
                }
                userTransaction.commit();
            } catch (final Exception e) {
                rollback(FILE_CONFIG_TO_BROWSER_TYPE_MAPPING_KEY, e);
            }
            // dataConfigToBrowserTypeMapping
            try {
                userTransaction.begin();

                final List<DataConfigToBrowserTypeMapping> dataConfigToBrowserTypeMappingList = (List<DataConfigToBrowserTypeMapping>) dataSet
                        .get(DATA_CONFIG_TO_BROWSER_TYPE_MAPPING_KEY
                                + LIST_SUFFIX);
                if (dataConfigToBrowserTypeMappingList != null) {
                    for (DataConfigToBrowserTypeMapping dataConfigToBrowserTypeMapping : dataConfigToBrowserTypeMappingList) {
                        final Long id = dataConfigToBrowserTypeMapping.getId();

                        final Long browserTypeId = idMap.get(BROWSER_TYPE_KEY
                                + ":"
                                + dataConfigToBrowserTypeMapping
                                        .getBrowserTypeId());
                        final Long dataConfigId = idMap
                                .get(DATA_CRAWLING_CONFIG_KEY
                                        + ":"
                                        + dataConfigToBrowserTypeMapping
                                                .getDataConfigId());
                        if (browserTypeId == null || dataConfigId == null) {
                            // skip
                            continue;
                        }

                        final DataConfigToBrowserTypeMappingCB cb = new DataConfigToBrowserTypeMappingCB();
                        cb.query().setBrowserTypeId_Equal(browserTypeId);
                        cb.query().setDataConfigId_Equal(dataConfigId);
                        final DataConfigToBrowserTypeMapping entity = dataConfigToBrowserTypeMappingBhv
                                .selectEntity(cb);
                        if (entity == null) {
                            dataConfigToBrowserTypeMapping = new DataConfigToBrowserTypeMapping();
                            dataConfigToBrowserTypeMapping
                                    .setBrowserTypeId(browserTypeId);
                            dataConfigToBrowserTypeMapping
                                    .setDataConfigId(dataConfigId);
                            dataConfigToBrowserTypeMappingBhv
                                    .insert(dataConfigToBrowserTypeMapping);
                        }
                        idMap.put(DATA_CONFIG_TO_BROWSER_TYPE_MAPPING_KEY + ":"
                                + id.toString(),
                                dataConfigToBrowserTypeMapping.getId());
                    }
                }
                userTransaction.commit();
            } catch (final Exception e) {
                rollback(DATA_CONFIG_TO_BROWSER_TYPE_MAPPING_KEY, e);
            }
            // webConfigToBrowserTypeMapping
            try {
                userTransaction.begin();

                final List<WebConfigToBrowserTypeMapping> webConfigToBrowserTypeMappingList = (List<WebConfigToBrowserTypeMapping>) dataSet
                        .get(WEB_CONFIG_TO_BROWSER_TYPE_MAPPING_KEY
                                + LIST_SUFFIX);
                if (webConfigToBrowserTypeMappingList != null) {
                    for (WebConfigToBrowserTypeMapping webConfigToBrowserTypeMapping : webConfigToBrowserTypeMappingList) {
                        final Long id = webConfigToBrowserTypeMapping.getId();

                        final Long browserTypeId = idMap.get(BROWSER_TYPE_KEY
                                + ":"
                                + webConfigToBrowserTypeMapping
                                        .getBrowserTypeId());
                        final Long webConfigId = idMap
                                .get(WEB_CRAWLING_CONFIG_KEY
                                        + ":"
                                        + webConfigToBrowserTypeMapping
                                                .getWebConfigId());
                        if (browserTypeId == null || webConfigId == null) {
                            // skip
                            continue;
                        }

                        final WebConfigToBrowserTypeMappingCB cb = new WebConfigToBrowserTypeMappingCB();
                        cb.query().setBrowserTypeId_Equal(browserTypeId);
                        cb.query().setWebConfigId_Equal(webConfigId);
                        final WebConfigToBrowserTypeMapping entity = webConfigToBrowserTypeMappingBhv
                                .selectEntity(cb);
                        if (entity == null) {
                            webConfigToBrowserTypeMapping = new WebConfigToBrowserTypeMapping();
                            webConfigToBrowserTypeMapping
                                    .setBrowserTypeId(browserTypeId);
                            webConfigToBrowserTypeMapping
                                    .setWebConfigId(webConfigId);
                            webConfigToBrowserTypeMappingBhv
                                    .insert(webConfigToBrowserTypeMapping);
                        }
                        idMap.put(WEB_CONFIG_TO_BROWSER_TYPE_MAPPING_KEY + ":"
                                + id.toString(),
                                webConfigToBrowserTypeMapping.getId());
                    }
                }
                userTransaction.commit();
            } catch (final Exception e) {
                rollback(WEB_CONFIG_TO_BROWSER_TYPE_MAPPING_KEY, e);
            }
            // fileConfigToLabelTypeMapping
            try {
                userTransaction.begin();

                final List<FileConfigToLabelTypeMapping> fileConfigToLabelTypeMappingList = (List<FileConfigToLabelTypeMapping>) dataSet
                        .get(FILE_CONFIG_TO_LABEL_TYPE_MAPPING_KEY
                                + LIST_SUFFIX);
                if (fileConfigToLabelTypeMappingList != null) {
                    for (FileConfigToLabelTypeMapping fileConfigToLabelTypeMapping : fileConfigToLabelTypeMappingList) {
                        final Long id = fileConfigToLabelTypeMapping.getId();

                        final Long labelTypeId = idMap
                                .get(LABEL_TYPE_KEY
                                        + ":"
                                        + fileConfigToLabelTypeMapping
                                                .getLabelTypeId());
                        final Long fileConfigId = idMap
                                .get(FILE_CRAWLING_CONFIG_KEY
                                        + ":"
                                        + fileConfigToLabelTypeMapping
                                                .getFileConfigId());
                        if (labelTypeId == null || fileConfigId == null) {
                            // skip
                            continue;
                        }

                        final FileConfigToLabelTypeMappingCB cb = new FileConfigToLabelTypeMappingCB();
                        cb.query().setLabelTypeId_Equal(labelTypeId);
                        cb.query().setFileConfigId_Equal(fileConfigId);
                        final FileConfigToLabelTypeMapping entity = fileConfigToLabelTypeMappingBhv
                                .selectEntity(cb);
                        if (entity == null) {
                            fileConfigToLabelTypeMapping = new FileConfigToLabelTypeMapping();
                            fileConfigToLabelTypeMapping
                                    .setLabelTypeId(labelTypeId);
                            fileConfigToLabelTypeMapping
                                    .setFileConfigId(fileConfigId);
                            fileConfigToLabelTypeMappingBhv
                                    .insert(fileConfigToLabelTypeMapping);
                        }
                        idMap.put(FILE_CONFIG_TO_LABEL_TYPE_MAPPING_KEY + ":"
                                + id.toString(),
                                fileConfigToLabelTypeMapping.getId());
                    }
                }
                userTransaction.commit();
            } catch (final Exception e) {
                rollback(FILE_CONFIG_TO_LABEL_TYPE_MAPPING_KEY, e);
            }
            // dataConfigToLabelTypeMapping
            try {
                userTransaction.begin();

                final List<DataConfigToLabelTypeMapping> dataConfigToLabelTypeMappingList = (List<DataConfigToLabelTypeMapping>) dataSet
                        .get(DATA_CONFIG_TO_LABEL_TYPE_MAPPING_KEY
                                + LIST_SUFFIX);
                if (dataConfigToLabelTypeMappingList != null) {
                    for (DataConfigToLabelTypeMapping dataConfigToLabelTypeMapping : dataConfigToLabelTypeMappingList) {
                        final Long id = dataConfigToLabelTypeMapping.getId();

                        final Long labelTypeId = idMap
                                .get(LABEL_TYPE_KEY
                                        + ":"
                                        + dataConfigToLabelTypeMapping
                                                .getLabelTypeId());
                        final Long dataConfigId = idMap
                                .get(DATA_CRAWLING_CONFIG_KEY
                                        + ":"
                                        + dataConfigToLabelTypeMapping
                                                .getDataConfigId());
                        if (labelTypeId == null || dataConfigId == null) {
                            // skip
                            continue;
                        }

                        final DataConfigToLabelTypeMappingCB cb = new DataConfigToLabelTypeMappingCB();
                        cb.query().setLabelTypeId_Equal(labelTypeId);
                        cb.query().setDataConfigId_Equal(dataConfigId);
                        final DataConfigToLabelTypeMapping entity = dataConfigToLabelTypeMappingBhv
                                .selectEntity(cb);
                        if (entity == null) {
                            dataConfigToLabelTypeMapping = new DataConfigToLabelTypeMapping();
                            dataConfigToLabelTypeMapping
                                    .setLabelTypeId(labelTypeId);
                            dataConfigToLabelTypeMapping
                                    .setDataConfigId(dataConfigId);
                            dataConfigToLabelTypeMappingBhv
                                    .insert(dataConfigToLabelTypeMapping);
                        }
                        idMap.put(DATA_CONFIG_TO_LABEL_TYPE_MAPPING_KEY + ":"
                                + id.toString(),
                                dataConfigToLabelTypeMapping.getId());
                    }
                }
                userTransaction.commit();
            } catch (final Exception e) {
                rollback(DATA_CONFIG_TO_LABEL_TYPE_MAPPING_KEY, e);
            }
            // webConfigToLabelTypeMapping
            try {
                userTransaction.begin();

                final List<WebConfigToLabelTypeMapping> webConfigToLabelTypeMappingList = (List<WebConfigToLabelTypeMapping>) dataSet
                        .get(WEB_CONFIG_TO_LABEL_TYPE_MAPPING_KEY + LIST_SUFFIX);
                if (webConfigToLabelTypeMappingList != null) {
                    for (WebConfigToLabelTypeMapping webConfigToLabelTypeMapping : webConfigToLabelTypeMappingList) {
                        final Long id = webConfigToLabelTypeMapping.getId();

                        final Long labelTypeId = idMap.get(LABEL_TYPE_KEY + ":"
                                + webConfigToLabelTypeMapping.getLabelTypeId());
                        final Long webConfigId = idMap
                                .get(WEB_CRAWLING_CONFIG_KEY
                                        + ":"
                                        + webConfigToLabelTypeMapping
                                                .getWebConfigId());
                        if (labelTypeId == null || webConfigId == null) {
                            // skip
                            continue;
                        }

                        final WebConfigToLabelTypeMappingCB cb = new WebConfigToLabelTypeMappingCB();
                        cb.query().setLabelTypeId_Equal(labelTypeId);
                        cb.query().setWebConfigId_Equal(webConfigId);
                        final WebConfigToLabelTypeMapping entity = webConfigToLabelTypeMappingBhv
                                .selectEntity(cb);
                        if (entity == null) {
                            webConfigToLabelTypeMapping = new WebConfigToLabelTypeMapping();
                            webConfigToLabelTypeMapping
                                    .setLabelTypeId(labelTypeId);
                            webConfigToLabelTypeMapping
                                    .setWebConfigId(webConfigId);
                            webConfigToLabelTypeMappingBhv
                                    .insert(webConfigToLabelTypeMapping);
                        }
                        idMap.put(WEB_CONFIG_TO_LABEL_TYPE_MAPPING_KEY + ":"
                                + id.toString(),
                                webConfigToLabelTypeMapping.getId());
                    }
                }
                userTransaction.commit();
            } catch (final Exception e) {
                rollback(WEB_CONFIG_TO_LABEL_TYPE_MAPPING_KEY, e);
            }
            // fileConfigToRoleTypeMapping
            try {
                userTransaction.begin();

                final List<FileConfigToRoleTypeMapping> fileConfigToRoleTypeMappingList = (List<FileConfigToRoleTypeMapping>) dataSet
                        .get(FILE_CONFIG_TO_ROLE_TYPE_MAPPING_KEY + LIST_SUFFIX);
                if (fileConfigToRoleTypeMappingList != null) {
                    for (FileConfigToRoleTypeMapping fileConfigToRoleTypeMapping : fileConfigToRoleTypeMappingList) {
                        final Long id = fileConfigToRoleTypeMapping.getId();

                        final Long roleTypeId = idMap.get(ROLE_TYPE_KEY + ":"
                                + fileConfigToRoleTypeMapping.getRoleTypeId());
                        final Long fileConfigId = idMap
                                .get(FILE_CRAWLING_CONFIG_KEY
                                        + ":"
                                        + fileConfigToRoleTypeMapping
                                                .getFileConfigId());
                        if (roleTypeId == null || fileConfigId == null) {
                            // skip
                            continue;
                        }

                        final FileConfigToRoleTypeMappingCB cb = new FileConfigToRoleTypeMappingCB();
                        cb.query().setRoleTypeId_Equal(roleTypeId);
                        cb.query().setFileConfigId_Equal(fileConfigId);
                        final FileConfigToRoleTypeMapping entity = fileConfigToRoleTypeMappingBhv
                                .selectEntity(cb);
                        if (entity == null) {
                            fileConfigToRoleTypeMapping = new FileConfigToRoleTypeMapping();
                            fileConfigToRoleTypeMapping
                                    .setRoleTypeId(roleTypeId);
                            fileConfigToRoleTypeMapping
                                    .setFileConfigId(fileConfigId);
                            fileConfigToRoleTypeMappingBhv
                                    .insert(fileConfigToRoleTypeMapping);
                        }
                        idMap.put(FILE_CONFIG_TO_ROLE_TYPE_MAPPING_KEY + ":"
                                + id.toString(),
                                fileConfigToRoleTypeMapping.getId());
                    }
                }
                userTransaction.commit();
            } catch (final Exception e) {
                rollback(FILE_CONFIG_TO_ROLE_TYPE_MAPPING_KEY, e);
            }
            // dataConfigToRoleTypeMapping
            try {
                userTransaction.begin();

                final List<DataConfigToRoleTypeMapping> dataConfigToRoleTypeMappingList = (List<DataConfigToRoleTypeMapping>) dataSet
                        .get(DATA_CONFIG_TO_ROLE_TYPE_MAPPING_KEY + LIST_SUFFIX);
                if (dataConfigToRoleTypeMappingList != null) {
                    for (DataConfigToRoleTypeMapping dataConfigToRoleTypeMapping : dataConfigToRoleTypeMappingList) {
                        final Long id = dataConfigToRoleTypeMapping.getId();

                        final Long roleTypeId = idMap.get(ROLE_TYPE_KEY + ":"
                                + dataConfigToRoleTypeMapping.getRoleTypeId());
                        final Long dataConfigId = idMap
                                .get(DATA_CRAWLING_CONFIG_KEY
                                        + ":"
                                        + dataConfigToRoleTypeMapping
                                                .getDataConfigId());
                        if (roleTypeId == null || dataConfigId == null) {
                            // skip
                            continue;
                        }

                        final DataConfigToRoleTypeMappingCB cb = new DataConfigToRoleTypeMappingCB();
                        cb.query().setRoleTypeId_Equal(roleTypeId);
                        cb.query().setDataConfigId_Equal(dataConfigId);
                        final DataConfigToRoleTypeMapping entity = dataConfigToRoleTypeMappingBhv
                                .selectEntity(cb);
                        if (entity == null) {
                            dataConfigToRoleTypeMapping = new DataConfigToRoleTypeMapping();
                            dataConfigToRoleTypeMapping
                                    .setRoleTypeId(roleTypeId);
                            dataConfigToRoleTypeMapping
                                    .setDataConfigId(dataConfigId);
                            dataConfigToRoleTypeMappingBhv
                                    .insert(dataConfigToRoleTypeMapping);
                        }
                        idMap.put(DATA_CONFIG_TO_ROLE_TYPE_MAPPING_KEY + ":"
                                + id.toString(),
                                dataConfigToRoleTypeMapping.getId());
                    }
                }
                userTransaction.commit();
            } catch (final Exception e) {
                rollback(DATA_CONFIG_TO_ROLE_TYPE_MAPPING_KEY, e);
            }
            // webConfigToRoleTypeMapping
            try {
                userTransaction.begin();

                final List<WebConfigToRoleTypeMapping> webConfigToRoleTypeMappingList = (List<WebConfigToRoleTypeMapping>) dataSet
                        .get(WEB_CONFIG_TO_ROLE_TYPE_MAPPING_KEY + LIST_SUFFIX);
                if (webConfigToRoleTypeMappingList != null) {
                    for (WebConfigToRoleTypeMapping webConfigToRoleTypeMapping : webConfigToRoleTypeMappingList) {
                        final Long id = webConfigToRoleTypeMapping.getId();

                        final Long roleTypeId = idMap.get(ROLE_TYPE_KEY + ":"
                                + webConfigToRoleTypeMapping.getRoleTypeId());
                        final Long webConfigId = idMap
                                .get(WEB_CRAWLING_CONFIG_KEY
                                        + ":"
                                        + webConfigToRoleTypeMapping
                                                .getWebConfigId());
                        if (roleTypeId == null || webConfigId == null) {
                            // skip
                            continue;
                        }

                        final WebConfigToRoleTypeMappingCB cb = new WebConfigToRoleTypeMappingCB();
                        cb.query().setRoleTypeId_Equal(roleTypeId);
                        cb.query().setWebConfigId_Equal(webConfigId);
                        final WebConfigToRoleTypeMapping entity = webConfigToRoleTypeMappingBhv
                                .selectEntity(cb);
                        if (entity == null) {
                            webConfigToRoleTypeMapping = new WebConfigToRoleTypeMapping();
                            webConfigToRoleTypeMapping
                                    .setRoleTypeId(roleTypeId);
                            webConfigToRoleTypeMapping
                                    .setWebConfigId(webConfigId);
                            webConfigToRoleTypeMappingBhv
                                    .insert(webConfigToRoleTypeMapping);
                        }
                        idMap.put(WEB_CONFIG_TO_ROLE_TYPE_MAPPING_KEY + ":"
                                + id.toString(),
                                webConfigToRoleTypeMapping.getId());
                    }
                }
                userTransaction.commit();
            } catch (final Exception e) {
                rollback(WEB_CONFIG_TO_ROLE_TYPE_MAPPING_KEY, e);
            }
            // labelTypeToRoleTypeMapping
            try {
                userTransaction.begin();

                final List<LabelTypeToRoleTypeMapping> labelTypeToRoleTypeMappingList = (List<LabelTypeToRoleTypeMapping>) dataSet
                        .get(LABEL_TYPE_TO_ROLE_TYPE_MAPPING_KEY + LIST_SUFFIX);
                if (labelTypeToRoleTypeMappingList != null) {
                    for (LabelTypeToRoleTypeMapping labelTypeToRoleTypeMapping : labelTypeToRoleTypeMappingList) {
                        final Long id = labelTypeToRoleTypeMapping.getId();

                        final Long roleTypeId = idMap.get(ROLE_TYPE_KEY + ":"
                                + labelTypeToRoleTypeMapping.getRoleTypeId());
                        final Long labelTypeId = idMap.get(LABEL_TYPE_KEY + ":"
                                + labelTypeToRoleTypeMapping.getLabelTypeId());
                        if (roleTypeId == null || labelTypeId == null) {
                            // skip
                            continue;
                        }

                        final LabelTypeToRoleTypeMappingCB cb = new LabelTypeToRoleTypeMappingCB();
                        cb.query().setRoleTypeId_Equal(roleTypeId);
                        cb.query().setLabelTypeId_Equal(labelTypeId);
                        final LabelTypeToRoleTypeMapping entity = labelTypeToRoleTypeMappingBhv
                                .selectEntity(cb);
                        if (entity == null) {
                            labelTypeToRoleTypeMapping = new LabelTypeToRoleTypeMapping();
                            labelTypeToRoleTypeMapping
                                    .setRoleTypeId(roleTypeId);
                            labelTypeToRoleTypeMapping
                                    .setLabelTypeId(labelTypeId);
                            labelTypeToRoleTypeMappingBhv
                                    .insert(labelTypeToRoleTypeMapping);
                        }
                        idMap.put(LABEL_TYPE_TO_ROLE_TYPE_MAPPING_KEY + ":"
                                + id.toString(),
                                labelTypeToRoleTypeMapping.getId());
                    }
                }
                userTransaction.commit();
            } catch (final Exception e) {
                rollback(LABEL_TYPE_TO_ROLE_TYPE_MAPPING_KEY, e);
            }
            // webAuthentication
            try {
                userTransaction.begin();

                final List<WebAuthentication> webAuthenticationList = (List<WebAuthentication>) dataSet
                        .get(WEB_AUTHENTICATION_KEY + LIST_SUFFIX);
                if (webAuthenticationList != null) {
                    for (WebAuthentication webAuthentication : webAuthenticationList) {
                        final Long id = webAuthentication.getId();

                        final Long webConfigId = idMap
                                .get(WEB_CRAWLING_CONFIG_KEY
                                        + ":"
                                        + webAuthentication
                                                .getWebCrawlingConfigId());
                        if (webConfigId == null) {
                            // skip
                            continue;
                        }

                        final WebAuthenticationCB cb = new WebAuthenticationCB();
                        cb.query().setWebCrawlingConfigId_Equal(webConfigId);
                        final WebAuthentication entity = webAuthenticationBhv
                                .selectEntity(cb);
                        webAuthentication.setId(null);
                        webAuthentication.setWebCrawlingConfigId(webConfigId);
                        if (entity == null) {
                            webAuthenticationBhv.insert(webAuthentication);
                        } else {
                            if (overwrite) {
                                webAuthentication.setVersionNo(null);
                                Beans.copy(webAuthentication, entity)
                                        .excludesNull().execute();
                                webAuthentication = entity;
                                webAuthenticationBhv.update(webAuthentication);
                            } else {
                                webAuthenticationBhv.insert(webAuthentication);
                            }
                        }
                        idMap.put(WEB_AUTHENTICATION_KEY + ":" + id.toString(),
                                webAuthentication.getId());
                    }
                }
                userTransaction.commit();
            } catch (final Exception e) {
                rollback(WEB_AUTHENTICATION_KEY, e);
            }
            // fileAuthentication
            try {
                userTransaction.begin();

                final List<FileAuthentication> fileAuthenticationList = (List<FileAuthentication>) dataSet
                        .get(FILE_AUTHENTICATION_KEY + LIST_SUFFIX);
                if (fileAuthenticationList != null) {
                    for (FileAuthentication fileAuthentication : fileAuthenticationList) {
                        final Long id = fileAuthentication.getId();

                        final Long webConfigId = idMap
                                .get(FILE_CRAWLING_CONFIG_KEY
                                        + ":"
                                        + fileAuthentication
                                                .getFileCrawlingConfigId());
                        if (webConfigId == null) {
                            // skip
                            continue;
                        }

                        final FileAuthenticationCB cb = new FileAuthenticationCB();
                        cb.query().setFileCrawlingConfigId_Equal(webConfigId);
                        final FileAuthentication entity = fileAuthenticationBhv
                                .selectEntity(cb);
                        fileAuthentication.setId(null);
                        fileAuthentication.setFileCrawlingConfigId(webConfigId);
                        if (entity == null) {
                            fileAuthenticationBhv.insert(fileAuthentication);
                        } else {
                            if (overwrite) {
                                fileAuthentication.setVersionNo(null);
                                Beans.copy(fileAuthentication, entity)
                                        .excludesNull().execute();
                                fileAuthentication = entity;
                                fileAuthenticationBhv
                                        .update(fileAuthentication);
                            } else {
                                fileAuthenticationBhv
                                        .insert(fileAuthentication);
                            }
                        }
                        idMap.put(
                                FILE_AUTHENTICATION_KEY + ":" + id.toString(),
                                fileAuthentication.getId());
                    }
                }
                userTransaction.commit();
            } catch (final Exception e) {
                rollback(FILE_AUTHENTICATION_KEY, e);
            }
            // requestHeader
            try {
                userTransaction.begin();

                final List<RequestHeader> requestHeaderList = (List<RequestHeader>) dataSet
                        .get(REQUEST_HEADER_KEY + LIST_SUFFIX);
                if (requestHeaderList != null) {
                    for (RequestHeader requestHeader : requestHeaderList) {
                        final Long id = requestHeader.getId();

                        final Long webConfigId = idMap
                                .get(WEB_CRAWLING_CONFIG_KEY
                                        + ":"
                                        + requestHeader
                                                .getWebCrawlingConfigId());
                        if (webConfigId == null) {
                            // skip
                            continue;
                        }

                        final RequestHeaderCB cb = new RequestHeaderCB();
                        cb.query().setWebCrawlingConfigId_Equal(webConfigId);
                        final RequestHeader entity = requestHeaderBhv
                                .selectEntity(cb);
                        requestHeader.setId(null);
                        requestHeader.setWebCrawlingConfigId(webConfigId);
                        if (entity == null) {
                            requestHeaderBhv.insert(requestHeader);
                        } else {
                            if (overwrite) {
                                requestHeader.setVersionNo(null);
                                Beans.copy(requestHeader, entity)
                                        .excludesNull().execute();
                                requestHeader = entity;
                                requestHeaderBhv.update(requestHeader);
                            } else {
                                requestHeaderBhv.insert(requestHeader);
                            }
                        }
                        idMap.put(REQUEST_HEADER_KEY + ":" + id.toString(),
                                requestHeader.getId());
                    }
                }
                userTransaction.commit();
            } catch (final Exception e) {
                rollback(REQUEST_HEADER_KEY, e);
            }

            // crawlerProperties
            try {
                final Map<String, String> crawlerPropertyMap = (Map<String, String>) dataSet
                        .get(CRAWLER_PROPERTIES_KEY);
                for (final Map.Entry<String, String> entry : crawlerPropertyMap
                        .entrySet()) {
                    final String value = entry.getValue();
                    if (StringUtil.isNotBlank(value)) {
                        crawlerProperties.setProperty(entry.getKey(), value);
                    }
                }
                crawlerProperties.store();
            } catch (final Exception e) {
                logger.warn("Failed to restore properties: "
                        + CRAWLER_PROPERTIES_KEY, e);
            }

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
