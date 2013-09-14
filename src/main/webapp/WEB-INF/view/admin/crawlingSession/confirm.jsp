<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><tiles:insert template="/WEB-INF/view/common/admin/layout.jsp"
	flush="true">
	<tiles:put name="title">
		<bean:message key="labels.crawling_session_configuration" />
	</tiles:put>
	<tiles:put name="header" value="/WEB-INF/view/common/admin/header.jsp" />
	<tiles:put name="footer" value="/WEB-INF/view/common/admin/footer.jsp" />
	<tiles:put name="menu" value="/WEB-INF/view/common/admin/menu.jsp" />
	<tiles:put name="menuType" value="crawlingSession" />
	<tiles:put name="headerScript" type="string"></tiles:put>
	<tiles:put name="body" type="string">

		<h3>
			<bean:message key="labels.crawling_session_title_confirm" />
		</h3>

		<%-- Message: BEGIN --%>
		<div>
			<html:messages id="msg" message="true">
				<div class="alert-message info"><bean:write name="msg" ignore="true" /></div>
			</html:messages>
			<html:errors />
		</div>
		<%-- Message: END --%>

		<%-- Confirm Form: BEGIN --%>
		<s:form>
			<html:hidden property="crudMode" />
			<div>
				<html:hidden property="id" />
				<table class="bordered-table zebra-striped">
					<tbody>
						<tr>
							<th><bean:message key="labels.crawling_session_session_id" /></th>
							<td><html:link
									href="${f:url('/admin/searchList/search')}?query=segment:${f:u(sessionId)}">${f:h(sessionId)}</html:link>
								<html:hidden property="sessionId" /></td>
						</tr>
						<tr>
							<th><bean:message key="labels.crawling_session_name" /></th>
							<td>${f:h(sessionId)}</td>
						</tr>
						<c:forEach var="info" items="${crawlingSessionInfoItems}">
							<tr>
								<th>${f:h(info.keyMsg)}</th>
								<td>${f:h(info.value)}</td>
							</tr>
						</c:forEach>
					</tbody>
					<tfoot>
						<tr>
							<td colspan="2"><c:if test="${crudMode == 1}">
									<input type="submit" class="btn mini" name="back"
										value="<bean:message key="labels.crawling_session_button_back"/>" />
								</c:if> <c:if test="${crudMode == 2}">
									<input type="submit" class="btn mini" name="back"
										value="<bean:message key="labels.crawling_session_button_back"/>" />
								</c:if> <c:if test="${crudMode == 3}">
									<input type="submit" class="btn mini" name="delete"
										value="<bean:message key="labels.crawling_session_button_delete"/>" />
									<input type="submit" class="btn mini" name="back"
										value="<bean:message key="labels.crawling_session_button_back"/>" />
								</c:if> <c:if test="${crudMode == 4}">
									<input type="submit" class="btn mini" name="back"
										value="<bean:message key="labels.crawling_session_button_back"/>" />
									<input type="submit" class="btn mini" name="deletefromconfirm"
										value="<bean:message key="labels.crawling_session_button_delete"/>" />
								</c:if></td>
						</tr>
					</tfoot>
				</table>
			</div>
		</s:form>
		<%-- Confirm Form: BEGIN --%>

	</tiles:put>
</tiles:insert>
