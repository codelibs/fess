<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<s:form action="search" method="get" styleId="searchForm">
${fe:facetForm()}${fe:mltForm()}${fe:geoForm()}
	<div class="navbar navbar-inverse navbar-fixed-top">
		<div class="navbar-inner">
			<div class="container">
				<s:link styleClass="brand" href="/">
					<img src="${f:url('/images/logo-head.gif')}"
						alt="<bean:message key="labels.header_brand_name" />" />
				</s:link>
				<div class="navbar-form pull-right">
					<html:text property="query" maxlength="1000" styleId="query" autocomplete="off"/>
					<button class="btn medium btn-primary" type="submit" name="search"
						id="searchButton">
						<i class="icon-search icon-white"></i><span class="hidden-phone"><bean:message
								key="labels.search" /></span>
					</button>
					<a href="#searchOptions" role="button" class="btn"
						data-toggle="modal"><i class="icon-cog"></i><span
						class="hidden-phone"><bean:message
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
				<bean:message key="labels.search_options" />
			</h3>
		</div>
		<div class="modal-body">
			<fieldset>
				<c:if test="${displayLabelTypeItems}">
					<div class="clearfix">
						<label for="contentLabelType"><bean:message
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
							<span id="contentLabelTypeNoneSelectedText" class="hide"><bean:message
									key="labels.search_result_noneselect_label" /></span> <span
								id="contentLabelTypeSelectedText" class="hide"><bean:message
									key="labels.search_result_select_label" /></span>
						</div>
					</div>
				</c:if>
				<div class="clearfix">
					<label for="contentSort"><bean:message
							key="labels.index_sort" /></label>
					<div class="input">
						<html:select property="sort" styleId="sortSearchOption"
							styleClass="span4" style="display:block;">
							<option value="">
								<bean:message key="labels.search_result_select_sort" />
							</option>
							<html:option value="created.asc">
								<bean:message key="labels.search_result_sort_created_asc" />
							</html:option>
							<html:option value="created.desc">
								<bean:message key="labels.search_result_sort_created_desc" />
							</html:option>
							<html:option value="contentLength.asc">
								<bean:message key="labels.search_result_sort_contentLength_asc" />
							</html:option>
							<html:option value="contentLength.desc">
								<bean:message key="labels.search_result_sort_contentLength_desc" />
							</html:option>
							<html:option value="lastModified.asc">
								<bean:message key="labels.search_result_sort_lastModified_asc" />
							</html:option>
							<html:option value="lastModified.desc">
								<bean:message key="labels.search_result_sort_lastModified_desc" />
							</html:option>
							<c:if test="${searchLogSupport}">
							<html:option value="clickCount_l_x_dv.asc">
								<bean:message key="labels.search_result_sort_clickCount_asc" />
							</html:option>
							<html:option value="clickCount_l_x_dv.desc">
								<bean:message key="labels.search_result_sort_clickCount_desc" />
							</html:option>
							</c:if>
							<c:if test="${favoriteSupport}">
							<html:option value="favoriteCount_l_x_dv.asc">
								<bean:message key="labels.search_result_sort_favoriteCount_asc" />
							</html:option>
							<html:option value="favoriteCount_l_x_dv.desc">
								<bean:message
									key="labels.search_result_sort_favoriteCount_desc" />
							</html:option>
							</c:if>
						</html:select>
					</div>
				</div>
				<div class="clearfix">
					<label for="contentNum"><bean:message
							key="labels.index_num" /></label>
					<div class="input">
						<html:select property="num" styleId="numSearchOption"
							styleClass="span4" style="display:block;">
							<option value="">
								<bean:message key="labels.search_result_select_num" />
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
			</fieldset>
		</div>
		<div class="modal-footer">
			<button class="btn" id="searchOptionsClearButton">
				<bean:message key="labels.search_options_clear" />
			</button>
			<button class="btn" data-dismiss="modal" aria-hidden="true">
				<bean:message key="labels.search_options_close" />
			</button>
		</div>
	</div>
</s:form>

