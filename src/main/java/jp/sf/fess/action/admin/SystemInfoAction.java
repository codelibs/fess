/*
 * Copyright 2009-2014 the CodeLibs Project and the Others.
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

import javax.annotation.Resource;

import jp.sf.fess.Constants;
import jp.sf.fess.form.admin.SystemInfoForm;
import jp.sf.fess.helper.SystemHelper;

import org.codelibs.core.util.DynamicProperties;
import org.codelibs.core.util.StringUtil;
import org.seasar.struts.annotation.ActionForm;
import org.seasar.struts.annotation.Execute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemInfoAction implements Serializable {
    private static final Logger logger = LoggerFactory
            .getLogger(CrawlAction.class);

    private static final long serialVersionUID = 1L;

    @ActionForm
    @Resource
    protected SystemInfoForm systemInfoForm;

    @Resource
    protected DynamicProperties crawlerProperties;

    public List<Map<String, String>> envItems;

    public List<Map<String, String>> propItems;

    public List<Map<String, String>> fessPropItems;

    public List<Map<String, String>> bugReportItems;

    @Resource
    protected SystemHelper systemHelper;

    public String getHelpLink() {
        return systemHelper.getHelpLink("systemInfo");
    }

    @Execute(validator = false)
    public String index() {
        envItems = new ArrayList<Map<String, String>>();
        propItems = new ArrayList<Map<String, String>>();
        fessPropItems = new ArrayList<Map<String, String>>();
        bugReportItems = new ArrayList<Map<String, String>>();

        for (final Map.Entry<String, String> entry : System.getenv().entrySet()) {
            envItems.add(createItem(entry.getKey(), entry.getValue()));
        }

        for (final Map.Entry<Object, Object> entry : System.getProperties()
                .entrySet()) {
            propItems.add(createItem(entry.getKey(), entry.getValue()));
        }

        if (crawlerProperties.isEmpty()) {
            crawlerProperties.setProperty(Constants.DIFF_CRAWLING_PROPERTY, Constants.TRUE);
            crawlerProperties.setProperty(Constants.USE_ACL_AS_ROLE, Constants.FALSE);
            crawlerProperties.setProperty(Constants.SERVER_ROTATION_PROPERTY, Constants.FALSE);
            crawlerProperties.setProperty(Constants.DAY_FOR_CLEANUP_PROPERTY, "1");
            crawlerProperties.setProperty(Constants.CRAWLING_THREAD_COUNT_PROPERTY, "5");
            crawlerProperties.setProperty(Constants.SEARCH_LOG_PROPERTY, Constants.TRUE);
            crawlerProperties.setProperty(Constants.USER_INFO_PROPERTY, Constants.TRUE);
            crawlerProperties.setProperty(Constants.USER_FAVORITE_PROPERTY, Constants.FALSE);
            crawlerProperties.setProperty(Constants.WEB_API_XML_PROPERTY, Constants.TRUE);
            crawlerProperties.setProperty(Constants.WEB_API_JSON_PROPERTY, Constants.TRUE);
            crawlerProperties.setProperty(Constants.DEFAULT_LABEL_VALUE_PROPERTY, StringUtil.EMPTY);
            crawlerProperties.setProperty(Constants.APPEND_QUERY_PARAMETER_PROPERTY, Constants.FALSE);
            crawlerProperties.setProperty(Constants.SUPPORTED_SEARCH_FEATURE_PROPERTY,
                    Constants.SUPPORTED_SEARCH_WEB);
            crawlerProperties.setProperty(Constants.IGNORE_FAILURE_TYPE_PROPERTY,
                    Constants.DEFAULT_IGNORE_FAILURE_TYPE);
            crawlerProperties.setProperty(Constants.FAILURE_COUNT_THRESHOLD_PROPERTY,
                    Constants.DEFAULT_FAILURE_COUNT);
            crawlerProperties.setProperty(Constants.WEB_API_HOT_SEARCH_WORD_PROPERTY, Constants.TRUE);
            crawlerProperties.setProperty(Constants.CSV_FILE_ENCODING_PROPERTY, Constants.UTF_8);
            crawlerProperties.setProperty(Constants.PURGE_SEARCH_LOG_DAY_PROPERTY,
                    Constants.DEFAULT_PURGE_DAY);
            crawlerProperties.setProperty(Constants.PURGE_JOB_LOG_DAY_PROPERTY,
                    Constants.DEFAULT_PURGE_DAY);
            crawlerProperties.setProperty(Constants.PURGE_USER_INFO_DAY_PROPERTY,
                    Constants.DEFAULT_PURGE_DAY);
            crawlerProperties.setProperty(Constants.PURGE_BY_BOTS_PROPERTY,
                    Constants.DEFAULT_PURGE_BY_BOTS);
            crawlerProperties.setProperty(Constants.NOTIFICATION_TO_PROPERTY, StringUtil.EMPTY);
            crawlerProperties.setProperty(Constants.SUGGEST_SEARCH_LOG_PROPERTY, Constants.TRUE);
            crawlerProperties.setProperty(Constants.PURGE_SUGGEST_SEARCH_LOG_DAY_PROPERTY, "30");
            try {
                crawlerProperties.store();
            } catch (final Exception e) {
                logger.error("Failed to initialize crawler parameters.", e);
            }
        }

        for (final Map.Entry<Object, Object> entry : crawlerProperties
                .entrySet()) {
            fessPropItems.add(createItem(entry.getKey(), entry.getValue()));
        }

        bugReportItems.add(createPropItem("file.separator"));
        bugReportItems.add(createPropItem("file.encoding"));
        bugReportItems.add(createPropItem("java.runtime.version"));
        bugReportItems.add(createPropItem("java.vm.info"));
        bugReportItems.add(createPropItem("java.vm.name"));
        bugReportItems.add(createPropItem("java.vm.vendor"));
        bugReportItems.add(createPropItem("java.vm.version"));
        bugReportItems.add(createPropItem("os.arch"));
        bugReportItems.add(createPropItem("os.name"));
        bugReportItems.add(createPropItem("os.version"));
        bugReportItems.add(createPropItem("user.country"));
        bugReportItems.add(createPropItem("user.language"));
        bugReportItems.add(createPropItem("user.timezone"));
        for (final Map.Entry<Object, Object> entry : crawlerProperties
                .entrySet()) {
            if (isBugReportTarget(entry.getKey())) {
                bugReportItems
                        .add(createItem(entry.getKey(), entry.getValue()));
            }
        }

        return "index.jsp";
    }

    private boolean isBugReportTarget(final Object key) {
        if ("snapshot.path".equals(key) || "label.value".equals(key)) {
            return false;
        }
        return true;
    }

    private Map<String, String> createPropItem(final String key) {
        return createItem(key, System.getProperty(key));
    }

    private Map<String, String> createItem(final Object label,
            final Object value) {
        final Map<String, String> map = new HashMap<String, String>(2);
        map.put(Constants.ITEM_LABEL, label != null ? label.toString()
                : StringUtil.EMPTY);
        map.put(Constants.ITEM_VALUE, value != null ? value.toString()
                : StringUtil.EMPTY);
        return map;
    }
}