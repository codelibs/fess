<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="content-style-type" content="text/css" />
<meta http-equiv="content-script-type" content="text/javascript" />
<title><bean:message key="labels.login_title" /></title>
<link href="${f:url('/css/admin/style-base.css')}" rel="stylesheet"
	type="text/css" />
<link href="${f:url('/css/admin/style.css')}" rel="stylesheet"
	type="text/css" />
</head>
<body>
	<jsp:include page="header.jsp" />
	<div id="content-body" class="container">
		<div id="main" class="content" style="background-color:#fafafa;">
			<form id="login" method="post"
				action="<%=response.encodeURL("j_security_check")%>">
				<div class="row">
					<div class="modal" style="top:320px;">
						<div class="modal-header">
							<h3>
								<bean:message key="labels.login" />
							</h3>
						</div>
						<div class="modal-body">
							<fieldset>
								<%
									if ("error.login_error".equals(request.getParameter("msgs"))) {
								%>
								<div class="alert-message error">
									<p>
										<bean:message key="error.login_error" />
									</p>
								</div>
								<%
									}
								%>
								<div class="clearfix">
									<label for="j_username"> <bean:message
											key="labels.user_name" />
									</label>
									<div class="input">
										<input type="text" name="j_username" size="30" />
									</div>
								</div>
								<div class="clearfix">
									<label for="j_username"> <bean:message
											key="labels.password" />
									</label>
									<div class="input">
										<input type="password" name="j_password" size="30" />
									</div>
								</div>
							</fieldset>
						</div>
						<div class="modal-footer">
							<div class="clearfix">
								<input type="submit" name="loginButton"
										value="<bean:message key="labels.login"/>"
										class="btn small primary" />
							</div>
						</div>
					</div>
				</div>
			</form>
		</div>
		<jsp:include page="footer.jsp" />
	</div>
</body>
</html>
