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
			<bean:message key="labels.web_authentication_title_confirm" />
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

		<%-- Confirm Form: BEGIN --%>
		<s:form>
			<html:hidden property="crudMode" />
			<div>
				<c:if test="${crudMode==2 || crudMode==3 || crudMode==4}">
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
							<td>${f:h(hostname)}<html:hidden property="hostname" /></td>
						</tr>
						<tr>
							<th><bean:message key="labels.web_authentication_port" /></th>
							<td>${f:h(port)}<html:hidden property="port" /></td>
						</tr>
						<tr>
							<th><bean:message key="labels.web_authentication_realm" /></th>
							<td>${f:h(authRealm)}<html:hidden property="authRealm" /></td>
						</tr>
						<tr>
							<th><bean:message key="labels.web_authentication_scheme" /></th>
							<td><c:forEach var="item" items="${protocolSchemeItems}">
									<c:if test="${protocolScheme==item.value}">${f:h(item.label)}</c:if>
								</c:forEach> <html:hidden property="protocolScheme" /></td>
						</tr>
						<tr>
							<th><bean:message key="labels.web_authentication_username" /></th>
							<td>${f:h(username)}<html:hidden property="username" /></td>
						</tr>
						<tr>
							<th><bean:message key="labels.web_authentication_password" /></th>
							<td><c:if test="${password!=''}">******</c:if> <html:hidden
									property="password" /></td>
						</tr>
						<tr>
							<th><bean:message key="labels.web_authentication_parameters" /></th>
							<td style="width: 345px; word-break: break-all;">${f:br(f:h(parameters))}<html:hidden
									property="parameters" /></td>
						</tr>
						<tr>
							<th><bean:message
									key="labels.web_authentication_web_crawling_config" /></th>
							<td><c:forEach var="item" items="${webConfigItems}">
									<c:if test="${webConfigId==item.value}">${f:h(item.label)}</c:if>
								</c:forEach> <html:hidden property="webConfigId" /></td>
						</tr>
					</tbody>
					<tfoot>
						<tr>
							<td colspan="2"><c:if test="${crudMode == 1}">
									<input type="submit" class="btn small" name="create"
										value="<bean:message key="labels.web_authentication_button_create"/>" />
									<input type="submit" class="btn small" name="editagain"
										value="<bean:message key="labels.web_authentication_button_back"/>" />
								</c:if> <c:if test="${crudMode == 2}">
									<input type="submit" class="btn small" name="update"
										value="<bean:message key="labels.web_authentication_button_update"/>" />
									<input type="submit" class="btn small" name="editagain"
										value="<bean:message key="labels.web_authentication_button_back"/>" />
								</c:if> <c:if test="${crudMode == 3}">
									<input type="submit" class="btn small" name="delete"
										value="<bean:message key="labels.web_authentication_button_delete"/>" />
									<input type="submit" class="btn small" name="back"
										value="<bean:message key="labels.web_authentication_button_back"/>" />
								</c:if> <c:if test="${crudMode == 4}">
									<input type="submit" class="btn small" name="back"
										value="<bean:message key="labels.web_authentication_button_back"/>" />
									<input type="submit" class="btn small" name="editfromconfirm"
										value="<bean:message key="labels.web_authentication_button_edit"/>" />
									<input type="submit" class="btn small" name="deletefromconfirm"
										value="<bean:message key="labels.web_authentication_button_delete"/>" />
								</c:if></td>
						</tr>
					</tfoot>
				</table>
			</div>
		</s:form>
		<%-- Confirm Form: BEGIN --%>

	</tiles:put>
</tiles:insert>
