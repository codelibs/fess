<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div class="topbar">
	<div class="fill">
		<div class="container">
			<s:link styleClass="brand" href="/admin/index" title="Fess Home">
				<bean:message key="labels.header.logo_alt" />
			</s:link>
			<ul class="nav ">
			</ul>
			<ul class="nav secondary-nav">
				<li><s:link href="/admin/" title="Fess Home">
						<bean:message key="labels.header.home" />
					</s:link></li>
				<c:if test="${helpLink!=null}">
				<li><s:link href="${helpLink}" target="_olh">
						<bean:message key="labels.header.help" />
					</s:link></li>
				</c:if>
				<li><s:link href="${contextPath}/admin/logout">
						<bean:message key="labels.menu.logout" />
					</s:link></li>
			</ul>
			<s:form styleClass="pull-right" action="/admin/searchList/search"
				method="get">
				<html:text styleClass="input-large" property="query" title="Search"
					maxlength="1000" styleId="query" />
				<button class="btn medium primary" type="submit" name="search"
					id="searchButton">
					<bean:message key="labels.search" />
				</button>
			</s:form>
		</div>
	</div>
</div>

