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
		<a href="#" class="sidebar-toggle" data-toggle="push-menu"
			role="button"> <span class="sr-only"><la:message
					key="labels.admin_toggle_navi" /></span>
		</a>
		<!-- Navbar Right Menu -->
		<div class="navbar-custom-menu">
			<ul class="nav navbar-nav">
				<c:if test="${developmentMode}">
				<li data-toggle="tooltip" data-placement="left" title="<la:message
					key="labels.development_mode_warning" />">
					<a href="${installationLink}" target="_olh"><i class="fa fa-exclamation-triangle"></i></a></li>
				</c:if>
				<li><a href="${contextPath}/"><i class="fa fa-list-alt"></i></a></li>
				<li><a
					href="${contextPath}/admin/scheduler/details/4/default_crawler"><i
						class="fa fa-play-circle"></i></a></li>
				<c:if test="${not empty helpLink}">
				<li><a href="${helpLink}" target="_olh"><i
						class="fa fa-question-circle"></i></a></li>
				</c:if>
				<li><a href="${contextPath}/logout"><i
						class="fa fa-sign-out"></i></a></li>
			</ul>
		</div>
	</nav>
</header>
