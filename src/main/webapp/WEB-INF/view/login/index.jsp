<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head profile="http://a9.com/-/spec/opensearch/1.1/">
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title><la:message key="labels.login.title" /></title>
<link href="${fe:url('/css/admin/bootstrap.min.css')}" rel="stylesheet" type="text/css" />
<link href="${fe:url('/css/admin/font-awesome.min.css')}" rel="stylesheet" type="text/css" />
<link href="${fe:url('/css/admin/adminlte.min.css')}" rel="stylesheet" type="text/css" />
<link href="${fe:url('/css/admin/style.css')}" rel="stylesheet" type="text/css" />
<!--[if lt IE 9]>
<script src="${fe:url('/css/admin/html5shiv.min.js')}"></script>
<script src="${fe:url('/css/admin/respond.min.js')}"></script>
<![endif]-->
</head>
<body class="hold-transition login-page">
	<div class="login-box">
		<div class="login-logo">
			<la:link href="/">
				<img src="${fe:url('/images/logo-top.png')}"
					alt="<la:message key="labels.header_brand_name" />" />
			</la:link>
		</div>
		<div class="notification">${notification}</div>
		<div class="card">
			<div class="card-body login-card-body">
				<p class="login-box-msg">
					<la:message key="labels.login" />
				</p>
				<%-- Message --%>
				<div>
					<la:info id="msg" message="false">
						<div class="alert alert-info">${msg}</div>
					</la:info>
					<la:errors />
				</div>
				<la:form styleId="login" method="post">
					<div class="input-group mb-3">
						<c:set var="ph_username">
							<la:message key="labels.login.placeholder_username" />
						</c:set>
						<la:text property="username" styleId="username"
							class="form-control" placeholder="${ph_username}" />
						<div class="input-group-append">
							<span class="input-group-text">
								<em class="fa fa-user fa-fw"></em>
            				</span>
						</div>
					</div>
					<div class="input-group mb-3">
						<c:set var="ph_password">
							<la:message key="labels.login.placeholder_password" />
						</c:set>
						<la:password property="password" class="form-control"
							placeholder="${ph_password}" />
						<div class="input-group-append">
							<span class="input-group-text">
								<em class="fa fa-lock fa-fw"></em>
							</span>
						</div>
					</div>
					<div class="text-center">
						<button type="submit" name="login"
							class="btn btn-primary btn-block"
							value="<la:message key="labels.login"/>">
							<em class="fa fa-sign-in"></em>
							<la:message key="labels.login" />
						</button>
					</div>
				</la:form>
			</div>
		</div>
	</div>
	<input type="hidden" id="contextPath" value="${contextPath}" />
	<script type="text/javascript" src="${fe:url('/js/admin/popper.min.js')}"></script>
	<script type="text/javascript"
		src="${fe:url('/js/admin/jquery-3.6.3.min.js')}"></script>
	<script type="text/javascript" src="${fe:url('/js/admin/bootstrap.min.js')}"></script>
	<script type="text/javascript" src="${fe:url('/js/login.js')}"></script>
</body>
</html>
