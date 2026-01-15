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
						<div id="statusArea" class="status-ready">
							<i class="fa fa-robot me-2" aria-hidden="true"></i><la:message key="labels.chat_status_ready" />
						</div>
						<button type="button" id="newChatBtn" class="btn btn-outline-secondary btn-sm">
							<i class="fa fa-plus" aria-hidden="true"></i>
							<la:message key="labels.chat_new_chat" />
						</button>
					</div>
					<div class="card-body p-0">
						<div id="chatMessages" class="chat-messages">
						</div>
					</div>
					<div class="card-footer">
						<div class="input-group">
							<textarea id="chatInput" class="form-control"
								placeholder="<la:message key="labels.chat_input_placeholder" />"
								rows="1" maxlength="4000"></textarea>
							<button type="button" id="sendBtn" class="btn btn-primary">
								<i class="fa fa-paper-plane" aria-hidden="true"></i>
							</button>
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
