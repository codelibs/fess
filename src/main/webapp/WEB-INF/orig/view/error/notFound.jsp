<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="content-style-type" content="text/css" />
<meta http-equiv="content-script-type" content="text/javascript" />
<title><bean:message key="labels.system_error_title" /></title>
<link href="${f:url('/css/style-base.css')}" rel="stylesheet"
	type="text/css" />
<link href="${f:url('/css/style.css')}" rel="stylesheet"
	type="text/css" />
</head>
<body>
	<jsp:include page="header.jsp" />
	<div class="container">
		<div class="content">
			<div class="center row">
				<div class="span10">
					<h2>
						<bean:message key="labels.page_not_found_title" />
					</h2>
					<div class="message">
						<bean:message key="labels.check_url" />
						<br /> ${f:h(url)}
					</div>
				</div>
			</div>
		</div>
		<jsp:include page="footer.jsp" />
	</div>
	<script type="text/javascript"
		src="${f:url('/js/jquery-1.11.0.min.js')}"></script>
	<script type="text/javascript" src="${f:url('/js/bootstrap.js')}"></script>
</body>
</html>
