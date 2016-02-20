<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<la:form action="search" method="get" styleId="searchForm" role="search">
${fe:facetForm()}${fe:geoForm()}
<nav class="navbar navbar-dark bg-inverse navbar-fixed-top">
		<la:link styleClass="navbar-brand" href="/">
			<img src="${f:url('/images/logo-head.png')}"
				alt="<la:message key="labels.header_brand_name" />" />
		</la:link>
		<ul class="nav navbar-nav hidden-xs-down">
			<c:if test="${!empty username && username != 'guest'}">
				<li class="nav-item username">
					<div class="dropdown">
						<a class="nav-link dropdown-toggle" data-toggle="dropdown"
							href="#" role="button" aria-haspopup="true" aria-expanded="false">
							<i class="fa fa-user"></i>${username}
						</a>
						<div class="dropdown-menu" aria-labelledby="userMenu">
							<la:link href="/profile" styleClass="dropdown-item">
								<la:message key="labels.profile" />
							</la:link>
							<la:link href="/logout" styleClass="dropdown-item">
								<la:message key="labels.logout" />
							</la:link>
						</div>
					</div>
				</li>
			</c:if>
		</ul>
		<div
			class="search-box navbar-form col-lg-5 col-md-6 col-sm-6 col-xs-8 pull-right"
			role="search">
			<div class="input-group">
				<la:text property="q" maxlength="1000" styleId="query"
					styleClass="form-control" autocomplete="off" />
				<span class="input-group-btn">
					<button type="submit" name="search" id="searchButton"
						class="btn btn-primary">
						<i class="fa fa-search"></i>
					</button>
					<button type="button" class="btn btn-default"
						data-toggle="control-options" data-target="#searchOptions"
						id="searchOptionsButton">
						<i class="fa fa-cog"></i> <span class="sr-only"><la:message
								key="labels.header_form_option_btn" /></span>
					</button>
				</span>
			</div>
		</div>
	</nav>
	<div id="searchOptions" class="control-options">
		<div class="container">
			<h4 id="searchOptionsLabel">
				<la:message key="labels.search_options" />
			</h4>
			<div>
				<fieldset class="form-group">
					<label for="contentNum"><la:message key="labels.index_num" /></label>
					<la:select property="num" styleId="numSearchOption"
						styleClass="form-control">
						<option value="">
							<la:message key="labels.search_result_select_num" />
						</option>
						<la:option value="10">10</la:option>
						<la:option value="20">20</la:option>
						<la:option value="30">30</la:option>
						<la:option value="40">40</la:option>
						<la:option value="50">50</la:option>
						<la:option value="100">100</la:option>
					</la:select>
				</fieldset>
				<fieldset class="form-group">
					<label for="contentSort"><la:message
							key="labels.index_sort" /></label>
					<la:select property="sort" styleId="sortSearchOption"
						styleClass="form-control">
						<option value="">
							<la:message key="labels.search_result_select_sort" />
						</option>
						<la:option value="created.asc">
							<la:message key="labels.search_result_sort_created_asc" />
						</la:option>
						<la:option value="created.desc">
							<la:message key="labels.search_result_sort_created_desc" />
						</la:option>
						<la:option value="content_length.asc">
							<la:message key="labels.search_result_sort_content_length_asc" />
						</la:option>
						<la:option value="content_length.desc">
							<la:message key="labels.search_result_sort_content_length_desc" />
						</la:option>
						<la:option value="last_modified.asc">
							<la:message key="labels.search_result_sort_last_modified_asc" />
						</la:option>
						<la:option value="last_modified.desc">
							<la:message key="labels.search_result_sort_last_modified_desc" />
						</la:option>
						<c:if test="${searchLogSupport}">
							<la:option value="click_count.asc">
								<la:message key="labels.search_result_sort_click_count_asc" />
							</la:option>
							<la:option value="click_count.desc">
								<la:message key="labels.search_result_sort_click_count_desc" />
							</la:option>
						</c:if>
						<c:if test="${favoriteSupport}">
							<la:option value="favorite_count.asc">
								<la:message key="labels.search_result_sort_favorite_count_asc" />
							</la:option>
							<la:option value="favorite_count.desc">
								<la:message key="labels.search_result_sort_favorite_count_desc" />
							</la:option>
						</c:if>
					</la:select>
				</fieldset>
				<fieldset class="form-group">
					<label for="contentLang"><la:message
							key="labels.index_lang" /></label>
					<la:select property="lang" styleId="langSearchOption"
						multiple="true" styleClass="form-control">
						<c:forEach var="item" items="${langItems}">
							<la:option value="${f:u(item.value)}">
													${f:h(item.label)}
												</la:option>
						</c:forEach>
					</la:select>
				</fieldset>
				<c:if test="${displayLabelTypeItems}">
					<fieldset class="form-group">
						<label for="contentLabelType"><la:message
								key="labels.index_label" /></label>
						<la:select property="fields.label" styleId="labelTypeSearchOption"
							multiple="true" styleClass="form-control">
							<c:forEach var="item" items="${labelTypeItems}">
								<la:option value="${f:u(item.value)}">
														${f:h(item.label)}
													</la:option>
							</c:forEach>
						</la:select>
					</fieldset>
				</c:if>
			</div>
			<div>
				<button class="btn btn-secondary" id="searchOptionsClearButton">
					<la:message key="labels.search_options_clear" />
				</button>
				<button class="btn btn-primary" type="submit">
					<i class="fa fa-search"></i>
					<la:message key="labels.search" />
				</button>
				<button class="btn btn-secondary pull-right"
					data-toggle="control-options" data-target="#searchOptions"
					id="searchOptionsCloseButton">
					<i class="fa fa-angle-double-right"></i>
					<la:message key="labels.search_options_close" />
				</button>
			</div>
		</div>
	</div>
	<!--  /#searchOptions -->
</la:form>
