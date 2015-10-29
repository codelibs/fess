<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><la:message key="labels.admin_brand_title" /> | <la:message
		key="labels.design_configuration" /></title>
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
						<la:info id="msg" message="true">
							<div class="alert alert-info">${msg}</div>
						</la:info>
						<la:errors />
					</div>
					<div class="col-md-6">
						<div class="box box-primary">
							<la:form>
								<%-- Box Header --%>
								<div class="box-header with-border">
									<h3 class="box-title">
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
									<div class="form-group">
										<la:select property="fileName" styleClass="form-control">
											<c:forEach var="item" varStatus="s" items="${fileNameItems}">
												<la:option value="${item}">${f:h(item)}</la:option>
											</c:forEach>
										</la:select>
									</div>
								</div>
								<%-- Box Footer --%>
								<div class="box-footer">
									<button type="submit" class="btn btn-primary" name="download"
										value="<la:message key="labels.design_download_button" />">
										<la:message key="labels.design_download_button" />
									</button>
									<button type="submit" class="btn btn-danger" name="delete"
										value="<la:message key="labels.design_delete_button" />"
										onclick="return confirmToDelete();">
										<la:message key="labels.design_delete_button" />
									</button>
								</div>
							</la:form>
						</div>
						<div class="box box-primary">
							<la:form>
								<%-- Box Header --%>
								<div class="box-header with-border">
									<h3 class="box-title">
										<la:message key="labels.design_file_title_edit" />
									</h3>
								</div>
								<%-- Box Body --%>
								<div class="box-body">
									<div class="form-group">
										<la:select property="fileName" styleClass="form-control">
											<la:option value="index">
												<la:message key="labels.design_file_index" />
											</la:option>
											<la:option value="header">
												<la:message key="labels.design_file_header" />
											</la:option>
											<la:option value="footer">
												<la:message key="labels.design_file_footer" />
											</la:option>
											<la:option value="search">
												<la:message key="labels.design_file_search" />
											</la:option>
											<la:option value="searchResults">
												<la:message key="labels.design_file_searchResults" />
											</la:option>
											<la:option value="searchNoResult">
												<la:message key="labels.design_file_searchNoResult" />
											</la:option>
											<la:option value="help">
												<la:message key="labels.design_file_help" />
											</la:option>
											<la:option value="cache">
												<la:message key="labels.design_file_cache" />
											</la:option>
											<%-- Error --%>
											<la:option value="error">
												<la:message key="labels.design_file_error" />
											</la:option>
											<la:option value="errorHeader">
												<la:message key="labels.design_file_errorHeader" />
											</la:option>
											<la:option value="errorFooter">
												<la:message key="labels.design_file_errorFooter" />
											</la:option>
											<la:option value="errorNotFound">
												<la:message key="labels.design_file_errorNotFound" />
											</la:option>
											<la:option value="errorSystem">
												<la:message key="labels.design_file_errorSystem" />
											</la:option>
											<la:option value="errorRedirect">
												<la:message key="labels.design_file_errorRedirect" />
											</la:option>
											<la:option value="errorBadRequest">
												<la:message key="labels.design_file_errorBadRequest" />
											</la:option>
										</la:select>
									</div>
								</div>
								<%-- Box Footer --%>
								<div class="box-footer">
									<button type="submit" class="btn btn-warning" name="edit"
										value="<la:message key="labels.design_edit_button" />">
										<la:message key="labels.design_edit_button" />
									</button>
									<button type="submit" class="btn btn-danger"
										name="editAsUseDefault"
										value="<la:message key="labels.design_use_default_button" />">
										<la:message key="labels.design_use_default_button" />
									</button>
								</div>
							</la:form>
						</div>
					</div>
					<div class="col-md-6">
						<div class="box box-primary">
							<la:form action="upload" enctype="multipart/form-data">
								<%-- Box Header --%>
								<div class="box-header with-border">
									<h3 class="box-title">
										<la:message key="labels.design_title_file_upload" />
									</h3>
								</div>
								<%-- Box Body --%>
								<div class="box-body">
									<div class="form-group row">
										<label for="searchParams.url"
											class="col-sm-3 form-control-label"><la:message
												key="labels.design_file" /></label>
										<div class="col-sm-9">
											<input type="file" name="designFile" class="form-control" />
										</div>
									</div>
									<div class="form-group row">
										<label for="searchParams.url"
											class="col-sm-3 form-control-label"><la:message
												key="labels.design_file_name" /></label>
										<div class="col-sm-9">
											<la:text property="designFileName" styleClass="form-control" />
										</div>
									</div>
								</div>
								<%-- Box Footer --%>
								<div class="box-footer">
									<button type="submit" class="btn btn-success" name="upload"
										value="<la:message key="labels.design_button_upload" />">
										<la:message key="labels.design_button_upload" />
									</button>
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
