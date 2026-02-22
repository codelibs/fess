<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<aside class="fads-sidenav">
	<div class="fads-sidenav-brand">
		<la:link href="/admin/">
		<c:if test="${empty param.brandName or empty param.logoPath}"><img src="${fe:url('/images/logo-head.png')}" alt="<la:message key="labels.header_brand_name" />" /></c:if
		><c:if test="${not empty param.brandName and not empty param.logoPath}"><img src="${fe:url(param.logoPath)}" alt="${f:h(param.brandName)}" /></c:if>
		</la:link>
	</div>
	<c:if test="${fe:permission('admin-searchlist-view')}">
		<form action="<%=request.getContextPath()%>/admin/searchlist/search" method="GET" class="fads-sidenav-search">
			<div class="fads-input-group">
				<input class="fads-textfield" name="q" id="query"
					   maxlength="1000"
					   placeholder="<la:message key="labels.sidebar.placeholder_search" />">
				<button class="fads-btn fads-btn-default" type="submit" name="search" id="search-btn">
					<i class="fa fa-search" aria-hidden="true"></i>
				</button>
			</div>
		</form>
	</c:if>
	<nav>
	<ul class="fads-sidenav-list">
		<li class="fads-sidenav-header"><la:message key="labels.sidebar.menu" /></li>
		<c:if test="${fe:permission('admin-dashboard-view')}">
			<li class="fads-sidenav-item">
				<a class="fads-sidenav-link <c:if test="${param.menuCategoryType=='dashboard'}">active</c:if>" <c:if test="${param.menuCategoryType=='dashboard'}">aria-current="page"</c:if>
				   href="${fe:url('/admin/dashboard/')}">
					<i class="fa fa-tachometer-alt" aria-hidden="true"></i>
					<span><la:message key="labels.menu_dashboard_config" /></span>
				</a>
			</li>
		</c:if>

		<c:if test="${fe:permission('admin-wizard-view') or fe:permission('admin-general-view') or fe:permission('admin-scheduler-view') or fe:permission('admin-design-view') or fe:permission('admin-dict-view') or fe:permission('admin-accesstoken-view') or fe:permission('admin-plugin-view') or fe:permission('admin-storage-view')}">
		<li class="fads-sidenav-item" aria-expanded="${param.menuCategoryType=='system' ? 'true' : 'false'}">
			<a href="#" class="fads-sidenav-link <c:if test="${param.menuCategoryType=='system'}">active</c:if>">
				<i class="fa fa-laptop" aria-hidden="true"></i>
				<span><la:message key="labels.menu_system" /></span>
				<i class="fas fa-angle-left" style="margin-left:auto" aria-hidden="true"></i>
			</a>
			<ul class="fads-sidenav-nested">
				<c:if test="${fe:permission('admin-wizard-view')}">
				<li class="fads-sidenav-item">
					<a href="${fe:url('/admin/wizard/')}" class="fads-sidenav-link <c:if test="${param.menuType=='wizard'}">active</c:if>" <c:if test="${param.menuType=='wizard'}">aria-current="page"</c:if>>
						<i class="fa fa-genderless" aria-hidden="true"></i>
						<span><la:message key="labels.menu_wizard" /></span>
					</a>
				</li>
				</c:if>
				<c:if test="${fe:permission('admin-general-view')}">
				<li class="fads-sidenav-item">
					<a href="${fe:url('/admin/general/')}" class="fads-sidenav-link <c:if test="${param.menuType=='general'}">active</c:if>" <c:if test="${param.menuType=='general'}">aria-current="page"</c:if>>
						<i class="fa fa-genderless" aria-hidden="true"></i>
						<span><la:message key="labels.menu_crawl_config" /></span>
					</a>
				</li>
				</c:if>
				<c:if test="${fe:permission('admin-scheduler-view')}">
				<li class="fads-sidenav-item">
					<a href="${fe:url('/admin/scheduler/')}" class="fads-sidenav-link <c:if test="${param.menuType=='scheduler'}">active</c:if>" <c:if test="${param.menuType=='scheduler'}">aria-current="page"</c:if>>
						<i class="fa fa-genderless" aria-hidden="true"></i>
						<span><la:message key="labels.menu_scheduler_config" /></span>
					</a>
				</li>
				</c:if>
				<c:if test="${fe:permission('admin-design-view')}">
				<li class="fads-sidenav-item">
					<a href="${fe:url('/admin/design/')}" class="fads-sidenav-link <c:if test="${param.menuType=='design'}">active</c:if>" <c:if test="${param.menuType=='design'}">aria-current="page"</c:if>>
						<i class="fa fa-genderless" aria-hidden="true"></i>
						<span><la:message key="labels.menu_design" /></span>
					</a>
				</li>
				</c:if>
				<c:if test="${fe:permission('admin-dict-view') and fesenType!='cloud' and fesenType!='aws'}">
				<li class="fads-sidenav-item">
					<a href="${fe:url('/admin/dict/')}" class="fads-sidenav-link <c:if test="${param.menuType=='dict'}">active</c:if>" <c:if test="${param.menuType=='dict'}">aria-current="page"</c:if>>
						<i class="fa fa-genderless" aria-hidden="true"></i>
						<span><la:message key="labels.menu_dict" /></span>
					</a>
				</li>
				</c:if>
				<c:if test="${fe:permission('admin-accesstoken-view')}">
				<li class="fads-sidenav-item">
					<a href="${fe:url('/admin/accesstoken/')}" class="fads-sidenav-link <c:if test="${param.menuType=='accessToken'}">active</c:if>" <c:if test="${param.menuType=='accessToken'}">aria-current="page"</c:if>>
						<i class="fa fa-genderless" aria-hidden="true"></i>
						<span><la:message key="labels.menu_access_token" /></span>
					</a>
				</li>
				</c:if>
				<c:if test="${fe:permission('admin-plugin-view')}">
				<li class="fads-sidenav-item">
					<a href="${fe:url('/admin/plugin/')}" class="fads-sidenav-link <c:if test="${param.menuType=='plugin'}">active</c:if>" <c:if test="${param.menuType=='plugin'}">aria-current="page"</c:if>>
						<i class="fa fa-genderless" aria-hidden="true"></i>
						<span><la:message key="labels.menu_plugin" /></span>
					</a>
				</li>
				</c:if>
				<c:if test="${fe:permission('admin-storage-view') and storageEnabled.booleanValue()}">
				<li class="fads-sidenav-item">
					<a href="${fe:url('/admin/storage/')}" class="fads-sidenav-link <c:if test="${param.menuType=='storage'}">active</c:if>" <c:if test="${param.menuType=='storage'}">aria-current="page"</c:if>>
						<i class="fa fa-genderless" aria-hidden="true"></i>
						<span><la:message key="labels.menu_storage" /></span>
					</a>
				</li>
				</c:if>
			</ul>
		</li>
		</c:if>
		<c:if test="${fe:permission('admin-webconfig-view') or fe:permission('admin-fileconfig-view') or fe:permission('admin-dataconfig-view') or fe:permission('admin-labeltype-view') or fe:permission('admin-keymatch-view') or fe:permission('admin-boostdoc-view') or fe:permission('admin-relatedcontent-view') or fe:permission('admin-relatedquery-view') or fe:permission('admin-pathmap-view') or fe:permission('admin-webauth-view') or fe:permission('admin-fileauth-view') or fe:permission('admin-reqheader-view') or fe:permission('admin-duplicatehost-view')}">
		<li class="fads-sidenav-item" aria-expanded="${param.menuCategoryType=='crawl' ? 'true' : 'false'}">
			<a href="#" class="fads-sidenav-link <c:if test="${param.menuCategoryType=='crawl'}">active</c:if>">
				<i class="fa fa-cogs" aria-hidden="true"></i>
				<span><la:message key="labels.menu_crawl" /></span>
				<i class="fas fa-angle-left" style="margin-left:auto" aria-hidden="true"></i>
			</a>
			<ul class="fads-sidenav-nested">
				<c:if test="${fe:permission('admin-webconfig-view')}">
				<li class="fads-sidenav-item">
					<a href="${fe:url('/admin/webconfig/')}" class="fads-sidenav-link <c:if test="${param.menuType=='webConfig'}">active</c:if>" <c:if test="${param.menuType=='webConfig'}">aria-current="page"</c:if>>
						<i class="fa fa-genderless" aria-hidden="true"></i>
						<span><la:message key="labels.menu_web" /></span>
					</a>
				</li>
				</c:if>
				<c:if test="${fe:permission('admin-fileconfig-view')}">
				<li class="fads-sidenav-item">
					<a href="${fe:url('/admin/fileconfig/')}" class="fads-sidenav-link <c:if test="${param.menuType=='fileConfig'}">active</c:if>" <c:if test="${param.menuType=='fileConfig'}">aria-current="page"</c:if>>
						<i class="fa fa-genderless" aria-hidden="true"></i>
						<span><la:message key="labels.menu_file_system" /></span>
					</a>
				</li>
				</c:if>
				<c:if test="${fe:permission('admin-dataconfig-view')}">
				<li class="fads-sidenav-item">
					<a href="${fe:url('/admin/dataconfig/')}" class="fads-sidenav-link <c:if test="${param.menuType=='dataConfig'}">active</c:if>" <c:if test="${param.menuType=='dataConfig'}">aria-current="page"</c:if>>
						<i class="fa fa-genderless" aria-hidden="true"></i>
						<span><la:message key="labels.menu_data_store" /></span>
					</a>
				</li>
				</c:if>
				<c:if test="${fe:permission('admin-labeltype-view')}">
				<li class="fads-sidenav-item">
					<a href="${fe:url('/admin/labeltype/')}" class="fads-sidenav-link <c:if test="${param.menuType=='labelType'}">active</c:if>" <c:if test="${param.menuType=='labelType'}">aria-current="page"</c:if>>
						<i class="fa fa-genderless" aria-hidden="true"></i>
						<span><la:message key="labels.menu_label_type" /></span>
					</a>
				</li>
				</c:if>
				<c:if test="${fe:permission('admin-keymatch-view')}">
				<li class="fads-sidenav-item">
					<a href="${fe:url('/admin/keymatch/')}" class="fads-sidenav-link <c:if test="${param.menuType=='keyMatch'}">active</c:if>" <c:if test="${param.menuType=='keyMatch'}">aria-current="page"</c:if>>
						<i class="fa fa-genderless" aria-hidden="true"></i>
						<span><la:message key="labels.menu_key_match" /></span>
					</a>
				</li>
				</c:if>
				<c:if test="${fe:permission('admin-boostdoc-view')}">
				<li class="fads-sidenav-item">
					<a href="${fe:url('/admin/boostdoc/')}" class="fads-sidenav-link <c:if test="${param.menuType=='boostDocumentRule'}">active</c:if>" <c:if test="${param.menuType=='boostDocumentRule'}">aria-current="page"</c:if>>
						<i class="fa fa-genderless" aria-hidden="true"></i>
						<span><la:message key="labels.menu_boost_document_rule" /></span>
					</a>
				</li>
				</c:if>
				<c:if test="${fe:permission('admin-relatedcontent-view')}">
				<li class="fads-sidenav-item">
					<a href="${fe:url('/admin/relatedcontent/')}" class="fads-sidenav-link <c:if test="${param.menuType=='relatedContent'}">active</c:if>" <c:if test="${param.menuType=='relatedContent'}">aria-current="page"</c:if>>
						<i class="fa fa-genderless" aria-hidden="true"></i>
						<span><la:message key="labels.menu_related_content" /></span>
					</a>
				</li>
				</c:if>
				<c:if test="${fe:permission('admin-relatedquery-view')}">
				<li class="fads-sidenav-item">
					<a href="${fe:url('/admin/relatedquery/')}" class="fads-sidenav-link <c:if test="${param.menuType=='relatedQuery'}">active</c:if>" <c:if test="${param.menuType=='relatedQuery'}">aria-current="page"</c:if>>
						<i class="fa fa-genderless" aria-hidden="true"></i>
						<span><la:message key="labels.menu_related_query" /></span>
					</a>
				</li>
				</c:if>
				<c:if test="${fe:permission('admin-pathmap-view')}">
				<li class="fads-sidenav-item">
					<a href="${fe:url('/admin/pathmap/')}" class="fads-sidenav-link <c:if test="${param.menuType=='pathMapping'}">active</c:if>" <c:if test="${param.menuType=='pathMapping'}">aria-current="page"</c:if>>
						<i class="fa fa-genderless" aria-hidden="true"></i>
						<span><la:message key="labels.menu_path_mapping" /></span>
					</a>
				</li>
				</c:if>
				<c:if test="${fe:permission('admin-webauth-view')}">
				<li class="fads-sidenav-item">
					<a href="${fe:url('/admin/webauth/')}" class="fads-sidenav-link <c:if test="${param.menuType=='webAuthentication'}">active</c:if>" <c:if test="${param.menuType=='webAuthentication'}">aria-current="page"</c:if>>
						<i class="fa fa-genderless" aria-hidden="true"></i>
						<span><la:message key="labels.menu_web_authentication" /></span>
					</a>
				</li>
				</c:if>
				<c:if test="${fe:permission('admin-fileauth-view')}">
				<li class="fads-sidenav-item">
					<a href="${fe:url('/admin/fileauth/')}" class="fads-sidenav-link <c:if test="${param.menuType=='fileAuthentication'}">active</c:if>" <c:if test="${param.menuType=='fileAuthentication'}">aria-current="page"</c:if>>
						<i class="fa fa-genderless" aria-hidden="true"></i>
						<span><la:message key="labels.menu_file_authentication" /></span>
					</a>
				</li>
				</c:if>
				<c:if test="${fe:permission('admin-reqheader-view')}">
				<li class="fads-sidenav-item">
					<a href="${fe:url('/admin/reqheader/')}" class="fads-sidenav-link <c:if test="${param.menuType=='requestHeader'}">active</c:if>" <c:if test="${param.menuType=='requestHeader'}">aria-current="page"</c:if>>
						<i class="fa fa-genderless" aria-hidden="true"></i>
						<span><la:message key="labels.menu_request_header" /></span>
					</a>
				</li>
				</c:if>
				<c:if test="${fe:permission('admin-duplicatehost-view')}">
				<li class="fads-sidenav-item">
					<a href="${fe:url('/admin/duplicatehost/')}" class="fads-sidenav-link <c:if test="${param.menuType=='duplicateHost'}">active</c:if>" <c:if test="${param.menuType=='duplicateHost'}">aria-current="page"</c:if>>
						<i class="fa fa-genderless" aria-hidden="true"></i>
						<span><la:message key="labels.menu_duplicate_host" /></span>
					</a>
				</li>
				</c:if>
			</ul>
		</li>
		</c:if>

		<c:if test="${fe:permission('admin-user-view') or fe:permission('admin-role-view') or fe:permission('admin-group-view')}">
		<li class="fads-sidenav-item" aria-expanded="${param.menuCategoryType=='user' ? 'true' : 'false'}">
			<a href="#" class="fads-sidenav-link <c:if test="${param.menuCategoryType=='user'}">active</c:if>">
				<i class="fa fa-user" aria-hidden="true"></i>
				<span><la:message key="labels.menu_user" /></span>
				<i class="fas fa-angle-left" style="margin-left:auto" aria-hidden="true"></i>
			</a>
			<ul class="fads-sidenav-nested">
				<c:if test="${fe:permission('admin-user-view')}">
				<li class="fads-sidenav-item">
					<a href="${fe:url('/admin/user/')}" class="fads-sidenav-link <c:if test="${param.menuType=='user'}">active</c:if>" <c:if test="${param.menuType=='user'}">aria-current="page"</c:if>>
						<i class="fa fa-genderless" aria-hidden="true"></i>
						<span><la:message key="labels.menu_user" /></span>
					</a>
				</li>
				</c:if>
				<c:if test="${fe:permission('admin-role-view')}">
				<li class="fads-sidenav-item">
					<a href="${fe:url('/admin/role/')}" class="fads-sidenav-link <c:if test="${param.menuType=='role'}">active</c:if>" <c:if test="${param.menuType=='role'}">aria-current="page"</c:if>>
						<i class="fa fa-genderless" aria-hidden="true"></i>
						<span><la:message key="labels.menu_role" /></span>
					</a>
				</li>
				</c:if>
				<c:if test="${fe:permission('admin-group-view')}">
				<li class="fads-sidenav-item">
					<a href="${fe:url('/admin/group/')}" class="fads-sidenav-link <c:if test="${param.menuType=='group'}">active</c:if>" <c:if test="${param.menuType=='group'}">aria-current="page"</c:if>>
						<i class="fa fa-genderless" aria-hidden="true"></i>
						<span><la:message key="labels.menu_group" /></span>
					</a>
				</li>
				</c:if>
			</ul>
		</li>
		</c:if>

		<c:if test="${fe:permission('admin-suggest-view') or fe:permission('admin-elevateword-view') or fe:permission('admin-badword-view')}">
		<li class="fads-sidenav-item" aria-expanded="${param.menuCategoryType=='suggest' ? 'true' : 'false'}">
			<a href="#" class="fads-sidenav-link <c:if test="${param.menuCategoryType=='suggest'}">active</c:if>">
				<i class="fa fa-list" aria-hidden="true"></i>
				<span><la:message key="labels.menu_suggest" /></span>
				<i class="fas fa-angle-left" style="margin-left:auto" aria-hidden="true"></i>
			</a>
			<ul class="fads-sidenav-nested">
				<c:if test="${fe:permission('admin-suggest-view')}">
				<li class="fads-sidenav-item">
					<a href="${fe:url('/admin/suggest/')}" class="fads-sidenav-link <c:if test="${param.menuType=='suggestWord'}">active</c:if>" <c:if test="${param.menuType=='suggestWord'}">aria-current="page"</c:if>>
						<i class="fa fa-genderless" aria-hidden="true"></i>
						<span><la:message key="labels.menu_suggest_word" /></span>
					</a>
				</li>
				</c:if>
				<c:if test="${fe:permission('admin-elevateword-view')}">
				<li class="fads-sidenav-item">
					<a href="${fe:url('/admin/elevateword/')}" class="fads-sidenav-link <c:if test="${param.menuType=='elevateWord'}">active</c:if>" <c:if test="${param.menuType=='elevateWord'}">aria-current="page"</c:if>>
						<i class="fa fa-genderless" aria-hidden="true"></i>
						<span><la:message key="labels.menu_elevate_word" /></span>
					</a>
				</li>
				</c:if>
				<c:if test="${fe:permission('admin-badword-view')}">
				<li class="fads-sidenav-item">
					<a href="${fe:url('/admin/badword/')}" class="fads-sidenav-link <c:if test="${param.menuType=='badWord'}">active</c:if>" <c:if test="${param.menuType=='badWord'}">aria-current="page"</c:if>>
						<i class="fa fa-genderless" aria-hidden="true"></i>
						<span><la:message key="labels.menu_bad_word" /></span>
					</a>
				</li>
				</c:if>
			</ul>
		</li>
		</c:if>

		<c:if test="${fe:permission('admin-systeminfo-view') or fe:permission('admin-searchlog-view') or fe:permission('admin-joblog-view') or fe:permission('admin-crawlinginfo-view') or fe:permission('admin-log-view') or fe:permission('admin-failureurl-view') or fe:permission('admin-searchlist-view') or fe:permission('admin-backup-view') or fe:permission('admin-maintenance-view')}">
		<li class="fads-sidenav-item" aria-expanded="${param.menuCategoryType=='log' ? 'true' : 'false'}">
			<a href="#" class="fads-sidenav-link <c:if test="${param.menuCategoryType=='log'}">active</c:if>">
				<i class="fa fa-rss" aria-hidden="true"></i>
				<span><la:message key="labels.menu_system_log" /></span>
				<i class="fas fa-angle-left" style="margin-left:auto" aria-hidden="true"></i>
			</a>
			<ul class="fads-sidenav-nested">
				<c:if test="${fe:permission('admin-systeminfo-view')}">
				<li class="fads-sidenav-item">
					<a href="${fe:url('/admin/systeminfo/')}" class="fads-sidenav-link <c:if test="${param.menuType=='systemInfo'}">active</c:if>" <c:if test="${param.menuType=='systemInfo'}">aria-current="page"</c:if>>
						<i class="fa fa-genderless" aria-hidden="true"></i>
						<span><la:message key="labels.menu_system_info" /></span>
					</a>
				</li>
				</c:if>
				<c:if test="${fe:permission('admin-searchlog-view')}">
				<li class="fads-sidenav-item">
					<a href="${fe:url('/admin/searchlog/')}" class="fads-sidenav-link <c:if test="${param.menuType=='searchLog'}">active</c:if>" <c:if test="${param.menuType=='searchLog'}">aria-current="page"</c:if>>
						<i class="fa fa-genderless" aria-hidden="true"></i>
						<span><la:message key="labels.menu_searchLog" /></span>
					</a>
				</li>
				</c:if>
				<c:if test="${fe:permission('admin-joblog-view')}">
				<li class="fads-sidenav-item">
					<a href="${fe:url('/admin/joblog/')}" class="fads-sidenav-link <c:if test="${param.menuType=='jobLog'}">active</c:if>" <c:if test="${param.menuType=='jobLog'}">aria-current="page"</c:if>>
						<i class="fa fa-genderless" aria-hidden="true"></i>
						<span><la:message key="labels.menu_jobLog" /></span>
					</a>
				</li>
				</c:if>
				<c:if test="${fe:permission('admin-crawlinginfo-view')}">
				<li class="fads-sidenav-item">
					<a href="${fe:url('/admin/crawlinginfo/')}" class="fads-sidenav-link <c:if test="${param.menuType=='crawlingInfo'}">active</c:if>" <c:if test="${param.menuType=='crawlingInfo'}">aria-current="page"</c:if>>
						<i class="fa fa-genderless" aria-hidden="true"></i>
						<span><la:message key="labels.menu_crawling_info" /></span>
					</a>
				</li>
				</c:if>
				<c:if test="${fe:permission('admin-log-view')}">
				<li class="fads-sidenav-item">
					<a href="${fe:url('/admin/log/')}" class="fads-sidenav-link <c:if test="${param.menuType=='log'}">active</c:if>" <c:if test="${param.menuType=='log'}">aria-current="page"</c:if>>
						<i class="fa fa-genderless" aria-hidden="true"></i>
						<span><la:message key="labels.menu_log" /></span>
					</a>
				</li>
				</c:if>
				<c:if test="${fe:permission('admin-failureurl-view')}">
				<li class="fads-sidenav-item">
					<a href="${fe:url('/admin/failureurl/')}" class="fads-sidenav-link <c:if test="${param.menuType=='failureUrl'}">active</c:if>" <c:if test="${param.menuType=='failureUrl'}">aria-current="page"</c:if>>
						<i class="fa fa-genderless" aria-hidden="true"></i>
						<span><la:message key="labels.menu_failure_url" /></span>
					</a>
				</li>
				</c:if>
				<c:if test="${fe:permission('admin-searchlist-view')}">
				<li class="fads-sidenav-item">
					<a href="${fe:url('/admin/searchlist/')}" class="fads-sidenav-link <c:if test="${param.menuType=='searchList'}">active</c:if>" <c:if test="${param.menuType=='searchList'}">aria-current="page"</c:if>>
						<i class="fa fa-genderless" aria-hidden="true"></i>
						<span><la:message key="labels.menu_search_list" /></span>
					</a>
				</li>
				</c:if>
				<c:if test="${fe:permission('admin-backup-view')}">
				<li class="fads-sidenav-item">
					<a href="${fe:url('/admin/backup/')}" class="fads-sidenav-link <c:if test="${param.menuType=='backup'}">active</c:if>" <c:if test="${param.menuType=='backup'}">aria-current="page"</c:if>>
						<i class="fa fa-genderless" aria-hidden="true"></i>
						<span><la:message key="labels.menu_backup" /></span>
					</a>
				</li>
				</c:if>
				<c:if test="${fe:permission('admin-maintenance-view')}">
				<li class="fads-sidenav-item">
					<a href="${fe:url('/admin/maintenance/')}" class="fads-sidenav-link <c:if test="${param.menuType=='maintenance'}">active</c:if>" <c:if test="${param.menuType=='maintenance'}">aria-current="page"</c:if>>
						<i class="fa fa-genderless" aria-hidden="true"></i>
						<span><la:message key="labels.menu_maintenance" /></span>
					</a>
				</li>
				</c:if>
			</ul>
		</li>
		</c:if>

		<c:if test="${fe:fileExists('/WEB-INF/view/common/admin/sidebar_extra.jsp')}">
			<c:import url="/WEB-INF/view/common/admin/sidebar_extra.jsp" />
		</c:if>
	</ul>
	</nav>
</aside>
