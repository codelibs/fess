<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<!-- Left side column. contains the logo and sidebar -->
<aside class="main-sidebar">


	<nav class="navbar  ">
	<!-- Brand Logo -->
		<la:link href="/admin/" styleClass="brand-link　logo-switch brand-text">
			<img src="${fe:url('/images/logo-head.png')}"
				alt="<la:message key="labels.header_brand_name" />" />
		</la:link>
	</nav>

	<!-- sidebar -->
	<div class="sidebar">

		<!-- search form -->
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

		<!-- Sidebar Menu -->
		<nav class="mt-2">

		<ul class="nav nav-pills nav-sidebar nav-child-indent flex-column " data-widget="treeview" role="menu">
			<li class="nav-link"><la:message key="labels.sidebar.menu" /></li>
			<li
				class="nav-link <c:if test="${param.menuCategoryType=='dashboard'}">active</c:if>"><la:link
					href="/admin/dashboard/">
					<em class="fa fa-tachometer-alt"></em>
					<span><la:message key="labels.menu_dashboard_config" /></span>
				</la:link></li>

			<li class="nav-item has-treeview <c:if test="${param.menuCategoryType=='system'}">menu-open</c:if>">
				<a href="#" class="nav-link <c:if test="${param.menuCategoryType=='system'}">active</c:if>"><em class='fa fa-laptop'></em> <span><la:message
							key="labels.menu_system" /></span>
					<i class="right fas fa-angle-left"></i></a>
				<ul class="nav nav-treeview">
					<li class= "nav-item">
						<a href="${fe:url("/admin/wizard/")}" class="nav-link <c:if test="${param.menuType=='wizard'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<span><la:message key="labels.menu_wizard" /></span>
						</a></li>
					<li class= "nav-item">
					<a href="${fe:url("/admin/general/")}" class="nav-link <c:if test="${param.menuType=='general'}">active</c:if>">
						<em class='fa fa-genderless nav-icon'></em>
						<span><la:message key="labels.menu_crawl_config" /></span>
					</a></li>
					<li class= "nav-item">
						<a href="${fe:url("/admin/scheduler/")}" class="nav-link <c:if test="${param.menuType=='scheduler'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<span><la:message key="labels.menu_scheduler_config" /></span>
						</a></li>
					<li class= "nav-item">
						<a href="${fe:url("/admin/design/")}" class="nav-link <c:if test="${param.menuType=='design'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<span><la:message key="labels.menu_design" /></span>
						</a></li>
					<li class= "nav-item">
						<a href="${fe:url("/admin/dict/")}" class="nav-link <c:if test="${param.menuType=='dict'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<span><la:message key="labels.menu_dict" /></span>
						</a></li>
					<li class= "nav-item">
						<a href="${fe:url("/admin/accesstoken/")}" class="nav-link <c:if test="${param.menuType=='accessToken'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<span><la:message key="labels.menu_access_token" /></span>
						</a></li>
					<li class= "nav-item">
						<a href="${fe:url("/admin/plugin/")}" class="nav-link <c:if test="${param.menuType=='plugin'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<span><la:message key="labels.menu_plugin" /></span>
						</a></li>
					<c:if test="${storageEnabled.booleanValue()}">
						<li class= "nav-item">
							<a href="${fe:url("/admin/storage/")}" class="nav-link <c:if test="${param.menuType=='storage'}">active</c:if>">
								<em class='fa fa-genderless nav-icon'></em>
								<span><la:message key="labels.menu_storage" /></span>
							</a></li>
					</c:if>
				</ul></li>
			<li class="nav-item has-treeview <c:if test="${param.menuCategoryType=='crawl'}">menu-open</c:if>">
				<a href="#" class="nav-link <c:if test="${param.menuCategoryType=='crawl'}">active</c:if>"><em class='fa fa-cogs'></em> <span><la:message
						key="labels.menu_crawl" /></span>
					<i class="right fas fa-angle-left"></i></a>
				<ul class="nav nav-treeview">
					<li class= "nav-item">
						<a href="${fe:url("/admin/webconfig/")}" class="nav-link <c:if test="${param.menuType=='webConfig'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<span><la:message key="labels.menu_web" /></span>
						</a></li>
					<li class= "nav-item">
						<a href="${fe:url("/admin/fileconfig/")}" class="nav-link <c:if test="${param.menuType=='fileConfig'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<span><la:message key="labels.menu_file_system" /></span>
						</a></li>
					<li class= "nav-item">
						<a href="${fe:url("/admin/dataconfig/")}" class="nav-link <c:if test="${param.menuType=='dataConfig'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<span><la:message key="labels.menu_data_store" /></span>
						</a></li>
					<li class= "nav-item">
						<a href="${fe:url("/admin/labeltype/")}" class="nav-link <c:if test="${param.menuType=='labelType'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<span><la:message key="labels.menu_label_type" /></span>
						</a></li>
					<li class= "nav-item">
						<a href="${fe:url("/admin/keymatch/")}" class="nav-link <c:if test="${param.menuType=='keyMatch'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<span><la:message key="labels.menu_key_match" /></span>
						</a></li>
					<li class= "nav-item">
						<a href="${fe:url("/admin/boostdoc/")}" class="nav-link <c:if test="${param.menuType=='boostDocumentRule'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<span><la:message key="labels.menu_boost_document_rule" /></span>
						</a></li>
					<li class= "nav-item">
						<a href="${fe:url("/admin/relatedcontent/")}" class="nav-link <c:if test="${param.menuType=='relatedContentRule'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<span><la:message key="labels.menu_related_content" /></span>
						</a></li>
					<li class= "nav-item">
						<a href="${fe:url("/admin/relatedquery/")}" class="nav-link <c:if test="${param.menuType=='relatedQueryRule'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<span><la:message key="labels.menu_related_query" /></span>
						</a></li>
					<li class= "nav-item">
						<a href="${fe:url("/admin/pathmap/")}" class="nav-link <c:if test="${param.menuType=='pathMapping'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<span><la:message key="labels.menu_path_mapping" /></span>
						</a></li>
					<li class= "nav-item">
						<a href="${fe:url("/admin/webauth/")}" class="nav-link <c:if test="${param.menuType=='webAuthentication'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<span><la:message key="labels.menu_web_authentication" /></span>
						</a></li>
					<li class= "nav-item">
						<a href="${fe:url("/admin/fileauth/")}" class="nav-link <c:if test="${param.menuType=='fileAuthentication'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<span><la:message key="labels.menu_file_authentication" /></span>
						</a></li>
					<li class= "nav-item">
						<a href="${fe:url("/admin/reqheader/")}" class="nav-link <c:if test="${param.menuType=='requestHeader'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<span><la:message key="labels.menu_request_header" /></span>
						</a></li>
					<li class= "nav-item">
						<a href="${fe:url("/admin/duplicatehost/")}" class="nav-link <c:if test="${param.menuType=='duplicateHost'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<span><la:message key="labels.menu_duplicate_host" /></span>
						</a></li>
				</ul></li>

			<li class="nav-item has-treeview <c:if test="${param.menuCategoryType=='user'}">menu-open</c:if>">
				<a href="#" class="nav-link <c:if test="${param.menuCategoryType=='user'}">active</c:if>"><em class='fa fa-user'></em> <span><la:message
						key="labels.menu_user" /></span>
					<i class="right fas fa-angle-left"></i></a>
				<ul class="nav nav-treeview">
					<li class= "nav-item">
						<a href="${fe:url("/admin/user/")}" class="nav-link <c:if test="${param.menuType=='user'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<span><la:message key="labels.menu_user" /></span>
						</a></li>
					<li class= "nav-item">
						<a href="${fe:url("/admin/role/")}" class="nav-link <c:if test="${param.menuType=='role'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<span><la:message key="labels.menu_role" /></span>
						</a></li>
					<li class= "nav-item">
						<a href="${fe:url("/admin/group/")}" class="nav-link <c:if test="${param.menuType=='group'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<span><la:message key="labels.menu_group" /></span>
						</a></li>
				</ul></li>

			<li class="nav-item has-treeview <c:if test="${param.menuCategoryType=='suggest'}">menu-open</c:if>">
				<a href="#" class="nav-link <c:if test="${param.menuCategoryType=='suggest'}">active</c:if>"><em class='fa fa-list'></em> <span><la:message
						key="labels.menu_suggest" /></span>
					<i class="right fas fa-angle-left"></i></a>
				<ul class="nav nav-treeview">
					<li class= "nav-item">
						<a href="${fe:url("/admin/suggest/")}" class="nav-link <c:if test="${param.menuType=='suggestWord'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<span><la:message key="labels.menu_suggest_word" /></span>
						</a></li>
					<li class= "nav-item">
						<a href="${fe:url("/admin/elevateword/")}" class="nav-link <c:if test="${param.menuType=='elevateWord'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<span><la:message key="labels.menu_elevate_word" /></span>
						</a></li>
					<li class= "nav-item">
						<a href="${fe:url("/admin/badword/")}" class="nav-link <c:if test="${param.menuType=='badWord'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<span><la:message key="labels.menu_bad_word" /></span>
						</a></li>
				</ul></li>

			<li class="nav-item has-treeview <c:if test="${param.menuCategoryType=='log'}">menu-open</c:if>">
				<a href="#" class="nav-link <c:if test="${param.menuCategoryType=='log'}">active</c:if>"><em class='fa fa-rss'></em> <span><la:message
						key="labels.menu_system_log" /></span>
					<i class="right fas fa-angle-left"></i></a>
				<ul class="nav nav-treeview">
					<li class= "nav-item">
						<a href="${fe:url("/admin/systeminfo/")}" class="nav-link <c:if test="${param.menuType=='systemInfo'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<span><la:message key="labels.menu_system_info" /></span>
						</a></li>
					<li class= "nav-item">
						<a href="${fe:url("/admin/searchlog/")}" class="nav-link <c:if test="${param.menuType=='searchLog'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<span><la:message key="labels.menu_searchLog" /></span>
						</a></li>
					<li class= "nav-item">
						<a href="${fe:url("/admin/joblog/")}" class="nav-link <c:if test="${param.menuType=='jobLog'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<span><la:message key="labels.menu_jobLog" /></span>
						</a></li>
					<li class= "nav-item">
						<a href="${fe:url("/admin/crawlinginfo/")}" class="nav-link <c:if test="${param.menuType=='crawlingInfo'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<span><la:message key="labels.menu_crawling_info" /></span>
						</a></li>
					<li class= "nav-item">
						<a href="${fe:url("/admin/log/")}" class="nav-link <c:if test="${param.menuType=='log'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<span><la:message key="labels.menu_log" /></span>
						</a></li>
					<li class= "nav-item">
						<a href="${fe:url("/admin/failureurl/")}" class="nav-link <c:if test="${param.menuType=='failureUrl'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<span><la:message key="labels.menu_failure_url" /></span>
						</a></li>
					<li class= "nav-item">
						<a href="${fe:url("/admin/searchlist/")}" class="nav-link <c:if test="${param.menuType=='searchList'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<span><la:message key="labels.menu_search_list" /></span>
						</a></li>
					<li class= "nav-item">
						<a href="${fe:url("/admin/backup/")}" class="nav-link <c:if test="${param.menuType=='backup'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<span><la:message key="labels.menu_backup" /></span>
						</a></li>
					<li class= "nav-item">
						<a href="${fe:url("/admin/maintenance/")}" class="nav-link <c:if test="${param.menuType=='maintenance'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<span><la:message key="labels.menu_maintenance" /></span>
						</a></li>
				</ul></li>

			<c:if test="${fe:fileExists('/WEB-INF/view/common/admin/sidebar_extra.jsp')}">
				<c:import url="/WEB-INF/view/common/admin/sidebar_extra.jsp" />
			</c:if>
		</ul>
		</nav>
		<!-- /.sidebar-menu -->
	</div>
	<!-- /.sidebar -->
</aside>
