<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head profile="http://a9.com/-/spec/opensearch/1.1/">
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title><la:message key="labels.search_title" /></title>
<c:if test="${osddLink}">
	<link rel="search" type="application/opensearchdescription+xml" href="${fe:url('/osdd')}"
		title="<la:message key="labels.index_osdd_title" />"
	/>
</c:if>
<link href="${fe:url('/css/bootstrap.min.css')}" rel="stylesheet" type="text/css" />
<link href="${fe:url('/css/style.css')}" rel="stylesheet" type="text/css" />
<link href="${fe:url('/css/font-awesome.min.css')}" rel="stylesheet" type="text/css" />
</head>
<body>
	<la:form styleClass="form-stacked" action="/search/" method="get" styleId="searchForm">
		${fe:facetForm()}${fe:geoForm()}
		<header>
			<nav class="navbar navbar-expand-md navbar-dark fixed-top bg-dark">
				<div class="container">
					<la:link styleClass="navbar-brand d-inline-flex" href="/">
						<img src="${fe:url('/images/logo-head.png')}"
							alt="<la:message key="labels.header_brand_name" />"
							class="align-items-center" />
					</la:link>
					<button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbar" aria-controls="navbar"
						aria-expanded="false" aria-label="Toggle navigation">
						<span class="navbar-toggler-icon"></span>
					</button>
					<div class="collapse navbar-collapse" id="navbar">
						<div class="mr-auto"></div>
						<ul class="nav navbar-nav">
							<c:if test="${eoled}">
								<li class="nav-item" data-toggle="tooltip" data-placement="left" title="<la:message key="labels.eol_error" />">
									<a class="nav-link active" href="${eolLink}" target="_olh"><em class="fas fa-times-circle text-danger"></em></a>
								</li>
							</c:if>
							<c:if test="${developmentMode}">
								<li class="nav-item" data-toggle="tooltip" data-placement="left"
									title="<la:message key="labels.development_mode_warning" />"
								><a class="nav-link active" href="${installationLink}" target="_olh"><em
										class="fa fa-exclamation-triangle text-warning"
									></em></a></li>
							</c:if>
							<c:choose>
								<c:when test="${!empty username && username != 'guest'}">
									<li class="nav-item">
										<div class="dropdown">
											<a class="nav-link dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-haspopup="true"
												aria-expanded="false"
											> <em class="fa fa-fw fa-user"></em>${username}
											</a>
											<div class="dropdown-menu" aria-labelledby="userMenu">
												<c:if test="${editableUser == true}">
													<la:link href="/profile" styleClass="dropdown-item">
														<la:message key="labels.profile" />
													</la:link>
												</c:if>
												<c:if test="${adminUser == true}">
													<la:link href="/admin" styleClass="dropdown-item">
														<la:message key="labels.administration" />
													</la:link>
												</c:if>
												<la:link href="/logout/" styleClass="dropdown-item">
													<la:message key="labels.logout" />
												</la:link>
											</div>
										</div>
									</li>
								</c:when>
								<c:when test="${ pageLoginLink }">
									<li class="nav-item"><la:link href="/login" styleClass="nav-link" role="button" aria-haspopup="true"
											aria-expanded="false"
										>
											<em class="fa fa-fw fa-sign-in"></em>
											<la:message key="labels.login" />
										</la:link></li>
								</c:when>
							</c:choose>
							<li class="nav-item"><la:link href="/help" styleClass="nav-link help-link">
									<em class="fa fa-fw fa-question-circle"></em>
									<la:message key="labels.index_help" />
								</la:link></li>
						</ul>
					</div>
				</div>
			</nav>
		</header>

		<main id="content" class="container">
			<h2>
				<la:message key="labels.advance_search_title" />
			</h2>
			<div class="notification">${notification}</div>
			<div>
				<la:info id="msg" message="true">
					<div class="alert alert-info">${msg}</div>
				</la:info>
				<la:errors header="errors.front_header" footer="errors.front_footer" prefix="errors.front_prefix"
					suffix="errors.front_suffix"
				/>
			</div>
			<div class="form-group row">
				<label for="as_q" class="col-lg-3 col-md-4 col-sm-5 col-12 col-form-label"><la:message
						key="labels.advance_search_must_queries"
					/></label>
				<div class="col-lg-5 col-md-8 col-sm-7 col-xs-6">
					<input class="form-control" type="text" id="as_q" name="as.q" value="${f:h(fe:join(as.q))}">
				</div>
				<div class="col-lg-4 d-none d-lg-flex align-items-center">
				</div>
			</div>
			<div class="form-group row">
				<label for="as_epq" class="col-lg-3 col-md-4 col-sm-5 col-12 col-form-label"><la:message
						key="labels.advance_search_phrase_query"
					/></label>
				<div class="col-lg-5 col-md-8 col-sm-7 col-xs-6">
					<input class="form-control" type="text" id="as_epq" name="as.epq" value="${f:h(fe:join(as.epq))}">
				</div>
				<div class="col-lg-4 d-none d-lg-flex align-items-center">
				</div>
			</div>
			<div class="form-group row">
				<label for="as_oq" class="col-lg-3 col-md-4 col-sm-5 col-12 col-form-label"><la:message
						key="labels.advance_search_should_queries"
					/></label>
				<div class="col-lg-5 col-md-8 col-sm-7 col-xs-6">
					<input class="form-control" type="text" id="as_oq" name="as.oq" value="${f:h(fe:join(as.oq))}">
				</div>
				<div class="col-lg-4 d-none d-lg-flex align-items-center">
				</div>
			</div>
			<div class="form-group row">
				<label for="as_nq" class="col-lg-3 col-md-4 col-sm-5 col-12 col-form-label"><la:message
						key="labels.advance_search_not_queries"
					/></label>
				<div class="col-lg-5 col-md-8 col-sm-7 col-xs-6">
					<input class="form-control" type="text" id="as_nq" name="as.nq" value="${f:h(fe:join(as.nq))}">
				</div>
				<div class="col-lg-4 d-none d-lg-flex align-items-center">
				</div>
			</div>
			<div class="form-group row">
				<label for="contentNum" class="col-lg-3 col-md-4 col-sm-5 col-12 col-form-label"><la:message
						key="labels.index_num"
					/></label>
				<div class="col-lg-5 col-md-8 col-sm-7 col-xs-6">
					<la:select property="num" styleId="numSearchOption" styleClass="form-control">
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
				<div class="col-lg-4 d-none d-lg-flex align-items-center">
				</div>
			</div>
			<div class="form-group row">
				<label for="contentSort" class="col-lg-3 col-md-4 col-sm-5 col-12 col-form-label"><la:message
						key="labels.index_sort"
					/></label>
				<div class="col-lg-5 col-md-8 col-sm-7 col-xs-6">
					<la:select property="sort" styleId="sortSearchOption" styleClass="form-control">
						<option value="">
							<la:message key="labels.search_result_select_sort" />
						</option>
						<la:option value="score.desc">
							<la:message key="labels.search_result_sort_score_desc" />
						</la:option>
						<la:option value="filename.asc">
							<la:message key="labels.search_result_sort_filename_asc" />
						</la:option>
						<la:option value="filename.desc">
							<la:message key="labels.search_result_sort_filename_desc" />
						</la:option>
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
				</div>
				<div class="col-lg-4 d-none d-lg-flex align-items-center">
				</div>
			</div>
			<div class="form-group row">
				<label for="contentLang" class="col-lg-3 col-md-4 col-sm-5 col-12 col-form-label"><la:message
						key="labels.index_lang"
					/></label>
				<div class="col-lg-5 col-md-8 col-sm-7 col-xs-6">
					<la:select property="lang" styleId="langSearchOption" multiple="true" styleClass="form-control">
						<c:forEach var="item" items="${langItems}">
							<la:option value="${f:u(item.value)}">${f:h(item.label)}</la:option>
						</c:forEach>
					</la:select>
				</div>
				<div class="col-lg-4 d-none d-lg-flex align-items-center">
				</div>
			</div>
			<c:if test="${displayLabelTypeItems}">
				<div class="form-group row">
					<label for="contentLabelType" class="col-lg-3 col-md-4 col-sm-5 col-12 col-form-label"><la:message
							key="labels.index_label"
						/></label>
					<div class="col-lg-5 col-md-8 col-sm-7 col-xs-6">
						<la:select property="fields.label" styleId="labelTypeSearchOption" multiple="true" styleClass="form-control">
							<c:forEach var="item" items="${labelTypeItems}">
								<la:option value="${f:u(item.value)}">${f:h(item.label)}</la:option>
							</c:forEach>
						</la:select>
					</div>
					<div class="col-lg-4 d-none d-lg-flex align-items-center">
					</div>
				</div>
			</c:if>
			<div class="form-group row">
				<label for="as_timestamp" class="col-lg-3 col-md-4 col-sm-5 col-12 col-form-label"><la:message
						key="labels.advance_search_timestamp"
					/></label>
				<div class="col-lg-5 col-md-8 col-sm-7 col-xs-6">
					<select id="as_timestamp" name="as.timestamp" class="form-control">
						<option value=""><la:message key="labels.advance_search_timestamp_default" /></option>
						<option value="[now-1d/d TO *]"
							<c:if test="${as.timestamp.contains('[now-1d/d TO *]')}">selected</c:if>
						><la:message key="labels.advance_search_timestamp_pastday" /></option>
						<option value="[now-1w/d TO *]"
							<c:if test="${as.timestamp.contains('[now-1w/d TO *]')}">selected</c:if>
						><la:message key="labels.advance_search_timestamp_pastweek" /></option>
						<option value="[now-1M/d TO *]"
							<c:if test="${as.timestamp.contains('[now-1M/d TO *]')}">selected</c:if>
						><la:message key="labels.advance_search_timestamp_pastmonth" /></option>
						<option value="[now-1y/d TO *]"
							<c:if test="${as.timestamp.contains('[now-1y/d TO *]')}">selected</c:if>
						><la:message key="labels.advance_search_timestamp_pastyear" /></option>
					</select>
				</div>
				<div class="col-lg-4 d-none d-lg-flex align-items-center">
				</div>
			</div>
			<div class="form-group row">
				<label for="as_filetype" class="col-lg-3 col-md-4 col-sm-5 col-12 col-form-label"><la:message
						key="labels.advance_search_filetype"
					/></label>
				<div class="col-lg-5 col-md-8 col-sm-7 col-xs-6">
					<select id="as_filetype" name="as.filetype" class="form-control">
						<option value=""><la:message key="labels.advance_search_filetype_default" /></option>
						<option value="html" <c:if test="${as.filetype.contains('html')}">selected</c:if>><la:message
								key="labels.advance_search_filetype_html"
							/></option>
						<option value="pdf" <c:if test="${as.filetype.contains('pdf')}">selected</c:if>><la:message
								key="labels.advance_search_filetype_pdf"
							/></option>
						<option value="word" <c:if test="${as.filetype.contains('word')}">selected</c:if>><la:message
								key="labels.advance_search_filetype_word"
							/></option>
						<option value="excel" <c:if test="${as.filetype.contains('excel')}">selected</c:if>><la:message
								key="labels.advance_search_filetype_excel"
							/></option>
						<option value="powerpoint" <c:if test="${as.filetype.contains('powerpoint')}">selected</c:if>><la:message
								key="labels.advance_search_filetype_powerpoint"
							/></option>
					</select>
				</div>
				<div class="col-lg-4 d-none d-lg-flex align-items-center">
				</div>
			</div>
			<div class="form-group row">
				<label for="as_occt" class="col-lg-3 col-md-4 col-sm-5 col-12 col-form-label"><la:message
						key="labels.advance_search_occt"
					/></label>
				<div class="col-lg-5 col-md-8 col-sm-7 col-xs-6">
					<select id="as_occt" name="as.occt" class="form-control">
						<option value=""><la:message key="labels.advance_search_occt_default" /></option>
						<option value="allintitle" <c:if test="${as.occt.contains('allintitle')}">selected</c:if>><la:message
								key="labels.advance_search_occt_allintitle"
							/></option>
						<option value="allinurl" <c:if test="${as.occt.contains('allinurl')}">selected</c:if>><la:message
								key="labels.advance_search_occt_allinurl"
							/></option>
					</select>
				</div>
				<div class="col-lg-4 d-none d-lg-flex align-items-center">
				</div>
			</div>
			<div class="form-group row">
				<label for="as_sitesearch" class="col-lg-3 col-md-4 col-sm-5 col-12 col-form-label"><la:message
						key="labels.advance_search_sitesearch"
					/></label>
				<div class="col-lg-5 col-md-8 col-sm-7 col-xs-6">
					<input class="form-control" type="text" id="as_sitesearch" name="as.sitesearch" value="${f:h(fe:join(as.sitesearch))}">
				</div>
				<div class="col-lg-4 d-none d-lg-flex align-items-center">
				</div>
			</div>

			<div class="row">
				<button type="submit" name="search" id="searchButton" class="btn btn-primary mx-auto">
					<em class="fa fa-search"></em>
					<la:message key="labels.index_form_search_btn" />
				</button>
			</div>
		</main>
		<jsp:include page="footer.jsp" />
	</la:form>
	<input type="hidden" id="contextPath" value="${contextPath}" />
	<script type="text/javascript" src="${fe:url('/js/jquery-3.6.3.min.js')}"></script>
	<script type="text/javascript" src="${fe:url('/js/bootstrap.min.js')}"></script>
	<script type="text/javascript" src="${fe:url('/js/suggestor.js')}"></script>
	<script type="text/javascript" src="${fe:url('/js/advance.js')}"></script>
</body>
</html>
