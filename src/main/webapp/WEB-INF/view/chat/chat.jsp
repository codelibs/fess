<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
${fe:html(true)}
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title><la:message key="labels.chat_title" /></title>
<link href="${fe:url('/css/bootstrap.min.css')}" rel="stylesheet" type="text/css" />
<link href="${fe:url('/css/style.css')}" rel="stylesheet" type="text/css" />
<link href="${fe:url('/css/font-awesome.min.css')}" rel="stylesheet" type="text/css" />
<link href="${fe:url('/css/chat.css')}" rel="stylesheet" type="text/css" />
</head>
<body>
	<jsp:include page="../header.jsp" />
	<main class="container">
		<div class="row">
			<div class="col-12 col-lg-10 offset-lg-1">
				<div class="card shadow-sm">
					<div class="card-header d-flex justify-content-between align-items-center">
						<div id="statusArea" class="status-lozenge status-ready" role="status" aria-live="polite">
							<i class="fa fa-robot me-1" aria-hidden="true"></i>
							<span class="status-text"><la:message key="labels.chat_status_ready" /></span>
						</div>
						<button type="button" id="newChatBtn" class="btn btn-outline-secondary btn-sm" aria-label="<la:message key="labels.chat_new_chat" />">
							<i class="fa fa-plus" aria-hidden="true"></i>
							<la:message key="labels.chat_new_chat" />
						</button>
					</div>
					<div class="card-body p-0">
						<div id="chatMessages" class="chat-messages" role="log" aria-live="polite" aria-label="<la:message key="labels.chat_messages_area" />">
							<div id="emptyState" class="empty-state">
								<div class="empty-state-icon">
									<i class="fa fa-comments" aria-hidden="true"></i>
								</div>
								<h5 class="empty-state-title"><la:message key="labels.chat_welcome_title" /></h5>
								<p class="empty-state-description"><la:message key="labels.chat_welcome_description" /></p>
							</div>
						</div>
						<div id="progressIndicator" class="progress-indicator d-none" role="status" aria-live="polite">
							<div class="progress-steps">
								<div class="progress-step" data-phase="intent">
									<div class="step-icon"><i class="fa fa-lightbulb-o" aria-hidden="true"></i></div>
									<span class="step-label"><la:message key="labels.chat_step_intent" /></span>
								</div>
								<div class="progress-step" data-phase="search">
									<div class="step-icon"><i class="fa fa-search" aria-hidden="true"></i></div>
									<span class="step-label"><la:message key="labels.chat_step_search" /></span>
								</div>
								<div class="progress-step" data-phase="evaluate">
									<div class="step-icon"><i class="fa fa-check-circle-o" aria-hidden="true"></i></div>
									<span class="step-label"><la:message key="labels.chat_step_evaluate" /></span>
								</div>
								<div class="progress-step" data-phase="fetch">
									<div class="step-icon"><i class="fa fa-file-text-o" aria-hidden="true"></i></div>
									<span class="step-label"><la:message key="labels.chat_step_fetch" /></span>
								</div>
								<div class="progress-step" data-phase="answer">
									<div class="step-icon"><i class="fa fa-pencil" aria-hidden="true"></i></div>
									<span class="step-label"><la:message key="labels.chat_step_answer" /></span>
								</div>
							</div>
							<div class="progress-message" id="progressMessage"></div>
						</div>
					</div>
					<div class="card-footer">
						<div id="errorBanner" class="error-banner d-none" role="alert">
							<div class="error-banner-content">
								<i class="fa fa-exclamation-triangle me-2" aria-hidden="true"></i>
								<span class="error-message"></span>
							</div>
							<button type="button" class="error-banner-retry btn btn-sm btn-outline-light">
								<i class="fa fa-refresh me-1" aria-hidden="true"></i><la:message key="labels.chat_retry" />
							</button>
							<button type="button" class="error-banner-dismiss btn-close btn-close-white" aria-label="<la:message key="labels.chat_dismiss" />"></button>
						</div>
						<div class="input-wrapper">
							<div class="input-group">
								<textarea id="chatInput" class="form-control"
									placeholder="<la:message key="labels.chat_input_placeholder" />"
									rows="1" maxlength="4000"
									aria-label="<la:message key="labels.chat_input_placeholder" />"></textarea>
								<button type="button" id="sendBtn" class="btn btn-primary" aria-label="<la:message key="labels.chat_send" />">
									<i class="fa fa-paper-plane" aria-hidden="true"></i>
								</button>
							</div>
							<div class="input-footer">
								<span class="input-hint"><la:message key="labels.chat_input_hint" /></span>
								<span class="char-counter"><span id="charCount">0</span> / 4000</span>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</main>
	<jsp:include page="../footer.jsp" />
	<input type="hidden" id="contextPath" value="${contextPath}" />
	<script type="text/javascript" src="${fe:url('/js/jquery-3.7.1.min.js')}"></script>
	<script type="text/javascript" src="${fe:url('/js/popper.min.js')}"></script>
	<script type="text/javascript" src="${fe:url('/js/bootstrap.min.js')}"></script>
	<script type="text/javascript" src="${fe:url('/js/chat.js')}"></script>
	<script type="text/javascript">
		$(function() {
			FessChat.init({
				apiUrl: '${fe:url('/api/v1/chat')}',
				streamUrl: '${fe:url('/api/v1/chat/stream')}',
				labels: {
					thinking: '<la:message key="labels.chat_thinking" />',
					waiting: '<la:message key="labels.chat_waiting" />',
					error: '<la:message key="labels.chat_error" />',
					sources: '<la:message key="labels.chat_sources" />',
					statusReady: '<la:message key="labels.chat_status_ready" />',
					statusThinking: '<la:message key="labels.chat_status_thinking" />',
					statusError: '<la:message key="labels.chat_status_error" />',
					copied: '<la:message key="labels.chat_copied" />',
					copyFailed: '<la:message key="labels.chat_copy_failed" />',
					phases: {
						intent: '<la:message key="labels.chat_phase_intent" />',
						search: '<la:message key="labels.chat_phase_search" />',
						evaluate: '<la:message key="labels.chat_phase_evaluate" />',
						fetch: '<la:message key="labels.chat_phase_fetch" />',
						answer: '<la:message key="labels.chat_phase_answer" />'
					}
				}
			});
		});
	</script>
</body>
${fe:html(false)}
