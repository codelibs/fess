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
        if (!fessConfig.isRagChatEnabled()) {
            return false;
        }

        final String servletPath = request.getServletPath();
        return servletPath.startsWith(CHAT_API_PATH);
    }

    @Override
    public void process(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        final String servletPath = request.getServletPath();

        if (servletPath.equals(STREAM_API_PATH) || servletPath.equals(STREAM_API_PATH + "/")) {
            processStreamRequest(request, response);
        } else if (servletPath.equals(CHAT_API_PATH) || servletPath.equals(CHAT_API_PATH + "/")) {
            processChatRequest(request, response);
        } else {
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

            // Stream the response
            final ChatResult result = ComponentUtil.getChatClient().streamChat(sessionId, message, userId, (chunk, done) -> {
                try {
                    if (!done && StringUtil.isNotBlank(chunk)) {
                        sendSseEvent(writer, "chunk", Map.of("content", chunk));
                    }
                } catch (final Exception e) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Failed to send SSE chunk. error={}", e.getMessage());
                    }
                }
            });

            // Send sources
            final List<ChatSource> sources = result.getMessage().getSources();
            if (sources != null && !sources.isEmpty()) {
                sendSseEvent(writer, "sources", Map.of("sources", sources));
                if (logger.isDebugEnabled()) {
                    logger.debug("SSE sources event sent. sourcesCount={}", sources.size());
                }
            }

            // Send completion event
            sendSseEvent(writer, "done", Map.of("sessionId", result.getSessionId()));
            if (logger.isDebugEnabled()) {
                logger.debug("SSE stream completed. sessionId={}", result.getSessionId());
            }

        } catch (final Exception e) {
            logger.warn("Failed to process stream request. message={}", e.getMessage(), e);
            try (final PrintWriter writer = response.getWriter()) {
                sendSseEvent(writer, "error", Map.of("message", "Internal server error"));
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
