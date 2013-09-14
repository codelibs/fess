<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><tiles:insert template="/WEB-INF/view/common/admin/layout.jsp" flush="true">
	<tiles:put name="title"><bean:message key="labels.wizard_title_configuration" /></tiles:put>
	<tiles:put name="header" value="/WEB-INF/view/common/admin/header.jsp" />
	<tiles:put name="footer" value="/WEB-INF/view/common/admin/footer.jsp" />
	<tiles:put name="menu" value="/WEB-INF/view/common/admin/menu.jsp" />
	<tiles:put name="menuType" value="wizard" />
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

	<s:form>
	<h3><bean:message key="labels.wizard_start_crawling_title"/></h3>
	<div>
		<p class="span8">
			<bean:message key="labels.wizard_start_crawling_desc"/>
		</p>
		<div class="span8">
			<input type="submit" class="btn small" name="startCrawling" value="<bean:message key="labels.wizard_button_start_crawling"/>"/>
			<input type="submit" class="btn small" name="index" value="<bean:message key="labels.wizard_button_finish"/>"/>
		</div>
	</div>
	</s:form>

      </div>

	</tiles:put>
</tiles:insert>
