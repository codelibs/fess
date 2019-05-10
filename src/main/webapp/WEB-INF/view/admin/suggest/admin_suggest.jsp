<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><la:message key="labels.admin_brand_title" /> | <la:message
		key="labels.suggest_word_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="suggest" />
			<jsp:param name="menuType" value="suggestWord" />
		</jsp:include>
		<div class="content-wrapper">
			<section class="content-header">
				<h1>
					<la:message key="labels.suggest_word_title_details" />
				</h1>
			</section>
			<section class="content">
				<div class="row">
					<div class="col-md-12">
						<div class="box box-primary">
							<!-- /.box-header -->
							<div class="box-body">
								<%-- Message --%>
								<div>
									<la:info id="msg" message="true">
										<div class="alert alert-info">${msg}</div>
									</la:info>
									<la:errors />
								</div>
								<div class="row">
									<div class="col-sm-12">
										<la:form action="/admin/suggest/">
											<table class="table table-bordered table-striped">
												<thead>
													<tr>
														<th class="col-md-3"><la:message key="labels.suggest_word_type" /></th>
														<th class="col-md-2 text-center"><la:message key="labels.suggest_word_number" /></th>
														<th class="text-center"></th>
													</tr>
												</thead>
												<tbody>
													<tr>
														<td><la:message key="labels.suggest_word_type_all" /></td>
														<td class="text-right">${f:h(totalWordsNum)}</td>
														<td class="text-center">
															<button type="button" class="btn btn-danger" name="deleteAllWords"
																data-toggle="modal" data-target="#confirmToAllDelete"
																value="<la:message key="labels.design_delete_button" />">
																<i class="fa fa-trash"></i>
																<la:message key="labels.design_delete_button" />
															</button>
															<div class="modal modal-danger fade" id="confirmToAllDelete"
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
																				name="deleteAllWords"
																				value="<la:message key="labels.crud_button_delete" />">
																				<i class="fa fa-trash"></i>
																				<la:message key="labels.crud_button_delete" />
																			</button>
																		</div>
																	</div>
																</div>
															</div>
														</td>
													</tr>
													<tr>
														<td><la:message key="labels.suggest_word_type_document" /></td>
														<td class="text-right">${f:h(documentWordsNum)}</td>
														<td class="text-center">
															<button type="button" class="btn btn-danger" name="deleteDocumentWords"
																data-toggle="modal" data-target="#confirmToDocumentDelete"
																value="<la:message key="labels.design_delete_button" />">
																<i class="fa fa-trash"></i>
																<la:message key="labels.design_delete_button" />
															</button>
															<div class="modal modal-danger fade" id="confirmToDocumentDelete"
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
																				name="deleteDocumentWords"
																				value="<la:message key="labels.crud_button_delete" />">
																				<i class="fa fa-trash"></i>
																				<la:message key="labels.crud_button_delete" />
																			</button>
																		</div>
																	</div>
																</div>
															</div>
														</td>
													</tr>
													<tr>
														<td><la:message key="labels.suggest_word_type_query" /></td>
														<td class="text-right">${f:h(queryWordsNum)}</td>
														<td class="text-center">
															<button type="button" class="btn btn-danger" name="deleteQueryWords"
																data-toggle="modal" data-target="#confirmToQueryDelete"
																value="<la:message key="labels.design_delete_button" />">
																<i class="fa fa-trash"></i>
																<la:message key="labels.design_delete_button" />
															</button>
															<div class="modal modal-danger fade" id="confirmToQueryDelete"
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
																				name="deleteQueryWords"
																				value="<la:message key="labels.crud_button_delete" />">
																				<i class="fa fa-trash"></i>
																				<la:message key="labels.crud_button_delete" />
																			</button>
																		</div>
																	</div>
																</div>
															</div>
														</td>
													</tr>
												</tbody>
											</table>
										</la:form>
									</div>
								</div>
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
