<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><tiles:insert template="/WEB-INF/view/common/admin/layout.jsp"
	flush="true">
	<tiles:put name="title">
		<bean:message key="labels.system_info_configuration" />
	</tiles:put>
	<tiles:put name="header" value="/WEB-INF/view/common/admin/header.jsp" />
	<tiles:put name="footer" value="/WEB-INF/view/common/admin/footer.jsp" />
	<tiles:put name="menu" value="/WEB-INF/view/common/admin/menu.jsp" />
	<tiles:put name="menuType" value="systemInfo" />
	<tiles:put name="headerScript" type="string"></tiles:put>
	<tiles:put name="body" type="string">

		<div id="main">

		<%-- Message: BEGIN --%>
		<div>
			<html:messages id="msg" message="true">
				<div class="alert-message info"><bean:write name="msg" ignore="true" /></div>
			</html:messages>
			<html:errors />
		</div>
		<%-- Message: END --%>

			<div>
				<h4>
					<bean:message key="labels.system_info_env_title" />
				</h4>
				<table>
					<tbody>
						<tr>
							<td><textarea id="envData"
									style="height: 300px;" class="xxlarge">
<c:forEach var="item" items="${envItems}">${f:h(item.label)}=${f:h(item.value)}
</c:forEach>
								</textarea></td>
						</tr>
					</tbody>
				</table>
			</div>

			<div style="margin-top: 5px;">
				<h4>
					<bean:message key="labels.system_info_prop_title" />
				</h4>
				<table>
					<tbody>
						<tr>
							<td><textarea id="propData"
									style="height: 300px;" class="xxlarge">
<c:forEach var="item" items="${propItems}">${f:h(item.label)}=${f:h(item.value)}
</c:forEach>
								</textarea></td>
						</tr>
					</tbody>
				</table>
			</div>

			<div style="margin-top: 5px;">
				<h4>
					<bean:message key="labels.system_info_fess_prop_title" />
				</h4>
				<table>
					<tbody>
						<tr>
<c:if test="${empty fessPropItems}">
							<td><textarea id="fessPropData"
									style="height: 300px;" class="xxlarge">
<bean:message key="labels.system_info_crawler_properties_does_not_exist" />
							    </textarea></td>
</c:if>
<c:if test="${!empty fessPropItems}">
							<td><textarea id="fessPropData"
									style="height: 300px;" class="xxlarge">
<c:forEach var="item" items="${fessPropItems}">${f:h(item.label)}=${f:h(item.value)}
</c:forEach>
								</textarea></td>
</c:if>
						</tr>
					</tbody>
				</table>
			</div>

			<div style="margin-top: 5px;">
				<h4>
					<bean:message key="labels.system_info_bug_report_title" />
				</h4>
				<table>
					<tbody>
						<tr>
							<td><textarea id="bugReportData"
									style="height: 300px;" class="xxlarge">
<c:forEach var="item" items="${bugReportItems}">${f:h(item.label)}=${f:h(item.value)}
</c:forEach>
								</textarea></td>
						</tr>
					</tbody>
				</table>
			</div>

		</div>

	</tiles:put>
</tiles:insert>
