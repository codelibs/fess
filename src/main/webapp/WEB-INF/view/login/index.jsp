<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html>
<html>
<head profile="http://a9.com/-/spec/opensearch/1.1/">
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="x-ua-compatible" content="ie=edge">
<title><la:message key="labels.login_title" /></title>
<link href="${f:url('/css/style-base.css')}" rel="stylesheet" type="text/css" />
<link href="${f:url('/css/style.css')}" rel="stylesheet" type="text/css" />
</head>
<body>
	<jsp:include page="header.jsp" />
	<div id="content-body" class="container">
		<div id="main" class="content">
			<la:form styleId="login" method="post">
				<div class="row">
					<div class="modal m-t-lg" style="display: block">
						<div class="modal-dialog" role="document">
							<div class="modal-content">
								<div class="modal-header">
									<h4 class="modal-title">
										<la:message key="labels.login" />
									</h4>
								</div>
								<div class="modal-body">
									<%-- Message --%>
									<div class="form-group row">
										<la:info id="msg" message="true">
											<div class="alert-message info">${msg}</div>
										</la:info>
										<la:errors />
									</div>
									<div class="form-group row">
										<label for="username" class="col-sm-3 form-control-label"><la:message key="labels.user_name" /></label>
										<div class="col-sm-9">
											<la:text property="username" class="form-control" />
										</div>
									</div>
									<div class="form-group row">
										<label for="password" class="col-sm-3 form-control-label"><la:message key="labels.password" /></label>
										<div class="col-sm-9">
											<la:password property="password" class="form-control" />
										</div>
									</div>
									<div class="modal-footer">
										<input type="submit" name="login" value="<la:message key="labels.login"/>" class="btn btn-primary" />
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</la:form>
		</div>
		<jsp:include page="footer.jsp" />
	</div>
	<input type="hidden" id="contextPath" value="<%=request.getContextPath()%>" />
	<script type="text/javascript" src="${f:url('/js/jquery-2.1.4.min.js')}"></script>
	<script type="text/javascript" src="${f:url('/js/bootstrap.js')}"></script>
</body>
</html>
