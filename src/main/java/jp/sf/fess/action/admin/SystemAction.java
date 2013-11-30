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

package jp.sf.fess.action.admin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import jp.sf.fess.Constants;
import jp.sf.fess.crud.util.SAStrutsUtil;
import jp.sf.fess.db.exentity.ScheduledJob;
import jp.sf.fess.form.admin.SystemForm;
import jp.sf.fess.helper.SystemHelper;
import jp.sf.fess.helper.WebManagementHelper;
import jp.sf.fess.service.ScheduledJobService;

import org.codelibs.core.util.DynamicProperties;
import org.codelibs.sastruts.core.annotation.Token;
import org.codelibs.sastruts.core.exception.SSCActionMessagesException;
import org.codelibs.solr.lib.SolrGroup;
import org.codelibs.solr.lib.SolrGroupManager;
import org.codelibs.solr.lib.SolrLibConstants;
import org.codelibs.solr.lib.policy.QueryType;
import org.codelibs.solr.lib.policy.impl.StatusPolicyImpl;
import org.seasar.framework.container.SingletonS2Container;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.annotation.ActionForm;
import org.seasar.struts.annotation.Execute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemAction implements Serializable {
    private static final Logger logger = LoggerFactory
            .getLogger(SystemAction.class);

    private static final long serialVersionUID = 1L;

    @ActionForm
    @Resource
    protected SystemForm systemForm;

    @Resource
    protected DynamicProperties solrProperties;

    @Resource
    protected SolrGroupManager solrGroupManager;

    @Resource
    protected WebManagementHelper webManagementHelper;

    @Resource
    protected SystemHelper systemHelper;

    public String getHelpLink() {
        return systemHelper.getHelpLink("system");
    }

    protected String showIndex(final boolean redirect) {
        final Map<String, DynamicProperties> groupPropMap = new HashMap<String, DynamicProperties>();
        for (final String groupName : solrGroupManager.getSolrGroupNames()) {
            final DynamicProperties props = SingletonS2Container
                    .getComponent(groupName + "Properties");
            if (props != null) {
                groupPropMap.put(groupName, props);
            }
        }

        final String[] serverNames = solrGroupManager.getSolrServerNames();
        for (final String name : serverNames) {
            final String[] names = name.split(":");
            if (names.length == 2) {
                final Map<String, String> map = new HashMap<String, String>(4);
                map.put("groupName", names[0]);
                map.put("serverName", names[1]);
                final DynamicProperties props = groupPropMap.get(names[0]);
                if (props != null) {
                    String status = props
                            .getProperty(StatusPolicyImpl.STATUS_PREFIX
                                    + names[1]);
                    if (StringUtil.isBlank(status)) {
                        status = StatusPolicyImpl.ACTIVE;
                    }
                    map.put("status", status);
                    String index = props
                            .getProperty(StatusPolicyImpl.INDEX_PREFIX
                                    + names[1]);
                    if (StringUtil.isBlank(index)) {
                        index = StatusPolicyImpl.READY;
                    }
                    map.put("index", index);
                } else {
                    map.put("status", StatusPolicyImpl.ACTIVE);
                    map.put("index", StatusPolicyImpl.READY);
                }
                systemForm.serverStatusList.add(map);
            }
        }
        // select group status
        systemForm.currentServerForSelect = solrProperties
                .getProperty(SolrLibConstants.SELECT_GROUP);
        final SolrGroup selectSolrGroup = solrGroupManager
                .getSolrGroup(systemForm.currentServerForSelect);
        if (selectSolrGroup != null
                && selectSolrGroup.isActive(QueryType.QUERY)) {
            systemForm.currentServerStatusForSelect = Constants.ACTIVE;
        } else {
            systemForm.currentServerStatusForSelect = Constants.INACTIVE;
        }

        // update group status
        systemForm.currentServerForUpdate = solrProperties
                .getProperty(SolrLibConstants.SELECT_GROUP);
        final SolrGroup updateSolrGroup = solrGroupManager
                .getSolrGroup(systemForm.currentServerForUpdate);
        if (updateSolrGroup != null
                && updateSolrGroup.isActive(QueryType.QUERY)) {
            systemForm.currentServerStatusForUpdate = Constants.ACTIVE;
        } else {
            systemForm.currentServerStatusForUpdate = Constants.INACTIVE;
        }

        if (redirect) {
            return "index?redirect=true";
        } else {
            return "index.jsp";
        }
    }

    @Token(save = true, validate = false)
    @Execute(validator = false)
    public String index() {
        return showIndex(false);
    }

    @Token(save = false, validate = true)
    @Execute(validator = true, input = "index.jsp")
    public String update() {
        // load solr group properties
        final Map<String, DynamicProperties> groupPropMap = new HashMap<String, DynamicProperties>();
        for (final String groupName : solrGroupManager.getSolrGroupNames()) {
            final DynamicProperties props = SingletonS2Container
                    .getComponent(groupName + "Properties");
            if (props != null) {
                groupPropMap.put(groupName, props);
            }
        }

        try {
            // server status
            for (final Map<String, String> statusMap : systemForm.serverStatusList) {
                for (final Map.Entry<String, String> entry : statusMap
                        .entrySet()) {
                    final String[] names = entry.getKey().split("/");
                    if (names.length == 3) {
                        final DynamicProperties props = groupPropMap
                                .get(names[0]);
                        if (props != null) {
                            final String value = entry.getValue();
                            String key;
                            if ("status".equals(names[2])) {
                                key = StatusPolicyImpl.STATUS_PREFIX + names[1];
                            } else if ("index".equals(names[2])) {
                                key = StatusPolicyImpl.INDEX_PREFIX + names[1];
                            } else {
                                logger.error("Invalid parameter: "
                                        + entry.getKey());
                                throw new SSCActionMessagesException(
                                        "errors.failed_to_update_solr_params");
                            }
                            props.setProperty(key, value);
                            props.store();
                        } else {
                            logger.warn("Solr group properties is not found: "
                                    + names[0]);
                        }
                    }
                }
            }

            SAStrutsUtil.addSessionMessage("success.update_solr_params");
        } catch (final Exception e) {
            logger.error("Failed to update solr parameters.", e);
            throw new SSCActionMessagesException(e,
                    "errors.failed_to_update_solr_params", e);
        }

        return showIndex(true);

    }

    @Token(save = false, validate = true)
    @Execute(validator = true, input = "index")
    public String start() {
        final String groupName = solrProperties
                .getProperty(SolrLibConstants.UPDATE_GROUP);
        final SolrGroup solrGroup = solrGroupManager.getSolrGroup(groupName);
        if (solrGroup != null) {
            if (!systemHelper.isCrawlProcessRunning()) {
                final ScheduledJobService scheduledJobService = SingletonS2Container
                        .getComponent(ScheduledJobService.class);
                final List<ScheduledJob> scheduledJobList = scheduledJobService
                        .getCrawloerJobList();
                for (final ScheduledJob scheduledJob : scheduledJobList) {
                    scheduledJob.start();
                }
                SAStrutsUtil.addSessionMessage("success.start_crawl_process");
            } else {
                SAStrutsUtil
                        .addSessionMessage("success.failed_to_start_crawl_process");
            }
        } else {
            SAStrutsUtil
                    .addSessionMessage("success.failed_to_start_crawl_process");
        }
        return showIndex(true);
    }

    @Token(save = false, validate = true)
    @Execute(validator = true, input = "index")
    public String stop() {
        if (systemHelper.isCrawlProcessRunning()) {
            if (StringUtil.isNotBlank(systemForm.sessionId)) {
                systemHelper.destroyCrawlerProcess(systemForm.sessionId);
            } else {
                for (final String sessionId : systemHelper
                        .getRunningSessionIdSet()) {
                    systemHelper.destroyCrawlerProcess(sessionId);
                }
            }
            SAStrutsUtil.addSessionMessage("success.stopping_crawl_process");
        } else {
            SAStrutsUtil.addSessionMessage("errors.no_running_crawl_process");
        }
        return showIndex(true);
    }

    @Token(save = false, validate = true)
    @Execute(validator = true, input = "index.jsp")
    public String startSolrInstance() {
        try {
            webManagementHelper.start(systemForm.solrInstanceName);
            SAStrutsUtil.addSessionMessage("success.starting_solr_instance");
        } catch (final Exception e) {
            logger.error("Failed to start a solr instance: "
                    + systemForm.solrInstanceName, e);
            throw new SSCActionMessagesException(e,
                    "errors.failed_to_start_solr_instance");
        }
        return showIndex(true);
    }

    @Token(save = false, validate = true)
    @Execute(validator = true, input = "index.jsp")
    public String stopSolrInstance() {
        try {
            webManagementHelper.stop(systemForm.solrInstanceName);
            SAStrutsUtil.addSessionMessage("success.stopping_solr_instance");
        } catch (final Exception e) {
            logger.error("Failed to stop a solr instance: "
                    + systemForm.solrInstanceName, e);
            throw new SSCActionMessagesException(e,
                    "errors.failed_to_stop_solr_instance");
        }
        return showIndex(true);
    }

    @Token(save = false, validate = true)
    @Execute(validator = true, input = "index.jsp")
    public String reloadSolrInstance() {
        try {
            webManagementHelper.reload(systemForm.solrInstanceName);
            SAStrutsUtil.addSessionMessage("success.reloading_solr_instance");
        } catch (final Exception e) {
            logger.error("Failed to reload a solr instance: "
                    + systemForm.solrInstanceName, e);
            throw new SSCActionMessagesException(e,
                    "errors.failed_to_reload_solr_instance");
        }
        return showIndex(true);
    }

    public List<Map<String, String>> getSolrInstanceList() {
        final List<String> solrInstanceNameList = webManagementHelper
                .getSolrInstanceNameList();
        final List<Map<String, String>> solrInstanceList = new ArrayList<Map<String, String>>();
        for (final String solrInstanceName : solrInstanceNameList) {
            final Map<String, String> map = new HashMap<String, String>();
            map.put("name", solrInstanceName);
            map.put("status", webManagementHelper.getStatus(solrInstanceName));
            solrInstanceList.add(map);
        }
        return solrInstanceList;
    }

    public boolean isCrawlerRunning() {
        return systemHelper.isCrawlProcessRunning();
    }

    public String[] getRunningSessionIds() {
        final Set<String> idSet = systemHelper.getRunningSessionIdSet();
        return idSet.toArray(new String[idSet.size()]);
    }

}