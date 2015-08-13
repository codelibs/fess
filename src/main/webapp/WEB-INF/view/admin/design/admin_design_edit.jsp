<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><tiles:insert template="/WEB-INF/view/common/admin/layout.jsp"
	flush="true">
	<tiles:put name="title">
		<la:message key="labels.design_configuration" />
	</tiles:put>
	<tiles:put name="header" value="/WEB-INF/view/common/admin/header.jsp" />
	<tiles:put name="footer" value="/WEB-INF/view/common/admin/footer.jsp" />
	<tiles:put name="menu" value="/WEB-INF/view/common/admin/menu.jsp" />
	<tiles:put name="menuType" value="design" />
	<tiles:put name="headerScript" type="string"></tiles:put>
	<tiles:put name="body" type="string">

		<%-- Message: BEGIN --%>
		<div>
			<html:messages id="msg" message="true">
				<div class="alert-message info"><bean:write name="msg" ignore="true" /></div>
			</html:messages>
			<html:errors />
		</div>
		<%-- Message: END --%>
		<c:if test="${editable}">
			<s:form>
				<div>
					<h3>
						<la:message key="labels.design_title_edit_content" />
					</h3>
					<h4>${f:h(fileName)}</h4>
					<div>
						<html:textarea
								property="content" style="width:98%" rows="20"></html:textarea>
					</div>
					<div style="margin-top:5px;">
						<input type="submit" class="btn small" name="update"
							value="<la:message key="labels.design_button_update"/>" />
						<input type="submit" class="btn small" name="back"
							value="<la:message key="labels.design_button_back"/>" />
					</div>
				</div>
				<html:hidden property="fileName" />
			</s:form>
		</c:if>

	</tiles:put>
</tiles:insert>
