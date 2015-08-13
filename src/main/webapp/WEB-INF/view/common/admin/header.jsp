<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div class="topbar">
	<div class="fill">
		<div class="container">
			<la:link styleClass="brand" href="/admin/index" title="Fess Home">
				<la:message key="labels.header.logo_alt" />
			</la:link>
			<ul class="nav ">
			</ul>
			<ul class="nav secondary-nav">
				<li><la:link href="/admin/" title="Fess Home">
						<la:message key="labels.header.home" />
					</la:link></li>
				<c:if test="${helpLink!=null}">
				<li><la:link href="${helpLink}" target="_olh">
						<la:message key="labels.header.help" />
					</la:link></li>
				</c:if>
				<li><la:link href="${contextPath}/admin/logout">
						<la:message key="labels.menu.logout" />
					</la:link></li>
			</ul>
			<s:form styleClass="pull-right" action="/admin/searchList/search"
				method="get">
				<html:text styleClass="input-large" property="query" title="Search"
					maxlength="1000" styleId="query" />
				<button class="btn medium primary" type="submit" name="search"
					id="searchButton">
					<la:message key="labels.search" />
				</button>
			</s:form>
		</div>
	</div>
</div>

