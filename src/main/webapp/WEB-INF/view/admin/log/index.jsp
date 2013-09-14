<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><tiles:insert template="/WEB-INF/view/common/admin/layout.jsp"
	flush="true">
	<tiles:put name="title">
		<bean:message key="labels.log_configuration" />
	</tiles:put>
	<tiles:put name="header" value="/WEB-INF/view/common/admin/header.jsp" />
	<tiles:put name="footer" value="/WEB-INF/view/common/admin/footer.jsp" />
	<tiles:put name="menu" value="/WEB-INF/view/common/admin/menu.jsp" />
	<tiles:put name="menuType" value="log" />
	<tiles:put name="headerScript" type="string"></tiles:put>
	<tiles:put name="body" type="string">

		<h3>
			<bean:message key="labels.log_file_download_title" />
		</h3>

		<%-- Message: BEGIN --%>
		<div>
			<html:messages id="msg" message="true">
				<div class="alert-message info"><bean:write name="msg" ignore="true" /></div>
			</html:messages>
			<html:errors />
		</div>
		<%-- Message: END --%>

		<s:form>
			<div>
				<table class="bordered-table zebra-striped">
					<tbody>
						<tr>
							<th><bean:message key="labels.log_file_name" /></th>
							<th style="width: 200px;text-align: center;"><bean:message
									key="labels.log_file_date" /></th>
						</tr>
						<c:forEach var="logFile" varStatus="s" items="${logFileItems}">
							<tr class="${s.index % 2 == 0 ? 'row1' : 'row2'}">
								<td><s:link href="download/${f:u(logFile.logFileName)}">${f:h(logFile.name)}</s:link>
								</td>
								<td style="text-align: center;"><fmt:formatDate
										value="${logFile.lastModified}" type="BOTH" dateStyle="MEDIUM" />
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</s:form>

	</tiles:put>
</tiles:insert>
