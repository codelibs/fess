<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
${fe:html(true)}
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title><la:message key="labels.login.title" /></title>
<link href="${fe:url('/css/tokens.css')}" rel="stylesheet" type="text/css" />
<link href="${fe:url('/css/fess-ads.css')}" rel="stylesheet" type="text/css" />
<link href="${fe:url('/css/font-awesome.min.css')}" rel="stylesheet" type="text/css" />
</head>
<body class="fads-auth-layout">
	<div class="fads-auth-card">
		<div class="fads-auth-logo">
			<la:link href="/">
				<img src="${fe:url('/images/logo-top.png')}"
					alt="<la:message key="labels.header_brand_name" />" />
			</la:link>
		</div>
		<div class="notification">${notification}</div>
		<div class="fads-card">
			<div class="fads-card-body">
				<p class="fads-auth-msg">
					<la:message key="labels.login" />
				</p>
				<%-- Message --%>
				<div>
					<la:info id="msg" message="false">
						<div class="fads-banner fads-banner-info">${msg}</div>
					</la:info>
					<la:errors />
				</div>
				<la:form styleId="login" method="post">
					<div class="fads-input-group" style="margin-bottom:var(--ds-space-200)">
						<c:set var="ph_username">
							<la:message key="labels.login.placeholder_username" />
						</c:set>
						<la:text property="username" styleId="username"
							styleClass="fads-textfield" placeholder="${ph_username}" />
						<span class="fads-input-group-text">
							<i class="fa fa-user fa-fw" aria-hidden="true"></i>
						</span>
					</div>
					<div class="fads-input-group" style="margin-bottom:var(--ds-space-200)">
						<c:set var="ph_password">
							<la:message key="labels.login.placeholder_password" />
						</c:set>
						<la:password property="password" styleClass="fads-textfield"
							placeholder="${ph_password}" />
						<span class="fads-input-group-text">
							<i class="fa fa-lock fa-fw" aria-hidden="true"></i>
						</span>
					</div>
					<div class="fads-text-center">
						<button type="submit" name="login"
							class="fads-btn fads-btn-primary fads-btn-block"
							value="<la:message key="labels.login"/>">
							<i class="fa fa-sign-in" aria-hidden="true"></i>
							<la:message key="labels.login" />
						</button>
					</div>
				</la:form>
			</div>
		</div>
	</div>
	<input type="hidden" id="contextPath" value="${contextPath}" />
	<script type="text/javascript" src="${fe:url('/js/jquery-3.7.1.min.js')}"></script>
	<script type="text/javascript" src="${fe:url('/js/fads-ui.js')}"></script>
	<script type="text/javascript" src="${fe:url('/js/login.js')}"></script>
</body>
${fe:html(false)}