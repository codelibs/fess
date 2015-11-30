<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><la:message key="labels.admin_brand_title" /> | <la:message
		key="labels.wizard_title_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="system" />
			<jsp:param name="menuType" value="wizard" />
		</jsp:include>
		<div class="content-wrapper">
			<section class="content-header">
				<h1>
					<la:message key="labels.wizard_start_crawling_title" />
				</h1>
				<ol class="breadcrumb">
					<li><la:link href="/admin/wizard/">
							<la:message key="labels.wizard_start_title" />
						</la:link></li>
					<li><la:message key="labels.wizard_crawling_config_title" /></li>
					<li class="active"><la:message
							key="labels.wizard_start_crawling_title" /></li>
				</ol>
			</section>
			<section class="content">
				<la:form action="/admin/wizard/">
					<div class="row">
						<div class="col-md-12">
							<div class="box box-primary">
								<div class="box-header with-border">
									<h3 class="box-title">
										<la:message key="labels.wizard_start_crawler_title" />
									</h3>
								</div>
								<!-- /.box-header -->
								<div class="box-body">
									<div>
										<la:info id="msg" message="true">
											<div class="alert alert-info">${msg}</div>
										</la:info>
										<la:errors property="_global" />
									</div>
									<p class="span8">
										<la:message key="labels.wizard_start_crawling_desc" />
									</p>
								</div>
								<!-- /.box-body -->
								<div class="box-footer">
									<button type="submit" class="btn btn-success"
										name="startCrawling"
										value="<la:message key="labels.wizard_button_start_crawling"/>">
										<i class="fa fa-play-circle"></i>
										<la:message key="labels.wizard_button_start_crawling" />
									</button>
									<button type="submit" class="btn btn-default" name="index"
										value="<la:message key="labels.wizard_button_finish"/>">
										<i class="fa fa-step-forward"></i>
										<la:message key="labels.wizard_button_finish" />
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
