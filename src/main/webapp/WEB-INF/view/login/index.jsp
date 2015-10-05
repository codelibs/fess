<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="content-style-type" content="text/css" />
<meta http-equiv="content-script-type" content="text/javascript" />
<title><la:message key="labels.login_title" /></title>
<link href="${f:url('/css/admin/style-base.css')}" rel="stylesheet"
	type="text/css" />
<link href="${f:url('/css/admin/style.css')}" rel="stylesheet"
	type="text/css" />
</head>
<body>
	<jsp:include page="header.jsp" />
	<div id="content-body" class="container">
		<div id="main" class="content" style="background-color:#fafafa;">
			<la:form styleId="login" method="post">
				<div class="row">
					<div class="modal" style="top:320px;">
						<div class="modal-header">
							<h3>
								<la:message key="labels.login" />
							</h3>
						</div>
						<div class="modal-body">
							<fieldset>
								<%-- Message --%>
								<div>
									<la:info id="msg" message="true">
										<div class="alert-message info">
											${msg}
										</div>
									</la:info>
									<la:errors />
								</div>
								<div class="clearfix">
									<label for="username"> <la:message
											key="labels.user_name" />
									</label>
									<div class="input">
										<la:text property="username" size="30" />
									</div>
								</div>
								<div class="clearfix">
									<label for="password"> <la:message
											key="labels.password" />
									</label>
									<div class="input">
										<la:password property="password" size="30" />
									</div>
								</div>
							</fieldset>
						</div>
						<div class="modal-footer">
							<div class="clearfix">
								<input type="submit" name="login"
										value="<la:message key="labels.login"/>"
										class="btn small primary" />
							</div>
						</div>
					</div>
				</div>
			</la:form>
		</div>
		<jsp:include page="footer.jsp" />
	</div>
</body>
</html>
