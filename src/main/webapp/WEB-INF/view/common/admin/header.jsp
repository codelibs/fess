<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<!-- Main Header -->
<header class="main-header">
	<!-- Logo -->
	<!-- Header Navbar -->
	<nav class="navbar navbar-expand navbar-white navbar-light" role="navigation">
		<!-- Sidebar toggle button-->
		<a href="#" class="fas sidebar-toggle"  data-toggle="push-menu"
			role="button"> <span class="sr-only"><la:message
					key="labels.admin_toggle_navi" /></span>
		</a>

		<!-- Navbar Right Menu -->
		<div class="collapse navbar-collapse">
			<ul class="navbar-nav">
				<li class="nav-item">
					<a data-widget="pushmenu" href="#"><em class="fa fa-bars"></em></a>
				</li>
			</ul>
			<ul class="navbar-nav ml-auto">
				<c:if test="${developmentMode}">
				<li class="nav-item" data-toggle="tooltip" data-placement="left" title="<la:message
					key="labels.development_mode_warning" />">
					<a  styleClass="nav-link active" href="${installationLink}" target="_olh"><em class="fa fa-exclamation-triangle"></em></a></li>
				</c:if>
				<li class="nav-item" ><a styleClass="nav-link" href="${contextPath}/"><em class="fa fa-list-alt"></em></a></li>
				<c:if test="${fe:permission('admin-scheduler')}">
				<li class="nav-item" ><a styleClass="nav-link"
					href="${contextPath}/admin/scheduler/details/4/default_crawler"><em
						class="fa fa-play-circle"></em></a></li>
				</c:if>
				<c:if test="${not empty helpLink}">
				<li class="nav-item" ><a styleClass="nav-link" href="${helpLink}" target="_olh"><em
						class="fa fa-question-circle"></em></a></li>
				</c:if>
				<li class="nav-item" ><a styleClass="nav-link" href="${contextPath}/logout"><em
						class="fa fa-sign-out-alt"></em></a></li>
			</ul>
		</div>
	</nav>
</header>
