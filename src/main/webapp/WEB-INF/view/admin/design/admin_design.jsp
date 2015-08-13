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
						<la:message key="labels.design_title_file" />
					</h3>
					<script>
					<!--
						function confirmToDelete() {
							if (confirm('<la:message key="labels.design_delete_confirmation"/>')) {
								return true;
							} else {
								return false;
							}
						}
					// -->
					</script>
					<la:form>
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
											value="<la:message key="labels.design_download_button"/>" />
										<input type="submit" class="btn "
											name="delete" onclick="return confirmToDelete();"
											value="<la:message key="labels.design_delete_button"/>"  />
									</td>
								</tr>
						</table>
					</la:form>
				</div>

				<div style="margin-top: 5px;">
					<h3>
						<la:message key="labels.design_title_file_upload" />
					</h3>
					<la:form action="upload" enctype="multipart/form-data">
						<table class="bordered-table zebra-striped">
							<tbody>
							<tr>
								<th style="width: 200px;"><la:message
											key="labels.design_file" /></th>
									<td style="text-align: center;"><input type="file"
										name="designFile" style="width: 330;" /></td>
								</tr>
								<tr>
									<th><la:message key="labels.design_file_name" /></th>
									<td style="text-align: center;"><la:text
											property="designFileName" style="width:98%;" /></td>
								</tr>
							</tbody>
							<tfoot>
								<tr>
									<td colspan="2" style="text-align:center;"><input type="submit" class="btn "
										name="update"
										value="<la:message key="labels.design_button_upload"/>" /></td>
								</tr>
							</tfoot>
						</table>
					</la:form>
				</div>

				<div style="margin-top: 5px;">
					<h3>
						<la:message key="labels.design_file_title_edit" />
					</h3>
					<la:form>
					<table class="bordered-table zebra-striped">
						<tbody>
							<tr>
								<th>
								<html:select property="fileName" style="width:300px;">
									<html:option value="index"><la:message key="labels.design_file_index" /></html:option>
									<html:option value="header"><la:message key="labels.design_file_header" /></html:option>
									<html:option value="footer"><la:message key="labels.design_file_footer" /></html:option>
									<html:option value="search"><la:message key="labels.design_file_search" /></html:option>
									<html:option value="searchResults"><la:message key="labels.design_file_searchResults" /></html:option>
									<html:option value="searchNoResult"><la:message key="labels.design_file_searchNoResult" /></html:option>
									<html:option value="help"><la:message key="labels.design_file_help" /></html:option>
									<html:option value="cache"><la:message key="labels.design_file_cache" /></html:option>
									<%-- Applet --%>
									<html:option value="appletLauncher"><la:message key="labels.design_file_appletLauncher" /></html:option>
									<%-- Error --%>
									<html:option value="error"><la:message key="labels.design_file_error" /></html:option>
									<html:option value="errorHeader"><la:message key="labels.design_file_errorHeader" /></html:option>
									<html:option value="errorFooter"><la:message key="labels.design_file_errorFooter" /></html:option>
									<html:option value="errorNotFound"><la:message key="labels.design_file_errorNotFound" /></html:option>
									<html:option value="errorSystem"><la:message key="labels.design_file_errorSystem" /></html:option>
									<html:option value="errorRedirect"><la:message key="labels.design_file_errorRedirect" /></html:option>
									<html:option value="errorBadRequest"><la:message key="labels.design_file_errorBadRequest" /></html:option>
								</html:select>
								</th>
								<td style="text-align: center;">
									<input type="submit" class="btn " name="edit"
										value="<la:message key="labels.design_edit_button"/>" />
									<input type="submit" class="btn "
										name="editAsUseDefault"
										value="<la:message key="labels.design_use_default_button"/>" />
								</td>
							</tr>
					</table>
					</la:form>
				</div>
			</c:if>
		</div>

	</tiles:put>
</tiles:insert>
