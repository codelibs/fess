<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><la:message key="labels.admin_brand_title" /> | <la:message
		key="labels.storage_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="system" />
			<jsp:param name="menuType" value="storage" />
		</jsp:include>
		<div class="content-wrapper">
			<section class="content-header">
				<h1>
					<la:message key="labels.storage_configuration" />
				</h1>
				<jsp:include page="/WEB-INF/view/common/admin/crud/breadcrumb.jsp"></jsp:include>
			</section>
			<section class="content">
				<div class="row">
					<div class="col-md-12">
						<div class="box box-primary">
							<div class="box-header with-border">
								<h3 class="box-title">
									<a aria-hidden="true" href="${contextPath}/admin/storage/"><i class="fas fa-database" aria-hidden="true"></i>${f:h(bucket)}</a>
									<c:forEach var="item" varStatus="s" items="${pathItems}">
										/ <span><a href="${contextPath}/admin/storage/list/${f:u(item.id)}/">${f:h(item.name)}</a></span>
									</c:forEach>
									/ <a data-toggle="modal" data-target="#createDir"> <i class="fas fa-folder" aria-hidden="true"></i></a>
								</h3>
								<span class="pull-right"> <a data-toggle="modal" data-target="#uploadeFile"><i class="fa fa-upload"
										aria-hidden="true"
									></i></a>
								</span>
							</div>
							<!-- /.box-header -->
							<div class="box-body">
								<%-- Message --%>
								<div>
									<la:info id="msg" message="true">
										<div class="alert alert-info">${msg}</div>
									</la:info>
									<la:errors />
								</div>
								<%-- List --%>
								<div class="data-wrapper">
									<div class="modal modal-primary" id="createDir" tabindex="-1" role="dialog">
										<div class="modal-dialog">
											<div class="modal-content">
												<la:form action="/admin/storage/createDir/" enctype="multipart/form-data" styleClass="form-inline">
													<input type="hidden" name="path" value="${path}" />
													<div class="modal-header">
														<button type="button" class="close" data-dismiss="modal" aria-label="Close">
															<span aria-hidden="true">&times;</span>
														</button>
														<h4 class="modal-title">
															<la:message key="labels.crud_title_create" />
														</h4>
													</div>
													<div class="modal-body">
														<div class="form-group">
															<label for="name" class="control-label"><la:message key="labels.storage_folder_name" /></label>
															<input type="text" name="name" class="form-control"/>
														</div>
													</div>
													<div class="modal-footer">
														<button type="button" class="btn btn-outline pull-left" data-dismiss="modal">
															<la:message key="labels.crud_button_cancel" />
														</button>
														<button type="submit" class="btn btn-success" name="createDir">
															<em class="fa fa-make"></em>
															<la:message key="labels.crud_button_create" />
														</button>
													</div>
												</la:form>
											</div>
										</div>
									</div>

									<div class="modal modal-primary" id="uploadeFile" tabindex="-1" role="dialog">
										<div class="modal-dialog">
											<div class="modal-content">
												<la:form action="/admin/storage/upload/" enctype="multipart/form-data" styleClass="form-inline">
													<input type="hidden" name="path" value="${path}" />
													<div class="modal-header">
														<button type="button" class="close" data-dismiss="modal" aria-label="Close">
															<span aria-hidden="true">&times;</span>
														</button>
														<h4 class="modal-title">
															<la:message key="labels.storage_upload_file" />
														</h4>
													</div>
													<div class="modal-body">
														<div class="form-group">
															<label for="uploadFile" class="control-label"><la:message key="labels.storage_file" /></label>
															<input type="file" name="uploadFile" class="form-control" />
														</div>
													</div>
													<div class="modal-footer">
														<button type="button" class="btn btn-outline pull-left" data-dismiss="modal">
															<la:message key="labels.crud_button_cancel" />
														</button>
														<button type="submit" class="btn btn-success" name="upload">
															<em class="fa fa-upload"></em>
															<la:message key="labels.storage_button_upload" />
														</button>
													</div>
												</la:form>
											</div>
										</div>
									</div>

									<div class="row">
										<div class="col-sm-12">
											<table class="table table-bordered table-striped dataTable">
												<tbody>
													<tr>
														<th><la:message key="labels.storage_name" /></th>
														<th class="col-md-1"><la:message key="labels.storage_size" /></th>
														<th class="col-md-3"><la:message key="labels.storage_last_modified" /></th>
														<th class="col-md-2"></th>
													</tr>
													<c:if test="${not empty path and not empty parentId}">
													<tr
														data-href="${contextPath}/admin/storage/list/${f:u(data.parentId)}/">
														<td>..</td>
														<td></td>
														<td></td>
													</tr></c:if>
													<c:if test="${not empty path and empty parentId}">
													<tr
														data-href="${contextPath}/admin/storage/">
														<td>..</td>
														<td></td>
														<td></td>
														<td></td>
													</tr></c:if>
													<c:forEach var="data" varStatus="s" items="${fileItems}">
														<c:if test="${not data.directory}">
														<tr>
															<td>
																<em class="far fa-file"></em>
																	${f:h(data.name)}
															</td>
															<td>${f:h(data.size)}</td>
															<td>${f:h(data.lastModified)}</td>
														</c:if>
														<c:if test="${data.directory.booleanValue()}">
														<tr
															data-href="${contextPath}/admin/storage/list/${f:h(data.id)}/">
															<td>
																<em class="fa fa-folder-open" style="color:#F7C502;"></em>
																	${f:h(data.name)}
															</td>
															<td></td>
															<td></td>
														</c:if>
														<td>
															<c:if test="${not data.directory}">
																<a class="btn btn-primary btn-xs" role="button" name="download" data-toggle="modal"
																		href="${contextPath}/admin/storage/download/${f:h(data.id)}/" download="${f:u(data.name)}"
																	value="<la:message key="labels.design_download_button" />"
																	>
																	<em class="fa fa-download"></em>
																	<la:message key="labels.design_download_button" />
																</a>
																<button type="button" class="btn btn-danger btn-xs" name="delete" data-toggle="modal"
																		data-target="#confirmToDelete-${f:h(data.hashCode)}"
																		value="<la:message key="labels.design_delete_button" />"
																>
																	<em class="fa fa-times"></em>
																	<la:message key="labels.design_delete_button" />
																</button>
																<div class="modal modal-danger fade" id="confirmToDelete-${f:h(data.hashCode)}"
																	 tabindex="-1" role="dialog"
																>
																	<div class="modal-dialog">
																		<div class="modal-content">
																			<div class="modal-header">
																				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
																					<span aria-hidden="true">×</span>
																				</button>
																				<h4 class="modal-title">
																					<la:message key="labels.crud_title_delete" /> : ${f:h(data.name)}
																				</h4>
																			</div>
																			<div class="modal-body">
																				<p>
																					<la:message key="labels.crud_delete_confirmation" />
																				</p>
																			</div>
																			<div class="modal-footer">
																				<button type="button" class="btn btn-outline pull-left" data-dismiss="modal">
																					<la:message key="labels.crud_button_cancel" />
																				</button>
																				<la:form action="${contextPath}/admin/storage/delete/${f:h(data.id)}/" styleClass="form-horizontal">
																					<button type="submit" class="btn btn-outline btn-danger" name="delete"
																							value="<la:message key="labels.crud_button_delete" />"
																					>
																						<em class="fa fa-trash"></em>
																						<la:message key="labels.crud_button_delete" />
																					</button>
																				</la:form>
																			</div>
																		</div>
																	</div>
																</div>
															</c:if>
														</td>
														</tr>
													</c:forEach>
												</tbody>
											</table>
										</div>
									</div>
								</div>
								<!-- /.data-wrapper -->
							</div>
							<!-- /.box-body -->
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
