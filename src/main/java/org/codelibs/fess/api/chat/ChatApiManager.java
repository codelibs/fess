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
package org.codelibs.fess.api.chat;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.api.BaseApiManager;
import org.codelibs.fess.chat.ChatClient.ChatResult;
import org.codelibs.fess.chat.ChatPhaseCallback;
import org.codelibs.fess.entity.ChatMessage.ChatSource;
import org.codelibs.fess.entity.FacetQueryView;
import org.codelibs.fess.entity.SearchRequestParams;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.llm.LlmException;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * API Manager for RAG chat endpoints with SSE streaming support.
 *
 * @author FessProject
 */
public class ChatApiManager extends BaseApiManager {

    private static final Logger logger = LogManager.getLogger(ChatApiManager.class);

    private static final String CHAT_API_PATH = "/api/v1/chat";
    private static final String STREAM_API_PATH = "/api/v1/chat/stream";

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /** The path prefix for the chat API endpoints. */
    protected String pathPrefix = CHAT_API_PATH;

    /**
     * Default constructor.
     */
    public ChatApiManager() {
        // Default constructor
    }

    /**
     * Registers this API manager with the WebApiManagerFactory.
     */
    @PostConstruct
    public void register() {
        if (logger.isInfoEnabled()) {
            logger.info("Registering ChatApiManager");
        }
        ComponentUtil.getWebApiManagerFactory().add(this);
    }

    @Override
    public boolean matches(final HttpServletRequest request) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final boolean ragChatEnabled = fessConfig.isRagChatEnabled();
        if (!ragChatEnabled) {
            if (logger.isTraceEnabled()) {
                logger.trace("ChatApiManager.matches() returning false. ragChatEnabled={}", ragChatEnabled);
            }
            return false;
        }

