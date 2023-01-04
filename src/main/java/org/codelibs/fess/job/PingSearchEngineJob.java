/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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
package org.codelibs.fess.job;

import static org.codelibs.core.stream.StreamUtil.stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.entity.PingResponse;
import org.codelibs.fess.es.client.SearchEngineClient;
import org.codelibs.fess.helper.NotificationHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.mylasta.mail.EsStatusPostcard;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.mail.send.hook.SMailCallbackContext;
import org.lastaflute.core.mail.Postbox;

public class PingSearchEngineJob {

    private static final Logger logger = LogManager.getLogger(PingSearchEngineJob.class);

    public String execute() {
        final SearchEngineClient searchEngineClient = ComponentUtil.getSearchEngineClient();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();

        final StringBuilder resultBuf = new StringBuilder();

        final PingResponse ping = searchEngineClient.ping();
        final int status = ping.getStatus();
        if (systemHelper.isChangedClusterState(status)) {
            if (fessConfig.hasNotification()) {
                final String toStrs = fessConfig.getNotificationTo();
                final String[] toAddresses;
                if (StringUtil.isNotBlank(toStrs)) {
                    toAddresses = toStrs.split(",");
                } else {
                    toAddresses = StringUtil.EMPTY_STRINGS;
                }
                final Postbox postbox = ComponentUtil.getComponent(Postbox.class);
                try {
                    final NotificationHelper notificationHelper = ComponentUtil.getNotificationHelper();
                    SMailCallbackContext.setPreparedMessageHookOnThread(notificationHelper::send);
                    EsStatusPostcard.droppedInto(postbox, postcard -> {
                        postcard.setFrom(fessConfig.getMailFromAddress(), fessConfig.getMailFromName());
                        postcard.addReplyTo(fessConfig.getMailReturnPath());
                        if (toAddresses.length > 0) {
                            stream(toAddresses).of(stream -> stream.map(String::trim).forEach(address -> {
                                postcard.addTo(address);
                            }));
                        } else {
                            postcard.addTo(fessConfig.getMailFromAddress());
                            postcard.dryrun();
                        }
                        postcard.setHostname(systemHelper.getHostname());
                        postcard.setClustername(ping.getClusterName());
                        postcard.setClusterstatus(ping.getClusterStatus());
                    });
                } catch (final Exception e) {
                    logger.warn("Failed to send a test mail.", e);
                } finally {
                    SMailCallbackContext.clearPreparedMessageHookOnThread();
                }
            }
            resultBuf.append("Status of ").append(ping.getClusterName()).append(" is changed to ").append(ping.getClusterStatus())
                    .append('.');
        } else if (status == 0) {
            resultBuf.append(ping.getClusterName()).append(" is alive.");
        } else {
            resultBuf.append(ping.getClusterName()).append(" is not available.");
        }

        return resultBuf.toString();
    }

}
