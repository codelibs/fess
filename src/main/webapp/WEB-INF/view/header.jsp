<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<s:form action="search" method="get" styleId="searchForm" styleClass="searchResultForm">
${fe:facetForm()}${fe:geoForm()}
	<div class="navbar navbar-inverse navbar-fixed-top">
		<div class="navbar-inner">
			<div class="container">
				<la:link styleClass="brand" href="/">
					<img src="${f:url('/images/logo-head.gif')}"
						alt="<la:message key="labels.header_brand_name" />" />
				</la:link>
				<div class="navbar-form pull-right">
					<html:text property="query" maxlength="1000" styleId="query" autocomplete="off"/>
					<button class="btn medium btn-primary" type="submit" name="search"
						id="searchButton">
						<i class="icon-search icon-white"></i><span class="hidden-phone"><la:message
								key="labels.search" /></span>
					</button>
					<a href="#searchOptions" role="button" class="btn"
						data-toggle="modal"><i class="icon-cog"></i><span
						class="hidden-phone"><la:message
								key="labels.header_form_option_btn" /></span></a>
				</div>
			</div>
		</div>
	</div>
	<div class="modal hide fade" id="searchOptions">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal"
				aria-hidden="true">&times;</button>
			<h3>
				<la:message key="labels.search_options" />
			</h3>
		</div>
		<div class="modal-body">
			<fieldset>
				<div class="clearfix">
					<label for="contentNum"><la:message
							key="labels.index_num" /></label>
					<div class="input">
						<html:select property="num" styleId="numSearchOption"
							styleClass="span4" style="display:block;">
							<option value="">
								<la:message key="labels.search_result_select_num" />
							</option>
							<html:option value="10">10</html:option>
							<html:option value="20">20</html:option>
							<html:option value="30">30</html:option>
							<html:option value="40">40</html:option>
							<html:option value="50">50</html:option>
							<html:option value="100">100</html:option>
						</html:select>
					</div>
				</div>
				<div class="clearfix">
					<label for="contentSort"><la:message
							key="labels.index_sort" /></label>
					<div class="input">
						<html:select property="sort" styleId="sortSearchOption"
							styleClass="span4" style="display:block;">
							<option value="">
								<la:message key="labels.search_result_select_sort" />
							</option>
							<html:option value="created.asc">
								<la:message key="labels.search_result_sort_created_asc" />
							</html:option>
							<html:option value="created.desc">
								<la:message key="labels.search_result_sort_created_desc" />
							</html:option>
							<html:option value="contentLength.asc">
								<la:message key="labels.search_result_sort_contentLength_asc" />
							</html:option>
							<html:option value="contentLength.desc">
								<la:message key="labels.search_result_sort_contentLength_desc" />
							</html:option>
							<html:option value="lastModified.asc">
								<la:message key="labels.search_result_sort_lastModified_asc" />
							</html:option>
							<html:option value="lastModified.desc">
								<la:message key="labels.search_result_sort_lastModified_desc" />
							</html:option>
							<c:if test="${searchLogSupport}">
							<html:option value="clickCount_l_x_dv.asc">
								<la:message key="labels.search_result_sort_clickCount_asc" />
							</html:option>
							<html:option value="clickCount_l_x_dv.desc">
								<la:message key="labels.search_result_sort_clickCount_desc" />
							</html:option>
							</c:if>
							<c:if test="${favoriteSupport}">
							<html:option value="favoriteCount_l_x_dv.asc">
								<la:message key="labels.search_result_sort_favoriteCount_asc" />
							</html:option>
							<html:option value="favoriteCount_l_x_dv.desc">
								<la:message
									key="labels.search_result_sort_favoriteCount_desc" />
							</html:option>
							</c:if>
						</html:select>
					</div>
				</div>
				<div class="clearfix">
					<label for="contentLang"><la:message
							key="labels.index_lang" /></label>
					<div class="input">
						<html:select property="lang"
							styleId="langSearchOption" multiple="true"
							styleClass="span4">
							<c:forEach var="item" items="${langItems}">
								<html:option value="${f:u(item.value)}">
													${f:h(item.label)}
												</html:option>
							</c:forEach>
						</html:select>
					</div>
				</div>
				<c:if test="${displayLabelTypeItems}">
					<div class="clearfix">
						<label for="contentLabelType"><la:message
								key="labels.index_label" /></label>
						<div class="input">
							<html:select property="fields.label"
								styleId="labelTypeSearchOption" multiple="true"
								styleClass="span4">
								<c:forEach var="item" items="${labelTypeItems}">
									<html:option value="${f:u(item.value)}">
														${f:h(item.label)}
													</html:option>
								</c:forEach>
							</html:select>
						</div>
					</div>
				</c:if>
			</fieldset>
		</div>
		<div class="modal-footer">
			<button class="btn" id="searchOptionsClearButton">
				<la:message key="labels.search_options_clear" />
			</button>
			<button class="btn" data-dismiss="modal" aria-hidden="true">
				<la:message key="labels.search_options_close" />
			</button>
			<button class="btn btn-primary" type="submit">
				<la:message key="labels.search" />
			</button>
		</div>
	</div>
</s:form>

