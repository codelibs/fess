<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html>
<html>
<head profile="http://a9.com/-/spec/opensearch/1.1/">
<meta charset="utf-8">
<meta http-equiv="content-style-type" content="text/css" />
<meta http-equiv="content-script-type" content="text/javascript" />
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title><la:message key="labels.search_title" /></title>
<c:if test="${osddLink}">
	<link rel="search" type="application/opensearchdescription+xml"
		href="${f:url('/osdd')}"
		title="<la:message key="labels.index_osdd_title" />" />
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
					<c:if test="${!empty username}">
					<todo:link href="/login/logout" styleClass="logout-link">
						<la:message key="labels.logout" />
					</todo:link>
					</c:if>
					<la:link href="/help" styleClass="help-link">
						<la:message key="labels.index_help" />
					</la:link>
				</p>
			</div>
		</div>
	</div>
	<div class="container">
		<div class="row">
			<div class="center searchFormBox">
				<h1 class="mainLogo">
					<img src="${f:url('/images/logo.gif')}"
						alt="<la:message key="labels.index_title" />" />
				</h1>
				<div>
				<%-- TODO
					<la:messages id="msg" message="true">
						<div class="alert-message info">
							<bean:write name="msg" ignore="true" />
						</div>
					</la:messages>
				 --%>
					<la:errors header="errors.front_header"
						footer="errors.front_footer" prefix="errors.front_prefix"
						suffix="errors.front_suffix" />
				</div>
				<la:form styleClass="form-stacked" action="search" method="get"
					styleId="searchForm">
					${fe:facetForm()}${fe:geoForm()}
					<fieldset>
						<div class="clearfix">
							<div class="input">
								<la:text styleClass="query" property="query" size="50"
									maxlength="1000" styleId="contentQuery" autocomplete="off" />
							</div>
						</div>
						<c:if test="${fe:hswsize(null) != 0}">
							<div>
								<p class="hotSearchWordBody ellipsis">
									<la:message key="labels.search_hot_search_word" />
									<c:forEach var="item" items="${fe:hsw(null, 5)}">
										<la:link href="search?query=${f:u(item)}${fe:facetQuery()}${fe:geoQuery()}">${f:h(item)}</la:link>
									</c:forEach>
								</p>
							</div>
						</c:if>
						<div class="clearfix searchButtonBox">
							<button type="submit" name="search" id="searchButton"
								class="btn btn-primary">
								<la:message key="labels.index_form_search_btn" />
							</button>
							<a href="#searchOptions" role="button" class="btn"
								data-toggle="modal"><la:message
									key="labels.index_form_option_btn" /></a>
						</div>
					</fieldset>
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
										<la:select property="num" styleId="numSearchOption"
											styleClass="span4" style="display:block;">
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
									</div>
								</div>
								<div class="clearfix">
									<label for="contentSort"><la:message
											key="labels.index_sort" /></label>
									<div class="input">
										<la:select property="sort" styleId="sortSearchOption"
											styleClass="span4" style="display:block;">
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
												<la:message
													key="labels.search_result_sort_contentLength_asc" />
											</la:option>
											<la:option value="contentLength.desc">
												<la:message
													key="labels.search_result_sort_contentLength_desc" />
											</la:option>
											<la:option value="lastModified.asc">
												<la:message
													key="labels.search_result_sort_lastModified_asc" />
											</la:option>
											<la:option value="lastModified.desc">
												<la:message
													key="labels.search_result_sort_lastModified_desc" />
											</la:option>
											<c:if test="${searchLogSupport}">
											<la:option value="clickCount_l_x_dv.asc">
												<la:message
													key="labels.search_result_sort_clickCount_asc" />
											</la:option>
											<la:option value="clickCount_l_x_dv.desc">
												<la:message
													key="labels.search_result_sort_clickCount_desc" />
											</la:option>
											</c:if><c:if test="${favoriteSupport}">
											<la:option value="favoriteCount_l_x_dv.asc">
												<la:message
													key="labels.search_result_sort_favoriteCount_asc" />
											</la:option>
											<la:option value="favoriteCount_l_x_dv.desc">
												<la:message
													key="labels.search_result_sort_favoriteCount_desc" />
											</la:option>
											</c:if>
										</la:select>
									</div>
								</div>
								<div class="clearfix">
									<label for="contentLang"><la:message
											key="labels.index_lang" /></label>
									<div class="input">
										<la:select property="lang"
											styleId="langSearchOption" multiple="true"
											styleClass="span4">
											<c:forEach var="item" items="${langItems}">
												<la:option value="${f:u(item.value)}">
																	${f:h(item.label)}
																</la:option>
											</c:forEach>
										</la:select>
									</div>
								</div>
								<c:if test="${displayLabelTypeItems}">
									<div class="clearfix">
										<label for="contentLabelType"><la:message
												key="labels.index_label" /></label>
										<div class="input">
											<la:select property="fields.label"
												styleId="labelTypeSearchOption" multiple="true"
												styleClass="span4">
												<c:forEach var="item" items="${labelTypeItems}">
													<la:option value="${f:u(item.value)}">
														${f:h(item.label)}
													</la:option>
												</c:forEach>
											</la:select>
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
						</div>
					</div>
				</la:form>
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
