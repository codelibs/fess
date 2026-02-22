<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<la:form action="/search" method="get" styleId="searchForm"
	role="search">
	${fe:facetForm()}${fe:geoForm()}
	<header>
		<nav class="fads-topnav fads-topnav-fixed fads-d-print-none">
			<div id="content" class="container">
				<la:link styleClass="fads-topnav-brand fads-d-inline-flex" href="/">
					<img src="${fe:url('/images/logo-head.png')}"
						alt="<la:message key="labels.header_brand_name" />"
						class="align-items-center" />
				</la:link>
			<c:if test="${!chatPage}">
				<div
					class="fads-d-flex fads-col-md-6 fads-col-sm-8 fads-col-7 fads-p-0"
					role="search">
					<div class="fads-input-group">
						<la:text property="q" maxlength="1000" styleId="query"
							styleClass="fads-textfield" autocomplete="off" />
						<button type="submit" name="search" id="searchButton"
							class="fads-btn fads-btn-primary">
							<i class="fa fa-search" aria-hidden="true"></i>
						</button>
						<button type="button" class="fads-btn fads-btn-default"
							data-fads-collapse="#searchOptions"
							id="searchOptionsButton">
							<i class="fa fa-cog" aria-hidden="true"></i> <span class="fads-visually-hidden"><la:message
									key="labels.header_form_option_btn" /></span>
						</button>
					</div>
				</div>
			</c:if>
				<ul class="nav fads-d-none fads-d-md-flex">
					<c:if test="${eoled}">
						<li class="nav-item" title="<la:message key="labels.eol_error" />">
							<a class="nav-link active" href="${eolLink}" target="_olh"><i class="fas fa-times-circle fads-text-danger" aria-hidden="true"></i></a>
						</li>
					</c:if>
					<c:if test="${developmentMode}">
						<li class="nav-item"
							title="<la:message key="labels.development_mode_warning" />"
						><a class="nav-link active" href="${installationLink}" target="_olh"><i
								class="fa fa-exclamation-triangle fads-text-warning" aria-hidden="true"
							></i></a></li>
					</c:if>
					<c:choose>
						<c:when test="${!empty username && username != 'guest'}">
							<li class="nav-item">
								<div class="fads-dropdown">
									<a id="userMenu" class="nav-link" data-fads-dropdown
										href="#" role="button" aria-haspopup="true"
										aria-expanded="false"> <i class="fa fa-fw fa-user" aria-hidden="true"></i>
										<span>${username}</span>
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
									<span><la:message key="labels.login" /></span>
								</la:link></li>
						</c:when>
					</c:choose>
					<c:choose>
						<c:when test="${chatPage}">
							<li class="nav-item"><la:link href="/" styleClass="nav-link" role="button" aria-haspopup="true"
									aria-expanded="false">
									<i class="fa fa-fw fa-search" aria-hidden="true"></i>
									<span><la:message key="labels.search" /></span>
								</la:link></li>
						</c:when>
						<c:when test="${chatEnabled}">
							<li class="nav-item"><la:link href="/chat" styleClass="nav-link" role="button" aria-haspopup="true"
									aria-expanded="false">
									<i class="fa fa-fw fa-robot" aria-hidden="true"></i>
									<span><la:message key="labels.chat_ai_mode" /></span>
								</la:link></li>
						</c:when>
					</c:choose>
					<li class="nav-item"><la:link href="/help" styleClass="nav-link" role="help" aria-haspopup="true"
							aria-expanded="false">
							<i class="fa fa-fw fa-question-circle" aria-hidden="true"></i>
							<span><la:message key="labels.index_help" /></span>
						</la:link></li>
				</ul>
			</div>
		</nav>
	</header>
	<c:if test="${!chatPage}">
	<div id="searchOptions" class="fads-collapse">
		<div class="container">
			<jsp:include page="/WEB-INF/view/searchOptions.jsp" />
			<div>
				<button type="button" class="fads-btn fads-btn-default" id="searchOptionsClearButton">
					<la:message key="labels.search_options_clear" />
				</button>
				<button type="submit" class="fads-btn fads-btn-primary">
					<i class="fa fa-search" aria-hidden="true"></i>
					<la:message key="labels.search" />
				</button>
				<la:link href="/search/advance?q=${f:u(q)}${fe:pagingQuery(null)}" styleClass="fads-btn fads-btn-info">
					<i class="fa fa-cog" aria-hidden="true"></i>
					<la:message key="labels.advance" />
				</la:link>
			</div>
		</div>
	</div>
	</c:if>
</la:form>