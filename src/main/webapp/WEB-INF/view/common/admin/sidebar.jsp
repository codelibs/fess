<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<!-- Left side column. contains the logo and sidebar -->
<aside class="main-sidebar">

	<!-- sidebar -->
	<section class="sidebar">

		<!-- search form -->
		<form action="<%=request.getContextPath()%>/admin/searchlist/search" method="post"
			class="sidebar-form">
			<div class="input-group">
				<input type="text" name="q" id="query" class="form-control"
					maxlength="1000"
					placeholder="<la:message key="labels.sidebar.placeholder_search" />">
				<span class="input-group-btn">
					<button type="submit" name="search" id="search-btn"
						class="btn btn-flat">
						<i class="fa fa-search"></i>
					</button>
				</span>
			</div>
		</form>

		<!-- Sidebar Menu -->
		<ul class="sidebar-menu">
			<li class="header">MENU</li>

			<li
				class="treeview <c:if test="${param.menuCategoryType=='dashboard'}">active</c:if>"><la:link
					href="/admin/dashboard/">
					<i class="fa fa-dashboard"></i>
					<span><la:message key="labels.menu_dashboard_config" /></span>
				</la:link></li>

			<li
				class="treeview <c:if test="${param.menuCategoryType=='system'}">active</c:if>"><a
				href="#"><i class='fa fa-laptop'></i> <span><la:message
							key="labels.menu_system" /></span> <i
					class="fa fa-angle-left pull-right"></i></a>
				<ul class="treeview-menu">

					<li <c:if test="${param.menuType=='wizard'}">class="active"</c:if>><la:link
							href="/admin/wizard/">
							<i class='fa fa-circle-o'></i>
							<span><la:message key="labels.menu_wizard" /></span>
						</la:link></li>

					<li <c:if test="${param.menuType=='general'}">class="active"</c:if>><la:link
							href="/admin/general/">
							<i class='fa fa-circle-o'></i>
							<span><la:message key="labels.menu_crawl_config" /></span>
						</la:link></li>

					<li
						<c:if test="${param.menuType=='scheduler'}">class="active"</c:if>><la:link
							href="/admin/scheduler/">
							<i class='fa fa-circle-o'></i>
							<span><la:message key="labels.menu_scheduler_config" /></span>
						</la:link></li>

					<li <c:if test="${param.menuType=='design'}">class="active"</c:if>><la:link
							href="/admin/design/">
							<i class='fa fa-circle-o'></i>
							<span><la:message key="labels.menu_design" /></span>
						</la:link></li>

					<li <c:if test="${param.menuType=='dict'}">class="active"</c:if>><la:link
							href="/admin/dict/">
							<i class='fa fa-circle-o'></i>
							<span><la:message key="labels.menu_dict" /></span>
						</la:link></li>

				</ul></li>
			<li
				class="treeview <c:if test="${param.menuCategoryType=='crawl'}">active</c:if>"><a
				href="#"><i class='fa fa-cogs'></i> <span><la:message
							key="labels.menu_crawl" /></span> <i
					class="fa fa-angle-left pull-right"></i></a>
				<ul class="treeview-menu">

					<li
						<c:if test="${param.menuType=='webConfig'}">class="active"</c:if>><la:link
							href="/admin/webconfig/">
							<i class='fa fa-circle-o'></i>
							<span><la:message key="labels.menu_web" /></span>
						</la:link></li>

					<li
						<c:if test="${param.menuType=='fileConfig'}">class="active"</c:if>><la:link
							href="/admin/fileconfig/">
							<i class='fa fa-circle-o'></i>
							<span><la:message key="labels.menu_file_system" /></span>
						</la:link></li>

					<li
						<c:if test="${param.menuType=='dataConfig'}">class="active"</c:if>><la:link
							href="/admin/dataconfig/">
							<i class='fa fa-circle-o'></i>
							<span><la:message key="labels.menu_data_store" /></span>
						</la:link></li>

					<li
						<c:if test="${param.menuType=='labelType'}">class="active"</c:if>><la:link
							href="/admin/labeltype/">
							<i class='fa fa-circle-o'></i>
							<span><la:message key="labels.menu_label_type" /></span>
						</la:link></li>

					<li
						<c:if test="${param.menuType=='keyMatch'}">class="active"</c:if>><la:link
							href="/admin/keymatch/">
							<i class='fa fa-circle-o'></i>
							<span><la:message key="labels.menu_key_match" /></span>
						</la:link></li>

					<li
						<c:if test="${param.menuType=='boostDocumentRule'}">class="active"</c:if>><la:link
							href="/admin/boostdoc/">
							<i class='fa fa-circle-o'></i>
							<span><la:message key="labels.menu_boost_document_rule" /></span>
						</la:link></li>

					<li
						<c:if test="${param.menuType=='pathMapping'}">class="active"</c:if>><la:link
							href="/admin/pathmap/">
							<i class='fa fa-circle-o'></i>
							<span><la:message key="labels.menu_path_mapping" /></span>
						</la:link></li>

					<li
						<c:if test="${param.menuType=='webAuthentication'}">class="active"</c:if>><la:link
							href="/admin/webauth/">
							<i class='fa fa-circle-o'></i>
							<span><la:message key="labels.menu_web_authentication" /></span>
						</la:link></li>

					<li
						<c:if test="${param.menuType=='fileAuthentication'}">class="active"</c:if>><la:link
							href="/admin/fileauth/">
							<i class='fa fa-circle-o'></i>
							<span><la:message key="labels.menu_file_authentication" /></span>
						</la:link></li>

					<li
						<c:if test="${param.menuType=='requestHeader'}">class="active"</c:if>><la:link
							href="/admin/reqheader/">
							<i class='fa fa-circle-o'></i>
							<span><la:message key="labels.menu_request_header" /></span>
						</la:link></li>

					<li
						<c:if test="${param.menuType=='duplicateHost'}">class="active"</c:if>><la:link
							href="/admin/duplicatehost/">
							<i class='fa fa-circle-o'></i>
							<span><la:message key="labels.menu_duplicate_host" /></span>
						</la:link></li>

					<li
						<c:if test="${param.menuType=='roleType'}">class="active"</c:if>><la:link
							href="/admin/roletype/">
							<i class='fa fa-circle-o'></i>
							<span><la:message key="labels.menu_role_type" /></span>
						</la:link></li>

				</ul></li>
			<li
				class="treeview <c:if test="${param.menuCategoryType=='user'}">active</c:if>"><a
				href="#"><i class='fa fa-user'></i> <span><la:message
							key="labels.menu_user" /></span> <i class="fa fa-angle-left pull-right"></i></a>
				<ul class="treeview-menu">

					<li <c:if test="${param.menuType=='user'}">class="active"</c:if>><la:link
							href="/admin/user/">
							<i class='fa fa-circle-o'></i>
							<span><la:message key="labels.menu_user" /></span>
						</la:link></li>

					<li <c:if test="${param.menuType=='role'}">class="active"</c:if>><la:link
							href="/admin/role/">
							<i class='fa fa-circle-o'></i>
							<span><la:message key="labels.menu_role" /></span>
						</la:link></li>

					<li <c:if test="${param.menuType=='group'}">class="active"</c:if>><la:link
							href="/admin/group/">
							<i class='fa fa-circle-o'></i>
							<span><la:message key="labels.menu_group" /></span>
						</la:link></li>

				</ul></li>
			<li
				class="treeview <c:if test="${param.menuCategoryType=='suggest'}">active</c:if>"><a
				href="#"><i class='fa fa-list'></i> <span><la:message
							key="labels.menu_suggest" /></span> <i
					class="fa fa-angle-left pull-right"></i></a>
				<ul class="treeview-menu">

					<li
						<c:if test="${param.menuType=='elevateWord'}">class="active"</c:if>><la:link
							href="/admin/elevateword/">
							<i class='fa fa-circle-o'></i>
							<span><la:message key="labels.menu_elevate_word" /></span>
						</la:link></li>

					<li
						<c:if test="${param.menuType=='badWord'}">class="active"</c:if>><la:link
							href="/admin/badword/">
							<i class='fa fa-circle-o'></i>
							<span><la:message key="labels.menu_bad_word" /></span>
						</la:link></li>

				</ul></li>
			<li
				class="treeview <c:if test="${param.menuCategoryType=='log'}">active</c:if>"><a
				href="#"><i class='fa fa-rss'></i> <span><la:message
							key="labels.menu_system_log" /></span> <i
					class="fa fa-angle-left pull-right"></i></a>
				<ul class="treeview-menu">

					<li
						<c:if test="${param.menuType=='systemInfo'}">class="active"</c:if>><la:link
							href="/admin/systeminfo/">
							<i class='fa fa-circle-o'></i>
							<span><la:message key="labels.menu_system_info" /></span>
						</la:link></li>

					<li <c:if test="${param.menuType=='jobLog'}">class="active"</c:if>><la:link
							href="/admin/joblog/">
							<i class='fa fa-circle-o'></i>
							<span><la:message key="labels.menu_jobLog" /></span>
						</la:link></li>

					<li
						<c:if test="${param.menuType=='crawlingInfo'}">class="active"</c:if>><la:link
							href="/admin/crawlinginfo/">
							<i class='fa fa-circle-o'></i>
							<span><la:message key="labels.menu_crawling_info" /></span>
						</la:link></li>

					<li <c:if test="${param.menuType=='log'}">class="active"</c:if>><la:link
							href="/admin/log/">
							<i class='fa fa-circle-o'></i>
							<span><la:message key="labels.menu_log" /></span>
						</la:link></li>

					<li
						<c:if test="${param.menuType=='failureUrl'}">class="active"</c:if>><la:link
							href="/admin/failureurl/">
							<i class='fa fa-circle-o'></i>
							<span><la:message key="labels.menu_failure_url" /></span>
						</la:link></li>

					<li
						<c:if test="${param.menuType=='searchList'}">class="active"</c:if>><la:link
							href="/admin/searchlist/">
							<i class='fa fa-circle-o'></i>
							<span><la:message key="labels.menu_search_list" /></span>
						</la:link></li>

					<li
						<c:if test="${param.menuType=='backup'}">class="active"</c:if>><la:link
							href="/admin/backup/">
							<i class='fa fa-circle-o'></i>
							<span><la:message key="labels.menu_backup" /></span>
						</la:link></li>

				</ul></li>
		</ul>
		<!-- /.sidebar-menu -->
	</section>
	<!-- /.sidebar -->
</aside>
