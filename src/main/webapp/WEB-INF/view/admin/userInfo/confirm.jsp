<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><tiles:insert template="/WEB-INF/view/common/admin/layout.jsp"
	flush="true">
	<tiles:put name="title">
		<bean:message key="labels.user_info_configuration" />
	</tiles:put>
	<tiles:put name="header" value="/WEB-INF/view/common/admin/header.jsp" />
	<tiles:put name="footer" value="/WEB-INF/view/common/admin/footer.jsp" />
	<tiles:put name="menu" value="/WEB-INF/view/common/admin/menu.jsp" />
	<tiles:put name="menuType" value="userInfo" />
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

<%-- Confirm Form: BEGIN --%>
	<s:form>
		<html:hidden property="crudMode"/>
		<div>
		<c:if test="${crudMode==2 || crudMode==3 || crudMode==4}">
		<html:hidden property="id"/>
		</c:if>
		<h3>
			<bean:message key="labels.user_info_title_confirm" />
		</h3>
		<table class="bordered-table zebra-striped">
			<tbody>
                <tr>
                    <th><bean:message key="labels.user_info_edit_code"/></th>
                    <td>${f:h(code)}<html:hidden property="code"/></td>
                </tr>
					                <tr>
                    <th><bean:message key="labels.user_info_edit_createddate"/></th>
                    <td>${f:h(createdTime)}<html:hidden property="createdTime"/></td>
                </tr>
					                <tr>
                    <th><bean:message key="labels.user_info_edit_lastupdated"/></th>
                    <td>${f:h(updatedTime)}<html:hidden property="updatedTime"/></td>
                </tr>
			     

			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
        <c:if test="${crudMode == 1}">
            <input class="btn mini primary" type="submit" name="create" value="<bean:message key="labels.crud_button_create"/>"/>
            <input class="btn small" type="submit" name="editagain" value="<bean:message key="labels.crud_button_back"/>"/>
        </c:if>
        <c:if test="${crudMode == 2}">
            <input class="btn mini primary" type="submit" name="update" value="<bean:message key="labels.crud_button_update"/>"/>
            <input class="btn small" type="submit" name="editagain" value="<bean:message key="labels.crud_button_back"/>"/>
        </c:if>
        <c:if test="${crudMode == 3}">
            <input class="btn mini primary" type="submit" name="delete" value="<bean:message key="labels.crud_button_delete"/>"/>
            <input class="btn small" type="submit" name="back" value="<bean:message key="labels.crud_button_back"/>"/>
        </c:if>
        <c:if test="${crudMode == 4}">
            <input class="btn mini primary" type="submit" name="back" value="<bean:message key="labels.crud_button_back"/>"/>
            <input class="btn mini primary" type="submit" name="editfromconfirm" value="<bean:message key="labels.crud_button_edit"/>"/>
            <input class="btn small" type="submit" name="deletefromconfirm" value="<bean:message key="labels.crud_button_delete"/>"/>
        </c:if>
					</td>
				</tr>
			</tfoot>
		</table>
		</div>
	</s:form>
<%-- Confirm Form: BEGIN --%>

	</div>

	</tiles:put>
</tiles:insert>
