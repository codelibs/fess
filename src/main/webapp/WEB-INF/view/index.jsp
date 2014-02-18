<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html>
<html>
<head profile="http://a9.com/-/spec/opensearch/1.1/">
<meta charset="utf-8">
<meta http-equiv="content-style-type" content="text/css" />
<meta http-equiv="content-script-type" content="text/javascript" />
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title><bean:message key="labels.search_title" /></title>
<c:if test="${osddLink}">
	<link rel="search" type="application/opensearchdescription+xml"
		href="${f:url('/osdd')}"
		title="<bean:message key="labels.index_osdd_title" />" />
</c:if>
<link href="${f:url('/css/style-base.css')}" rel="stylesheet"
	type="text/css" />
<link href="${f:url('/css/style.css')}" rel="stylesheet" type="text/css" />
</head>
<body>
	<div class="navbar navbar-inverse navbar-fixed-top">
		<div class="navbar-inner">
			<div class="container">
				<p class="navbar-text pull-right">
					<s:link href="/help" styleClass="help-link">
						<bean:message key="labels.index_help" />
					</s:link>
				</p>
			</div>
		</div>
	</div>
	<div class="container">
		<div class="row">
			<div class="center searchFormBox">
				<h1 class="mainLogo">
					<img src="${f:url('/images/logo.gif')}"
						alt="<bean:message key="labels.index_title" />" />
				</h1>
				<div>
					<html:messages id="msg" message="true">
						<div class="alert-message info">
							<bean:write name="msg" ignore="true" />
						</div>
					</html:messages>
					<html:errors header="errors.front_header"
						footer="errors.front_footer" prefix="errors.front_prefix"
						suffix="errors.front_suffix" />
				</div>
				<s:form styleClass="form-stacked" action="search" method="get"
					styleId="searchForm">
					${fe:facetForm()}${fe:mltForm()}${fe:geoForm()}
					<fieldset>
						<div class="clearfix">
							<div class="input">
								<html:text styleClass="query" property="query" size="50"
									maxlength="1000" styleId="contentQuery" autocomplete="off" />
							</div>
						</div>
						<c:if test="${fe:hswsize(null) != 0}">
							<div>
								<p class="hotSearchWordBody ellipsis">
									<bean:message key="labels.search_hot_search_word" />
									<c:forEach var="item" items="${fe:hsw(null, 5)}">
										<html:link href="search?query=${f:u(item)}${fe:facetQuery()}${fe:mltQuery()}${fe:geoQuery()}">${f:h(item)}</html:link>
									</c:forEach>
								</p>
							</div>
						</c:if>
						<div class="clearfix searchButtonBox">
							<button type="submit" name="search" id="searchButton"
								class="btn btn-primary">
								<bean:message key="labels.index_form_search_btn" />
							</button>
							<a href="#searchOptions" role="button" class="btn"
								data-toggle="modal"><bean:message
									key="labels.index_form_option_btn" /></a>
						</div>
					</fieldset>
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
												<bean:message
													key="labels.search_result_sort_contentLength_asc" />
											</html:option>
											<html:option value="contentLength.desc">
												<bean:message
													key="labels.search_result_sort_contentLength_desc" />
											</html:option>
											<html:option value="lastModified.asc">
												<bean:message
													key="labels.search_result_sort_lastModified_asc" />
											</html:option>
											<html:option value="lastModified.desc">
												<bean:message
													key="labels.search_result_sort_lastModified_desc" />
											</html:option>
											<c:if test="${searchLogSupport}">
											<html:option value="clickCount_l_x_dv.asc">
												<bean:message
													key="labels.search_result_sort_clickCount_asc" />
											</html:option>
											<html:option value="clickCount_l_x_dv.desc">
												<bean:message
													key="labels.search_result_sort_clickCount_desc" />
											</html:option>
											</c:if><c:if test="${favoriteSupport}">
											<html:option value="favoriteCount_l_x_dv.asc">
												<bean:message
													key="labels.search_result_sort_favoriteCount_asc" />
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
			</div>
		</div>
		<jsp:include page="footer.jsp" />
	</div>
	<input type="hidden" id="contextPath" value="<%=request.getContextPath()%>" />
	<script type="text/javascript"
		src="${f:url('/js/jquery-1.11.0.min.js')}"></script>
	<script type="text/javascript" src="${f:url('/js/bootstrap.js')}"></script>
	<script type="text/javascript" src="${f:url('/js/suggestor.js')}"></script>
	<script type="text/javascript" src="${f:url('/js/index.js')}"></script>
</body>
</html>
