<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
${fe:html(true)}
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title><la:message key="labels.search_title" /></title>
<link href="${fe:url('/css/tokens.css')}" rel="stylesheet" type="text/css" />
<link href="${fe:url('/css/fess-ads.css')}" rel="stylesheet" type="text/css" />
<link href="${fe:url('/css/font-awesome.min.css')}"
	rel="stylesheet" type="text/css" />
</head>
<body>
	<jsp:include page="header.jsp" />
	<main class="container">
		<div class="fads-row">
			<div class="fads-col">

				<jsp:include page="${helpPage}" />

			</div>
		</div>
		<div class="fads-text-right">
			<a href="#"><la:message key="labels.footer_back_to_top" /></a>
		</div>
	</main>
	<jsp:include page="footer.jsp" />
	<input type="hidden" id="contextPath" value="${contextPath}" />
	<script type="text/javascript" src="${fe:url('/js/jquery-3.7.1.min.js')}"></script>
	<script type="text/javascript" src="${fe:url('/js/fads-ui.js')}"></script>
	<script type="text/javascript" src="${fe:url('/js/suggestor.js')}"></script>
	<script type="text/javascript" src="${fe:url('/js/help.js')}"></script>
</body>
${fe:html(false)}