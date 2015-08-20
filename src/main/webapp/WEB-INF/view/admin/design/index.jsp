<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Fess | <bean:message key="labels.design_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin2/head.jsp"></jsp:include>
</head>
<body class="skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin2/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin2/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="crawl" />
			<jsp:param name="menuType" value="design" />
		</jsp:include>

		<div class="content-wrapper">

			<%-- Content Header --%>
			<section class="content-header">
				<h1>
					<bean:message key="labels.design_configuration" />
				</h1>
			</section>
			
			<section class="content">	
				
				<div class="row">
					<div class="col-md-12">
						<div>
							<html:messages id="msg" message="true">
								<div class="alert-message info"><bean:write name="msg" ignore="true" /></div>
							</html:messages>
							<html:errors />
						</div>
						<div class="box">
							<s:form>
								<%-- Box Header --%>
								<div class="box-header with-border">
									<h3>
										<bean:message key="labels.design_title_file" />
									</h3>
								</div>
								<%-- Box Body --%>
								<div class="box-body">
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
									<html:select property="fileName">
										<c:forEach var="item" varStatus="s" items="${fileNameItems}">
											<html:option value="${item}">${f:h(item)}</html:option>
										</c:forEach>
									</html:select>
								</div>
								<%-- Box Footer --%>
								<div class="box-footer">
									<input type="submit" class="btn " name="download" value="<bean:message key="labels.design_download_button"/>" />
									<input type="submit" class="btn " name="delete" onclick="return confirmToDelete();" value="<bean:message key="labels.design_delete_button"/>"  />
								</div>
							</s:form>
						</div>
						<div class="box">
							<s:form action="upload" enctype="multipart/form-data">
								<%-- Box Header --%>
								<div class="box-header with-border">
									<h3>
										<bean:message key="labels.design_title_file_upload" />
									</h3>
								</div>
								<%-- Box Body --%>
								<div class="box-body">
									<table class="bordered-table zebra-striped">
										<tr>
											<th style="width: 200px;">
												<bean:message key="labels.design_file" />
											</th>
											<td style="text-align: center;">
												<input type="file" name="designFile" style="width: 330;" />
											</td>
										</tr>
										<tr>
											<th>
												<bean:message key="labels.design_file_name" />
											</th>
											<td style="text-align: center;">
												<html:text property="designFileName" style="width:98%;" />
											</td>
										</tr>
									</table>
								</div>
								<%-- Box Footer --%>
								<div class="box-footer">
									<input type="submit" class="btn " name="update"	value="<bean:message key="labels.design_button_upload"/>" />
								</div>
							</s:form>
						</div>
						<div class="box">
							<s:form>
								<%-- Box Header --%>
								<div class="box-header with-border">
									<h3>
										<bean:message key="labels.design_file_title_edit" />
									</h3>
								</div>
								<%-- Box Body --%>
								<div class="box-body">
									<html:select property="fileName" style="width:300px;">
										<html:option value="index"><bean:message key="labels.design_file_index" /></html:option>
										<html:option value="header"><bean:message key="labels.design_file_header" /></html:option>
										<html:option value="footer"><bean:message key="labels.design_file_footer" /></html:option>
										<html:option value="search"><bean:message key="labels.design_file_search" /></html:option>
										<html:option value="searchResults"><bean:message key="labels.design_file_searchResults" /></html:option>
										<html:option value="searchNoResult"><bean:message key="labels.design_file_searchNoResult" /></html:option>
										<html:option value="help"><bean:message key="labels.design_file_help" /></html:option>
										<html:option value="cache"><bean:message key="labels.design_file_cache" /></html:option>
										<%-- Applet --%>
										<html:option value="appletLauncher"><bean:message key="labels.design_file_appletLauncher" /></html:option>
										<%-- Error --%>
										<html:option value="error"><bean:message key="labels.design_file_error" /></html:option>
										<html:option value="errorHeader"><bean:message key="labels.design_file_errorHeader" /></html:option>
										<html:option value="errorFooter"><bean:message key="labels.design_file_errorFooter" /></html:option>
										<html:option value="errorNotFound"><bean:message key="labels.design_file_errorNotFound" /></html:option>
										<html:option value="errorSystem"><bean:message key="labels.design_file_errorSystem" /></html:option>
										<html:option value="errorRedirect"><bean:message key="labels.design_file_errorRedirect" /></html:option>
										<html:option value="errorBadRequest"><bean:message key="labels.design_file_errorBadRequest" /></html:option>
									</html:select>
								</div>
								<%-- Box Footer --%>
								<div class="box-footer">
									<input type="submit" class="btn " name="edit"
										value="<bean:message key="labels.design_edit_button"/>" />
									<input type="submit" class="btn "
										name="editAsUseDefault"
										value="<bean:message key="labels.design_use_default_button"/>" />
								</div>
							</s:form>
						</div>
					</div>
				</div>
				
			</section>
		</div>
		<jsp:include page="/WEB-INF/view/common/admin2/footer.jsp"></jsp:include>
	</div>
	<jsp:include page="/WEB-INF/view/common/admin2/foot.jsp"></jsp:include>
</body>
</html>