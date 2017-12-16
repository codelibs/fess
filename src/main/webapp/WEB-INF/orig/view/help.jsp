<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html>
<html>
<head profile="http://a9.com/-/spec/opensearch/1.1/">
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title><la:message key="labels.search_title" /></title>
<link href="${fe:url('/css/style-base.css')}" rel="stylesheet"
	type="text/css" />
<link href="${fe:url('/css/style.css')}" rel="stylesheet" type="text/css" />
<link href="${fe:url('/css/font-awesome.min.css')}"
	rel="stylesheet" type="text/css" />
</head>
<body class="help">
	<jsp:include page="header.jsp" />
	<div class="container">
		<div class="row">
			<div class="col-md-12">

				<jsp:include page="${helpPage}" />

			</div>
		</div>
		<p class="pull-right move-to-top">
			<a href="#"><la:message key="labels.footer_back_to_top" /></a>
		</p>
		<jsp:include page="footer.jsp" />
	</div>
	<input type="hidden" id="contextPath" value="${contextPath}" />
	<script type="text/javascript"
		src="${fe:url('/js/jquery-3.2.1.min.js')}"></script>
	<script type="text/javascript" src="${fe:url('/js/bootstrap.js')}"></script>
	<script type="text/javascript" src="${fe:url('/js/suggestor.js')}"></script>
	<script type="text/javascript" src="${fe:url('/js/help.js')}"></script>
</body>
</html>
