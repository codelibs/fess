<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><tiles:insert template="/WEB-INF/view/common/admin/layout.jsp"
	flush="true">
	<tiles:put name="title">
		<bean:message key="labels.design_configuration" />
	</tiles:put>
	<tiles:put name="header" value="/WEB-INF/view/common/admin/header.jsp" />
	<tiles:put name="footer" value="/WEB-INF/view/common/admin/footer.jsp" />
	<tiles:put name="menu" value="/WEB-INF/view/common/admin/menu.jsp" />
	<tiles:put name="menuType" value="design" />
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

			<c:if test="${editable}">
				<div>
					<h3>
						<bean:message key="labels.design_title_file" />
					</h3>
					<script>
					<!--
						function confirmToDelete() {
							if (confirm('<bean:message key="labels.design_delete_confirmation"/>')) {
								return true;
							} else {
								return false;
							}
						}
					// -->
					</script>
					<s:form>
						<table class="bordered-table zebra-striped">
							<tbody>
								<tr>
									<th>
									<html:select property="fileName">
										<c:forEach var="item" varStatus="s" items="${fileNameItems}">
											<html:option value="${item}">${f:h(item)}</html:option>
										</c:forEach>
									</html:select>
									</th>
									<td style="text-align: center;">
										<input type="submit" class="btn " name="download"
											value="<bean:message key="labels.design_download_button"/>" />
										<input type="submit" class="btn "
											name="delete" onclick="return confirmToDelete();"
											value="<bean:message key="labels.design_delete_button"/>"  />
									</td>
								</tr>
						</table>
					</s:form>
				</div>

				<div style="margin-top: 5px;">
					<h3>
						<bean:message key="labels.design_title_file_upload" />
					</h3>
					<s:form action="upload" enctype="multipart/form-data">
						<table class="bordered-table zebra-striped">
							<tbody>
							<tr>
								<th style="width: 200px;"><bean:message
											key="labels.design_file" /></th>
									<td style="text-align: center;"><input type="file"
										name="designFile" style="width: 330;" /></td>
								</tr>
								<tr>
									<th><bean:message key="labels.design_file_name" /></th>
									<td style="text-align: center;"><html:text
											property="designFileName" style="width:98%;" /></td>
								</tr>
							</tbody>
							<tfoot>
								<tr>
									<td colspan="2" style="text-align:center;"><input type="submit" class="btn "
										name="update"
										value="<bean:message key="labels.design_button_upload"/>" /></td>
								</tr>
							</tfoot>
						</table>
					</s:form>
				</div>

				<div style="margin-top: 5px;">
					<h3>
						<bean:message key="labels.design_file_title_edit" />
					</h3>
					<s:form>
					<table class="bordered-table zebra-striped">
						<tbody>
							<tr>
								<th>
								<html:select property="fileName" style="width:300px;">
									<html:option value="index"><bean:message key="labels.design_file_index" /></html:option>
									<html:option value="header"><bean:message key="labels.design_file_header" /></html:option>
									<html:option value="footer"><bean:message key="labels.design_file_footer" /></html:option>
									<html:option value="search"><bean:message key="labels.design_file_search" /></html:option>
									<html:option value="searchResults"><bean:message key="labels.design_file_searchResults" /></html:option>
									<html:option value="searchNoResult"><bean:message key="labels.design_file_searchNoResult" /></html:option>
									<html:option value="help"><bean:message key="labels.design_file_help" /></html:option>
									<html:option value="error"><bean:message key="labels.design_file_error" /></html:option>
									<%-- Applet --%>
									<html:option value="appletLauncher"><bean:message key="labels.design_file_appletLauncher" /></html:option>
									<%-- Error --%>
									<html:option value="errorHeader"><bean:message key="labels.design_file_errorHeader" /></html:option>
									<html:option value="errorFooter"><bean:message key="labels.design_file_errorFooter" /></html:option>
									<html:option value="errorNotFound"><bean:message key="labels.design_file_errorNotFound" /></html:option>
									<html:option value="errorSystem"><bean:message key="labels.design_file_errorSystem" /></html:option>
									<html:option value="errorRedirect"><bean:message key="labels.design_file_errorRedirect" /></html:option>
									<html:option value="errorBadRequest"><bean:message key="labels.design_file_errorBadRequest" /></html:option>
								</html:select>
								</th>
								<td style="text-align: center;">
									<input type="submit" class="btn " name="edit"
										value="<bean:message key="labels.design_edit_button"/>" />
									<input type="submit" class="btn "
										name="editAsUseDefault"
										value="<bean:message key="labels.design_use_default_button"/>" />
								</td>
							</tr>
					</table>
					</s:form>
				</div>
			</c:if>
		</div>

	</tiles:put>
</tiles:insert>
