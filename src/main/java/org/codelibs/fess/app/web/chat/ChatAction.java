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
package org.codelibs.fess.app.web.chat;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.web.base.FessSearchAction;
import org.codelibs.fess.app.web.base.SearchForm;
import org.codelibs.fess.app.web.search.SearchAction;
import org.codelibs.fess.chat.ChatClient;
import org.codelibs.fess.chat.ChatSessionManager;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.RenderDataUtil;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;

import jakarta.annotation.Resource;

/**
 * Action class for the RAG chat page.
 *
 * @author FessProject
 */
public class ChatAction extends FessSearchAction {

    private static final Logger logger = LogManager.getLogger(ChatAction.class);

    /**
     * Default constructor.
     */
    public ChatAction() {
        // Default constructor
    }

    /** The chat client for handling chat operations. */
    @Resource
    protected ChatClient chatClient;

    /** The session manager for managing chat sessions. */
    @Resource
    protected ChatSessionManager chatSessionManager;

    /**
     * Displays the chat page.
     *
     * @return the HTML response for the chat page, or redirects to search if chat is not available
     */
    @Execute
    public HtmlResponse index() {
        if (logger.isDebugEnabled()) {
            logger.debug("Chat page requested. Checking availability...");
        }

        if (!chatClient.isAvailable()) {
            if (logger.isInfoEnabled()) {
                logger.info("Redirecting to search page. RAG chat is not available.");
            }
            return redirect(SearchAction.class);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Displaying chat page. chatEnabled=true");
        }

        return asHtml(virtualHost(path_Chat_ChatJsp)).useForm(SearchForm.class).renderWith(data -> {
            RenderDataUtil.register(data, "chatEnabled", true);
            RenderDataUtil.register(data, "chatPage", true);
            // Label type items for filter UI
            final List<Map<String, String>> labelTypeItems = labelTypeHelper.getLabelTypeItemList(SearchRequestType.SEARCH,
                    request.getLocale() == null ? Locale.ROOT : request.getLocale());
            RenderDataUtil.register(data, "labelTypeItems", labelTypeItems);
            RenderDataUtil.register(data, "displayLabelTypeItems", labelTypeItems != null && !labelTypeItems.isEmpty());
            // Facet query views for filter UI (file type, timestamp, size)
            RenderDataUtil.register(data, "facetQueryViewList", ComponentUtil.getViewHelper().getFacetQueryViewList());
        });
    }

    /**
     * Clears the chat session.
     *
     * @param form the chat form containing the session ID to clear
     * @return the HTML response redirecting to the chat page
     */
    @Execute
    public HtmlResponse clear(final ChatForm form) {
        if (form.sessionId != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Clearing chat session. sessionId={}", form.sessionId);
            }
            chatSessionManager.clearSession(form.sessionId, getUserId());
        }
        return redirect(getClass());
    }

    /**
     * Returns the user ID for the current session. If the user is authenticated, returns the username;
     * otherwise, returns the guest user code.
     *
     * @return the user ID
     */
    protected String getUserId() {
        final String username = systemHelper.getUsername();
        if (!Constants.GUEST_USER.equals(username)) {
            return username;
        }
        return userInfoHelper.getUserCode();
    }

}
