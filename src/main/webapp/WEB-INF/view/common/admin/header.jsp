<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<!-- Main Header -->
<header class="main-header">
	<!-- Logo -->
	<la:link href="/admin/" styleClass="logo">
		<span class="logo-mini"><img
			src="${f:url('/images/logo-head.png')}"
			alt="<la:message key="labels.header_brand_name" />" /></span>
		<span class="logo-lg"><img
			src="${f:url('/images/logo-head.png')}"
			alt="<la:message key="labels.header_brand_name" />" /></span>
	</la:link>
	<!-- Header Navbar -->
	<nav class="navbar navbar-static-top" role="navigation">
		<!-- Sidebar toggle button-->
		<a href="#" class="sidebar-toggle" data-toggle="offcanvas"
			role="button"> <span class="sr-only"><la:message
					key="labels.admin_toggle_navi" /></span>
		</a>
		<!-- Navbar Right Menu -->
		<div class="navbar-custom-menu">
			<ul class="nav navbar-nav">
				<li><a
					href="${contextPath}/admin/scheduler/details/4/default_crawler"><i
						class="fa fa-play-circle"></i></a></li>
				<li><a href="${helpLink}" target="_olh"><i
						class="fa fa-question-circle"></i></a></li>
				<li><a href="${contextPath}/logout"><i
						class="fa fa-sign-out"></i></a></li>
			</ul>
		</div>
	</nav>
</header>
