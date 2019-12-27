<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><la:message key="labels.admin_brand_title" /> | <la:message
		key="labels.bad_word_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="suggest" />
			<jsp:param name="menuType" value="badWord" />
		</jsp:include>
		<div class="content-wrapper">
			<section class="content-header">
				<h1>
					<la:message key="labels.bad_word_configuration" />
				</h1>
				<ol class="breadcrumb">
					<li class="active"><la:link href="/admin/badword">
							<la:message key="labels.bad_word_link_list" />
						</la:link></li>
					<li class="active"><a href="#"><la:message
								key="labels.bad_word_link_upload" /></a></li>
				</ol>
			</section>
			<section class="content">
				<div class="row">
					<div class="col-md-12">
						<div class="box box-primary">
							<div class="box-header with-border">
								<h3 class="box-title">
									<la:message key="labels.bad_word_link_download" />
								</h3>
								<div class="btn-group pull-right">
									<la:link href="/admin/badword"
										styleClass="btn btn-default btn-xs">
										<em class="fa fa-th-list"></em>
										<la:message key="labels.bad_word_link_list" />
									</la:link>
									<la:link href="../createnew"
										styleClass="btn btn-success btn-xs ${f:h(editableClass)}">
										<em class="fa fa-plus"></em>
										<la:message key="labels.bad_word_link_create" />
									</la:link>
									<la:link href="../downloadpage"
										styleClass="btn btn-primary btn-xs">
										<em class="fa fa-download"></em>
										<la:message key="labels.bad_word_link_download" />
									</la:link>
									<la:link href="../uploadpage"
										styleClass="btn btn-success btn-xs ${f:h(editableClass)}">
										<em class="fa fa-upload"></em>
										<la:message key="labels.bad_word_link_upload" />
									</la:link>
								</div>
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
								<c:if test="${editable}">
								<la:form action="/admin/badword/upload/" enctype="multipart/form-data">
									<table class="table table-bordered">
										<tbody>
											<tr>
												<th class="col-sm-3"><la:message
														key="labels.bad_word_file" /></th>
												<td><input type="file" name="badWordFile" /></td>
											</tr>
										</tbody>
										<tfoot>
											<tr>
												<td colspan="2">
													<button type="submit" class="btn btn-success" name="upload"
														value="<la:message key="labels.bad_word_button_upload" />">
														<em class="fa fa-upload"></em>
														<la:message key="labels.bad_word_button_upload" />
													</button>
												</td>
											</tr>
										</tfoot>
									</table>
								</la:form>
								</c:if>
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

