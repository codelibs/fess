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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.api.BaseApiManager;
import org.codelibs.fess.chat.ChatClient.ChatResult;
import org.codelibs.fess.chat.ChatPhaseCallback;
import org.codelibs.fess.entity.ChatMessage.ChatSource;
import org.codelibs.fess.helper.SystemHelper;
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
            if (logger.isDebugEnabled()) {
                logger.debug("ChatApiManager.matches() returning false. ragChatEnabled={}", ragChatEnabled);
            }
            return false;
        }

        final String servletPath = request.getServletPath();
        final boolean matches = servletPath.startsWith(CHAT_API_PATH);
        if (logger.isDebugEnabled()) {
            logger.debug("ChatApiManager.matches() checking path. servletPath={}, expectedPrefix={}, matches={}", servletPath,
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
                    ComponentUtil.getChatSessionManager().clearSession(sessionId);
                    if (logger.isDebugEnabled()) {
                        logger.debug("Session cleared. sessionId={}", sessionId);
                    }
                    writeJsonResponse(response, HttpServletResponse.SC_OK, createSuccessResponse(sessionId, "Session cleared", null));
                    return;
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("Message is required but was empty");
                }
                writeJsonResponse(response, HttpServletResponse.SC_BAD_REQUEST, createErrorResponse("Message is required"));
                return;
            }

            final String userId = getUserId(request);
            final ChatResult result = ComponentUtil.getChatClient().chat(sessionId, message, userId);

            if (logger.isDebugEnabled()) {
                logger.debug("Chat request completed. sessionId={}, responseLength={}", result.getSessionId(),
                        result.getMessage().getContent() != null ? result.getMessage().getContent().length() : 0);
            }

            writeJsonResponse(response, HttpServletResponse.SC_OK,
                    createSuccessResponse(result.getSessionId(), result.getMessage().getContent(), result.getMessage().getSources()));

        } catch (final Exception e) {
            logger.warn("Failed to process chat request. message={}", e.getMessage(), e);
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
        if (!"POST".equalsIgnoreCase(request.getMethod()) && !"GET".equalsIgnoreCase(request.getMethod())) {
            if (logger.isDebugEnabled()) {
                logger.debug("Invalid method for stream request. method={}", request.getMethod());
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

        // Set SSE headers
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Connection", "keep-alive");
        response.setHeader("X-Accel-Buffering", "no"); // Disable nginx buffering

        try (final PrintWriter writer = response.getWriter()) {
            final String userId = getUserId(request);

            // Send initial session info
            sendSseEvent(writer, "session", Map.of("sessionId", sessionId != null ? sessionId : ""));
            if (logger.isDebugEnabled()) {
                logger.debug("SSE session event sent. sessionId={}", sessionId);
            }

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
                        if (!done && StringUtil.isNotBlank(content)) {
                            sendSseEvent(writer, "chunk", Map.of("content", content));
                        }
                    } catch (final Exception e) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Failed to send SSE chunk. error={}", e.getMessage());
                        }
                    }
                }

                @Override
                public void onError(final String phase, final String errorMessage) {
                    try {
                        sendSseEvent(writer, "error", Map.of("phase", phase, "message", errorMessage));
                        if (logger.isDebugEnabled()) {
                            logger.debug("SSE error event sent. phase={}, error={}", phase, errorMessage);
                        }
                    } catch (final Exception e) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Failed to send error event. phase={}, error={}", phase, e.getMessage());
                        }
                    }
                }
            };

            // Stream the response using enhanced flow
            final ChatResult result = ComponentUtil.getChatClient().streamChatEnhanced(sessionId, message, userId, phaseCallback);

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

        } catch (final Exception e) {
            logger.warn("Failed to process stream request. sessionId={}, message={}", sessionId, e.getMessage(), e);
            if (!response.isCommitted()) {
                try (final PrintWriter writer = response.getWriter()) {
                    sendSseEvent(writer, "error", Map.of("message", "Internal server error"));
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
            logger.warn("Error serializing SSE data", e);
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
        return org.codelibs.fess.Constants.GUEST_USER.equals(username) ? null : username;
    }

    @Override
    protected void writeHeaders(final HttpServletResponse response) {
        ComponentUtil.getFessConfig().getApiJsonResponseHeaderList().forEach(e -> response.setHeader(e.getFirst(), e.getSecond()));
    }
}
