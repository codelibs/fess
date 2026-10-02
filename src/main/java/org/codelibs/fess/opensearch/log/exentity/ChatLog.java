/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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
package org.codelibs.fess.opensearch.log.exentity;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;

import org.codelibs.fess.entity.SearchLogEvent;
import org.codelibs.fess.opensearch.log.bsentity.BsChatLog;

/**
 * A usage record of one RAG chat request: who asked, when, how it ended and how many LLM calls and
 * tokens it took. The question and the answer are never part of it.
 *
 * @author FreeGen
 */
public class ChatLog extends BsChatLog implements SearchLogEvent {

    private static final long serialVersionUID = 1L;

    /** The status of a chat request that produced an answer. */
    public static final String STATUS_SUCCESS = "success";

    /** The status of a chat request that failed. */
    public static final String STATUS_ERROR = "error";

    /** The status of a streaming chat request whose client disconnected before the answer was complete. */
    public static final String STATUS_CANCELLED = "cancelled";

    /** The chat type of a chat that searches the index for its sources. */
    public static final String CHAT_TYPE_CHAT = "chat";

    /** The chat type of a chat about one document. */
    public static final String CHAT_TYPE_DOCUMENT = "document";

    /** The access type of a chat answered in one response. */
    public static final String ACCESS_TYPE_SYNC = "sync";

    /** The access type of a chat streamed as server-sent events. */
    public static final String ACCESS_TYPE_STREAM = "stream";

    @Override
    public String getId() {
        return asDocMeta().id();
    }

    public void setId(final String id) {
        asDocMeta().id(id);
    }

    @Override
    public Long getVersionNo() {
        return asDocMeta().version();
    }

    public void setVersionNo(final Long version) {
        asDocMeta().version(version);
    }

    @Override
    protected void addFieldToSource(final Map<String, Object> sourceMap, final String field, final Object value) {
        if (value instanceof final LocalDateTime ldt) {
            final ZonedDateTime zdt = ZonedDateTime.of(ldt, ZoneId.systemDefault());
            super.addFieldToSource(sourceMap, field, DateTimeFormatter.ISO_INSTANT.format(zdt));
        } else {
            super.addFieldToSource(sourceMap, field, value);
        }
    }

    @Override
    public String toString() {
        return "ChatLog [accessType=" + accessType + ", chatSessionId=" + chatSessionId + ", chatType=" + chatType + ", completionTokens="
                + completionTokens + ", errorCode=" + errorCode + ", intent=" + intent + ", llmCalls=" + llmCalls + ", llmName=" + llmName
                + ", model=" + model + ", promptTokens=" + promptTokens + ", requestedAt=" + requestedAt + ", responseTime=" + responseTime
                + ", roles=" + Arrays.toString(roles) + ", searchQueryId=" + searchQueryId + ", sourceCount=" + sourceCount + ", status="
                + status + ", totalTokens=" + totalTokens + ", user=" + user + ", userSessionId=" + userSessionId + ", virtualHost="
                + virtualHost + ", docMeta=" + docMeta + "]";
    }

    @Override
    public String getEventType() {
        return "chat";
    }
}
