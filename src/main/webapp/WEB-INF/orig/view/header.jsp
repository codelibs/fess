<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<la:form action="/search" method="get" styleId="searchForm"
	role="search">
	${fe:facetForm()}${fe:geoForm()}
	<header>
		<nav class="navbar navbar-expand-md navbar-dark fixed-top bg-dark">
			<div id="content" class="container">
				<la:link styleClass="navbar-brand d-inline-flex" href="/">
					<img src="${fe:url('/images/logo-head.png')}"
						alt="<la:message key="labels.header_brand_name" />"
						class="align-items-center" />
				</la:link>
				<div
					class="navbar-form col-md-6 col-sm-8 col-7 mr-auto p-0"
					role="search">
					<div class="input-group">
						<la:text property="q" maxlength="1000" styleId="query"
							styleClass="form-control" autocomplete="off" />
						<span class="input-group-append">
							<button type="submit" name="search" id="searchButton"
								class="btn btn-primary">
								<em class="fa fa-search"></em>
							</button>
							<button type="button" class="btn btn-light"
								data-toggle="control-options" data-target="#searchOptions"
								id="searchOptionsButton">
								<em class="fa fa-cog"></em> <span class="sr-only"><la:message
										key="labels.header_form_option_btn" /></span>
							</button>
						</span>
					</div>
				</div>
				<ul class="nav navbar-nav d-none d-md-flex">
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
									<a class="nav-link dropdown-toggle" data-toggle="dropdown"
										href="#" role="button" aria-haspopup="true"
										aria-expanded="false"> <em class="fa fa-fw fa-user"></em>
										<span>${username}</span>
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
							<li class="nav-item"><la:link href="/login"
									styleClass="nav-link  " role="button" aria-haspopup="true"
									aria-expanded="false">
									<em class="fa fa-fw fa-sign-in"></em>
									<span><la:message key="labels.login" /></span>
								</la:link></li>
						</c:when>
					</c:choose>
					<li class="nav-item"><la:link href="/help" styleClass="nav-link" role="help" aria-haspopup="true"
							aria-expanded="false">
							<em class="fa fa-fw fa-question-circle"></em>
							<span><la:message key="labels.index_help" /></span>
						</la:link></li>
				</ul>
			</div>
		</nav>
	</header>
	<div id="searchOptions" class="control-options">
		<div class="container">
			<jsp:include page="/WEB-INF/view/searchOptions.jsp" />
			<div>
				<button type="button" class="btn btn-light" id="searchOptionsClearButton">
					<la:message key="labels.search_options_clear" />
				</button>
				<button type="submit" class="btn btn-primary">
					<em class="fa fa-search"></em>
					<la:message key="labels.search" />
				</button>
				<la:link href="/search/advance?q=${f:u(q)}${fe:pagingQuery(null)}" styleClass="btn btn-info">
					<em class="fa fa-cog"></em>
					<la:message key="labels.advance" />
				</la:link>
			</div>
		</div>
	</div>
</la:form>
