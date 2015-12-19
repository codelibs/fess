<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<la:form action="/search" method="get" styleId="searchForm"
	styleClass="searchResultForm">
${fe:facetForm()}${fe:geoForm()}
	<nav class="navbar navbar-dark bg-inverse navbar-static-top pos-f-t">
		<la:link styleClass="navbar-brand" href="/">
			<img src="${f:url('/images/logo-head.png')}"
				alt="<la:message key="labels.header_brand_name" />" />
		</la:link>
		<div class="form-inline navbar-form pull-right">
			<la:text property="q" maxlength="1000" styleId="query"
				styleClass="form-control" autocomplete="off" />
			<button class="btn medium btn-primary" type="submit" name="search"
				id="searchButton">
				<i class="icon-search icon-white"></i><span class="hidden-phone"><la:message
						key="labels.search" /></span>
			</button>
			<a href="#searchOptions" role="button" class="btn btn-secondary"
				data-toggle="modal"><i class="icon-cog"></i><span
				class="hidden-phone"><la:message
						key="labels.header_form_option_btn" /></span></a>
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
</la:form>