        final String servletPath = request.getServletPath();
        final boolean matches = servletPath.startsWith(CHAT_API_PATH);
        if (logger.isTraceEnabled()) {
            logger.trace("ChatApiManager.matches() checking path. servletPath={}, expectedPrefix={}, matches={}", servletPath,
                    CHAT_API_PATH, matches);
        }
        return matches;
    }

    @Override
    public void process(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        final String servletPath = request.getServletPath();

        if (logger.isDebugEnabled()) {
            logger.debug("Processing chat API request. path={}, method={}", servletPath, request.getMethod());
        }

        if (servletPath.equals(STREAM_API_PATH) || servletPath.equals(STREAM_API_PATH + "/")) {
            processStreamRequest(request, response);
        } else if (servletPath.equals(CHAT_API_PATH) || servletPath.equals(CHAT_API_PATH + "/")) {
            processChatRequest(request, response);
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Unknown chat API path. path={}", servletPath);
            }
            writeJsonResponse(response, HttpServletResponse.SC_NOT_FOUND, createErrorResponse("Not found"));
        }
    }

    /**
     * Processes a non-streaming chat request.
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @throws IOException if an I/O error occurs
     */
    protected void processChatRequest(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            if (logger.isDebugEnabled()) {
                logger.debug("Invalid method for chat request. method={}", request.getMethod());
            }
            writeJsonResponse(response, HttpServletResponse.SC_METHOD_NOT_ALLOWED, createErrorResponse("Method not allowed"));
            return;
        }

        try {
            final String message = request.getParameter("message");
            final String sessionId = request.getParameter("sessionId");
            final String clearParam = request.getParameter("clear");

            if (logger.isDebugEnabled()) {
                logger.debug("Processing chat request. sessionId={}, messageLength={}, clear={}", sessionId,
                        message != null ? message.length() : 0, clearParam);
            }

            if (StringUtil.isBlank(message)) {
                if ("true".equals(clearParam) && StringUtil.isNotBlank(sessionId)) {
                    final String clearUserId = getUserId(request);
                    final boolean cleared = ComponentUtil.getChatSessionManager().clearSession(sessionId, clearUserId);
                    if (cleared) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Session cleared. sessionId={}, userId={}", sessionId, clearUserId);
                        }
                        writeJsonResponse(response, HttpServletResponse.SC_OK, createSuccessResponse(sessionId, "Session cleared", null));
                    } else {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Session not found or not owned. sessionId={}, userId={}", sessionId, clearUserId);
                        }
                        writeJsonResponse(response, HttpServletResponse.SC_NOT_FOUND, createErrorResponse("Session not found"));
                    }
                    return;
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("Message is required but was empty");
                }
                writeJsonResponse(response, HttpServletResponse.SC_BAD_REQUEST, createErrorResponse("Message is required"));
                return;
            }

            final FessConfig fessConfig = ComponentUtil.getFessConfig();
            final int maxMessageLength = getMaxMessageLength(fessConfig);
            if (message.length() > maxMessageLength) {
                logger.warn("Chat message exceeds max length. length={}, max={}", message.length(), maxMessageLength);
                writeJsonResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                        createErrorResponse("Message is too long (max " + maxMessageLength + " characters)"));
                return;
            }

            final String userId = getUserId(request);

            // Set LLM type name as Access Type for search log
            request.setAttribute(Constants.SEARCH_LOG_ACCESS_TYPE,
                    ComponentUtil.getFessConfig().getSystemProperty("rag.llm.name", "ollama"));

            final Map<String, String[]> fields = parseFieldFilters(request);
            final String[] extraQueries = parseExtraQueries(request);
            final ChatResult result;
            if (fields.isEmpty() && extraQueries.length == 0) {
                result = ComponentUtil.getChatClient().chat(sessionId, message, userId);
            } else {
                result = ComponentUtil.getChatClient().chat(sessionId, message, userId, fields, extraQueries);
            }

            if (logger.isDebugEnabled()) {
                logger.debug("Chat request completed. sessionId={}, responseLength={}", result.getSessionId(),
                        result.getMessage().getContent() != null ? result.getMessage().getContent().length() : 0);
            }

            writeJsonResponse(response, HttpServletResponse.SC_OK,
                    createSuccessResponse(result.getSessionId(), result.getMessage().getContent(), result.getMessage().getSources()));

        } catch (final Exception e) {
            logger.warn("[RAG] Failed to process chat request. message={}", e.getMessage(), e);
            writeJsonResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, createErrorResponse("Internal server error"));
        }
    }

    /**
     * Processes a streaming chat request using Server-Sent Events (SSE).
     * Uses the enhanced multi-phase RAG flow with intent detection and result evaluation.
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @throws IOException if an I/O error occurs
     */
    protected void processStreamRequest(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        if (!"GET".equalsIgnoreCase(request.getMethod()) && !"POST".equalsIgnoreCase(request.getMethod())) {
            if (logger.isDebugEnabled()) {
                logger.debug("Invalid method for stream request. method={}, expected GET or POST", request.getMethod());
            }
            writeJsonResponse(response, HttpServletResponse.SC_METHOD_NOT_ALLOWED, createErrorResponse("Method not allowed"));
            return;
        }

        final String message = request.getParameter("message");
        final String sessionId = request.getParameter("sessionId");

        if (logger.isDebugEnabled()) {
            logger.debug("Processing stream request. sessionId={}, messageLength={}", sessionId, message != null ? message.length() : 0);
        }

        if (StringUtil.isBlank(message)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Message is required but was empty for stream request");
            }
            writeJsonResponse(response, HttpServletResponse.SC_BAD_REQUEST, createErrorResponse("Message is required"));
            return;
        }

        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final int maxMessageLength = getMaxMessageLength(fessConfig);
        if (message.length() > maxMessageLength) {
            logger.warn("Stream message exceeds max length. length={}, max={}", message.length(), maxMessageLength);
            writeJsonResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                    createErrorResponse("Message is too long (max " + maxMessageLength + " characters)"));
            return;
        }

        // Set SSE headers
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Connection", "keep-alive");
        response.setHeader("X-Accel-Buffering", "no"); // Disable nginx buffering

        try (final PrintWriter writer = response.getWriter()) {
            final String userId = getUserId(request);

            // Set LLM type name as Access Type for search log
            request.setAttribute(Constants.SEARCH_LOG_ACCESS_TYPE,
                    ComponentUtil.getFessConfig().getSystemProperty("rag.llm.name", "ollama"));

            // Create phase callback for SSE events
            final ChatPhaseCallback phaseCallback = new ChatPhaseCallback() {
                @Override
                public void onPhaseStart(final String phase, final String phaseMessage) {
                    onPhaseStart(phase, phaseMessage, null);
                }

                @Override
                public void onPhaseStart(final String phase, final String phaseMessage, final String keywords) {
                    try {
                        final Map<String, Object> data = new HashMap<>();
                        data.put("phase", phase);
                        data.put("status", "start");
                        data.put("message", phaseMessage);
                        if (keywords != null) {
                            data.put("keywords", keywords);
                        }
                        sendSseEvent(writer, "phase", data);
                        if (logger.isDebugEnabled()) {
                            logger.debug("SSE phase start event sent. phase={}, message={}, keywords={}", phase, phaseMessage, keywords);
                        }
                    } catch (final Exception e) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Failed to send phase start event. phase={}, error={}", phase, e.getMessage());
                        }
                    }
                }

                @Override
                public void onPhaseComplete(final String phase) {
                    try {
                        sendSseEvent(writer, "phase", Map.of("phase", phase, "status", "complete"));
                        if (logger.isDebugEnabled()) {
                            logger.debug("SSE phase complete event sent. phase={}", phase);
                        }
                    } catch (final Exception e) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Failed to send phase complete event. phase={}, error={}", phase, e.getMessage());
                        }
                    }
                }

                @Override
                public void onChunk(final String content, final boolean done) {
                    try {
                        if (!done && content != null && !content.isEmpty()) {
                            sendSseEvent(writer, "chunk", Map.of("content", content));
                        }
                    } catch (final Exception e) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Failed to send SSE chunk. error={}", e.getMessage());
                        }
                    }
                }

                @Override
                public void onError(final String phase, final String errorCode) {
                    try {
                        sendSseEvent(writer, "error", Map.of("phase", phase, "message", errorCode, "errorCode", errorCode));
                        if (logger.isDebugEnabled()) {
                            logger.debug("SSE error event sent. phase={}, error={}", phase, errorCode);
                        }
                    } catch (final Exception e) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Failed to send error event. phase={}, error={}", phase, e.getMessage());
                        }
                    }
                }
            };

            // Parse filter parameters
            final Map<String, String[]> fields = parseFieldFilters(request);
            final String[] extraQueries = parseExtraQueries(request);

            // Stream the response using enhanced flow (use legacy method when no filters for backward compatibility)
            final ChatResult result;
            if (fields.isEmpty() && extraQueries.length == 0) {
                result = ComponentUtil.getChatClient().streamChatEnhanced(sessionId, message, userId, phaseCallback);
            } else {
                result = ComponentUtil.getChatClient().streamChatEnhanced(sessionId, message, userId, fields, extraQueries, phaseCallback);
            }

            // Send sources
            final List<ChatSource> sources = result.getMessage().getSources();
            if (sources != null && !sources.isEmpty()) {
                sendSseEvent(writer, "sources", Map.of("sources", sources));
                if (logger.isDebugEnabled()) {
                    logger.debug("SSE sources event sent. sourcesCount={}", sources.size());
                }
            }

            // Send completion event with HTML content
            final Map<String, Object> doneData = new HashMap<>();
            doneData.put("sessionId", result.getSessionId());
            final String htmlContent = result.getMessage().getHtmlContent();
            if (htmlContent != null) {
                doneData.put("htmlContent", htmlContent);
            }
            sendSseEvent(writer, "done", doneData);
            if (logger.isDebugEnabled()) {
                logger.debug("SSE stream completed. sessionId={}, hasHtmlContent={}", result.getSessionId(), htmlContent != null);
            }

        } catch (final LlmException e) {
            // LlmException from streamChatEnhanced already sent onError via callback - avoid double-send
            logger.warn("LLM error during stream request. sessionId={}, errorCode={}, message={}", sessionId, e.getErrorCode(),
                    e.getMessage(), e);
        } catch (final Exception e) {
            logger.warn("[RAG] Failed to process stream request. sessionId={}, message={}", sessionId, e.getMessage(), e);
            if (!response.isCommitted()) {
                try (final PrintWriter writer = response.getWriter()) {
                    sendSseEvent(writer, "error", Map.of("message", "Internal server error", "errorCode", LlmException.ERROR_UNKNOWN));
                } catch (final IOException ioe) {
                    logger.warn("Failed to send error response. error={}", ioe.getMessage());
                }
            }
        }
    }

    /**
     * Sends a Server-Sent Event (SSE) to the client.
     *
     * @param writer the print writer to write the event to
     * @param event the event name
     * @param data the event data to serialize as JSON
     */
    protected void sendSseEvent(final PrintWriter writer, final String event, final Map<String, Object> data) {
        try {
            writer.write("event: " + event + "\n");
            writer.write("data: " + objectMapper.writeValueAsString(data) + "\n\n");
            writer.flush();
        } catch (final JsonProcessingException e) {
            logger.warn("[RAG] Failed to serialize SSE data. event={}", event, e);
        }
    }

    /**
     * Writes a JSON response to the HTTP response.
     *
     * @param response the HTTP response
     * @param status the HTTP status code
     * @param data the data to serialize as JSON
     * @throws IOException if an I/O error occurs
     */
    protected void writeJsonResponse(final HttpServletResponse response, final int status, final Map<String, Object> data)
            throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(data));
    }

    /**
     * Creates a success response map.
     *
     * @param sessionId the session ID
     * @param content the response content
     * @param sources the list of chat sources
     * @return a map containing the success response data
     */
    protected Map<String, Object> createSuccessResponse(final String sessionId, final String content, final List<ChatSource> sources) {
        final Map<String, Object> result = new HashMap<>();
        result.put("status", "ok");
        result.put("sessionId", sessionId);
        result.put("content", content);
        if (sources != null) {
            result.put("sources", sources);
        }
        return result;
    }

    /**
     * Creates an error response map.
     *
     * @param message the error message
     * @return a map containing the error response data
     */
    protected Map<String, Object> createErrorResponse(final String message) {
        final Map<String, Object> result = new HashMap<>();
        result.put("status", "error");
        result.put("message", message);
        return result;
    }

    /**
     * Gets the user ID from the request.
     *
     * @param request the HTTP request
     * @return the user ID, or null if the user is a guest
     */
    protected String getUserId(final HttpServletRequest request) {
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        final String username = systemHelper.getUsername();
        if (!org.codelibs.fess.Constants.GUEST_USER.equals(username)) {
            return username;
        }
        // For guest users, use cookie-based userCode for session identification
        return ComponentUtil.getUserInfoHelper().getUserCode();
    }

    /**
     * Returns the maximum message length for chat messages.
     *
     * @param fessConfig the Fess configuration
     * @return the maximum message length
     */
    protected int getMaxMessageLength(final FessConfig fessConfig) {
        try {
            return Integer.parseInt(fessConfig.getOrDefault("rag.chat.message.max.length", "4000"));
        } catch (final NumberFormatException e) {
            logger.warn("Invalid rag.chat.message.max.length config, using default 4000");
            return 4000;
        }
    }

    /**
     * Parses and validates field filter parameters from the request.
     * Only configured label values are accepted to prevent query injection.
     *
     * @param request the HTTP request
     * @return a map of field names to their validated filter values
     */
    protected Map<String, String[]> parseFieldFilters(final HttpServletRequest request) {
        final Map<String, String[]> fields = new HashMap<>();
        final String[] labels = request.getParameterValues("fields.label");
        if (labels != null && labels.length > 0) {
            // Validate against configured label types (union of request locale and ROOT for robustness)
            final Locale requestLocale = request.getLocale() != null ? request.getLocale() : Locale.ROOT;
            final Set<String> allowedLabels = new java.util.HashSet<>();
            ComponentUtil.getLabelTypeHelper()
                    .getLabelTypeItemList(SearchRequestParams.SearchRequestType.SEARCH, requestLocale)
                    .stream()
                    .map(m -> m.get("value"))
                    .forEach(allowedLabels::add);
            if (!Locale.ROOT.equals(requestLocale)) {
                ComponentUtil.getLabelTypeHelper()
                        .getLabelTypeItemList(SearchRequestParams.SearchRequestType.SEARCH, Locale.ROOT)
                        .stream()
                        .map(m -> m.get("value"))
                        .forEach(allowedLabels::add);
            }
            final List<String> validLabels = new ArrayList<>();
            for (final String label : labels) {
                if (label != null && allowedLabels.contains(label)) {
                    validLabels.add(label);
                } else if (logger.isDebugEnabled()) {
                    logger.debug("Rejected unknown label filter value: {}", label);
                }
            }
            if (!validLabels.isEmpty()) {
                fields.put("label", validLabels.toArray(new String[0]));
            }
        }
        return fields;
    }

    /**
     * Parses and validates extra query parameters from the request.
     * Only configured facet query values are accepted to prevent query injection.
     *
     * @param request the HTTP request
     * @return an array of validated extra query strings
     */
    protected String[] parseExtraQueries(final HttpServletRequest request) {
        final String[] extraQueries = request.getParameterValues("ex_q");
        if (extraQueries == null || extraQueries.length == 0) {
            return new String[0];
        }
        // Build allowlist from configured facet queries
        final List<FacetQueryView> facetQueryViewList = ComponentUtil.getViewHelper().getFacetQueryViewList();
        final Set<String> allowedQueries = new java.util.HashSet<>();
        for (final FacetQueryView view : facetQueryViewList) {
            allowedQueries.addAll(view.getQueryMap().values());
        }
        final List<String> validQueries = new ArrayList<>();
        for (final String eq : extraQueries) {
            if (eq != null && allowedQueries.contains(eq)) {
                validQueries.add(eq);
            } else if (logger.isDebugEnabled()) {
                logger.debug("Rejected unknown extra query filter value: {}", eq);
            }
        }
        if (validQueries.isEmpty()) {
            return new String[0];
        }
        // Group validated queries by FacetQueryView and OR-join within the same group
        final Set<String> used = new java.util.HashSet<>();
        final List<String> groupedQueries = new ArrayList<>();
        for (final FacetQueryView view : facetQueryViewList) {
            final Set<String> viewValues = new java.util.HashSet<>(view.getQueryMap().values());
            final List<String> matched = new ArrayList<>();
            for (final String vq : validQueries) {
                if (viewValues.contains(vq)) {
                    matched.add(vq);
                    used.add(vq);
                }
            }
            if (matched.size() == 1) {
                groupedQueries.add(matched.get(0));
            } else if (matched.size() > 1) {
                groupedQueries.add(String.join(" OR ", matched));
            }
        }
        for (final String vq : validQueries) {
            if (!used.contains(vq)) {
                groupedQueries.add(vq);
            }
        }
        return groupedQueries.toArray(new String[0]);
    }

    @Override
    protected void writeHeaders(final HttpServletResponse response) {
        ComponentUtil.getFessConfig().getApiJsonResponseHeaderList().forEach(e -> response.setHeader(e.getFirst(), e.getSecond()));
    }
}
