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
			<section class="content-header">
				<h1>
					<la:message key="labels.dict_kuromoji_title" />
				</h1>
				<ol class="breadcrumb">
					<li><la:link href="list">
							<la:message key="labels.dict_list_link" />
						</la:link></li>
					<li><la:link href="list/0/?dictId=${f:u(dictId)}">
							<la:message key="labels.dict_kuromoji_list_link" />
						</la:link></li>
					<c:if test="${crudMode == 1}">
						<li class="active"><la:message
								key="labels.dict_kuromoji_link_create" /></li>
					</c:if>
					<c:if test="${crudMode == 2}">
						<li class="active"><la:message
								key="labels.dict_kuromoji_link_edit" /></li>
					</c:if>
				</ol>
			</section>
			<section class="content">
				<la:form action="/admin/dict/kuromoji/" styleClass="form-horizontal">
					<la:hidden property="crudMode" />
					<la:hidden property="dictId" />
					<c:if test="${crudMode==2}">
						<la:hidden property="id" />
					</c:if>
					<div class="row">
						<div class="col-md-12">
							<div
								class="box <c:if test="${crudMode == 1}">box-success</c:if><c:if test="${crudMode == 2}">box-warning</c:if>">
								<div class="box-header with-border">
									<h3 class="box-title">
										<c:if test="${crudMode == 1}">
											<la:message key="labels.dict_kuromoji_link_create" />
										</c:if>
										<c:if test="${crudMode == 2}">
											<la:message key="labels.dict_kuromoji_link_edit" />
										</c:if>
									</h3>
									<div class="btn-group pull-right">
										<la:link href="/admin/dict"
											styleClass="btn btn-default btn-xs">
											<i class="fa fa-book"></i>
											<la:message key="labels.dict_list_link" />
										</la:link>
										<la:link href="../list/1?dictId=${f:u(dictId)}"
											styleClass="btn btn-primary btn-xs">
											<i class="fa fa-th-list"></i>
											<la:message key="labels.dict_kuromoji_list_link" />
										</la:link>
										<la:link href="../createnew/${f:u(dictId)}"
											styleClass="btn btn-success btn-xs">
											<i class="fa fa-plus"></i>
											<la:message key="labels.dict_kuromoji_link_create" />
										</la:link>
										<la:link href="../downloadpage/${f:u(dictId)}"
											styleClass="btn btn-primary btn-xs">
											<i class="fa fa-download"></i>
											<la:message key="labels.dict_kuromoji_link_download" />
										</la:link>
										<la:link href="../uploadpage/${f:u(dictId)}"
											styleClass="btn btn-success btn-xs">
											<i class="fa fa-upload"></i>
											<la:message key="labels.dict_kuromoji_link_upload" />
										</la:link>
									</div>
								</div>
								<!-- /.box-header -->
								<div class="box-body">
									<div>
										<la:info id="msg" message="true">
											<div class="alert alert-info">${msg}</div>
										</la:info>
										<la:errors property="_global" />
									</div>
									<div class="form-group">
										<label for="token" class="col-sm-3 control-label"><la:message
												key="labels.dict_kuromoji_token" /></label>
										<div class="col-sm-9">
											<la:errors property="token" />
											<la:text property="token" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="segmentation" class="col-sm-3 control-label"><la:message
												key="labels.dict_kuromoji_segmentation" /></label>
										<div class="col-sm-9">
											<la:errors property="segmentation" />
											<la:text property="segmentation" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="reading" class="col-sm-3 control-label"><la:message
												key="labels.dict_kuromoji_reading" /></label>
										<div class="col-sm-9">
											<la:errors property="reading" />
											<la:text property="reading" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="pos" class="col-sm-3 control-label"><la:message
												key="labels.dict_kuromoji_pos" /></label>
										<div class="col-sm-9">
											<la:errors property="pos" />
											<la:text property="pos" styleClass="form-control" />
										</div>
									</div>
								</div>
								<!-- /.box-body -->
								<div class="box-footer">
									<jsp:include page="/WEB-INF/view/common/admin/crud/buttons.jsp"></jsp:include>
								</div>
								<!-- /.box-footer -->
							</div>
							<!-- /.box -->
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
