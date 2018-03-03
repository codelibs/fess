<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html>
<html>
<head profile="http://a9.com/-/spec/opensearch/1.1/">
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title><la:message key="labels.search_title" /></title>
<c:if test="${osddLink}">
	<link rel="search" type="application/opensearchdescription+xml"
		href="${fe:url('/osdd')}"
		title="<la:message key="labels.index_osdd_title" />" />
</c:if>
<link href="${fe:url('/css/style-base.css')}" rel="stylesheet"
	type="text/css" />
<link href="${fe:url('/css/style.css')}" rel="stylesheet" type="text/css" />
<link href="${fe:url('/css/font-awesome.min.css')}" rel="stylesheet"
	type="text/css" />
</head>
<body>
	<la:form styleClass="form-stacked" action="/search/" method="get"
		styleId="searchForm">
		${fe:facetForm()}${fe:geoForm()}
		<nav class="navbar navbar-dark bg-inverse navbar-static-top pos-f-t">
			<div id="content" class="container">
				<ul class="nav navbar-nav pull-right">
					<c:choose>
						<c:when test="${!empty username && username != 'guest'}">
							<li class="nav-item">
								<div class="dropdown">
									<a class="nav-link dropdown-toggle" data-toggle="dropdown"
										href="#" role="button" aria-haspopup="true"
										aria-expanded="false"> <i class="fa fa-user"></i>${username}
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
							<li class="nav-item username"><la:link href="/login"
									styleClass="nav-link" role="button" aria-haspopup="true"
									aria-expanded="false">
									<i class="fa fa-sign-in"></i>
									<la:message key="labels.login" />
								</la:link></li>
						</c:when>
					</c:choose>
					<li class="nav-item"><la:link href="/help"
							styleClass="nav-link help-link">
							<i class="fa fa-question-circle"></i>
							<la:message key="labels.index_help" />
						</la:link></li>
				</ul>
			</div>
		</nav>
		<div class="container">
			<div class="row content">
				<div class="center-block center">
					<h2>
						<la:message key="labels.advance_search_title" />
					</h2>
					<div class="notification">${notification}</div>
					<div>
						<la:info id="msg" message="true">
							<div class="alert alert-info">${msg}</div>
						</la:info>
						<la:errors header="errors.front_header"
							footer="errors.front_footer" prefix="errors.front_prefix"
							suffix="errors.front_suffix" />
					</div>
					<div class="centered col-lg-7 col-md-8 col-sm-8 col-xs-12">
						<div class="form-group row">
							<label for="as_q" class="col-lg-4 col-md-6 col-sm-6 col-xs-6 col-form-label"><la:message
									key="labels.advance_search_must_queries"
								/></label>
							<div class="col-lg-8 col-md-6 col-sm-6 col-xs-6">
								<input class="form-control" type="text" id="as_q" name="as.q" value="${f:h(fe:join(as.q))}">
							</div>
						</div>
						<div class="form-group row">
							<label for="as_epq" class="col-lg-4 col-md-6 col-sm-6 col-xs-6 col-form-label"><la:message
									key="labels.advance_search_phrase_query"
								/></label>
							<div class="col-lg-8 col-md-6 col-sm-6 col-xs-6">
								<input class="form-control" type="text" id="as_epq" name="as.epq" value="${f:h(fe:join(as.epq))}">
							</div>
						</div>
						<div class="form-group row">
							<label for="as_oq" class="col-lg-4 col-md-6 col-sm-6 col-xs-6 col-form-label"><la:message
									key="labels.advance_search_should_queries"
								/></label>
							<div class="col-lg-8 col-md-6 col-sm-6 col-xs-6">
								<input class="form-control" type="text" id="as_oq" name="as.oq" value="${f:h(fe:join(as.oq))}">
							</div>
						</div>
						<div class="form-group row">
							<label for="as_nq" class="col-lg-4 col-md-6 col-sm-6 col-xs-6 col-form-label"><la:message
									key="labels.advance_search_not_queries"
								/></label>
							<div class="col-lg-8 col-md-6 col-sm-6 col-xs-6">
								<input class="form-control" type="text" id="as_nq" name="as.nq" value="${f:h(fe:join(as.nq))}">
							</div>
						</div>

						<div class="clearfix searchButtonBox btn-group">
							<button type="submit" name="search" id="searchButton" class="btn btn-primary">
								<i class="fa fa-search"></i>
								<la:message key="labels.index_form_search_btn" />
							</button>
						</div>
					</div>
				</div>
			</div>
			<jsp:include page="footer.jsp" />
		</div>
		<div id="searchOptions" class="control-options">
			<div class="container">
				<jsp:include page="searchOptions.jsp" />
				<div>
					<button type="button" class="btn btn-secondary" id="searchOptionsClearButton">
						<la:message key="labels.search_options_clear" />
					</button>
					<button type="button" class="btn btn-secondary pull-right"
						data-toggle="control-options" data-target="#searchOptions"
						id="searchOptionsCloseButton">
						<i class="fa fa-times-circle"></i>
						<la:message key="labels.search_options_close" />
					</button>
				</div>
			</div>
		</div>
	</la:form>
	<input type="hidden" id="contextPath" value="${contextPath}" />
	<script type="text/javascript"
		src="${fe:url('/js/jquery-3.2.1.min.js')}"></script>
	<script type="text/javascript" src="${fe:url('/js/bootstrap.js')}"></script>
	<script type="text/javascript" src="${fe:url('/js/suggestor.js')}"></script>
	<script type="text/javascript" src="${fe:url('/js/index.js')}"></script>
</body>
</html>
