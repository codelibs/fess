<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><tiles:insert template="/WEB-INF/view/common/admin/layout.jsp"
	flush="true">
	<tiles:put name="title">
		<bean:message key="labels.wizard_title_configuration" />
	</tiles:put>
	<tiles:put name="header" value="/WEB-INF/view/common/admin/header.jsp" />
	<tiles:put name="footer" value="/WEB-INF/view/common/admin/footer.jsp" />
	<tiles:put name="menu" value="/WEB-INF/view/common/admin/menu.jsp" />
	<tiles:put name="menuType" value="wizard" />
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

		<s:form>
			<div>
				<h3>
					<bean:message key="labels.wizard_crawling_config_title" />
				</h3>
				<table class="bordered-table zebra-striped" style="width: 500px;">
					<tbody>
						<tr>
							<th style="width: 200px;"><bean:message
									key="labels.wizard_crawling_config_name" /></th>
							<td><html:text property="crawlingConfigName"
									style="width:280;" /></td>
						</tr>
						<tr>
							<th><bean:message key="labels.wizard_crawling_config_path" />
							</th>
							<td><html:text property="crawlingConfigPath"
									style="width:280;" /></td>
						</tr>
						<tr>
							<th><bean:message key="labels.maxAccessCount" /></th>
							<td><html:text property="maxAccessCount" size="8" /></td>
						</tr>
						<tr>
							<th><bean:message key="labels.depth" /></th>
							<td><html:text property="depth" size="5" /></td>
						</tr>
					</tbody>
					<tfoot>
						<tr>
							<td colspan="2"><input type="submit" class="btn small" name="index"
								value="<bean:message key="labels.wizard_button_cancel"/>" /> <input
								type="submit" class="btn small" name="startCrawlingForm"
								value="<bean:message key="labels.wizard_button_skip"/>" /> <input
								type="submit" class="btn small" name="crawlingConfig"
								value="<bean:message key="labels.wizard_button_register_again"/>" />
								<input type="submit" class="btn small" name="crawlingConfigNext"
								value="<bean:message key="labels.wizard_button_register_next"/>" />
							</td>
						</tr>
					</tfoot>
				</table>
			</div>
		</s:form>


	</tiles:put>
</tiles:insert>
