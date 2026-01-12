/**
 * Fess RAG Chat JavaScript
 */
var FessChat = (function() {
    'use strict';

    var config = {
        apiUrl: '/api/v1/chat',
        streamUrl: '/api/v1/chat/stream',
        labels: {
            thinking: 'Thinking...',
            waiting: '...',
            error: 'An error occurred. Please try again.',
            sources: 'Sources'
        }
    };

    var state = {
        sessionId: null,
        isProcessing: false,
        eventSource: null
    };

    var elements = {};

    /**
     * Initialize the chat module
     */
    function init(options) {
        $.extend(true, config, options);

        elements = {
            chatMessages: $('#chatMessages'),
            chatInput: $('#chatInput'),
            sendBtn: $('#sendBtn'),
            newChatBtn: $('#newChatBtn'),
            statusArea: $('#statusArea')
        };

        bindEvents();
        autoResizeTextarea();
        showStatus('ready');
    }

    /**
     * Bind event handlers
     */
    function bindEvents() {
        elements.sendBtn.on('click', sendMessage);

        elements.chatInput.on('keydown', function(e) {
            // Prevent sending during IME composition (Japanese, Chinese, etc.)
            if (e.key === 'Enter' && !e.shiftKey && !e.isComposing) {
                e.preventDefault();
                sendMessage();
            }
        });

        // Also handle compositionend for older browsers
        elements.chatInput.on('compositionstart', function() {
            elements.chatInput.data('composing', true);
        });
        elements.chatInput.on('compositionend', function() {
            elements.chatInput.data('composing', false);
        });

        elements.chatInput.on('input', autoResizeTextarea);

        elements.newChatBtn.on('click', newChat);
    }

    /**
     * Auto-resize textarea based on content
     */
    function autoResizeTextarea() {
        var textarea = elements.chatInput[0];
        textarea.style.height = 'auto';
        textarea.style.height = Math.min(textarea.scrollHeight, 150) + 'px';
    }

    /**
     * Send a message
     */
    function sendMessage() {
        // Check for IME composition
        if (elements.chatInput.data('composing')) {
            return;
        }

        var message = elements.chatInput.val().trim();

        if (!message || state.isProcessing) {
            return;
        }

        state.isProcessing = true;
        updateUI();
        showStatus('thinking');

        // Add user message
        addMessage('user', message);

        // Clear input
        elements.chatInput.val('');
        autoResizeTextarea();

        // Add thinking indicator
        var thinkingId = addThinkingIndicator();

        // Use SSE for streaming
        streamChat(message, thinkingId);
    }

    /**
     * Stream chat using Server-Sent Events
     */
    function streamChat(message, thinkingId) {
        var url = config.streamUrl + '?message=' + encodeURIComponent(message);
        if (state.sessionId) {
            url += '&sessionId=' + encodeURIComponent(state.sessionId);
        }

        var eventSource = new EventSource(url);
        var responseContent = '';
        var messageElement = null;

        eventSource.onopen = function() {
            // Remove thinking indicator and create message element with waiting text
            $('#' + thinkingId).remove();
            messageElement = addMessage('assistant', config.labels.waiting, true);
        };

        eventSource.addEventListener('session', function(e) {
            var data = JSON.parse(e.data);
            if (data.sessionId) {
                state.sessionId = data.sessionId;
            }
        });

        eventSource.addEventListener('chunk', function(e) {
            var data = JSON.parse(e.data);
            if (data.content) {
                responseContent += data.content;
                if (messageElement) {
                    messageElement.find('.message-text').text(responseContent);
                    scrollToBottom();
                }
            }
        });

        eventSource.addEventListener('sources', function(e) {
            var data = JSON.parse(e.data);
            if (data.sources && data.sources.length > 0 && messageElement) {
                addSourcesToMessage(messageElement, data.sources);
            }
        });

        eventSource.addEventListener('done', function(e) {
            var data = JSON.parse(e.data);
            state.sessionId = data.sessionId;
            state.isProcessing = false;
            updateUI();
            showStatus('ready');
            eventSource.close();
            scrollToBottom();
        });

        eventSource.addEventListener('error', function(e) {
            var errorMessage = config.labels.error;
            try {
                var data = JSON.parse(e.data);
                if (data.message) {
                    errorMessage = data.message;
                }
            } catch (ex) {}

            $('#' + thinkingId).remove();
            if (messageElement) {
                messageElement.find('.message-text').text(errorMessage);
            } else {
                addMessage('assistant', errorMessage);
            }

            state.isProcessing = false;
            updateUI();
            showStatus('error');
            eventSource.close();
        });

        eventSource.onerror = function() {
            $('#' + thinkingId).remove();
            if (!messageElement) {
                addMessage('assistant', config.labels.error);
            }
            state.isProcessing = false;
            updateUI();
            showStatus('error');
            eventSource.close();
        };

        state.eventSource = eventSource;
    }

    /**
     * Add a message to the chat
     */
    function addMessage(role, content, streaming) {
        var avatarIcon = role === 'user' ? 'fa-user' : 'fa-robot';
        var html =
            '<div class="chat-message ' + role + '">' +
                '<div class="message-avatar"><i class="fa ' + avatarIcon + '"></i></div>' +
                '<div class="message-content">' +
                    '<div class="message-text">' + escapeHtml(content) + '</div>' +
                '</div>' +
            '</div>';

        var element = $(html);
        elements.chatMessages.append(element);
        scrollToBottom();

        return element;
    }

    /**
     * Validates and sanitizes a URL to prevent javascript: and other dangerous protocols
     */
    function sanitizeUrl(url) {
        if (!url || typeof url !== 'string') {
            return '#';
        }
        var trimmedUrl = url.trim().toLowerCase();
        // Only allow http, https, and relative URLs
        if (trimmedUrl.startsWith('http://') || trimmedUrl.startsWith('https://') || trimmedUrl.startsWith('/')) {
            return url;
        }
        // Block javascript:, data:, vbscript:, and other potentially dangerous protocols
        if (trimmedUrl.indexOf(':') !== -1) {
            return '#';
        }
        return url;
    }

    /**
     * Add sources to a message
     */
    function addSourcesToMessage(messageElement, sources) {
        var html = '<div class="message-sources"><h6>' + config.labels.sources + '</h6><ul class="source-list">';

        for (var i = 0; i < sources.length; i++) {
            var source = sources[i];
            var title = source.title || source.url || ('Source ' + (i + 1));
            var url = sanitizeUrl(source.url);
            html += '<li>' +
                '<a href="' + escapeHtml(url) + '" class="source-link" target="_blank" rel="noopener noreferrer">' +
                    '<span class="source-index">' + (i + 1) + '</span>' +
                    escapeHtml(title) +
                '</a>' +
            '</li>';
        }

        html += '</ul></div>';
        messageElement.find('.message-content').append(html);
    }

    /**
     * Add thinking indicator
     */
    function addThinkingIndicator() {
        var id = 'thinking-' + Date.now();
        var html =
            '<div id="' + id + '" class="chat-message assistant">' +
                '<div class="message-avatar"><i class="fa fa-robot"></i></div>' +
                '<div class="thinking-indicator">' +
                    config.labels.thinking +
                    '<div class="thinking-dots"><span></span><span></span><span></span></div>' +
                '</div>' +
            '</div>';

        elements.chatMessages.append(html);
        scrollToBottom();
        return id;
    }

    /**
     * Start a new chat
     */
    function newChat() {
        if (state.sessionId) {
            // Clear session on server
            $.post(config.apiUrl, {
                sessionId: state.sessionId,
                clear: 'true'
            });
        }
        state.sessionId = null;
        elements.chatMessages.empty();
        showStatus('ready');
    }

    /**
     * Show status message
     */
    function showStatus(status) {
        if (!elements.statusArea) return;

        var text = '';
        var cssClass = 'status-ready';

        switch (status) {
            case 'thinking':
                text = config.labels.thinking || 'Thinking...';
                cssClass = 'status-thinking';
                break;
            case 'error':
                text = config.labels.error || 'Error occurred';
                cssClass = 'status-error';
                break;
            case 'ready':
            default:
                text = config.labels.statusReady || 'AI Assistant';
                cssClass = 'status-ready';
                break;
        }

        elements.statusArea.removeClass('status-ready status-thinking status-error').addClass(cssClass);
        elements.statusArea.html('<i class="fa fa-robot me-2"></i>' + escapeHtml(text));
    }

    /**
     * Update UI state
     */
    function updateUI() {
        elements.sendBtn.prop('disabled', state.isProcessing);
        elements.chatInput.prop('disabled', state.isProcessing);
    }

    /**
     * Scroll chat to bottom
     */
    function scrollToBottom() {
        elements.chatMessages.scrollTop(elements.chatMessages[0].scrollHeight);
    }

    /**
     * Escape HTML characters
     */
    function escapeHtml(text) {
        if (!text) return '';
        var div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    // Public API
    return {
        init: init
    };
})();
