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
							<la:link href="/logout" styleClass="dropdown-item">
								<la:message key="labels.logout" />
							</la:link>
						</div>
					</div>
				</li>
			</c:if>
		</ul>
		<div class="search-box navbar-form col-lg-5 col-md-6 col-sm-6 col-xs-8 pull-right" role="search">
			<div class="input-group">
				<la:text property="query" maxlength="1000" styleId="query" styleClass="form-control"
					autocomplete="off" />
				<div class="input-group-btn">
					<button type="submit" name="search" id="searchButton"
						class="btn btn-primary">
						<i class="fa fa-search"></i> <span class="hidden-sm-down"><la:message
								key="labels.search" /></span>
					</button>
					<button type="button" class="btn btn-primary dropdown-toggle"
						id="searchOptionsButton" data-toggle="dropdown"
						aria-haspopup="true" aria-expanded="false">
						<span class="sr-only"><la:message
								key="labels.header_form_option_btn" /></span>
					</button>
					<div class="dropdown-menu dropdown-menu-right">
						<a href="#searchOptions" class="dropdown-item" data-toggle="modal"><la:message
								key="labels.header_form_option_btn" /></a>
					</div>
				</div>
			</div>
		</div>
	</nav>
	<div class="modal fade" id="searchOptions" tabindex="-1" role="dialog"
		aria-labelledby="searchOptionsLabel" aria-hidden="true">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span> <span class="sr-only">Close</span>
					</button>
					<h4 class="modal-title" id="searchOptionsLabel">
						<la:message key="labels.search_options" />
					</h4>
				</div>
				<div class="modal-body">
					<fieldset class="form-group">
						<label for="contentNum"><la:message key="labels.index_num" /></label>
						<la:select property="num" styleId="numSearchOption"
							styleClass="form-control" style="display:block;">
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
							styleClass="form-control" style="display:block;">
							<option value="">
								<la:message key="labels.search_result_select_sort" />
							</option>
							<la:option value="created.asc">
								<la:message key="labels.search_result_sort_created_asc" />
							</la:option>
							<la:option value="created.desc">
								<la:message key="labels.search_result_sort_created_desc" />
							</la:option>
							<la:option value="contentLength.asc">
								<la:message key="labels.search_result_sort_contentLength_asc" />
							</la:option>
							<la:option value="contentLength.desc">
								<la:message key="labels.search_result_sort_contentLength_desc" />
							</la:option>
							<la:option value="lastModified.asc">
								<la:message key="labels.search_result_sort_lastModified_asc" />
							</la:option>
							<la:option value="lastModified.desc">
								<la:message key="labels.search_result_sort_lastModified_desc" />
							</la:option>
							<c:if test="${searchLogSupport}">
								<la:option value="clickCount_l_x_dv.asc">
									<la:message key="labels.search_result_sort_clickCount_asc" />
								</la:option>
								<la:option value="clickCount_l_x_dv.desc">
									<la:message key="labels.search_result_sort_clickCount_desc" />
								</la:option>
							</c:if>
							<c:if test="${favoriteSupport}">
								<la:option value="favoriteCount_l_x_dv.asc">
									<la:message key="labels.search_result_sort_favoriteCount_asc" />
								</la:option>
								<la:option value="favoriteCount_l_x_dv.desc">
									<la:message key="labels.search_result_sort_favoriteCount_desc" />
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
							<la:select property="fields.label"
								styleId="labelTypeSearchOption" multiple="true"
								styleClass="form-control">
								<c:forEach var="item" items="${labelTypeItems}">
									<la:option value="${f:u(item.value)}">
														${f:h(item.label)}
													</la:option>
								</c:forEach>
							</la:select>
						</fieldset>
					</c:if>
				</div>
				<div class="modal-footer">
					<button class="btn btn-secondary" id="searchOptionsClearButton">
						<la:message key="labels.search_options_clear" />
					</button>
					<button class="btn btn-secondary" data-dismiss="modal"
						aria-hidden="true">
						<la:message key="labels.search_options_close" />
					</button>
					<button class="btn btn-primary" type="submit">
						<la:message key="labels.search" />
					</button>
				</div>
			</div>
		</div>
	</div>
	<!--  /.modal -->
</la:form>
