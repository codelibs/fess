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
package org.codelibs.fess.helper;

import java.io.IOException;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.stream.StreamUtil;
import org.codelibs.curl.Curl;
import org.codelibs.curl.CurlResponse;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.mail.CardView;
import org.dbflute.mail.send.supplement.SMailPostingDiscloser;

public class NotificationHelper {
    private static final Logger logger = LogManager.getLogger(NotificationHelper.class);

    protected static final char LF = '\n';

    public void send(final CardView cardView, final SMailPostingDiscloser discloser) {
        sendToSlack(cardView, discloser);
        sendToGoogleChat(cardView, discloser);
    }

    protected void sendToSlack(final CardView cardView, final SMailPostingDiscloser discloser) {
        // https://api.slack.com/messaging/webhooks#posting_with_webhooks
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String slackWebhookUrls = fessConfig.getSlackWebhookUrls();
        if (StringUtil.isBlank(slackWebhookUrls)) {
            return;
        }
        final String body = toSlackMessage(discloser);
        StreamUtil.split(slackWebhookUrls, "[,\\s]").of(stream -> stream.filter(StringUtil::isNotBlank).forEach(url -> {
            try (CurlResponse response = Curl.post(url).header("Content-Type", "application/json").body(body).execute()) {
                if (response.getHttpStatusCode() == 200) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Sent {} to {}.", body, url);
                    }
                } else {
                    logger.warn("Failed to send {} to {}. HTTP Status is {}. {}", body, url, response.getHttpStatusCode(),
                            response.getContentAsString());
                }
            } catch (final IOException e) {
                logger.warn("Failed to send {} to {}.", body, url, e);
            }
        }));
    }

    protected String toSlackMessage(final SMailPostingDiscloser discloser) {
        final StringBuilder buf = new StringBuilder(100);
        buf.append("{\"text\":\"");
        buf.append(LF);
        buf.append(StringEscapeUtils.escapeJson(discloser.getSavedSubject().orElse(StringUtil.EMPTY).trim()));
        buf.append(LF).append("```");
        buf.append(LF).append(StringEscapeUtils.escapeJson(discloser.getSavedPlainText().orElse(StringUtil.EMPTY).trim()));
        buf.append(LF).append("```\"}");
        return buf.toString();
    }

    protected void sendToGoogleChat(final CardView cardView, final SMailPostingDiscloser discloser) {
        // https://developers.google.com/hangouts/chat/how-tos/webhooks
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String googleChatWebhookUrls = fessConfig.getGoogleChatWebhookUrls();
        if (StringUtil.isBlank(googleChatWebhookUrls)) {
            return;
        }
        final String body = toGoogleChatMessage(discloser);
        StreamUtil.split(googleChatWebhookUrls, "[,\\s]").of(stream -> stream.filter(StringUtil::isNotBlank).forEach(url -> {
            try (CurlResponse response = Curl.post(url).header("Content-Type", "application/json").body(body).execute()) {
                if (response.getHttpStatusCode() == 200) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Sent {} to {}.", body, url);
                    }
                } else {
                    logger.warn("Failed to send {} to {}. HTTP Status is {}. {}", body, url, response.getHttpStatusCode(),
                            response.getContentAsString());
                }
            } catch (final IOException e) {
                logger.warn("Failed to send {} to {}.", body, url, e);
            }
        }));
    }

    protected String toGoogleChatMessage(final SMailPostingDiscloser discloser) {
        final StringBuilder buf = new StringBuilder(100);
        buf.append("{\"text\":\"");
        buf.append(LF);
        buf.append(StringEscapeUtils.escapeJson(discloser.getSavedSubject().orElse(StringUtil.EMPTY).trim()));
        buf.append(LF).append("```");
        buf.append(LF).append(StringEscapeUtils.escapeJson(discloser.getSavedPlainText().orElse(StringUtil.EMPTY).trim()));
        buf.append(LF).append("```\"}");
        return buf.toString();
    }
}
