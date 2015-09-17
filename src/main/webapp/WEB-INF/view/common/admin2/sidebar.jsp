<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<!-- Left side column. contains the logo and sidebar -->
<aside class="main-sidebar">

	<!-- sidebar -->
	<section class="sidebar">

		<!-- search form -->
		<todo:form action="/admin/searchList/search" method="get" styleClass="sidebar-form">
			<div class="input-group">
				<todo:text styleClass="form-control" property="query" title="Search" maxlength="1000" styleId="query" />
				<span class="input-group-btn">
					<button type='submit' name='search' id='search-btn' class="btn btn-flat">
						<i class="fa fa-search"></i>
					</button>
				</span>
			</div>
		</todo:form>
		<!-- /.search form -->

		<!-- Sidebar Menu -->
		<ul class="sidebar-menu">
			<li class="header">MENU</li>

			<li class="treeview <c:if test="${param.menuCategoryType=='system'}">active</c:if>"><a href="#"><i
					class='fa fa-server'
				></i> <span><la:message key="labels.menu_system" /></span> <i class="fa fa-angle-left pull-right"></i></a>
				<ul class="treeview-menu">

					<li <c:if test="${param.menuType=='wizard'}">class="active"</c:if>><todo:link href="/admin/wizard/index">
							<i class='fa fa-angle-right'></i>
							<span><la:message key="labels.menu.wizard" /></span>
						</todo:link></li>

					<li <c:if test="${param.menuType=='crawl'}">class="active"</c:if>><la:link href="/admin/crawl/index">
							<i class='fa fa-angle-right'></i>
							<span><la:message key="labels.menu.crawl_config" /></span>
						</la:link></li>

					<li <c:if test="${param.menuType=='system'}">class="active"</c:if>><la:link href="/admin/system/index">
							<i class='fa fa-angle-right'></i>
							<span><la:message key="labels.menu.system_config" /></span>
						</la:link></li>

					<li <c:if test="${param.menuType=='scheduledJob'}">class="active"</c:if>><la:link
							href="/admin/scheduledjob/index"
						>
							<i class='fa fa-angle-right'></i>
							<span><la:message key="labels.menu.scheduled_job_config" /></span>
						</la:link></li>

					<li <c:if test="${param.menuType=='design'}">class="active"</c:if>><la:link href="/admin/design/index">
							<i class='fa fa-angle-right'></i>
							<span><la:message key="labels.menu.design" /></span>
						</la:link></li>

					<li <c:if test="${param.menuType=='dict'}">class="active"</c:if>><todo:link href="/admin/dict/index">
							<i class='fa fa-angle-right'></i>
							<span><la:message key="labels.menu.dict" /></span>
						</todo:link></li>

				</ul>
			</li>
			<li class="treeview <c:if test="${param.menuCategoryType=='crawl'}">active</c:if>"><a href="#"><i
					class='fa fa-cogs'
				></i> <span><la:message key="labels.menu_crawl" /></span> <i class="fa fa-angle-left pull-right"></i></a>
				<ul class="treeview-menu">

					<li <c:if test="${param.menuType=='webConfig'}">class="active"</c:if>><la:link href="/admin/webconfig/index">
							<i class='fa fa-angle-right'></i>
							<span><la:message key="labels.menu.web" /></span>
						</la:link></li>

					<li <c:if test="${param.menuType=='fileConfig'}">class="active"</c:if>><la:link href="/admin/fileconfig/index">
							<i class='fa fa-angle-right'></i>
							<span><la:message key="labels.menu.file_system" /></span>
						</la:link></li>

					<li <c:if test="${param.menuType=='dataConfig'}">class="active"</c:if>><la:link href="/admin/dataconfig/index">
							<i class='fa fa-angle-right'></i>
							<span><la:message key="labels.menu.data_store" /></span>
						</la:link></li>

					<li <c:if test="${param.menuType=='labelType'}">class="active"</c:if>><la:link href="/admin/labeltype/index">
							<i class='fa fa-angle-right'></i>
							<span><la:message key="labels.menu.label_type" /></span>
						</la:link></li>

					<li <c:if test="${param.menuType=='keyMatch'}">class="active"</c:if>><la:link href="/admin/keymatch/">
							<i class='fa fa-angle-right'></i>
							<span><la:message key="labels.menu.key_match" /></span>
						</la:link></li>

					<li <c:if test="${param.menuType=='boostDocumentRule'}">class="active"</c:if>><la:link
							href="/admin/boostdocumentrule/index"
						>
							<i class='fa fa-angle-right'></i>
							<span><la:message key="labels.menu.boost_document_rule" /></span>
						</la:link></li>

					<li <c:if test="${param.menuType=='pathMapping'}">class="active"</c:if>><la:link
							href="/admin/pathmapping/index"
						>
							<i class='fa fa-angle-right'></i>
							<span><la:message key="labels.menu.path_mapping" /></span>
						</la:link></li>

					<li <c:if test="${param.menuType=='webAuthentication'}">class="active"</c:if>><la:link
							href="/admin/webauthentication/index"
						>
							<i class='fa fa-angle-right'></i>
							<span><la:message key="labels.menu.web_authentication" /></span>
						</la:link></li>

					<li <c:if test="${param.menuType=='fileAuthentication'}">class="active"</c:if>><la:link
							href="/admin/fileauthentication/index"
						>
							<i class='fa fa-angle-right'></i>
							<span><la:message key="labels.menu.file_authentication" /></span>
						</la:link></li>

					<li <c:if test="${param.menuType=='requestHeader'}">class="active"</c:if>><la:link
							href="/admin/requestheader/index"
						>
							<i class='fa fa-angle-right'></i>
							<span><la:message key="labels.menu.request_header" /></span>
						</la:link></li>

					<li <c:if test="${param.menuType=='overlappingHost'}">class="active"</c:if>><la:link
							href="/admin/overlappinghost/index"
						>
							<i class='fa fa-angle-right'></i>
							<span><la:message key="labels.menu.overlapping_host" /></span>
						</la:link></li>

					<li <c:if test="${param.menuType=='roleType'}">class="active"</c:if>><la:link href="/admin/roletype/index">
							<i class='fa fa-angle-right'></i>
							<span><la:message key="labels.menu.role_type" /></span>
						</la:link></li>

				</ul>
			</li>
			<li class="treeview <c:if test="${param.menuCategoryType=='user'}">active</c:if>"><a href="#"><i
					class='fa fa-list'
				></i> <span><la:message key="labels.menu_user" /></span> <i class="fa fa-angle-left pull-right"></i></a>
				<ul class="treeview-menu">

					<li <c:if test="${param.menuType=='user'}">class="active"</c:if>><la:link
							href="/admin/user/index"
						>
							<i class='fa fa-angle-right'></i>
							<span><la:message key="labels.menu.user" /></span>
						</la:link></li>

					<li <c:if test="${param.menuType=='role'}">class="active"</c:if>><la:link
							href="/admin/role/index"
						>
							<i class='fa fa-angle-right'></i>
							<span><la:message key="labels.menu.role" /></span>
						</la:link></li>

					<li <c:if test="${param.menuType=='group'}">class="active"</c:if>><la:link
							href="/admin/group/index"
						>
							<i class='fa fa-angle-right'></i>
							<span><la:message key="labels.menu.group" /></span>
						</la:link></li>

				</ul>
			</li>
			<li class="treeview <c:if test="${param.menuCategoryType=='suggest'}">active</c:if>"><a href="#"><i
					class='fa fa-list'
				></i> <span><la:message key="labels.menu_suggest" /></span> <i class="fa fa-angle-left pull-right"></i></a>
				<ul class="treeview-menu">

					<li <c:if test="${param.menuType=='suggestElevateWord'}">class="active"</c:if>><la:link
							href="/admin/suggestelevateword/index"
						>
							<i class='fa fa-angle-right'></i>
							<span><la:message key="labels.menu.suggest_elevate_word" /></span>
						</la:link></li>

					<li <c:if test="${param.menuType=='suggestBadWord'}">class="active"</c:if>><la:link
							href="/admin/suggestbadword/index"
						>
							<i class='fa fa-angle-right'></i>
							<span><la:message key="labels.menu.suggest_bad_word" /></span>
						</la:link></li>

				</ul>
			</li>
			<li class="treeview <c:if test="${param.menuCategoryType=='log'}">active</c:if>"><a href="#"><i
					class='fa fa-files-o'
				></i> <span><la:message key="labels.menu_system_log" /></span> <i class="fa fa-angle-left pull-right"></i></a>
				<ul class="treeview-menu">

					<li <c:if test="${param.menuType=='systemInfo'}">class="active"</c:if>><la:link href="/admin/systeminfo/index">
							<i class='fa fa-angle-right'></i>
							<span><la:message key="labels.menu.system_info" /></span>
						</la:link></li>

					<li <c:if test="${param.menuType=='jobLog'}">class="active"</c:if>><la:link href="/admin/joblog/index">
							<i class='fa fa-angle-right'></i>
							<span><la:message key="labels.menu.jobLog" /></span>
						</la:link></li>

					<li <c:if test="${param.menuType=='crawlingSession'}">class="active"</c:if>><la:link
							href="/admin/crawlingsession/index"
						>
							<i class='fa fa-angle-right'></i>
							<span><la:message key="labels.menu.session_info" /></span>
						</la:link></li>

					<li <c:if test="${param.menuType=='log'}">class="active"</c:if>><la:link href="/admin/log/index">
							<i class='fa fa-angle-right'></i>
							<span><la:message key="labels.menu.log" /></span>
						</la:link></li>

					<li <c:if test="${param.menuType=='failureUrl'}">class="active"</c:if>><la:link href="/admin/failureurl/index">
							<i class='fa fa-angle-right'></i>
							<span><la:message key="labels.menu.failure_url" /></span>
						</la:link></li>

					<li <c:if test="${param.menuType=='searchList'}">class="active"</c:if>><todo:link href="/admin/searchList/index">
							<i class='fa fa-angle-right'></i>
							<span><la:message key="labels.menu.search_list" /></span>
						</todo:link></li>

				</ul>
			</li>
		</ul>
		<!-- /.sidebar-menu -->
	</section>
	<!-- /.sidebar -->
</aside>
