<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Fess | <la:message key="labels.design_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="system" />
			<jsp:param name="menuType" value="design" />
		</jsp:include>

		<div class="content-wrapper">

			<%-- Content Header --%>
			<section class="content-header">
				<h1>
					<la:message key="labels.design_configuration" />
				</h1>
			</section>
			
			<section class="content">	
				
				<div class="row">
					<div class="col-md-12">
						<div>
							<la:info id="msg" message="true">
								<div class="alert-message info">${msg}</div>
							</la:info>
							<la:errors />
						</div>
						<div class="box">
							<la:form>
								<%-- Box Header --%>
								<div class="box-header with-border">
									<h3>
										<la:message key="labels.design_title_file" />
									</h3>
								</div>
								<%-- Box Body --%>
								<div class="box-body">
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
									<la:select property="fileName">
										<c:forEach var="item" varStatus="s" items="${fileNameItems}">
											<la:option value="${item}">${f:h(item)}</la:option>
										</c:forEach>
									</la:select>
								</div>
								<%-- Box Footer --%>
								<div class="box-footer">
									<input type="submit" class="btn " name="download" value="<la:message key="labels.design_download_button"/>" />
									<input type="submit" class="btn " name="delete" onclick="return confirmToDelete();" value="<la:message key="labels.design_delete_button"/>"  />
								</div>
							</la:form>
						</div>
						<div class="box">
							<la:form action="upload" enctype="multipart/form-data">
								<%-- Box Header --%>
								<div class="box-header with-border">
									<h3>
										<la:message key="labels.design_title_file_upload" />
									</h3>
								</div>
								<%-- Box Body --%>
								<div class="box-body">
									<table class="bordered-table zebra-striped">
										<tr>
											<th style="width: 200px;">
												<la:message key="labels.design_file" />
											</th>
											<td style="text-align: center;">
												<input type="file" name="designFile" style="width: 330;" />
											</td>
										</tr>
										<tr>
											<th>
												<la:message key="labels.design_file_name" />
											</th>
											<td style="text-align: center;">
												<la:text property="designFileName" style="width:98%;" />
											</td>
										</tr>
									</table>
								</div>
								<%-- Box Footer --%>
								<div class="box-footer">
									<input type="submit" class="btn " name="update"	value="<la:message key="labels.design_button_upload"/>" />
								</div>
							</la:form>
						</div>
						<div class="box">
							<la:form>
								<%-- Box Header --%>
								<div class="box-header with-border">
									<h3>
										<la:message key="labels.design_file_title_edit" />
									</h3>
								</div>
								<%-- Box Body --%>
								<div class="box-body">
									<la:select property="fileName" style="width:300px;">
										<la:option value="index"><la:message key="labels.design_file_index" /></la:option>
										<la:option value="header"><la:message key="labels.design_file_header" /></la:option>
										<la:option value="footer"><la:message key="labels.design_file_footer" /></la:option>
										<la:option value="search"><la:message key="labels.design_file_search" /></la:option>
										<la:option value="searchResults"><la:message key="labels.design_file_searchResults" /></la:option>
										<la:option value="searchNoResult"><la:message key="labels.design_file_searchNoResult" /></la:option>
										<la:option value="help"><la:message key="labels.design_file_help" /></la:option>
										<la:option value="cache"><la:message key="labels.design_file_cache" /></la:option>
										<%-- Error --%>
										<la:option value="error"><la:message key="labels.design_file_error" /></la:option>
										<la:option value="errorHeader"><la:message key="labels.design_file_errorHeader" /></la:option>
										<la:option value="errorFooter"><la:message key="labels.design_file_errorFooter" /></la:option>
										<la:option value="errorNotFound"><la:message key="labels.design_file_errorNotFound" /></la:option>
										<la:option value="errorSystem"><la:message key="labels.design_file_errorSystem" /></la:option>
										<la:option value="errorRedirect"><la:message key="labels.design_file_errorRedirect" /></la:option>
										<la:option value="errorBadRequest"><la:message key="labels.design_file_errorBadRequest" /></la:option>
									</la:select>
								</div>
								<%-- Box Footer --%>
								<div class="box-footer">
									<input type="submit" class="btn " name="edit"
										value="<la:message key="labels.design_edit_button"/>" />
									<input type="submit" class="btn "
										name="editAsUseDefault"
										value="<la:message key="labels.design_use_default_button"/>" />
								</div>
							</la:form>
						</div>
					</div>
				</div>
				
			</section>
		</div>
		<jsp:include page="/WEB-INF/view/common/admin/footer.jsp"></jsp:include>
	</div>
	<jsp:include page="/WEB-INF/view/common/admin/foot.jsp"></jsp:include>
</body>
</html>
