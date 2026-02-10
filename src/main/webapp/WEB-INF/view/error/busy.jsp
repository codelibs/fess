<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
${fe:html(true)}
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title><la:message key="labels.busy_title" /></title>
<link href="${fe:url('/css/bootstrap.min.css')}" rel="stylesheet"
	type="text/css" />
<link href="${fe:url('/css/style.css')}" rel="stylesheet"
	type="text/css" />
<link href="${fe:url('/css/font-awesome.min.css')}"
	rel="stylesheet" type="text/css" />
</head>
<body class="error">
	<jsp:include page="../header.jsp" />
	<main class="container">
		<div class="text-center">
			<h2>
				<la:message key="labels.busy_title" />
			</h2>
			<div class="errormessage"><la:message key="labels.busy_message" /></div>
		</div>
	</main>
	<jsp:include page="../footer.jsp" />
	<input type="hidden" id="contextPath" value="${pageContext.request.contextPath}" />
	<script type="text/javascript" src="${fe:url('/js/jquery-3.7.1.min.js')}"></script>
	<script type="text/javascript" src="${fe:url('/js/clipboard.min.js')}"></script>
	<script type="text/javascript" src="${fe:url('/js/popper.min.js')}"></script>
	<script type="text/javascript" src="${fe:url('/js/bootstrap.min.js')}"></script>
	<script type="text/javascript" src="${fe:url('/js/suggestor.js')}"></script>
	<script type="text/javascript" src="${fe:url('/js/search.js')}"></script>
</body>
${fe:html(false)}
