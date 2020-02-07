<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<!-- Navbar -->
<nav class="main-header navbar navbar-expand navbar-white navbar-light">
	<!-- Left navbar links -->
	<ul class="navbar-nav">
		<li class="nav-item">
			<a class="nav-link" data-widget="pushmenu" href="#">
				<em class="fas fa-bars"></em>
				<span class="sr-only"><la:message
						key="labels.admin_toggle_navi" /></span>
			</a>
		</li>
	</ul>

	<!-- Right navbar links -->
	<ul class="navbar-nav ml-auto">
		<c:if test="${eoled}">
		<li class="nav-item" data-toggle="tooltip" data-placement="left" title="<la:message
			key="labels.eol_error" />">
			<a class="nav-link active" href="${eolLink}" target="_olh"><em class="fas fa-times-circle" style="color:yellow;"></em></a></li>
		</c:if>
		<c:if test="${developmentMode}">
			<li class="nav-item" data-toggle="tooltip" data-placement="left" title="<la:message
				key="labels.development_mode_warning" />">
				<a  class="nav-link active" href="${installationLink}" target="_olh"><em class="fa fa-exclamation-triangle" style="color:yellow;"></em></a></li>
		</c:if>
		<li class="nav-item" ><a class="nav-link" href="${contextPath}/"><em class="fa fa-list-alt"></em></a></li>
		<c:if test="${fe:permission('admin-scheduler')}">
			<li class="nav-item" ><a class="nav-link"
									 href="${contextPath}/admin/scheduler/details/4/default_crawler"><em
					class="fa fa-play-circle"></em></a></li>
		</c:if>
		<c:if test="${not empty helpLink}">
			<li class="nav-item" ><a class="nav-link" href="${helpLink}" target="_olh"><em
					class="fa fa-question-circle"></em></a></li>
		</c:if>
		<li class="nav-item" ><a class="nav-link" href="${contextPath}/logout"><em
				class="fa fa-sign-out-alt"></em></a></li>
	</ul>
</nav>
<!-- /.navbar -->
