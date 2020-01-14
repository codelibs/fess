<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<!-- Main Header -->
<header class="main-header">
	<!-- Logo -->
	<la:link href="/admin/" styleClass="logo hidden-xs">
		<span class="logo-lg"><img
			src="${fe:url('/images/logo-head.png')}"
			alt="<la:message key="labels.header_brand_name" />" /></span>
	</la:link>
	<!-- Header Navbar -->
	<nav class="navbar navbar-static-top" role="navigation">
		<!-- Sidebar toggle button-->
		<a href="#" class="fas sidebar-toggle" data-toggle="push-menu"
			role="button"> <span class="sr-only"><la:message
					key="labels.admin_toggle_navi" /></span>
		</a>
		<!-- Navbar Right Menu -->
		<div class="navbar-custom-menu">
			<ul class="nav navbar-nav">
				<c:if test="${eoled}">
				<li data-toggle="tooltip" data-placement="left" title="<la:message
					key="labels.eol_error" />">
					<a href="${eolLink}" target="_olh"><em class="fas fa-times-circle" style="color:yellow;"></em></a></li>
				</c:if>
				<c:if test="${developmentMode}">
				<li data-toggle="tooltip" data-placement="left" title="<la:message
					key="labels.development_mode_warning" />">
					<a href="${installationLink}" target="_olh"><em class="fa fa-exclamation-triangle" style="color:yellow;"></em></a></li>
				</c:if>
				<li><a href="${contextPath}/"><em class="fa fa-list-alt"></em></a></li>
				<li><a
					href="${contextPath}/admin/scheduler/details/4/default_crawler"><em
						class="fa fa-play-circle"></em></a></li>
				<c:if test="${not empty helpLink}">
				<li><a href="${helpLink}" target="_olh"><em
						class="fa fa-question-circle"></em></a></li>
				</c:if>
				<li><a href="${contextPath}/logout"><em
						class="fa fa-sign-out-alt"></em></a></li>
			</ul>
		</div>
	</nav>
</header>
