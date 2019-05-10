/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
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

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.entity.PingResponse;
import org.codelibs.fess.es.client.FessEsClient;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.mylasta.mail.EsStatusPostcard;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.core.mail.Postbox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PingEsJob {

    private static final Logger logger = LoggerFactory.getLogger(PingEsJob.class);

    public String execute() {
        final FessEsClient fessEsClient = ComponentUtil.getFessEsClient();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();

        final StringBuilder resultBuf = new StringBuilder();

        final String notificationTo = fessConfig.getNotificationTo();
        final PingResponse ping = fessEsClient.ping();
        final int status = ping.getStatus();
        if (systemHelper.isChangedClusterState(status)) {
            if (StringUtil.isNotBlank(notificationTo)) {
                final Postbox postbox = ComponentUtil.getComponent(Postbox.class);
                try {
                    EsStatusPostcard.droppedInto(postbox, postcard -> {
                        postcard.setFrom(fessConfig.getMailFromAddress(), fessConfig.getMailFromName());
                        postcard.addReplyTo(fessConfig.getMailReturnPath());
                        postcard.addTo(notificationTo);
                        postcard.setHostname(systemHelper.getHostname());
                        postcard.setClustername(ping.getClusterName());
                        postcard.setClusterstatus(ping.getClusterStatus());
                    });
                } catch (final Exception e) {
                    logger.warn("Failed to send a test mail.", e);
                }
            }
            resultBuf.append("Status of ").append(ping.getClusterName()).append(" is changed to ").append(ping.getClusterStatus())
                    .append('.');
        } else {
            if (status == 0) {
                resultBuf.append(ping.getClusterName()).append(" is alive.");
            } else {
                resultBuf.append(ping.getClusterName()).append(" is not available.");
            }
        }

        return resultBuf.toString();
    }

}
