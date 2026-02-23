<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<nav class="main-header navbar navbar-expand navbar-dark navbar-secondary">
	<ul class="navbar-nav">
		<li class="nav-item">
			<a class="nav-link" data-widget="pushmenu" href="#">
				<i class="fas fa-bars" aria-hidden="true"></i>
				<span class="sr-only"><la:message
						key="labels.admin_toggle_navi" /></span>
			</a>
		</li>
	</ul>
	<ul class="navbar-nav ml-auto">
		<c:if test="${eoled}">
		<li class="nav-item" data-toggle="tooltip" data-placement="left"
			title="<la:message key="labels.eol_error" />">
			<a class="nav-link active" href="${eolLink}" target="_olh"><i class="fas fa-times-circle text-danger" aria-hidden="true"></i></a></li>
		</c:if>
		<c:if test="${developmentMode}">
			<li class="nav-item" data-toggle="tooltip" data-placement="left"
				title="<la:message key="labels.development_mode_warning" />">
				<a class="nav-link active" href="${installationLink}" target="_olh"><i class="fa fa-exclamation-triangle text-warning" aria-hidden="true"></i></a></li>
		</c:if>
		<li class="nav-item" data-toggle="tooltip" data-placement="left"
			title="<la:message key="labels.tooltip_search_view" />"><a class="nav-link" href="${contextPath}/" aria-label="<la:message key="labels.tooltip_search_view" />"><i class="fa fa-list-alt" aria-hidden="true"></i></a></li>
		<c:if test="${fe:permission('admin-scheduler')}">
			<li class="nav-item" data-toggle="tooltip" data-placement="left"
				title="<la:message key="labels.tooltip_run_crawler" />"><a class="nav-link"
								 href="${contextPath}/admin/scheduler/details/4/default_crawler" aria-label="<la:message key="labels.tooltip_run_crawler" />"><i class="fa fa-play-circle" aria-hidden="true"></i></a></li>
		</c:if>
		<c:if test="${not empty forumLink}">
			<li class="nav-item" data-toggle="tooltip" data-placement="left"
				title="<la:message key="labels.tooltip_forum" />"><a class="nav-link" href="${forumLink}" target="_forum" aria-label="<la:message key="labels.tooltip_forum" />"><i class="fas fa-comments" aria-hidden="true"></i></a></li>
		</c:if>
		<c:if test="${not empty helpLink}">
			<li class="nav-item" data-toggle="tooltip" data-placement="left"
				title="<la:message key="labels.tooltip_onlinehelp" />"><a class="nav-link" href="${helpLink}" target="_olh" aria-label="<la:message key="labels.tooltip_onlinehelp" />"><i class="fa fa-question-circle" aria-hidden="true"></i></a></li>
		</c:if>
		<li class="nav-item" data-toggle="tooltip" data-placement="left"
			title="<la:message key="labels.tooltip_logout" />"><a class="nav-link" href="${contextPath}/logout" aria-label="<la:message key="labels.tooltip_logout" />"><i class="fa fa-sign-out-alt" aria-hidden="true"></i></a></li>
	</ul>
</nav>
