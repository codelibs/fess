<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html>
<html>
<head profile="http://a9.com/-/spec/opensearch/1.1/">
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title><la:message key="labels.system_error_title" /></title>
<link href="${fe:url('/css/style-base.css')}" rel="stylesheet"
	type="text/css" />
<link href="${fe:url('/css/style.css')}" rel="stylesheet"
	type="text/css" />
<link href="${fe:url('/css/font-awesome.min.css')}"
	rel="stylesheet" type="text/css" />
</head>
<body class="error">
	<jsp:include page="../header.jsp" />
	<div class="container">
		<div class="content">
			<div class="center row">
				<div class="col-md-12">
					<h2>
						<la:message key="labels.page_not_found_title" />
					</h2>
					<div class="message">
						<la:message key="labels.check_url" />
						<br /> ${f:h(url)}
					</div>
				</div>
			</div>
		</div>
		<jsp:include page="../footer.jsp" />
	</div>
	<input type="hidden" id="contextPath" value="<%=request.getContextPath()%>" />
	<script type="text/javascript"
		src="${fe:url('/js/jquery-3.2.1.min.js')}"></script>
	<script type="text/javascript" src="${fe:url('/js/bootstrap.js')}"></script>
	<script type="text/javascript" src="${fe:url('/js/suggestor.js')}"></script>
	<script type="text/javascript" src="${fe:url('/js/search.js')}"></script>
</body>
</html>
