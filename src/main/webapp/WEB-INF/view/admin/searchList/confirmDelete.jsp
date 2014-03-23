<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><tiles:insert template="/WEB-INF/view/common/admin/layout.jsp"
	flush="true">
	<tiles:put name="title">
		<bean:message key="labels.search_list_configuration" />
	</tiles:put>
	<tiles:put name="header" value="/WEB-INF/view/common/admin/header.jsp" />
	<tiles:put name="footer" value="/WEB-INF/view/common/admin/footer.jsp" />
	<tiles:put name="menu" value="/WEB-INF/view/common/admin/menu.jsp" />
	<tiles:put name="menuType" value="searchList" />
	<tiles:put name="headerScript" type="string"></tiles:put>
	<tiles:put name="body" type="string">

		<h3>
			<bean:message key="labels.search_list_title_confirm_delete" />
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
			<html:hidden property="query" />
			<div>
				<table class="bordered-table zebra-striped">
					<tbody>
						<tr>
							<th style="width: 100px;"><bean:message
									key="labels.search_list_id" /></th>
							<td style="width: 400px;">${f:h(id)}<html:hidden
									property="id" /></td>
						</tr>
					</tbody>
					<tfoot>
						<tr>
							<td colspan="2"><input type="submit" class="btn small" name="delete"
								value="<bean:message key="labels.crud_button_delete"/>" /> <input
								type="submit" class="btn small" name="search"
								value="<bean:message key="labels.crud_button_back"/>" /></td>
						</tr>
					</tfoot>
				</table>
			</div>
		</s:form>
		<%-- Confirm Form: BEGIN --%>

	</tiles:put>
</tiles:insert>
