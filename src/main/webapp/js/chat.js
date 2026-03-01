/**
 * Fess RAG Chat JavaScript
 * Enhanced with Atlassian Design System patterns
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
            sources: 'Sources',
            statusReady: 'AI Assistant',
            statusThinking: 'Processing',
            statusError: 'Error',
            copied: 'Copied!',
            copyFailed: 'Copy failed',
            phases: {
                intent: 'Analyzing question...',
                search: 'Searching documents...',
                evaluate: 'Evaluating results...',
                fetch: 'Retrieving content...',
                answer: 'Generating answer...'
            }
        }
    };

    var state = {
        sessionId: null,
        isProcessing: false,
        eventSource: null,
        currentPhase: null,
        completedPhases: [],
        lastMessage: null,
        lastError: null
    };

    var elements = {};

    var phaseOrder = ['intent', 'search', 'evaluate', 'fetch', 'answer'];

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
            statusArea: $('#statusArea'),
            emptyState: $('#emptyState'),
            progressIndicator: $('#progressIndicator'),
            progressMessage: $('#progressMessage'),
            errorBanner: $('#errorBanner'),
            charCount: $('#charCount')
        };

        bindEvents();
        autoResizeTextarea();
        updateCharCount();
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

        // Handle IME composition for older browsers
        elements.chatInput.on('compositionstart', function() {
            elements.chatInput.data('composing', true);
        });
        elements.chatInput.on('compositionend', function() {
            elements.chatInput.data('composing', false);
        });

        elements.chatInput.on('input', function() {
            autoResizeTextarea();
            updateCharCount();
        });

        elements.newChatBtn.on('click', newChat);

        // Suggestion chip click handlers
        $('.suggestion-chip').on('click', function() {
            var suggestion = $(this).data('suggestion');
            if (suggestion) {
                elements.chatInput.val(suggestion);
                updateCharCount();
                autoResizeTextarea();
                sendMessage();
            }
        });

        // Error banner handlers
        elements.errorBanner.find('.error-banner-retry').on('click', function() {
            hideErrorBanner();
            if (state.lastMessage) {
                elements.chatInput.val(state.lastMessage);
                updateCharCount();
                sendMessage();
            }
        });

        elements.errorBanner.find('.error-banner-dismiss').on('click', function() {
            hideErrorBanner();
        });

        // Message action delegation
        elements.chatMessages.on('click', '.copy-btn', function() {
            var messageContent = $(this).closest('.chat-message').find('.message-text');
            copyToClipboard(messageContent.text(), $(this));
        });
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
     * Update character counter
     */
    function updateCharCount() {
        var count = elements.chatInput.val().length;
        var maxLength = 4000;
        elements.charCount.text(count);

        var counter = elements.charCount.parent();
        counter.removeClass('warning danger');
        if (count >= maxLength * 0.95) {
            counter.addClass('danger');
        } else if (count >= maxLength * 0.8) {
            counter.addClass('warning');
        }
    }

    /**
     * Copy text to clipboard
     */
    function copyToClipboard(text, button) {
        if (navigator.clipboard && navigator.clipboard.writeText) {
            navigator.clipboard.writeText(text).then(function() {
                showCopySuccess(button);
            }).catch(function() {
                showCopyError(button);
            });
        } else {
            // Fallback for older browsers
            var textarea = document.createElement('textarea');
            textarea.value = text;
            textarea.style.position = 'fixed';
            textarea.style.opacity = '0';
            document.body.appendChild(textarea);
            textarea.select();
            try {
                document.execCommand('copy');
                showCopySuccess(button);
            } catch (err) {
                showCopyError(button);
            }
            document.body.removeChild(textarea);
        }
    }

    function showCopySuccess(button) {
        var originalHtml = button.html();
        button.addClass('copied').html('<i class="fa fa-check"></i> ' + config.labels.copied);
        setTimeout(function() {
            button.removeClass('copied').html(originalHtml);
        }, 2000);
    }

    function showCopyError(button) {
        var originalHtml = button.html();
        button.html('<i class="fa fa-times"></i> ' + config.labels.copyFailed);
        setTimeout(function() {
            button.html(originalHtml);
        }, 2000);
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
        state.lastMessage = message;
        state.currentPhase = null;
        state.completedPhases = [];

        hideEmptyState();
        hideErrorBanner();
        updateUI();
        showStatus('thinking');
        showProgressIndicator();

        // Add user message
        addMessage('user', message);

        // Clear input
        elements.chatInput.val('');
        updateCharCount();
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

        eventSource.addEventListener('phase', function(e) {
            var data = JSON.parse(e.data);
            if (data.phase) {
                if (data.status === 'start') {
                    updatePhase(data.phase, 'active');
                    var phaseMessage = config.labels.phases[data.phase] || data.message || 'Processing...';
                    // Replace __keywords__ placeholder with actual keywords
                    if (data.keywords) {
                        phaseMessage = phaseMessage.replace('__keywords__', data.keywords);
                    }
                    showStatus('thinking', phaseMessage);
                    updateProgressMessage(phaseMessage);
                    // Update message-text element with phase progress
                    if (messageElement) {
                        messageElement.find('.message-text').text(phaseMessage);
                        scrollToBottom();
                    }
                } else if (data.status === 'complete') {
                    updatePhase(data.phase, 'completed');
                }
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

            // Replace streaming text with rendered HTML content if available
            if (data.htmlContent && messageElement) {
                messageElement.find('.message-text').html(data.htmlContent);
            }

            // Add message actions
            if (messageElement) {
                addMessageActions(messageElement);
            }

            state.isProcessing = false;
            state.lastError = null;
            updateUI();
            showStatus('ready');
            hideProgressIndicator();
            eventSource.close();
            scrollToBottom();

            // Focus back to input
            elements.chatInput.focus();
        });

        eventSource.addEventListener('error', function(e) {
            var errorMessage = config.labels.error;
            try {
                var data = JSON.parse(e.data);
                if (data.message) {
                    errorMessage = data.message;
                }
            } catch (ex) {}

            handleError(thinkingId, messageElement, errorMessage);
            eventSource.close();
        });

        eventSource.onerror = function() {
            handleError(thinkingId, messageElement, config.labels.error);
            eventSource.close();
        };

        state.eventSource = eventSource;
    }

    /**
     * Handle error state
     */
    function handleError(thinkingId, messageElement, errorMessage) {
        $('#' + thinkingId).remove();

        // Remove the assistant message if it only contains waiting/phase text
        if (messageElement) {
            messageElement.remove();
        }

        state.lastError = errorMessage;
        state.isProcessing = false;
        updateUI();
        showStatus('error');
        hideProgressIndicator();
        showErrorBanner(errorMessage);
    }

    /**
     * Show error banner
     */
    function showErrorBanner(message) {
        elements.errorBanner.find('.error-message').text(message);
        elements.errorBanner.removeClass('d-none');
    }

    /**
     * Hide error banner
     */
    function hideErrorBanner() {
        elements.errorBanner.addClass('d-none');
    }

    /**
     * Update phase indicator
     */
    function updatePhase(phase, status) {
        if (status === 'active') {
            state.currentPhase = phase;
        } else if (status === 'completed') {
            if (state.completedPhases.indexOf(phase) === -1) {
                state.completedPhases.push(phase);
            }
        }

        // Update visual indicators
        $('.progress-step').each(function() {
            var stepPhase = $(this).data('phase');
            $(this).removeClass('active completed');

            if (state.completedPhases.indexOf(stepPhase) !== -1) {
                $(this).addClass('completed');
            } else if (stepPhase === state.currentPhase) {
                $(this).addClass('active');
            }
        });
    }

    /**
     * Update progress message
     */
    function updateProgressMessage(message) {
        elements.progressMessage.text(message);
    }

    /**
     * Show progress indicator
     */
    function showProgressIndicator() {
        // Reset all steps
        $('.progress-step').removeClass('active completed');
        elements.progressMessage.text('');
        elements.progressIndicator.removeClass('d-none');
    }

    /**
     * Hide progress indicator
     */
    function hideProgressIndicator() {
        elements.progressIndicator.addClass('d-none');
        state.currentPhase = null;
        state.completedPhases = [];
    }

    /**
     * Hide empty state
     */
    function hideEmptyState() {
        elements.emptyState.hide();
    }

    /**
     * Show empty state
     */
    function showEmptyState() {
        elements.emptyState.show();
    }

    /**
     * Format timestamp
     */
    function formatTimestamp(date) {
        var hours = date.getHours();
        var minutes = date.getMinutes();
        var ampm = hours >= 12 ? 'PM' : 'AM';
        hours = hours % 12;
        hours = hours ? hours : 12;
        minutes = minutes < 10 ? '0' + minutes : minutes;
        return hours + ':' + minutes + ' ' + ampm;
    }

    /**
     * Add a message to the chat
     */
    function addMessage(role, content, streaming) {
        var avatarIcon = role === 'user' ? 'fa-user' : 'fa-robot';
        var timestamp = formatTimestamp(new Date());

        var html =
            '<div class="chat-message ' + role + '">' +
                '<div class="message-avatar"><i class="fa ' + avatarIcon + '" aria-hidden="true"></i></div>' +
                '<div class="message-wrapper">' +
                    '<div class="message-content">' +
                        '<div class="message-text">' + escapeHtml(content) + '</div>' +
                    '</div>' +
                    '<div class="message-timestamp">' + timestamp + '</div>' +
                '</div>' +
            '</div>';

        var element = $(html);
        elements.chatMessages.append(element);
        scrollToBottom();

        return element;
    }

    /**
     * Add message actions (copy button)
     */
    function addMessageActions(messageElement) {
        var actionsHtml =
            '<div class="message-actions">' +
                '<button type="button" class="message-action-btn copy-btn" aria-label="Copy message">' +
                    '<i class="fa fa-copy" aria-hidden="true"></i> Copy' +
                '</button>' +
            '</div>';

        messageElement.find('.message-wrapper').append(actionsHtml);
    }

    /**
     * Validates and sanitizes a URL to prevent javascript: and other dangerous protocols
     */
    function sanitizeUrl(url) {
        if (!url || typeof url !== 'string') {
            return '#';
        }
        var trimmedUrl = url.trim().toLowerCase();
        // Allow http, https, and absolute path URLs
        if (trimmedUrl.startsWith('http://') || trimmedUrl.startsWith('https://') || trimmedUrl.startsWith('/')) {
            return url;
        }
        // Allow relative URLs starting with ./ or ../
        if (trimmedUrl.startsWith('./') || trimmedUrl.startsWith('../')) {
            return url;
        }
        // Block known dangerous protocols
        var dangerousProtocols = ['javascript:', 'data:', 'vbscript:', 'file:', 'about:', 'blob:'];
        for (var i = 0; i < dangerousProtocols.length; i++) {
            if (trimmedUrl.startsWith(dangerousProtocols[i])) {
                return '#';
            }
        }
        // Block URLs that look like protocol:// (unknown protocols)
        if (/^[a-z][a-z0-9+.-]*:/i.test(trimmedUrl)) {
            return '#';
        }
        // Allow other relative URLs (may contain colons in path/query)
        return url;
    }

    /**
     * Get file type icon based on URL or mimetype
     */
    function getFileTypeIcon(url, mimetype) {
        if (mimetype) {
            if (mimetype.indexOf('pdf') !== -1) return 'fa-file-pdf-o';
            if (mimetype.indexOf('word') !== -1 || mimetype.indexOf('document') !== -1) return 'fa-file-word-o';
            if (mimetype.indexOf('excel') !== -1 || mimetype.indexOf('spreadsheet') !== -1) return 'fa-file-excel-o';
            if (mimetype.indexOf('powerpoint') !== -1 || mimetype.indexOf('presentation') !== -1) return 'fa-file-powerpoint-o';
            if (mimetype.indexOf('image') !== -1) return 'fa-file-image-o';
            if (mimetype.indexOf('text') !== -1) return 'fa-file-text-o';
        }

        if (url) {
            var ext = url.split('.').pop().toLowerCase().split('?')[0];
            switch (ext) {
                case 'pdf': return 'fa-file-pdf-o';
                case 'doc': case 'docx': return 'fa-file-word-o';
                case 'xls': case 'xlsx': return 'fa-file-excel-o';
                case 'ppt': case 'pptx': return 'fa-file-powerpoint-o';
                case 'jpg': case 'jpeg': case 'png': case 'gif': return 'fa-file-image-o';
                case 'txt': case 'md': return 'fa-file-text-o';
                case 'html': case 'htm': return 'fa-globe';
            }
        }

        return 'fa-file-o';
    }

    /**
     * Get file type label
     */
    function getFileTypeLabel(url, mimetype) {
        if (mimetype) {
            if (mimetype.indexOf('pdf') !== -1) return 'PDF';
            if (mimetype.indexOf('word') !== -1) return 'Word';
            if (mimetype.indexOf('excel') !== -1) return 'Excel';
            if (mimetype.indexOf('powerpoint') !== -1) return 'PowerPoint';
        }

        if (url) {
            var ext = url.split('.').pop().toLowerCase().split('?')[0];
            switch (ext) {
                case 'pdf': return 'PDF';
                case 'doc': case 'docx': return 'Word';
                case 'xls': case 'xlsx': return 'Excel';
                case 'ppt': case 'pptx': return 'PowerPoint';
                case 'html': case 'htm': return 'Web';
            }
        }

        return 'Document';
    }

    /**
     * Add sources to a message (card style)
     */
    function addSourcesToMessage(messageElement, sources) {
        var html = '<div class="message-sources"><h6>' + escapeHtml(config.labels.sources) + '</h6><ul class="source-list">';

        for (var i = 0; i < sources.length; i++) {
            var source = sources[i];
            var title = source.title || source.url || ('Source ' + (i + 1));
            var url = sanitizeUrl(source.url);
            var icon = getFileTypeIcon(source.url, source.mimetype);
            var typeLabel = getFileTypeLabel(source.url, source.mimetype);

            html += '<li>' +
                '<a href="' + escapeHtml(url) + '" class="source-card" target="_blank" rel="noopener noreferrer">' +
                    '<span class="source-index">' + (i + 1) + '</span>' +
                    '<div class="source-info">' +
                        '<span class="source-title">' + escapeHtml(title) + '</span>' +
                        '<div class="source-meta">' +
                            '<span class="source-type"><i class="fa ' + icon + '" aria-hidden="true"></i> ' + typeLabel + '</span>' +
                        '</div>' +
                    '</div>' +
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
                '<div class="message-avatar"><i class="fa fa-robot" aria-hidden="true"></i></div>' +
                '<div class="thinking-indicator">' +
                    escapeHtml(config.labels.thinking) +
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
        // Close any active EventSource connection
        if (state.eventSource) {
            state.eventSource.close();
            state.eventSource = null;
        }

        // Reset processing state
        state.isProcessing = false;

        if (state.sessionId) {
            // Clear session on server
            $.post(config.apiUrl, {
                sessionId: state.sessionId,
                clear: 'true'
            });
        }
        state.sessionId = null;
        state.lastMessage = null;
        state.lastError = null;
        state.currentPhase = null;
        state.completedPhases = [];
        elements.chatMessages.find('.chat-message').remove();
        showEmptyState();
        hideErrorBanner();
        hideProgressIndicator();
        showStatus('ready');
        updateUI();  // Re-enable buttons and input
    }

    /**
     * Show status message
     * @param {string} status - Status type (thinking, error, ready)
     * @param {string} customMessage - Optional custom message to display
     */
    function showStatus(status, customMessage) {
        if (!elements.statusArea) return;

        var text = '';
        var cssClass = 'status-lozenge status-ready';

        switch (status) {
            case 'thinking':
                text = customMessage || config.labels.statusThinking || 'Processing';
                cssClass = 'status-lozenge status-thinking';
                break;
            case 'error':
                text = config.labels.statusError || 'Error';
                cssClass = 'status-lozenge status-error';
                break;
            case 'ready':
            default:
                text = config.labels.statusReady || 'AI Assistant';
                cssClass = 'status-lozenge status-ready';
                break;
        }

        elements.statusArea.attr('class', cssClass);
        elements.statusArea.html('<i class="fa fa-robot me-1" aria-hidden="true"></i><span class="status-text">' + escapeHtml(text) + '</span>');
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
