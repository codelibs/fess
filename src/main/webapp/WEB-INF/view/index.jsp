<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
${fe:html(true)}
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title><la:message key="labels.search_title" /></title>
<c:if test="${osddLink}">
	<link rel="search" type="application/opensearchdescription+xml"
		href="${fe:url('/osdd')}"
		title="<la:message key="labels.index_osdd_title" />" />
</c:if>
<link href="${fe:url('/css/tokens.css')}" rel="stylesheet" type="text/css" />
<link href="${fe:url('/css/fess-ads.css')}" rel="stylesheet" type="text/css" />
<link href="${fe:url('/css/font-awesome.min.css')}" rel="stylesheet"
	type="text/css" />
</head>
<body>
	<la:form action="/search" method="get" styleId="searchForm">
		${fe:facetForm()}${fe:geoForm()}
		<header>
			<nav class="fads-topnav fads-topnav-fixed">
				<div id="content" class="container">
					<div class="fads-topnav-brand"></div>
					<button class="fads-navbar-toggler" type="button" data-fads-target="#navbar"
							aria-controls="navbar" aria-expanded="false" aria-label="Toggle navigation">
						<span class="fads-navbar-toggler-icon"></span>
					</button>
					<div class="fads-collapse" id="navbar">
						<div></div>
						<ul class="nav">
							<c:if test="${eoled}">
								<li class="nav-item" title="<la:message key="labels.eol_error" />">
									<a class="nav-link active" href="${eolLink}" target="_olh"><i class="fas fa-times-circle fads-text-danger" aria-hidden="true"></i></a>
								</li>
							</c:if>
							<c:if test="${developmentMode}">
								<li class="nav-item"
									title="<la:message key="labels.development_mode_warning" />"
								><a class="nav-link active" href="${installationLink}" target="_olh"><i class="fa fa-exclamation-triangle fads-text-warning" aria-hidden="true"
									></i></a></li>
							</c:if>
							<c:choose>
								<c:when test="${!empty username && username != 'guest'}">
									<li class="nav-item">
										<div class="fads-dropdown">
											<a id="userMenu" class="nav-link" data-fads-dropdown
												href="#" role="button" aria-haspopup="true"
												aria-expanded="false"> <i class="fa fa-fw fa-user" aria-hidden="true"></i>${username}
											</a>
											<div class="fads-dropdown-menu" aria-labelledby="userMenu">
												<c:if test="${editableUser == true}">
													<la:link href="/profile" styleClass="fads-dropdown-item">
														<la:message key="labels.profile" />
													</la:link>
												</c:if>
												<c:if test="${adminUser == true}">
													<la:link href="/admin" styleClass="fads-dropdown-item">
														<la:message key="labels.administration" />
													</la:link>
												</c:if>
												<la:link href="/logout/" styleClass="fads-dropdown-item">
													<la:message key="labels.logout" />
												</la:link>
											</div>
										</div>
									</li>
								</c:when>
								<c:when test="${ pageLoginLink }">
									<li class="nav-item"><la:link href="/login"
											styleClass="nav-link" role="button" aria-haspopup="true"
											aria-expanded="false">
											<i class="fa fa-fw fa-sign-in" aria-hidden="true"></i>
											<la:message key="labels.login" />
										</la:link></li>
								</c:when>
							</c:choose>
							<c:if test="${chatEnabled}">
								<li class="nav-item"><la:link href="/chat"
										styleClass="nav-link" role="button" aria-haspopup="true"
										aria-expanded="false">
										<i class="fa fa-fw fa-robot" aria-hidden="true"></i>
										<la:message key="labels.chat_ai_mode" />
									</la:link></li>
							</c:if>
							<li class="nav-item"><la:link href="/help"
									styleClass="nav-link help-link">
									<i class="fa fa-fw fa-question-circle" aria-hidden="true"></i>
									<la:message key="labels.index_help" />
								</la:link></li>
						</ul>
					</div>
				</div>
			</nav>
		</header>
		<div id="searchOptions" class="fads-collapse">
			<div class="container">
				<jsp:include page="searchOptions.jsp" />
				<div>
					<button type="button" class="fads-btn fads-btn-default" id="searchOptionsClearButton">
						<la:message key="labels.search_options_clear" />
					</button>
					<button type="submit" class="fads-btn fads-btn-primary">
						<i class="fa fa-search" aria-hidden="true"></i>
						<la:message key="labels.search" />
					</button>
					<la:link href="/search/advance" styleClass="fads-btn fads-btn-info">
						<i class="fa fa-cog" aria-hidden="true"></i>
						<la:message key="labels.advance" />
					</la:link>
				</div>
			</div>
		</div>
		<main class="container">
			<div class="fads-row">
				<div class="fads-col fads-text-center searchFormBox">
					<h1 class="mainLogo">
						<img src="${fe:url('/images/logo.png')}"
							alt="<la:message key="labels.index_title" />" />
					</h1>
					<div class="notification">${notification}</div>
					<div>
						<la:info id="msg" message="true">
							<div class="fads-banner fads-banner-info">${msg}</div>
						</la:info>
						<la:errors header="errors.front_header"
							footer="errors.front_footer" prefix="errors.front_prefix"
							suffix="errors.front_suffix" />
					</div>
					<fieldset>
						<legend><la:message key="labels.search" /></legend>
						<div class="fads-clearfix">
							<div style="margin:0 auto" class="fads-col-10 fads-col-sm-8 fads-col-md-8 fads-col-lg-6">
								<la:text styleClass="query fads-textfield"
									property="q" size="50" maxlength="1000" styleId="contentQuery"
									autocomplete="off" />
							</div>
						</div>
						<c:if test="${!empty popularWords}">
							<div class="fads-clearfix">
								<p class="fads-text-truncate">
									<la:message key="labels.search_popular_word_word" />
									<c:forEach var="item" varStatus="s" items="${popularWords}">
										<c:if test="${s.index < 3}">
											<la:link
												href="/search?q=${f:u(item)}${fe:facetQuery()}${fe:geoQuery()}">${f:h(item)}</la:link>
										</c:if>
										<c:if test="${3 <= s.index}">
											<la:link styleClass="fads-d-none fads-d-sm-inline-block"
												href="/search?q=${f:u(item)}${fe:facetQuery()}${fe:geoQuery()}">${f:h(item)}</la:link>
										</c:if>
									</c:forEach>
								</p>
							</div>
						</c:if>
						<div class="fads-clearfix searchButtonBox btn-group">
							<button type="submit" name="search" id="searchButton"
								class="fads-btn fads-btn-primary">
								<i class="fa fa-search" aria-hidden="true"></i>
								<la:message key="labels.index_form_search_btn" />
							</button>
							<button type="button" class="fads-btn fads-btn-subtle"
								data-fads-collapse="#searchOptions"
								id="searchOptionsButton">
								<i class="fa fa-cog" aria-hidden="true"></i>
								<la:message key="labels.index_form_option_btn" />
							</button>
						</div>
					</fieldset>
				</div>
			</div>
		</main>
		<jsp:include page="footer.jsp" />
	</la:form>
	<input type="hidden" id="contextPath" value="${contextPath}" />
	<script src="${fe:url('/js/jquery-3.7.1.min.js')}"></script>
	<script src="${fe:url('/js/fads-ui.js')}"></script>
	<script src="${fe:url('/js/suggestor.js')}"></script>
	<script src="${fe:url('/js/index.js')}"></script>
</body>
${fe:html(false)}