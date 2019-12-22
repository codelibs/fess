<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<!-- Left side column. contains the logo and sidebar -->
<aside class="control-sidebar main-sidebar">

	<!-- sidebar -->
	<section class="sidebar">

		<!-- search form -->
		<c:if test="${fe:permission('admin-searchlist-view')}">
		<form action="<%=request.getContextPath()%>/admin/searchlist/search" method="GET"
			class="sidebar-form">
			<div class="input-group">
				<input type="text" name="q" id="query" class="form-control"
					maxlength="1000"
					placeholder="<la:message key="labels.sidebar.placeholder_search" />">
				<span class="input-group-btn">
					<button type="submit" name="search" id="search-btn"
						class="btn btn-flat">
						<em class="fa fa-search"></em>
					</button>
				</span>
			</div>
		</form>
		</c:if>

		<!-- Sidebar Menu -->
		<ul class="sidebar-menu" data-widget="tree">
			<li class="header"><la:message key="labels.sidebar.menu" /></li>

			<c:if test="${fe:permission('admin-dashboard-view')}"><li
				class="<c:if test="${param.menuCategoryType=='dashboard'}">active</c:if>"><la:link
					href="/admin/dashboard/">
					<em class="fa fa-tachometer-alt"></em>
					<span><la:message key="labels.menu_dashboard_config" /></span>
				</la:link></li></c:if>

			<c:if test="${fe:permission('admin-wizard-view') or fe:permission('admin-general-view') or fe:permission('admin-scheduler-view') or fe:permission('admin-design-view') or fe:permission('admin-dict-view') or fe:permission('admin-accesstoken-view') or fe:permission('admin-plugin-view') or fe:permission('admin-storage-view')}">
			<li
				class="treeview <c:if test="${param.menuCategoryType=='system'}">active</c:if>"><a
				href="#"><em class='fa fa-laptop'></em> <span><la:message
							key="labels.menu_system" /></span> <em
					class="fa fa-angle-left pull-right"></em></a>
				<ul class="treeview-menu">

					<c:if test="${fe:permission('admin-wizard-view')}">
					<li <c:if test="${param.menuType=='wizard'}">class="active"</c:if>><la:link
							href="/admin/wizard/">
							<em class='fa fa-genderless'></em>
							<span><la:message key="labels.menu_wizard" /></span>
						</la:link></li></c:if>

					<c:if test="${fe:permission('admin-general-view')}">
					<li <c:if test="${param.menuType=='general'}">class="active"</c:if>><la:link
							href="/admin/general/">
							<em class='fa fa-genderless'></em>
							<span><la:message key="labels.menu_crawl_config" /></span>
						</la:link></li></c:if>

					<c:if test="${fe:permission('admin-scheduler-view')}">
					<li <c:if test="${param.menuType=='scheduler'}">class="active"</c:if>><la:link
							href="/admin/scheduler/">
							<em class='fa fa-genderless'></em>
							<span><la:message key="labels.menu_scheduler_config" /></span>
						</la:link></li></c:if>

					<c:if test="${fe:permission('admin-design-view')}">
					<li <c:if test="${param.menuType=='design'}">class="active"</c:if>><la:link
							href="/admin/design/">
							<em class='fa fa-genderless'></em>
							<span><la:message key="labels.menu_design" /></span>
						</la:link></li></c:if>

					<c:if test="${fe:permission('admin-dict-view')}">
					<li <c:if test="${param.menuType=='dict'}">class="active"</c:if>><la:link
							href="/admin/dict/">
							<em class='fa fa-genderless'></em>
							<span><la:message key="labels.menu_dict" /></span>
						</la:link></li></c:if>

					<c:if test="${fe:permission('admin-accesstoken-view')}">
					<li <c:if test="${param.menuType=='accessToken'}">class="active"</c:if>><la:link
							href="/admin/accesstoken/">
							<em class='fa fa-genderless'></em>
							<span><la:message key="labels.menu_access_token" /></span>
						</la:link></li></c:if>

					<c:if test="${fe:permission('admin-plugin-view')}">
					<li <c:if test="${param.menuType=='plugin'}">class="active"</c:if>><la:link
							href="/admin/plugin/">
							<em class='fa fa-genderless'></em>
							<span><la:message key="labels.menu_plugin" /></span>
						</la:link></li></c:if>

					<c:if test="${fe:permission('admin-storage-view') and storageEnabled.booleanValue()}">
					<li <c:if test="${param.menuType=='storage'}">class="active"</c:if>><la:link
							href="/admin/storage/">
							<em class='fa fa-genderless'></em>
							<span><la:message key="labels.menu_storage" /></span>
						</la:link></li></c:if>

				</ul></li></c:if>
			<c:if test="${fe:permission('admin-webconfig-view') or fe:permission('admin-fileconfig-view') or fe:permission('admin-dataconfig-view') or fe:permission('admin-labeltype-view') or fe:permission('admin-keymatch-view') or fe:permission('admin-boostdoc-view') or fe:permission('admin-relatedcontent-view') or fe:permission('admin-relatedquery-view') or fe:permission('admin-pathmap-view') or fe:permission('admin-webauth-view') or fe:permission('admin-fileauth-view') or fe:permission('admin-reqheader-view') or fe:permission('admin-duplicatehost-view')}">
			<li
				class="treeview <c:if test="${param.menuCategoryType=='crawl'}">active</c:if>"><a
				href="#"><em class='fa fa-cogs'></em> <span><la:message
							key="labels.menu_crawl" /></span> <em
					class="fa fa-angle-left pull-right"></em></a>
				<ul class="treeview-menu">

					<c:if test="${fe:permission('admin-webconfig-view')}">
					<li <c:if test="${param.menuType=='webConfig'}">class="active"</c:if>><la:link
							href="/admin/webconfig/">
							<em class='fa fa-genderless'></em>
							<span><la:message key="labels.menu_web" /></span>
						</la:link></li></c:if>

					<c:if test="${fe:permission('admin-fileconfig-view')}">
					<li <c:if test="${param.menuType=='fileConfig'}">class="active"</c:if>><la:link
							href="/admin/fileconfig/">
							<em class='fa fa-genderless'></em>
							<span><la:message key="labels.menu_file_system" /></span>
						</la:link></li></c:if>

					<c:if test="${fe:permission('admin-dataconfig-view')}">
					<li <c:if test="${param.menuType=='dataConfig'}">class="active"</c:if>><la:link
							href="/admin/dataconfig/">
							<em class='fa fa-genderless'></em>
							<span><la:message key="labels.menu_data_store" /></span>
						</la:link></li></c:if>

					<c:if test="${fe:permission('admin-labeltype-view')}">
					<li <c:if test="${param.menuType=='labelType'}">class="active"</c:if>><la:link
							href="/admin/labeltype/">
							<em class='fa fa-genderless'></em>
							<span><la:message key="labels.menu_label_type" /></span>
						</la:link></li></c:if>

					<c:if test="${fe:permission('admin-keymatch-view')}">
					<li <c:if test="${param.menuType=='keyMatch'}">class="active"</c:if>><la:link
							href="/admin/keymatch/">
							<em class='fa fa-genderless'></em>
							<span><la:message key="labels.menu_key_match" /></span>
						</la:link></li></c:if>

					<c:if test="${fe:permission('admin-boostdoc-view')}">
					<li <c:if test="${param.menuType=='boostDocumentRule'}">class="active"</c:if>><la:link
							href="/admin/boostdoc/">
							<em class='fa fa-genderless'></em>
							<span><la:message key="labels.menu_boost_document_rule" /></span>
						</la:link></li></c:if>

					<c:if test="${fe:permission('admin-relatedcontent-view')}">
					<li <c:if test="${param.menuType=='relatedContentRule'}">class="active"</c:if>><la:link
							href="/admin/relatedcontent/">
							<em class='fa fa-genderless'></em>
							<span><la:message key="labels.menu_related_content" /></span>
						</la:link></li></c:if>

					<c:if test="${fe:permission('admin-relatedquery-view')}">
					<li <c:if test="${param.menuType=='relatedQueryRule'}">class="active"</c:if>><la:link
							href="/admin/relatedquery/">
							<em class='fa fa-genderless'></em>
							<span><la:message key="labels.menu_related_query" /></span>
						</la:link></li></c:if>

					<c:if test="${fe:permission('admin-pathmap-view')}">
					<li <c:if test="${param.menuType=='pathMapping'}">class="active"</c:if>><la:link
							href="/admin/pathmap/">
							<em class='fa fa-genderless'></em>
							<span><la:message key="labels.menu_path_mapping" /></span>
						</la:link></li></c:if>

					<c:if test="${fe:permission('admin-webauth-view')}">
					<li <c:if test="${param.menuType=='webAuthentication'}">class="active"</c:if>><la:link
							href="/admin/webauth/">
							<em class='fa fa-genderless'></em>
							<span><la:message key="labels.menu_web_authentication" /></span>
						</la:link></li></c:if>

					<c:if test="${fe:permission('admin-fileauth-view')}">
					<li <c:if test="${param.menuType=='fileAuthentication'}">class="active"</c:if>><la:link
							href="/admin/fileauth/">
							<em class='fa fa-genderless'></em>
							<span><la:message key="labels.menu_file_authentication" /></span>
						</la:link></li></c:if>

					<c:if test="${fe:permission('admin-reqheader-view')}">
					<li <c:if test="${param.menuType=='requestHeader'}">class="active"</c:if>><la:link
							href="/admin/reqheader/">
							<em class='fa fa-genderless'></em>
							<span><la:message key="labels.menu_request_header" /></span>
						</la:link></li></c:if>

					<c:if test="${fe:permission('admin-duplicatehost-view')}">
					<li <c:if test="${param.menuType=='duplicateHost'}">class="active"</c:if>><la:link
							href="/admin/duplicatehost/">
							<em class='fa fa-genderless'></em>
							<span><la:message key="labels.menu_duplicate_host" /></span>
						</la:link></li></c:if>

				</ul></li></c:if>
			<c:if test="${fe:permission('admin-user-view') or fe:permission('admin-role-view') or fe:permission('admin-group-view')}">
			<li
				class="treeview <c:if test="${param.menuCategoryType=='user'}">active</c:if>"><a
				href="#"><em class='fa fa-user'></em> <span><la:message
							key="labels.menu_user" /></span> <em class="fa fa-angle-left pull-right"></em></a>
				<ul class="treeview-menu">

					<c:if test="${fe:permission('admin-user-view')}">
					<li <c:if test="${param.menuType=='user'}">class="active"</c:if>><la:link
							href="/admin/user/">
							<em class='fa fa-genderless'></em>
							<span><la:message key="labels.menu_user" /></span>
						</la:link></li></c:if>

					<c:if test="${fe:permission('admin-role-view')}">
					<li <c:if test="${param.menuType=='role'}">class="active"</c:if>><la:link
							href="/admin/role/">
							<em class='fa fa-genderless'></em>
							<span><la:message key="labels.menu_role" /></span>
						</la:link></li></c:if>

					<c:if test="${fe:permission('admin-group-view')}">
					<li <c:if test="${param.menuType=='group'}">class="active"</c:if>><la:link
							href="/admin/group/">
							<em class='fa fa-genderless'></em>
							<span><la:message key="labels.menu_group" /></span>
						</la:link></li></c:if>

				</ul></li></c:if>
			<c:if test="${fe:permission('admin-suggest-view') or fe:permission('admin-elevateword-view') or fe:permission('admin-badword-view')}">
			<li
				class="treeview <c:if test="${param.menuCategoryType=='suggest'}">active</c:if>"><a
				href="#"><em class='fa fa-list'></em> <span><la:message
							key="labels.menu_suggest" /></span> <em
					class="fa fa-angle-left pull-right"></em></a>
				<ul class="treeview-menu">

					<c:if test="${fe:permission('admin-suggest-view')}">
					<li <c:if test="${param.menuType=='suggestWord'}">class="active"</c:if>><la:link
							href="/admin/suggest/">
							<em class='fa fa-genderless'></em>
							<span><la:message key="labels.menu_suggest_word" /></span>
						</la:link></li></c:if>

					<c:if test="${fe:permission('admin-elevateword-view')}">
					<li <c:if test="${param.menuType=='elevateWord'}">class="active"</c:if>><la:link
							href="/admin/elevateword/">
							<em class='fa fa-genderless'></em>
							<span><la:message key="labels.menu_elevate_word" /></span>
						</la:link></li></c:if>

					<c:if test="${fe:permission('admin-badword-view')}">
					<li <c:if test="${param.menuType=='badWord'}">class="active"</c:if>><la:link
							href="/admin/badword/">
							<em class='fa fa-genderless'></em>
							<span><la:message key="labels.menu_bad_word" /></span>
						</la:link></li></c:if>

				</ul></li></c:if>
			<c:if test="${fe:permission('admin-systeminfo-view') or fe:permission('admin-searchlog-view') or fe:permission('admin-joblog-view') or fe:permission('admin-crawlinginfo-view') or fe:permission('admin-log-view') or fe:permission('admin-failureurl-view') or fe:permission('admin-searchlist-view') or fe:permission('admin-backup-view') or fe:permission('admin-maintenance-view')}">
			<li
				class="treeview <c:if test="${param.menuCategoryType=='log'}">active</c:if>"><a
				href="#"><em class='fa fa-rss'></em> <span><la:message
							key="labels.menu_system_log" /></span> <em
					class="fa fa-angle-left pull-right"></em></a>
				<ul class="treeview-menu">

					<c:if test="${fe:permission('admin-systeminfo-view')}">
					<li <c:if test="${param.menuType=='systemInfo'}">class="active"</c:if>><la:link
							href="/admin/systeminfo/">
							<em class='fa fa-genderless'></em>
							<span><la:message key="labels.menu_system_info" /></span>
						</la:link></li></c:if>

					<c:if test="${fe:permission('admin-searchlog-view')}">
					<li <c:if test="${param.menuType=='searchLog'}">class="active"</c:if>><la:link
							href="/admin/searchlog/">
							<em class='fa fa-genderless'></em>
							<span><la:message key="labels.menu_searchLog" /></span>
						</la:link></li></c:if>

					<c:if test="${fe:permission('admin-joblog-view')}">
					<li <c:if test="${param.menuType=='jobLog'}">class="active"</c:if>><la:link
							href="/admin/joblog/">
							<em class='fa fa-genderless'></em>
							<span><la:message key="labels.menu_jobLog" /></span>
						</la:link></li></c:if>

					<c:if test="${fe:permission('admin-crawlinginfo-view')}">
					<li <c:if test="${param.menuType=='crawlingInfo'}">class="active"</c:if>><la:link
							href="/admin/crawlinginfo/">
							<em class='fa fa-genderless'></em>
							<span><la:message key="labels.menu_crawling_info" /></span>
						</la:link></li></c:if>

					<c:if test="${fe:permission('admin-log-view')}">
					<li <c:if test="${param.menuType=='log'}">class="active"</c:if>><la:link
							href="/admin/log/">
							<em class='fa fa-genderless'></em>
							<span><la:message key="labels.menu_log" /></span>
						</la:link></li></c:if>

					<c:if test="${fe:permission('admin-failureurl-view')}">
					<li <c:if test="${param.menuType=='failureUrl'}">class="active"</c:if>><la:link
							href="/admin/failureurl/">
							<em class='fa fa-genderless'></em>
							<span><la:message key="labels.menu_failure_url" /></span>
						</la:link></li></c:if>

					<c:if test="${fe:permission('admin-searchlist-view')}">
					<li <c:if test="${param.menuType=='searchList'}">class="active"</c:if>><la:link
							href="/admin/searchlist/">
							<em class='fa fa-genderless'></em>
							<span><la:message key="labels.menu_search_list" /></span>
						</la:link></li></c:if>

					<c:if test="${fe:permission('admin-backup-view')}">
					<li <c:if test="${param.menuType=='backup'}">class="active"</c:if>><la:link
							href="/admin/backup/">
							<em class='fa fa-genderless'></em>
							<span><la:message key="labels.menu_backup" /></span>
						</la:link></li></c:if>

					<c:if test="${fe:permission('admin-maintenance-view')}">
					<li <c:if test="${param.menuType=='maintenance'}">class="active"</c:if>><la:link
							href="/admin/maintenance/">
							<em class='fa fa-genderless'></em>
							<span><la:message key="labels.menu_maintenance" /></span>
						</la:link></li></c:if>

				</ul></li></c:if>
			<c:if test="${fe:fileExists('/WEB-INF/view/common/admin/sidebar_extra.jsp')}">
				<c:import url="/WEB-INF/view/common/admin/sidebar_extra.jsp" />
			</c:if>
		</ul>
		<!-- /.sidebar-menu -->
	</section>
	<!-- /.sidebar -->
</aside>
