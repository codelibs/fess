<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="content-style-type" content="text/css" />
<meta http-equiv="content-script-type" content="text/javascript" />
<title><la:message key="labels.logout_title" /></title>
<link href="${f:url('/css/admin/style-base.css')}" rel="stylesheet"
	type="text/css" />
<link href="${f:url('/css/admin/style.css')}" rel="stylesheet"
	type="text/css" />
</head>
<body>
	<jsp:include page="header.jsp" />
	<div class="container">
		<div class="content">
			<div class="center row">
				<div class="col-md-12">
					<h2>
						<la:message key="labels.logout" />
					</h2>
					<div class="message">
						<la:message key="labels.do_you_want_to_logout" />
					</div>
					<div class="action">
						<la:form>
						<input type="submit" name="logout"
							value="<la:message key="labels.logout_button"/>" />
						</la:form>
					</div>
				</div>
			</div>
		</div>
		<jsp:include page="footer.jsp" />
	</div>
</body>
</html>
