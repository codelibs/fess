<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><la:message key="labels.admin_brand_title" /> | <la:message
		key="labels.dict_kuromoji_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="system" />
			<jsp:param name="menuType" value="dict" />
		</jsp:include>
		<div class="content-wrapper">

			<%-- Content Header --%>
			<section class="content-header">
				<h1>
					<la:message key="labels.dict_kuromoji_title" />
				</h1>
			</section>

			<section class="content">

				<%-- Form --%>
				<la:form styleClass="form-horizontal">
					<la:hidden property="crudMode" />
					<la:hidden property="dictId" />
					<c:if test="${crudMode==2}">
						<la:hidden property="id" />
					</c:if>
					<div class="row">
						<div class="col-md-12">
							<div
								class="box <c:if test="${crudMode == 1}">box-success</c:if><c:if test="${crudMode == 2}">box-warning</c:if>">
								<%-- Box Header --%>
								<div class="box-header with-border">
									<h3 class="box-title">
										<c:if test="${crudMode == 1}">
											<la:message key="labels.dict_kuromoji_link_create" />
										</c:if>
										<c:if test="${crudMode == 2}">
											<la:message key="labels.dict_kuromoji_link_update" />
										</c:if>
									</h3>
									<div class="btn-group pull-right">
										<la:link href="../" styleClass="btn btn-default btn-xs">
											<la:message key="labels.dict_list_link" />
										</la:link>
										<la:link href="list/1?dictId=${f:u(dictId)}"
											styleClass="btn btn-primary btn-xs">
											<la:message key="labels.dict_kuromoji_list_link" />
										</la:link>
										<la:link href="createpage/${f:u(dictId)}"
											styleClass="btn btn-success btn-xs">
											<la:message key="labels.dict_kuromoji_link_create" />
										</la:link>
										<la:link href="downloadpage/${f:u(dictId)}"
											styleClass="btn btn-primary btn-xs">
											<la:message key="labels.dict_kuromoji_link_download" />
										</la:link>
										<la:link href="uploadpage/${f:u(dictId)}"
											styleClass="btn btn-success btn-xs">
											<la:message key="labels.dict_kuromoji_link_upload" />
										</la:link>
									</div>
								</div>
								<%-- Box Body --%>
								<div class="box-body">
									<%-- Message --%>
									<div>
										<la:info id="msg" message="true">
											<div class="alert alert-info">${msg}</div>
										</la:info>
										<la:errors />
									</div>

									<%-- Form Fields --%>
									<div class="form-group">
										<label for="token" class="col-sm-3 control-label"><la:message
												key="labels.dict_kuromoji_token" /></label>
										<div class="col-sm-9">
											<la:text property="token" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="segmentation" class="col-sm-3 control-label"><la:message
												key="labels.dict_kuromoji_segmentation" /></label>
										<div class="col-sm-9">
											<la:text property="segmentation" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="reading" class="col-sm-3 control-label"><la:message
												key="labels.dict_kuromoji_reading" /></label>
										<div class="col-sm-9">
											<la:text property="reading" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="pos" class="col-sm-3 control-label"><la:message
												key="labels.dict_kuromoji_pos" /></label>
										<div class="col-sm-9">
											<la:text property="pos" styleClass="form-control" />
										</div>
									</div>
								</div>

								<%-- Box Footer --%>
								<div class="box-footer">
									<c:if test="${crudMode == 1}">
										<button type="submit" class="btn" name="back"
											value="<la:message key="labels.dict_kuromoji_button_back" />">
											<la:message key="labels.dict_kuromoji_button_back" />
										</button>
										<button type="submit" class="btn btn-success"
											name="confirmfromcreate"
											value="<la:message key="labels.dict_kuromoji_button_confirm" />">
											<la:message key="labels.dict_kuromoji_button_confirm" />
										</button>
									</c:if>
									<c:if test="${crudMode == 2}">
										<button type="submit" class="btn" name="back"
											value="<la:message key="labels.dict_kuromoji_button_back" />">
											<la:message key="labels.dict_kuromoji_button_back" />
										</button>
										<button type="submit" class="btn btn-warning"
											name="confirmfromupdate"
											value="<la:message key="labels.dict_kuromoji_button_confirm" />">
											<la:message key="labels.dict_kuromoji_button_confirm" />
										</button>
									</c:if>
								</div>
							</div>
						</div>
					</div>
				</la:form>

			</section>
		</div>

		<jsp:include page="/WEB-INF/view/common/admin/footer.jsp"></jsp:include>
	</div>
	<jsp:include page="/WEB-INF/view/common/admin/foot.jsp"></jsp:include>
</body>
</html>
