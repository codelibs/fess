<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
${fe:html(true)}
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="color-scheme" content="light dark">
<title><la:message key="labels.login.title" /></title>
<link href="${fe:url('/css/admin/fontawesome/css/fontawesome.min.css')}" rel="stylesheet" type="text/css" />
<link href="${fe:url('/css/admin/fontawesome/css/solid.min.css')}" rel="stylesheet" type="text/css" />
<link href="${fe:url('/css/admin/fontawesome/css/regular.min.css')}" rel="stylesheet" type="text/css" />
<link href="${fe:url('/css/admin/admin.css')}" rel="stylesheet" type="text/css" />
<link href="${fe:url('/css/admin/style.css')}" rel="stylesheet" type="text/css" />
</head>
<body class="hold-transition login-page">
	<div class="login-box">
		<div class="login-logo">
			<a href="${fe:url('/')}">
				<img src="${fe:url('/images/logo-top.png')}"
					alt="<la:message key="labels.header_brand_name" />" />
			</a>
		</div>
		<div class="card">
			<div class="card-body login-card-body">
				<p class="login-box-msg">
					<la:message key="labels.login.newpassword" />
				</p>
				<%-- Message --%>
				<div>
					<la:info id="msg" message="false">
						<div class="alert alert-info">${msg}</div>
					</la:info>
					<la:errors />
				</div>
				<la:form action="/login/" styleId="newPassword" method="post">
					<div class="input-group mb-3">
						<c:set var="ph_new_password">
							<la:message key="labels.login.placeholder_new_password" />
						</c:set>
						<la:password autocomplete="new-password" property="password" class="form-control"
							placeholder="${ph_new_password}" />
						<div class="input-group-append">
							<span class="input-group-text">
								<i class="fa fa-lock fa-fw" aria-hidden="true"></i>
							</span>
						</div>
					</div>
					<div class="input-group mb-3">
						<c:set var="ph_confirm_password">
							<la:message key="labels.login.placeholder_confirm_new_password" />
						</c:set>
						<la:password autocomplete="new-password" property="confirmPassword" class="form-control"
							placeholder="${ph_confirm_password}" />
						<div class="input-group-append">
							<span class="input-group-text">
								<i class="fa fa-lock fa-fw" aria-hidden="true"></i>
							</span>
						</div>
					</div>
					<div class="text-center">
						<button type="submit" name="changePassword"
							class="btn btn-primary btn-block"
							value="<la:message key="labels.login.update"/>">
							<i class="fa fa-pencil-alt" aria-hidden="true"></i>
							<la:message key="labels.login.update" />
						</button>
					</div>
				</la:form>
			</div>
		</div>
	</div>
	<input type="hidden" id="contextPath" value="${contextPath}" />
	<script type="text/javascript" src="${fe:url('/js/login.js')}"></script>
</body>
${fe:html(false)}
