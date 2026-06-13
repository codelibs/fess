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
package org.codelibs.fess.chat;

import java.util.List;
import java.util.Map;

/**
 * Resolves the content of relevant documents for the LLM answer context.
 *
 * <p>Implementations decide, per document, whether to use the full content or
 * query-relevant highlighted passages (e.g. based on document size), so that
 * large documents do not crowd out the answer context. The default
 * implementation is {@link DefaultChatContentFetcher}; replace the
 * {@code chatContentFetcher} DI component to override the behavior.</p>
 */
public interface ChatContentFetcher {

    /**
     * Resolves the LLM-context content for the given relevant documents.
     *
     * @param request the fetch request (doc ids, search results, query)
     * @return document maps in the same order as {@code request.getDocIds()},
     *         each with the {@code content} field set to the text to send to the LLM
     */
    List<Map<String, Object>> fetchContent(ChatContentRequest request);
}
