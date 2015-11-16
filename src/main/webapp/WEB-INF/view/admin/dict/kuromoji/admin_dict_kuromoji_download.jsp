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
					<li><la:link href="../list/0/?dictId=${f:u(dictId)}">
							<la:message key="labels.dict_kuromoji_list_link" />
						</la:link></li>
					<li class="active"><la:message
							key="labels.dict_kuromoji_link_download" /></li>
				</ol>
			</section>
			<section class="content">
				<la:form action="/admin/dict/kuromoji/">
					<la:hidden property="dictId" />
					<div class="row">
						<div class="col-md-12">
							<div class="box box-primary">
								<div class="box-header with-border">
									<h3 class="box-title">
										<la:message key="labels.dict_kuromoji_link_download" />
									</h3>
									<div class="btn-group pull-right">
										<la:link href="/admin/dict"
											styleClass="btn btn-default btn-xs">
											<i class="fa fa-book"></i>
											<la:message key="labels.dict_list_link" />
										</la:link>
										<la:link href="../list/0/?dictId=${f:u(dictId)}"
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
									<%-- Message --%>
									<div>
										<la:info id="msg" message="true">
											<div class="alert alert-info">${msg}</div>
										</la:info>
										<la:errors />
									</div>
									<div class="form-group">
										<label for="name" class="col-sm-12 control-label">${f:h(path)}</label>
									</div>
								</div>
								<!-- /.box-body -->
								<div class="box-footer">
									<button type="submit" class="btn btn-primary" name="download"
										value="<la:message key="labels.dict_kuromoji_button_download" />">
										<i class="fa fa-download"></i>
										<la:message key="labels.dict_kuromoji_button_download" />
									</button>
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
