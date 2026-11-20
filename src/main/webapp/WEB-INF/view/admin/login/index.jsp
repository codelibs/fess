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
					<la:message key="labels.login" />
				</p>
				<%-- Message --%>
				<div>
					<la:info id="msg" message="false">
						<div class="alert alert-info">${msg}</div>
					</la:info>
					<la:errors />
					<%-- Shown by js/login.js when a passkey login fails: the text of a failed password login. --%>
					<c:if test="${passkeyLogin}">
						<div id="passkey-error" class="d-none">
							<ul class="has-error" role="alert">
								<li><i class="fa fa-exclamation-circle"></i> <la:message key="errors.login_error" /></li>
							</ul>
						</div>
					</c:if>
				</div>
				<la:form action="/login/" styleId="login" method="post">
					<div class="input-group mb-3">
						<c:set var="ph_username">
							<la:message key="labels.login.placeholder_username" />
						</c:set>
						<label for="username" class="sr-only">${ph_username}</label>
						<la:text property="username" styleId="username"
							class="form-control" placeholder="${ph_username}"
							autocomplete="username" />
						<div class="input-group-append">
							<span class="input-group-text">
								<i class="fa fa-user fa-fw" aria-hidden="true"></i>
            				</span>
						</div>
					</div>
					<div class="input-group mb-3">
						<c:set var="ph_password">
							<la:message key="labels.login.placeholder_password" />
						</c:set>
						<label for="password" class="sr-only">${ph_password}</label>
						<la:password property="password" styleId="password"
							class="form-control" placeholder="${ph_password}"
							autocomplete="current-password" />
						<div class="input-group-append">
							<span class="input-group-text">
								<i class="fa fa-lock fa-fw" aria-hidden="true"></i>
							</span>
						</div>
					</div>
					<div class="text-center">
						<button type="submit" name="login"
							class="btn btn-primary btn-block"
							value="<la:message key="labels.login"/>">
							<i class="fa fa-sign-in" aria-hidden="true"></i>
							<la:message key="labels.login" />
						</button>
					</div>
				</la:form>
				<%-- Hidden until js/login.js finds WebAuthn in the browser. --%>
				<c:if test="${passkeyLogin}">
					<div class="text-center mt-3">
						<button type="button" id="passkey-login-btn"
							class="btn btn-outline-secondary btn-block d-none"
							data-redirect="<c:out value="${passkeyRedirectPath}" />">
							<i class="fa fa-key" aria-hidden="true"></i>
							<la:message key="labels.login_passkey" />
						</button>
					</div>
				</c:if>
			</div>
		</div>
	</div>
	<input type="hidden" id="contextPath" value="${contextPath}" />
	<script type="text/javascript" src="${fe:url('/js/login.js')}"></script>
</body>
${fe:html(false)}
