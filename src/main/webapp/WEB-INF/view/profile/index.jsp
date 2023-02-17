<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head profile="http://a9.com/-/spec/opensearch/1.1/">
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title><la:message key="labels.profile.title" /></title>
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
		<div class="card">
			<div class="card-body login-card-body">
				<p class="login-box-msg">
					<la:message key="labels.profile" />
				</p>
				<%-- Message --%>
				<div>
					<la:info id="msg" message="false">
						<div class="alert alert-info">${msg}</div>
					</la:info>
					<la:errors />
				</div>
				<la:form styleId="updatePassword" method="post">
					<div class="input-group mb-3">
						<c:set var="ph_old_password">
							<la:message key="labels.profile.placeholder_old_password" />
						</c:set>
						<la:password property="oldPassword" class="form-control"
									 placeholder="${ph_old_password}" />
						<div class="input-group-append">
							<span class="input-group-text"><em class="fa fa-lock fa-fw"></em></span>
						</div>
					</div>
					<div class="input-group mb-3">
						<c:set var="ph_new_password">
							<la:message key="labels.profile.placeholder_new_password" />
						</c:set>
						<la:password property="newPassword" class="form-control"
									 placeholder="${ph_new_password}" />
						<div class="input-group-append">
							<span class="input-group-text"><em class="fa fa-lock fa-fw"></em></span>
						</div>
					</div>
					<div class="input-group mb-3">
						<c:set var="ph_confirm_password">
							<la:message key="labels.profile.placeholder_confirm_new_password" />
						</c:set>
						<la:password property="confirmNewPassword" class="form-control"
									 placeholder="${ph_confirm_password}" />
						<div class="input-group-append">
							<span class="input-group-text"><em class="fa fa-lock fa-fw"></em></span>
						</div>
					</div>
					<div class="text-center">
						<la:link href="/"
							styleClass="btn btn-default">
							<em class="fa fa-arrow-circle-left"></em>
							<la:message key="labels.profile.back" />
						</la:link>
						<button type="submit" name="changePassword"
							class="btn btn-success"
							value="<la:message key="labels.profile.update"/>">
							<em class="fa fa-pencil-alt"></em>
							<la:message key="labels.profile.update" />
						</button>
					</div>
				</la:form>
			</div>
		</div>
	</div>
	<input type="hidden" id="contextPath" value="${contextPath}" />
	<script src="${fe:url('/js/admin/popper.min.js')}" type="text/javascript"></script>
	<script type="text/javascript"
		src="${fe:url('/js/admin/jquery-3.6.3.min.js')}"></script>
	<script type="text/javascript" src="${fe:url('/js/admin/bootstrap.min.js')}"></script>
	<script type="text/javascript" src="${fe:url('/js/profile.js')}"></script>
</body>
</html>
