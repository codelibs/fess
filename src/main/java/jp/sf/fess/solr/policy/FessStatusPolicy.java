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

package jp.sf.fess.solr.policy;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import jp.sf.fess.Constants;
import jp.sf.fess.helper.MailHelper;
import jp.sf.fess.util.ComponentUtil;
import jp.sf.fess.util.ResourceUtil;

import org.codelibs.core.util.DynamicProperties;
import org.codelibs.core.util.StringUtil;
import org.codelibs.solr.lib.policy.QueryType;
import org.codelibs.solr.lib.policy.impl.StatusPolicyImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.FileTemplateLoader;

public class FessStatusPolicy extends StatusPolicyImpl {
    private static final String MAIL_TEMPLATE_NAME = "solr_status";

    private static final Logger logger = LoggerFactory
            .getLogger(FessStatusPolicy.class);

    public String activateSubject = "[FESS] Solr status changed";

    public String deactivateSubject = "[FESS] Solr status changed";

    @Override
    public void activate(final QueryType queryType, final String serverName) {
        final String statusValue = solrGroupProperties
                .getProperty(getStatusKey(serverName));
        final String indexValue = solrGroupProperties
                .getProperty(getIndexKey(serverName));

        final Map<String, Object> dataMap = new HashMap<String, Object>();

        if (StringUtil.isNotBlank(statusValue) && INACTIVE.equals(statusValue)) {
            // status: INACTIVE -> ACTIVE
            dataMap.put("statusBefore", Constants.INACTIVE);
            dataMap.put("statusAfter", Constants.ACTIVE);
        }

        switch (queryType) {
            case COMMIT:
            case OPTIMIZE:
            case ROLLBACK:
                if (StringUtil.isNotBlank(indexValue)
                        && UNFINISHED.equals(indexValue)) {
                    // index: UNFINISHED -> COMPLETED
                    dataMap.put("indexBefore", UNFINISHED.toUpperCase());
                    dataMap.put("indexAfter", COMPLETED.toUpperCase());
                }
                break;
            case ADD:
            case DELETE:
                if (StringUtil.isNotBlank(indexValue)
                        && UNFINISHED.equals(indexValue)) {
                    // index: UNFINISHED -> READY
                    dataMap.put("indexBefore", UNFINISHED.toUpperCase());
                    dataMap.put("indexAfter", READY.toUpperCase());
                }
                break;
            case PING:
            case QUERY:
            case REQUEST:
            default:
                break;
        }

        if (!dataMap.isEmpty()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Server: " + serverName + "\n" + dataMap);
            }

            dataMap.put("server", serverName);
            send(activateSubject, dataMap);
        }

        super.activate(queryType, serverName);
    }

    @Override
    public void deactivate(final QueryType queryType, final String serverName) {
        final String statusValue = solrGroupProperties
                .getProperty(getStatusKey(serverName));
        final String indexValue = solrGroupProperties
                .getProperty(getIndexKey(serverName));

        final Map<String, Object> dataMap = new HashMap<String, Object>();

        if (StringUtil.isNotBlank(statusValue) && ACTIVE.equals(statusValue)) {
            // status: ACTIVE -> INACTIVE
            dataMap.put("statusBefore", Constants.ACTIVE);
            dataMap.put("statusAfter", Constants.INACTIVE);
        }

        switch (queryType) {
            case COMMIT:
            case OPTIMIZE:
            case ROLLBACK:
            case ADD:
            case DELETE:
                if (StringUtil.isNotBlank(indexValue)
                        && !UNFINISHED.equals(indexValue)) {
                    // index: READY/COMPLETED -> UNFINISHED
                    dataMap.put("indexBefore", indexValue == null ? "UNKNOWN"
                            : indexValue.toUpperCase());
                    dataMap.put("indexAfter", UNFINISHED.toUpperCase());
                }
                break;
            case PING:
            case QUERY:
            case REQUEST:
            default:
                break;
        }

        if (!dataMap.isEmpty()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Server: " + serverName + "\n" + dataMap);
            }

            dataMap.put("server", serverName);
            send(deactivateSubject, dataMap);
        }

        super.deactivate(queryType, serverName);
    }

    protected void send(final String subject, final Map<String, Object> dataMap) {
        final DynamicProperties crawlerProperties = ComponentUtil
                .getCrawlerProperties();
        if (crawlerProperties == null) {
            logger.info("crawlerProperties is not found.");
            return;
        }

        final MailHelper mailHelper = ComponentUtil.getMailHelper();
        if (mailHelper == null) {
            logger.info("mailHelper is not found.");
            return;
        }

        final String toStrs = (String) crawlerProperties
                .get(Constants.NOTIFICATION_TO_PROPERTY);
        if (StringUtil.isNotBlank(toStrs)) {
            final String[] toAddresses = toStrs.split(",");

            try {
                dataMap.put("hostname", InetAddress.getLocalHost()
                        .getHostAddress());
            } catch (final UnknownHostException e) {
                // ignore
            }

            final FileTemplateLoader loader = new FileTemplateLoader(new File(
                    ResourceUtil.getMailTemplatePath(StringUtil.EMPTY)));
            final Handlebars handlebars = new Handlebars(loader);

            try {
                final Template template = handlebars
                        .compile(MAIL_TEMPLATE_NAME);
                final Context hbsContext = Context.newContext(dataMap);
                final String body = template.apply(hbsContext);

                mailHelper.send(toAddresses, subject, body);
            } catch (final Exception e) {
                logger.warn("Failed to send the notification.", e);
            }
        }
    }
}
