<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<nav class="fads-topnav">
	<button class="fads-btn-icon fads-sidenav-toggle" aria-label="<la:message key="labels.admin_toggle_navi" />">
		<i class="fas fa-bars" aria-hidden="true"></i>
	</button>
	<div class="fads-d-flex fads-align-center fads-ms-auto" style="gap:var(--ds-space-050)">
		<c:if test="${eoled}">
		<a class="fads-btn-icon" href="${eolLink}" target="_olh"
			data-fads-tooltip="<la:message key="labels.eol_error" />" data-placement="left"><i class="fas fa-times-circle fads-text-danger" aria-hidden="true"></i></a>
		</c:if>
		<c:if test="${developmentMode}">
			<a class="fads-btn-icon" href="${installationLink}" target="_olh"
				data-fads-tooltip="<la:message key="labels.development_mode_warning" />" data-placement="left"><i class="fa fa-exclamation-triangle fads-text-warning" aria-hidden="true"></i></a>
		</c:if>
		<a class="fads-btn-icon" href="${contextPath}/"
			data-fads-tooltip="<la:message key="labels.tooltip_search_view" />" data-placement="left" aria-label="<la:message key="labels.tooltip_search_view" />"><i class="fa fa-list-alt" aria-hidden="true"></i></a>
		<c:if test="${fe:permission('admin-scheduler')}">
			<a class="fads-btn-icon" href="${contextPath}/admin/scheduler/details/4/default_crawler"
				data-fads-tooltip="<la:message key="labels.tooltip_run_crawler" />" data-placement="left" aria-label="<la:message key="labels.tooltip_run_crawler" />"><i class="fa fa-play-circle" aria-hidden="true"></i></a>
		</c:if>
		<c:if test="${not empty forumLink}">
			<a class="fads-btn-icon" href="${forumLink}" target="_forum"
				data-fads-tooltip="<la:message key="labels.tooltip_forum" />" data-placement="left" aria-label="<la:message key="labels.tooltip_forum" />"><i class="fas fa-comments" aria-hidden="true"></i></a>
		</c:if>
		<c:if test="${not empty helpLink}">
			<a class="fads-btn-icon" href="${helpLink}" target="_olh"
				data-fads-tooltip="<la:message key="labels.tooltip_onlinehelp" />" data-placement="left" aria-label="<la:message key="labels.tooltip_onlinehelp" />"><i class="fa fa-question-circle" aria-hidden="true"></i></a>
		</c:if>
		<button class="fads-btn-icon fads-theme-toggle" aria-label="Toggle theme">
			<i class="fas fa-moon"></i>
		</button>
		<a class="fads-btn-icon" href="${contextPath}/logout"
			data-fads-tooltip="<la:message key="labels.tooltip_logout" />" data-placement="left" aria-label="<la:message key="labels.tooltip_logout" />"><i class="fa fa-sign-out-alt" aria-hidden="true"></i></a>
	</div>
</nav>
