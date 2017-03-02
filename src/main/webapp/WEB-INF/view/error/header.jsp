<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<la:form action="/search" method="get" styleId="searchForm"
		 role="search">
	${fe:facetForm()}${fe:geoForm()}
	<nav class="navbar navbar-dark bg-inverse navbar-fixed-top">
		<la:link styleClass="navbar-brand" href="/">
			<img src="${f:url('/images/logo-head.png')}"
				 alt="<la:message key="labels.header_brand_name" />" />
		</la:link>
		<div
				class="search-box navbar-form col-lg-5 col-md-6 col-sm-6 col-xs-8"
				role="search">
			<div class="input-group">
				<la:text property="q" maxlength="1000" styleId="query"
						 styleClass="form-control" autocomplete="off" />
				<span class="input-group-btn">
					<button type="submit" name="search" id="searchButton"
							class="btn btn-primary">
						<i class="fa fa-search"></i>
					</button>
					<button type="button" class="btn btn-default"
							data-toggle="control-options" data-target="#searchOptions"
							id="searchOptionsButton">
						<i class="fa fa-cog"></i> <span class="sr-only"><la:message
							key="labels.header_form_option_btn" /></span>
					</button>
				</span>
			</div>
		</div>
		<ul class="nav navbar-nav hidden-xs-down pull-right">
			<c:choose>
				<c:when test="${!empty username && username != 'guest'}">
					<li class="nav-item">
						<div class="dropdown">
							<a class="nav-link dropdown-toggle" data-toggle="dropdown"
							   href="#" role="button" aria-haspopup="true"
							   aria-expanded="false"> <i class="fa fa-user"></i>
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
						<i class="fa fa-sign-in"></i>
						<span><la:message key="labels.login" /></span>
					</la:link></li>
				</c:when>
			</c:choose>
			<li class="nav-item"><la:link href="/help" styleClass="nav-link" role="help" aria-haspopup="true"
										  aria-expanded="false">
				<i class="fa fa-question-circle"></i>
				<span><la:message key="labels.index_help" /></span>
			</la:link></li>
		</ul>
	</nav>
	<div id="searchOptions" class="control-options">
		<div class="container">
			<jsp:include page="../searchOptions.jsp" />
			<div>
				<button type="button" class="btn btn-secondary" id="searchOptionsClearButton">
					<la:message key="labels.search_options_clear" />
				</button>
				<button type="submit" class="btn btn-primary">
					<i class="fa fa-search"></i>
					<la:message key="labels.search" />
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
