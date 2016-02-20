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
						<la:errors property="_global" />
					</div>
					<div class="col-md-6">
						<div class="box box-primary">
							<la:form action="/admin/design/">
								<div class="box-header with-border">
									<h3 class="box-title">
										<la:message key="labels.design_title_file" />
									</h3>
								</div>
								<!-- /.box-header -->
								<div class="box-body">
									<div class="form-group">
										<la:errors property="fileName" />
										<la:select property="fileName" styleClass="form-control">
											<c:forEach var="item" varStatus="s" items="${fileNameItems}">
												<la:option value="${item}">${f:h(item)}</la:option>
											</c:forEach>
										</la:select>
									</div>
								</div>
								<!-- /.box-body -->
								<div class="box-footer">
									<button type="submit" class="btn btn-primary" name="download"
										value="<la:message key="labels.design_download_button" />">
										<i class="fa fa-download"></i>
										<la:message key="labels.design_download_button" />
									</button>
									<button type="button" class="btn btn-danger" name="delete"
										data-toggle="modal" data-target="#confirmToDelete"
										value="<la:message key="labels.design_delete_button" />">
										<i class="fa fa-trash"></i>
										<la:message key="labels.design_delete_button" />
									</button>
									<div class="modal modal-danger fade" id="confirmToDelete"
										tabindex="-1" role="dialog">
										<div class="modal-dialog">
											<div class="modal-content">
												<div class="modal-header">
													<button type="button" class="close" data-dismiss="modal"
														aria-label="Close">
														<span aria-hidden="true">×</span>
													</button>
													<h4 class="modal-title">
														<la:message key="labels.crud_title_delete" />
													</h4>
												</div>
												<div class="modal-body">
													<p>
														<la:message key="labels.crud_delete_confirmation" />
													</p>
												</div>
												<div class="modal-footer">
													<button type="button" class="btn btn-outline pull-left"
														data-dismiss="modal">
														<la:message key="labels.crud_button_cancel" />
													</button>
													<button type="submit" class="btn btn-outline btn-danger"
														name="delete"
														value="<la:message key="labels.crud_button_delete" />">
														<i class="fa fa-trash"></i>
														<la:message key="labels.crud_button_delete" />
													</button>
												</div>
											</div>
										</div>
									</div>
								</div>
								<!-- /.box-footer -->
							</la:form>
						</div>
						<!-- /.box -->
						<div class="box box-primary">
							<la:form action="/admin/design/">
								<div class="box-header with-border">
									<h3 class="box-title">
										<la:message key="labels.design_file_title_edit" />
									</h3>
								</div>
								<!-- /.box-header -->
								<div class="box-body">
									<div class="form-group">
										<la:errors property="fileName" />
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
											<%-- Login --%>
											<la:option value="login">
												<la:message key="labels.design_file_login" />
											</la:option>
											<%-- Profile --%>
											<la:option value="profile">
												<la:message key="labels.design_file_profile" />
											</la:option>
										</la:select>
									</div>
								</div>
								<!-- /.box-body -->
								<div class="box-footer">
									<button type="submit" class="btn btn-warning" name="edit"
										value="<la:message key="labels.design_edit_button" />">
										<i class="fa fa-pencil"></i>
										<la:message key="labels.design_edit_button" />
									</button>
									<button type="submit" class="btn btn-danger"
										name="editAsUseDefault"
										value="<la:message key="labels.design_use_default_button" />">
										<i class="fa fa-recycle"></i>
										<la:message key="labels.design_use_default_button" />
									</button>
								</div>
								<!-- /.box-footer -->
							</la:form>
						</div>
						<!-- /.box -->
					</div>
					<div class="col-md-6">
						<div class="box box-primary">
							<la:form action="/admin/design/upload/"
								enctype="multipart/form-data">
								<div class="box-header with-border">
									<h3 class="box-title">
										<la:message key="labels.design_title_file_upload" />
									</h3>
								</div>
								<!-- /.box-header -->
								<div class="box-body">
									<div class="form-group row">
										<label for="searchParams.url"
											class="col-sm-3 form-control-label"><la:message
												key="labels.design_file" /></label>
										<div class="col-sm-9">
											<la:errors property="designFile" />
											<input type="file" name="designFile" class="form-control" />
										</div>
									</div>
									<div class="form-group row">
										<label for="searchParams.url"
											class="col-sm-3 form-control-label"><la:message
												key="labels.design_file_name" /></label>
										<div class="col-sm-9">
											<la:errors property="designFileName" />
											<la:text property="designFileName" styleClass="form-control" />
										</div>
									</div>
								</div>
								<!-- /.box-body -->
								<div class="box-footer">
									<button type="submit" class="btn btn-success" name="upload"
										value="<la:message key="labels.design_button_upload" />">
										<i class="fa fa-upload"></i>
										<la:message key="labels.design_button_upload" />
									</button>
								</div>
								<!-- /.box-footer -->
							</la:form>
						</div>
						<!-- /.box -->
					</div>
				</div>
			</section>
		</div>
		<jsp:include page="/WEB-INF/view/common/admin/footer.jsp"></jsp:include>
	</div>
	<jsp:include page="/WEB-INF/view/common/admin/foot.jsp"></jsp:include>
</body>
</html>
