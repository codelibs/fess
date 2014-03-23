<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><tiles:insert template="/WEB-INF/view/common/admin/layout.jsp"
	flush="true">
	<tiles:put name="title">
		<bean:message key="labels.web_authentication_configuration" />
	</tiles:put>
	<tiles:put name="header" value="/WEB-INF/view/common/admin/header.jsp" />
	<tiles:put name="footer" value="/WEB-INF/view/common/admin/footer.jsp" />
	<tiles:put name="menu" value="/WEB-INF/view/common/admin/menu.jsp" />
	<tiles:put name="menuType" value="webAuthentication" />
	<tiles:put name="headerScript" type="string"></tiles:put>
	<tiles:put name="body" type="string">

		<h3>
			<bean:message key="labels.web_authentication_title_details" />
		</h3>

		<%-- Message: BEGIN --%>
		<div>
			<html:messages id="msg" message="true">
				<div class="alert-message info"><bean:write name="msg" ignore="true" /></div>
			</html:messages>
			<html:errors />
		</div>
		<%-- Message: END --%>

			<div>
				<ul class="pills">
					<li><s:link href="index">
							<bean:message key="labels.web_authentication_link_list" />
						</s:link></li>
					<c:if test="${crudMode == 1}">
					<li class="active"><a href="#"><bean:message
								key="labels.web_authentication_link_create" /></a></li>
					</c:if>
					<c:if test="${crudMode == 2}">
					<li class="active"><a href="#"><bean:message
								key="labels.web_authentication_link_update" /></a></li>
					</c:if>
					<c:if test="${crudMode == 3}">
					<li class="active"><a href="#"><bean:message
								key="labels.web_authentication_link_delete" /></a></li>
					</c:if>
					<c:if test="${crudMode == 4}">
					<li class="active"><a href="#"><bean:message
								key="labels.web_authentication_link_confirm" /></a></li>
					</c:if>
				</ul>
			</div>

		<%-- Edit Form: BEGIN --%>
		<s:form>
			<html:hidden property="crudMode" />
			<div>
				<c:if test="${crudMode==2}">
					<html:hidden property="id" />
					<html:hidden property="versionNo" />
				</c:if>
				<html:hidden property="createdBy" />
				<html:hidden property="createdTime" />
				<table class="bordered-table zebra-striped" style="width: 500px;">
					<tbody>
						<tr>
							<th style="width: 150px;"><bean:message
									key="labels.web_authentication_hostname" /></th>
							<td><html:text property="hostname" style="width:98%;" /></td>
						</tr>
						<tr>
							<th><bean:message key="labels.web_authentication_port" /></th>
							<td><html:text property="port" style="width:98%;" /></td>
						</tr>
						<tr>
							<th><bean:message key="labels.web_authentication_realm" /></th>
							<td><html:text property="authRealm" style="width:98%;" /></td>
						</tr>
						<tr>
							<th><bean:message key="labels.web_authentication_scheme" /></th>
							<td><html:select property="protocolScheme">
									<c:forEach var="item" items="${protocolSchemeItems}">
										<html:option value="${f:u(item.value)}">${f:h(item.label)}</html:option>
									</c:forEach>
								</html:select></td>
						</tr>
						<tr>
							<th><bean:message key="labels.web_authentication_username" /></th>
							<td><html:text property="username" style="width:98%;" /></td>
						</tr>
						<tr>
							<th><bean:message key="labels.web_authentication_password" /></th>
							<td><html:password property="password" style="width:98%;" /></td>
						</tr>
						<tr>
							<th><bean:message key="labels.web_authentication_parameters" /></th>
							<td><html:textarea property="parameters" style="width:98%;"
									rows="5" /></td>
						</tr>
						<tr>
							<th><bean:message
									key="labels.web_authentication_web_crawling_config" /></th>
							<td><html:select property="webCrawlingConfigId">
									<c:forEach var="item" items="${webCrawlingConfigItems}">
										<html:option value="${f:u(item.value)}">${f:h(item.label)}</html:option>
									</c:forEach>
								</html:select></td>
						</tr>
					</tbody>
					<tfoot>
						<tr>
							<td colspan="2"><c:if test="${crudMode == 1}">
									<input type="submit" class="btn small" name="confirmfromcreate"
										value="<bean:message key="labels.web_authentication_button_create"/>" />
									<input type="submit" class="btn small" name="back"
										value="<bean:message key="labels.web_authentication_button_back"/>" />
								</c:if> <c:if test="${crudMode == 2}">
									<input type="submit" class="btn small" name="confirmfromupdate"
										value="<bean:message key="labels.web_authentication_button_confirm"/>" />
									<input type="submit" class="btn small" name="back"
										value="<bean:message key="labels.web_authentication_button_back"/>" />
								</c:if></td>
						</tr>
					</tfoot>
				</table>
			</div>
		</s:form>
		<%-- Edit Form: BEGIN --%>

	</tiles:put>
</tiles:insert>
