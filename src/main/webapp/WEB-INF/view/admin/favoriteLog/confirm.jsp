<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><tiles:insert template="/WEB-INF/view/common/admin/layout.jsp"
	flush="true">
	<tiles:put name="title">
		<bean:message key="labels.favorite_log_configuration" />
	</tiles:put>
	<tiles:put name="header" value="/WEB-INF/view/common/admin/header.jsp" />
	<tiles:put name="footer" value="/WEB-INF/view/common/admin/footer.jsp" />
	<tiles:put name="menu" value="/WEB-INF/view/common/admin/menu.jsp" />
	<tiles:put name="menuType" value="favoriteLog" />
	<tiles:put name="headerScript" type="string"></tiles:put>
	<tiles:put name="body" type="string">


		<h3>
			<bean:message key="labels.favorite_log_title_confirm" />
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
				<table class="bordered-table zebra-striped">
					<tbody>



						<c:if test="${crudMode==2 || crudMode==3 || crudMode==4}">
							<tr>
								<th style="width: 150px;"><bean:message
										key="labels.favorite_log_id" /></th>
								<td style="width: 350px;">${f:h(id)}<html:hidden
										property="id" /></td>
							</tr>
						</c:if>
						<tr>
							<th><bean:message key="labels.favorite_log_url" /></th>
							<td>${f:h(url)}<html:hidden property="url" /></td>
						</tr>
						<tr>
							<th><bean:message key="labels.favorite_log_created_time" /></th>
							<td>${f:h(createdTime)}<html:hidden
									property="createdTime" /></td>
						</tr>
					</tbody>
					<tfoot>
						<tr>
							<td colspan="2"><c:if test="${crudMode == 3}">
									<input type="submit" class="btn small" name="delete"
										value="<bean:message key="labels.crud_button_delete"/>" />
									<input type="submit" class="btn small" name="back"
										value="<bean:message key="labels.crud_button_back"/>" />
								</c:if> <c:if test="${crudMode == 4}">
									<input type="submit" class="btn small" name="back"
										value="<bean:message key="labels.crud_button_back"/>" />
									<input type="submit" class="btn small" name="deletefromconfirm"
										value="<bean:message key="labels.crud_button_delete"/>" />
								</c:if></td>
						</tr>
					</tfoot>
				</table>
			</div>
		</s:form>
		<%-- Confirm Form: END --%>

	</tiles:put>
</tiles:insert>
