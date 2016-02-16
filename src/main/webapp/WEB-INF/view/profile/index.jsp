<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html>
<html>
<head profile="http://a9.com/-/spec/opensearch/1.1/">
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="x-ua-compatible" content="ie=edge">
<title><la:message key="labels.profile.title" /></title>
<link href="${f:url('/css/style-base.css')}" rel="stylesheet"
	type="text/css" />
<link href="${f:url('/css/style.css')}" rel="stylesheet" type="text/css" />
<link href="${f:url('/css/admin/style.css')}" rel="stylesheet"
	type="text/css" />
<link href="${f:url('/css/admin/font-awesome.min.css')}"
	rel="stylesheet" type="text/css" />
<link href="${f:url('/css/admin/AdminLTE.min.css')}" rel="stylesheet"
	type="text/css" />
<link href="${f:url('/css/admin/skins/skin-blue.min.css')}"
	rel="stylesheet" type="text/css" />
<!--[if lt IE 9]>
<script src="${f:url('/css/admin/html5shiv.min.js')}"></script>
<script src="${f:url('/css/admin/respond.min.js')}"></script>
<![endif]-->
</head>
<body class="hold-transition login-page">
	<div class="login-box">
		<div class="login-logo">
			<la:link href="/">
				<img src="${f:url('/images/logo-top.png')}"
					alt="<la:message key="labels.header_brand_name" />" />
			</la:link>
		</div>
		<!-- /.login-logo -->
		<div class="login-box-body">
			<p class="login-box-msg">
				<la:message key="labels.profile" />
			</p>
			<p>
				Username: ${username}
			</p>
			<%-- Message --%>
			<div>
				<la:info id="msg" message="false">
					<div class="alert alert-info">${msg}</div>
				</la:info>
				<la:errors />
			</div>
			<la:form styleId="login" method="post">
				<div class="form-group has-feedback">
					<div class="input-group">
						<span class="input-group-addon"><i class="fa fa-lock fa-fw"></i></span>
						<c:set var="ph_old_password">
							<la:message key="labels.profile.placeholder_old_password" />
						</c:set>
						<la:password property="oldPassword" class="form-control"
							placeholder="${ph_old_password}" />
					</div>
				</div>
				<div class="form-group has-feedback">
					<div class="input-group">
						<span class="input-group-addon"><i class="fa fa-lock fa-fw"></i></span>
						<c:set var="ph_new_password">
							<la:message key="labels.profile.placeholder_new_password" />
						</c:set>
						<la:password property="newPassword" class="form-control"
							     placeholder="${ph_new_password}" />
					</div>
				</div>
				<div class="form-group has-feedback">
					<div class="input-group">
						<span class="input-group-addon"><i class="fa fa-lock fa-fw"></i></span>
						<c:set var="ph_confirm_password">
							<la:message key="labels.profile.placeholder_confirm_new_password" />
						</c:set>
						<la:password property="confirmPassword" class="form-control"
							     placeholder="${ph_confirm_password}" />
					</div>
				</div>
				<div class="row">
					<div class="col-xs-3"></div>
					<!-- /.col -->
					<div class="col-xs-6">
						<button type="submit" name="changePassword"
							class="btn btn-primary btn-block btn-flat"
							value="<la:message key="labels.profile.update"/>">
							<i class="fa fa-sign-in"></i>
							<la:message key="labels.profile.update" />
						</button>
					</div>
					<!-- /.col -->
					<div class="col-xs-3"></div>
					<!-- /.col -->
				</div>
			</la:form>
		</div>
		<!-- /.login-box-body -->
	</div>
	<!-- /.login-box -->

	<jsp:include page="footer.jsp" />

	<input type="hidden" id="contextPath" value="${contextPath}" />
	<script type="text/javascript"
		src="${f:url('/js/jquery-2.1.4.min.js')}"></script>
	<script type="text/javascript" src="${f:url('/js/bootstrap.js')}"></script>
	<script type="text/javascript" src="${f:url('/js/admin/admin.js')}"></script>
</body>
</html>
