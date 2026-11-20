<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<a href="#mainContent" class="sr-only sr-only-focusable"><la:message
		key="labels.skip_to_main_content" /></a>
<header class="main-header navbar navbar-expand navbar-dark navbar-secondary">
	<ul class="navbar-nav">
		<li class="nav-item">
			<a class="nav-link" data-widget="pushmenu" href="#" role="button">
				<i class="fas fa-bars" aria-hidden="true"></i>
				<span class="sr-only"><la:message
						key="labels.admin_toggle_navi" /></span>
			</a>
		</li>
	</ul>
	<ul class="navbar-nav ml-auto">
		<c:if test="${eoled}">
		<li class="nav-item">
			<a class="nav-link active" href="${eolLink}" target="_olh" title="<la:message key="labels.eol_error" />" aria-label="<la:message key="labels.eol_error" />"><i class="fas fa-times-circle text-danger" aria-hidden="true"></i></a></li>
		</c:if>
		<li class="nav-item"><a class="nav-link" href="${contextPath}/" title="<la:message key="labels.tooltip_search_view" />" aria-label="<la:message key="labels.tooltip_search_view" />"><i class="fa fa-list-alt" aria-hidden="true"></i></a></li>
		<c:if test="${fe:permission('admin-scheduler')}">
			<li class="nav-item"><a class="nav-link"
								 href="${contextPath}/admin/scheduler/details/4/default_crawler" title="<la:message key="labels.tooltip_run_crawler" />" aria-label="<la:message key="labels.tooltip_run_crawler" />"><i class="fa fa-play-circle" aria-hidden="true"></i></a></li>
		</c:if>
		<c:if test="${not empty forumLink}">
			<li class="nav-item"><a class="nav-link" href="${forumLink}" target="_forum" title="<la:message key="labels.tooltip_forum" />" aria-label="<la:message key="labels.tooltip_forum" />"><i class="fas fa-comments" aria-hidden="true"></i></a></li>
		</c:if>
		<c:if test="${not empty helpLink}">
			<li class="nav-item"><a class="nav-link" href="${helpLink}" target="_olh" title="<la:message key="labels.tooltip_onlinehelp" />" aria-label="<la:message key="labels.tooltip_onlinehelp" />"><i class="fa fa-question-circle" aria-hidden="true"></i></a></li>
		</c:if>
		<li class="nav-item"><a class="nav-link" href="${contextPath}/logout" title="<la:message key="labels.tooltip_logout" />" aria-label="<la:message key="labels.tooltip_logout" />"><i class="fa fa-sign-out-alt" aria-hidden="true"></i></a></li>
	</ul>
</header>
