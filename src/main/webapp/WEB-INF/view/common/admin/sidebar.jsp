<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<aside class="main-sidebar sidebar-dark-primary">
	<la:link href="/admin/" styleClass="brand-link text-center">
		<span class="brand-text">
		<c:if test="${empty param.brandName or empty param.logoPath}"><img src="${fe:url('/images/logo-head.png')}" alt="<la:message key="labels.header_brand_name" />" /></c:if
		><c:if test="${not empty param.brandName and not empty param.logoPath}"><img src="${fe:url(param.logoPath)}" alt="${f:h(param.brandName)}" /></c:if>
		</span>
	</la:link>
	<div class="sidebar">
		<c:if test="${fe:permission('admin-searchlist-view')}">
			<form action="<%=request.getContextPath()%>/admin/searchlist/search" method="GET" class="form-inline mt-2 sidebar-search">
				<div class="input-group">
					<input class="form-control form-control-sidebar" name="q" id="query"
						   maxlength="1000"
						   placeholder="<la:message key="labels.sidebar.placeholder_search" />">
					<div class="input-group-append">
						<button class="btn btn-sidebar" type="submit" name="search" id="search-btn">
							<em class="fa fa-search"></em>
						</button>
					</div>
				</div>
			</form>
		</c:if>
		<nav class="mt-2">

		<ul class="nav nav-pills nav-sidebar flex-column" data-widget="treeview" role="menu">
			<li class="nav-header"><la:message key="labels.sidebar.menu" /></li>
			<c:if test="${fe:permission('admin-dashboard-view')}">
				<li class="nav-item">
					<a class="nav-link <c:if test="${param.menuCategoryType=='dashboard'}">active</c:if>"
					   href="${fe:url('/admin/dashboard/')}">
						<em class="nav-icon fa fa-tachometer-alt"></em>
						<p><la:message key="labels.menu_dashboard_config" /></p>
					</a>
				</li>
			</c:if>
				
            <c:if test="${fe:permission('admin-wizard-view') or fe:permission('admin-general-view') or fe:permission('admin-scheduler-view') or fe:permission('admin-design-view') or fe:permission('admin-dict-view') or fe:permission('admin-accesstoken-view') or fe:permission('admin-plugin-view') or fe:permission('admin-storage-view')}">
			<li class="nav-item has-treeview <c:if test="${param.menuCategoryType=='system'}">menu-open</c:if>">
				<a href="#" class="nav-link <c:if test="${param.menuCategoryType=='system'}">active</c:if>">
					<em class='nav-icon fa fa-laptop'></em>
					<p>
						<la:message key="labels.menu_system" />
						<i class="right fas fa-angle-left"></i>
					</p>
				</a>
				<ul class="nav nav-treeview">
					<c:if test="${fe:permission('admin-wizard-view')}">
					<li class="nav-item">
						<a href="${fe:url('/admin/wizard/')}" class="nav-link <c:if test="${param.menuType=='wizard'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<p><la:message key="labels.menu_wizard" /></p>
						</a>
					</li>
					</c:if>
					<c:if test="${fe:permission('admin-general-view')}">
					<li class="nav-item">
						<a href="${fe:url('/admin/general/')}" class="nav-link <c:if test="${param.menuType=='general'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<p><la:message key="labels.menu_crawl_config" /></p>
						</a>
					</li>
					</c:if>
					<c:if test="${fe:permission('admin-scheduler-view')}">
					<li class="nav-item">
						<a href="${fe:url('/admin/scheduler/')}" class="nav-link <c:if test="${param.menuType=='scheduler'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<p><la:message key="labels.menu_scheduler_config" /></p>
						</a></li></c:if>
						
					<c:if test="${fe:permission('admin-design-view')}">
					<li class="nav-item">
						<a href="${fe:url('/admin/design/')}" class="nav-link <c:if test="${param.menuType=='design'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<p><la:message key="labels.menu_design" /></p>
						</a></li></c:if>
						
					<c:if test="${fe:permission('admin-dict-view') and fesenType!='cloud' and fesenType!='aws'}">
					<li class="nav-item">
						<a href="${fe:url('/admin/dict/')}" class="nav-link <c:if test="${param.menuType=='dict'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<p><la:message key="labels.menu_dict" /></p>
						</a>
					</li>
					</c:if>
					<c:if test="${fe:permission('admin-accesstoken-view')}">
					<li class="nav-item">
						<a href="${fe:url('/admin/accesstoken/')}"
						   class="nav-link <c:if test="${param.menuType=='accessToken'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<p><la:message key="labels.menu_access_token" /></p>
						</a>
					</li>
					</c:if>
					<c:if test="${fe:permission('admin-plugin-view')}">
					<li class="nav-item">
						<a href="${fe:url('/admin/plugin/')}" class="nav-link <c:if test="${param.menuType=='plugin'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<p><la:message key="labels.menu_plugin" /></p>
						</a>
					</li>
					</c:if>
					<c:if test="${fe:permission('admin-storage-view') and storageEnabled.booleanValue()}">
					<li class="nav-item">
						<a href="${fe:url('/admin/storage/')}" class="nav-link <c:if test="${param.menuType=='storage'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<p><la:message key="labels.menu_storage" /></p>
						</a>
					</li>
					</c:if>
				</ul>
			</li>
			</c:if>
			<c:if test="${fe:permission('admin-webconfig-view') or fe:permission('admin-fileconfig-view') or fe:permission('admin-dataconfig-view') or fe:permission('admin-labeltype-view') or fe:permission('admin-keymatch-view') or fe:permission('admin-boostdoc-view') or fe:permission('admin-relatedcontent-view') or fe:permission('admin-relatedquery-view') or fe:permission('admin-pathmap-view') or fe:permission('admin-webauth-view') or fe:permission('admin-fileauth-view') or fe:permission('admin-reqheader-view') or fe:permission('admin-duplicatehost-view')}">
			<li class="nav-item has-treeview <c:if test="${param.menuCategoryType=='crawl'}">menu-open</c:if>">
				<a href="#" class="nav-link <c:if test="${param.menuCategoryType=='crawl'}">active</c:if>">
					<em class='nav-icon fa fa-cogs'></em>
					<p>
						<la:message key="labels.menu_crawl" />
						<i class="right fas fa-angle-left"></i>
					</p>
				</a>
				<ul class="nav nav-treeview">
					<c:if test="${fe:permission('admin-webconfig-view')}">
					<li class="nav-item">
						<a href="${fe:url('/admin/webconfig/')}" class="nav-link <c:if test="${param.menuType=='webConfig'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<p><la:message key="labels.menu_web" /></p>
						</a></li></c:if>
						
					<c:if test="${fe:permission('admin-fileconfig-view')}">
					<li class="nav-item">
						<a href="${fe:url('/admin/fileconfig/')}"
						   class="nav-link <c:if test="${param.menuType=='fileConfig'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<p><la:message key="labels.menu_file_system" /></p>
						</a></li></c:if>
						
					<c:if test="${fe:permission('admin-dataconfig-view')}">
					<li class="nav-item">
						<a href="${fe:url('/admin/dataconfig/')}"
						   class="nav-link <c:if test="${param.menuType=='dataConfig'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<p><la:message key="labels.menu_data_store" /></p>
						</a></li></c:if>
						
					<c:if test="${fe:permission('admin-labeltype-view')}">
					<li class="nav-item">
						<a href="${fe:url('/admin/labeltype/')}" class="nav-link <c:if test="${param.menuType=='labelType'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<p><la:message key="labels.menu_label_type" /></p>
						</a></li></c:if>
						
					<c:if test="${fe:permission('admin-keymatch-view')}">
					<li class="nav-item">
						<a href="${fe:url('/admin/keymatch/')}" class="nav-link <c:if test="${param.menuType=='keyMatch'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<p><la:message key="labels.menu_key_match" /></p>
						</a></li></c:if>
						
					<c:if test="${fe:permission('admin-boostdoc-view')}">
					<li class="nav-item">
						<a href="${fe:url('/admin/boostdoc/')}"
						   class="nav-link <c:if test="${param.menuType=='boostDocumentRule'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<p><la:message key="labels.menu_boost_document_rule" /></p>
						</a></li></c:if>
						
					<c:if test="${fe:permission('admin-relatedcontent-view')}">
					<li class="nav-item">
						<a href="${fe:url('/admin/relatedcontent/')}"
						   class="nav-link <c:if test="${param.menuType=='relatedContent'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<p><la:message key="labels.menu_related_content" /></p>
						</a></li></c:if>
						
					<c:if test="${fe:permission('admin-relatedquery-view')}">
					<li class="nav-item">
						<a href="${fe:url('/admin/relatedquery/')}"
						   class="nav-link <c:if test="${param.menuType=='relatedQuery'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<p><la:message key="labels.menu_related_query" /></p>
						</a></li></c:if>
						
					<c:if test="${fe:permission('admin-pathmap-view')}">
					<li class="nav-item">
						<a href="${fe:url('/admin/pathmap/')}" class="nav-link <c:if test="${param.menuType=='pathMapping'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<p><la:message key="labels.menu_path_mapping" /></p>
						</a></li></c:if>
						
					<c:if test="${fe:permission('admin-webauth-view')}">
					<li class="nav-item">
						<a href="${fe:url('/admin/webauth/')}"
						   class="nav-link <c:if test="${param.menuType=='webAuthentication'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<p><la:message key="labels.menu_web_authentication" /></p>
						</a></li></c:if>
						
					<c:if test="${fe:permission('admin-fileauth-view')}">
					<li class="nav-item">
						<a href="${fe:url('/admin/fileauth/')}"
						   class="nav-link <c:if test="${param.menuType=='fileAuthentication'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<p><la:message key="labels.menu_file_authentication" /></p>
						</a></li></c:if>
						
					<c:if test="${fe:permission('admin-reqheader-view')}">
					<li class="nav-item">
						<a href="${fe:url('/admin/reqheader/')}"
						   class="nav-link <c:if test="${param.menuType=='requestHeader'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<p><la:message key="labels.menu_request_header" /></p>
						</a></li></c:if>
						
					<c:if test="${fe:permission('admin-duplicatehost-view')}">
					<li class="nav-item">
						<a href="${fe:url('/admin/duplicatehost/')}"
						   class="nav-link <c:if test="${param.menuType=='duplicateHost'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<p><la:message key="labels.menu_duplicate_host" /></p>
						</a></li></c:if>
						
				</ul></li></c:if>
						
			<c:if test="${fe:permission('admin-user-view') or fe:permission('admin-role-view') or fe:permission('admin-group-view')}">
			<li class="nav-item has-treeview <c:if test="${param.menuCategoryType=='user'}">menu-open</c:if>">
				<a href="#" class="nav-link <c:if test="${param.menuCategoryType=='user'}">active</c:if>">
					<em class='nav-icon fa fa-user'></em>
					<p>
						<la:message key="labels.menu_user" />
						<i class="right fas fa-angle-left"></i>
					</p>
				</a>
				<ul class="nav nav-treeview">
					<c:if test="${fe:permission('admin-user-view')}">
					<li class="nav-item">
						<a href="${fe:url('/admin/user/')}" class="nav-link <c:if test="${param.menuType=='user'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<p><la:message key="labels.menu_user" /></p>
						</a></li></c:if>

					<c:if test="${fe:permission('admin-role-view')}">
					<li class="nav-item">
						<a href="${fe:url('/admin/role/')}" class="nav-link <c:if test="${param.menuType=='role'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<p><la:message key="labels.menu_role" /></p>
						</a></li></c:if>

					<c:if test="${fe:permission('admin-group-view')}">
					<li class="nav-item">
						<a href="${fe:url('/admin/group/')}" class="nav-link <c:if test="${param.menuType=='group'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<p><la:message key="labels.menu_group" /></p>
						</a></li></c:if>

				</ul></li></c:if>

			<c:if test="${fe:permission('admin-suggest-view') or fe:permission('admin-elevateword-view') or fe:permission('admin-badword-view')}">

			<li class="nav-item has-treeview <c:if test="${param.menuCategoryType=='suggest'}">menu-open</c:if>">
				<a href="#" class="nav-link <c:if test="${param.menuCategoryType=='suggest'}">active</c:if>">
					<em class='nav-icon fa fa-list'></em>
					<p>
						<la:message key="labels.menu_suggest" />
						<i class="right fas fa-angle-left"></i>
					</p>
				</a>
				<ul class="nav nav-treeview">
					<c:if test="${fe:permission('admin-suggest-view')}">
					<li class="nav-item">
						<a href="${fe:url('/admin/suggest/')}" class="nav-link <c:if test="${param.menuType=='suggestWord'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<p><la:message key="labels.menu_suggest_word" /></p>
						</a></li></c:if>

					<c:if test="${fe:permission('admin-elevateword-view')}">
					<li class="nav-item">
						<a href="${fe:url('/admin/elevateword/')}"
						   class="nav-link <c:if test="${param.menuType=='elevateWord'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<p><la:message key="labels.menu_elevate_word" /></p>
						</a></li></c:if>

					<c:if test="${fe:permission('admin-badword-view')}">
					<li class="nav-item">
						<a href="${fe:url('/admin/badword/')}" class="nav-link <c:if test="${param.menuType=='badWord'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<p><la:message key="labels.menu_bad_word" /></p>
						</a></li></c:if>

				</ul></li></c:if>

			<c:if test="${fe:permission('admin-systeminfo-view') or fe:permission('admin-searchlog-view') or fe:permission('admin-joblog-view') or fe:permission('admin-crawlinginfo-view') or fe:permission('admin-log-view') or fe:permission('admin-failureurl-view') or fe:permission('admin-searchlist-view') or fe:permission('admin-backup-view') or fe:permission('admin-maintenance-view')}">
			<li class="nav-item has-treeview <c:if test="${param.menuCategoryType=='log'}">menu-open</c:if>">
				<a href="#" class="nav-link <c:if test="${param.menuCategoryType=='log'}">active</c:if>">
					<em class='nav-icon fa fa-rss'></em>
					<p>
						<la:message key="labels.menu_system_log" />
						<i class="right fas fa-angle-left"></i>
					</p>
				</a>
				<ul class="nav nav-treeview">
					<c:if test="${fe:permission('admin-systeminfo-view')}">
					<li class="nav-item">
						<a href="${fe:url('/admin/systeminfo/')}"
						   class="nav-link <c:if test="${param.menuType=='systemInfo'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<p><la:message key="labels.menu_system_info" /></p>
						</a></li></c:if>

					<c:if test="${fe:permission('admin-searchlog-view')}">
					<li class="nav-item">
						<a href="${fe:url('/admin/searchlog/')}" class="nav-link <c:if test="${param.menuType=='searchLog'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<p><la:message key="labels.menu_searchLog" /></p>
						</a></li></c:if>

					<c:if test="${fe:permission('admin-joblog-view')}">
					<li class="nav-item">
						<a href="${fe:url('/admin/joblog/')}" class="nav-link <c:if test="${param.menuType=='jobLog'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<p><la:message key="labels.menu_jobLog" /></p>
						</a></li></c:if>

					<c:if test="${fe:permission('admin-crawlinginfo-view')}">
					<li class="nav-item">
						<a href="${fe:url('/admin/crawlinginfo/')}"
						   class="nav-link <c:if test="${param.menuType=='crawlingInfo'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<p><la:message key="labels.menu_crawling_info" /></p>
						</a></li></c:if>

					<c:if test="${fe:permission('admin-log-view')}">
					<li class="nav-item">
						<a href="${fe:url('/admin/log/')}" class="nav-link <c:if test="${param.menuType=='log'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<p><la:message key="labels.menu_log" /></p>
						</a></li></c:if>

					<c:if test="${fe:permission('admin-failureurl-view')}">
					<li class="nav-item">
						<a href="${fe:url('/admin/failureurl/')}"
						   class="nav-link <c:if test="${param.menuType=='failureUrl'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<p><la:message key="labels.menu_failure_url" /></p>
						</a></li></c:if>

					<c:if test="${fe:permission('admin-searchlist-view')}">
					<li class="nav-item">
						<a href="${fe:url('/admin/searchlist/')}"
						   class="nav-link <c:if test="${param.menuType=='searchList'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<p><la:message key="labels.menu_search_list" /></p>
						</a></li></c:if>

					<c:if test="${fe:permission('admin-backup-view')}">
					<li class="nav-item">
						<a href="${fe:url('/admin/backup/')}" class="nav-link <c:if test="${param.menuType=='backup'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<p><la:message key="labels.menu_backup" /></p>
						</a></li></c:if>

					<c:if test="${fe:permission('admin-maintenance-view')}">
					<li class="nav-item">
						<a href="${fe:url('/admin/maintenance/')}"
						   class="nav-link <c:if test="${param.menuType=='maintenance'}">active</c:if>">
							<em class='fa fa-genderless nav-icon'></em>
							<p><la:message key="labels.menu_maintenance" /></p>
						</a></li></c:if>

				</ul></li></c:if>

			<c:if test="${fe:fileExists('/WEB-INF/view/common/admin/sidebar_extra.jsp')}">
				<c:import url="/WEB-INF/view/common/admin/sidebar_extra.jsp" />
			</c:if>
		</ul>
		</nav>
	</div>
</aside>
