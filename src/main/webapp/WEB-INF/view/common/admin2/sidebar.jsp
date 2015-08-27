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

					<li <c:if test="${param.menuType=='crawl'}">class="active"</c:if>><todo:link href="/admin/crawl/index">
							<i class='fa fa-angle-right'></i>
							<span><la:message key="labels.menu.crawl_config" /></span>
						</todo:link></li>

					<li <c:if test="${param.menuType=='system'}">class="active"</c:if>><todo:link href="/admin/system/index">
							<i class='fa fa-angle-right'></i>
							<span><la:message key="labels.menu.system_config" /></span>
						</todo:link></li>

					<li <c:if test="${param.menuType=='scheduledJob'}">class="active"</c:if>><todo:link
							href="/admin/scheduledJob/index"
						>
							<i class='fa fa-angle-right'></i>
							<span><la:message key="labels.menu.scheduled_job_config" /></span>
						</todo:link></li>

					<li <c:if test="${param.menuType=='design'}">class="active"</c:if>><la:link href="/admin/design/index">
							<i class='fa fa-angle-right'></i>
							<span><la:message key="labels.menu.design" /></span>
						</la:link></li>

					<li <c:if test="${param.menuType=='dict'}">class="active"</c:if>><todo:link href="/admin/dict/index">
							<i class='fa fa-angle-right'></i>
							<span><la:message key="labels.menu.dict" /></span>
						</todo:link></li>

				</ul></li>
			<li class="treeview <c:if test="${param.menuCategoryType=='crawl'}">active</c:if>"><a href="#"><i
					class='fa fa-cogs'
				></i> <span><la:message key="labels.menu_crawl" /></span> <i class="fa fa-angle-left pull-right"></i></a>
				<ul class="treeview-menu">

					<li <c:if test="${param.menuType=='webConfig'}">class="active"</c:if>><la:link href="/admin/webconfig/index">
							<i class='fa fa-angle-right'></i>
							<span><la:message key="labels.menu.web" /></span>
						</la:link></li>

					<li <c:if test="${param.menuType=='fileConfig'}">class="active"</c:if>><todo:link href="/admin/fileConfig/index">
							<i class='fa fa-angle-right'></i>
							<span><la:message key="labels.menu.file_system" /></span>
						</todo:link></li>

					<li <c:if test="${param.menuType=='dataConfig'}">class="active"</c:if>><todo:link href="/admin/dataConfig/index">
							<i class='fa fa-angle-right'></i>
							<span><la:message key="labels.menu.data_store" /></span>
						</todo:link></li>

					<li <c:if test="${param.menuType=='labelType'}">class="active"</c:if>><todo:link href="/admin/labelType/index">
							<i class='fa fa-angle-right'></i>
							<span><la:message key="labels.menu.label_type" /></span>
						</todo:link></li>

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

					<li <c:if test="${param.menuType=='webAuthentication'}">class="active"</c:if>><todo:link
							href="/admin/webAuthentication/index"
						>
							<i class='fa fa-angle-right'></i>
							<span><la:message key="labels.menu.web_authentication" /></span>
						</todo:link></li>

					<li <c:if test="${param.menuType=='fileAuthentication'}">class="active"</c:if>><todo:link
							href="/admin/fileAuthentication/index"
						>
							<i class='fa fa-angle-right'></i>
							<span><la:message key="labels.menu.file_authentication" /></span>
						</todo:link></li>

					<li <c:if test="${param.menuType=='requestHeader'}">class="active"</c:if>><la:link
							href="/admin/requestheader/index"
						>
							<i class='fa fa-angle-right'></i>
							<span><la:message key="labels.menu.request_header" /></span>
						</la:link></li>

					<li <c:if test="${param.menuType=='overlappingHost'}">class="active"</c:if>><todo:link
							href="/admin/overlappingHost/index"
						>
							<i class='fa fa-angle-right'></i>
							<span><la:message key="labels.menu.overlapping_host" /></span>
						</todo:link></li>

					<li <c:if test="${param.menuType=='roleType'}">class="active"</c:if>><todo:link href="/admin/roleType/index">
							<i class='fa fa-angle-right'></i>
							<span><la:message key="labels.menu.role_type" /></span>
						</todo:link></li>

				</ul></li>
			<li class="treeview <c:if test="${param.menuCategoryType=='suggest'}">active</c:if>"><a href="#"><i
					class='fa fa-list'
				></i> <span><la:message key="labels.menu_suggest" /></span> <i class="fa fa-angle-left pull-right"></i></a>
				<ul class="treeview-menu">

					<li <c:if test="${param.menuType=='suggestElevateWord'}">class="active"</c:if>><todo:link
							href="/admin/suggestElevateWord/index"
						>
							<i class='fa fa-angle-right'></i>
							<span><la:message key="labels.menu.suggest_elevate_word" /></span>
						</todo:link></li>

					<li <c:if test="${param.menuType=='suggestBadWord'}">class="active"</c:if>><todo:link
							href="/admin/suggestBadWord/index"
						>
							<i class='fa fa-angle-right'></i>
							<span><la:message key="labels.menu.suggest_bad_word" /></span>
						</todo:link></li>

				</ul></li>
			<li class="treeview <c:if test="${param.menuCategoryType=='log'}">active</c:if>"><a href="#"><i
					class='fa fa-files-o'
				></i> <span><la:message key="labels.menu_system_log" /></span> <i class="fa fa-angle-left pull-right"></i></a>
				<ul class="treeview-menu">

					<li <c:if test="${param.menuType=='systemInfo'}">class="active"</c:if>><todo:link href="/admin/systemInfo/index">
							<i class='fa fa-angle-right'></i>
							<span><la:message key="labels.menu.system_info" /></span>
						</todo:link></li>

					<li <c:if test="${param.menuType=='jobLog'}">class="active"</c:if>><todo:link href="/admin/jobLog/index">
							<i class='fa fa-angle-right'></i>
							<span><la:message key="labels.menu.jobLog" /></span>
						</todo:link></li>

					<li <c:if test="${param.menuType=='crawlingSession'}">class="active"</c:if>><todo:link
							href="/admin/crawlingSession/index"
						>
							<i class='fa fa-angle-right'></i>
							<span><la:message key="labels.menu.session_info" /></span>
						</todo:link></li>

					<li <c:if test="${param.menuType=='log'}">class="active"</c:if>><todo:link href="/admin/log/index">
							<i class='fa fa-angle-right'></i>
							<span><la:message key="labels.menu.log" /></span>
						</todo:link></li>

					<li <c:if test="${param.menuType=='failureUrl'}">class="active"</c:if>><todo:link href="/admin/failureUrl/index">
							<i class='fa fa-angle-right'></i>
							<span><la:message key="labels.menu.failure_url" /></span>
						</todo:link></li>

					<li <c:if test="${param.menuType=='searchList'}">class="active"</c:if>><todo:link href="/admin/searchList/index">
							<i class='fa fa-angle-right'></i>
							<span><la:message key="labels.menu.search_list" /></span>
						</todo:link></li>

				</ul></li>
		</ul>
		<!-- /.sidebar-menu -->
	</section>
	<!-- /.sidebar -->
</aside>


