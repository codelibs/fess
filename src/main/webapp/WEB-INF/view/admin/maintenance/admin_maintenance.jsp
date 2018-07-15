<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><la:message key="labels.admin_brand_title" /> | <la:message
		key="labels.maintenance_title_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="log" />
			<jsp:param name="menuType" value="maintenance" />
		</jsp:include>
		<div class="content-wrapper">
			<section class="content-header">
				<h1>
					<la:message key="labels.maintenance_title_configuration" />
				</h1>
			</section>
			<section class="content">
				<div class="row">
					<la:form action="/admin/maintenance/">
						<%-- Message: BEGIN --%>
						<div class="col-md-12">
							<la:info id="msg" message="true">
								<div class="alert alert-info">${msg}</div>
							</la:info>
							<la:errors />
						</div>
						<%-- Message: END --%>
						<div class="col-md-12">
							<div class="box box-primary">
								<div class="box-header with-border">
									<h3 class="box-title">
										<la:message key="labels.upgrade_reindex" />
									</h3>
								</div>
								<!-- /.box-header -->
								<div class="box-body">
									<div class="form-group">
										<label for="replaceAliases" class="col-sm-3 control-label"><la:message
												key="labels.replace_aliases" /></label>
										<div class="col-sm-9">
											<la:errors property="replaceAliases" />
											<div class="checkbox">
												<label> <la:checkbox styleId="replaceAliases" property="replaceAliases" /> <la:message
														key="labels.enabled" />
												</label>
											</div>
										</div>
									</div>
									<div class="form-group">
										<label for="numberOfShardsForDoc" class="col-sm-3 control-label"><la:message
												key="labels.number_of_shards_for_doc" /></label>
										<div class="col-sm-9">
											<la:errors property="numberOfShardsForDoc" />
											<la:text styleId="numberOfShardsForDoc" property="numberOfShardsForDoc" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="autoExpandReplicasForDoc" class="col-sm-3 control-label"><la:message
												key="labels.auto_expand_replicas_for_doc" /></label>
										<div class="col-sm-9">
											<la:errors property="autoExpandReplicasForDoc" />
											<la:text styleId="autoExpandReplicasForDoc" property="autoExpandReplicasForDoc" styleClass="form-control" />
										</div>
									</div>
								</div>
								<!-- /.box-body -->
								<div class="box-footer">
									<button type="submit" class="btn btn-primary"
										name="reindexOnly"
										value="<la:message key="labels.reindex_start_button"/>">
										<i class="fa fa-arrow-circle-right"></i>
										<la:message key="labels.reindex_start_button" />
									</button>
								</div>
								<!-- /.box-footer -->
							</div>
							<!-- /.box -->
						</div>
						<div class="col-md-12">
							<div class="box box-primary">
								<div class="box-header with-border">
									<h3 class="box-title">
										<la:message key="labels.clear_crawler_index" />
									</h3>
								</div>
								<!-- /.box-header -->
								<div class="box-footer">
									<button type="submit" class="btn btn-primary"
										name="clearCrawlerIndex"
										value="<la:message key="labels.clear_crawler_index_button"/>">
										<i class="fa fa-arrow-circle-right"></i>
										<la:message key="labels.clear_crawler_index_button" />
									</button>
								</div>
								<!-- /.box-footer -->
							</div>
							<!-- /.box -->
						</div>
						<div class="col-md-12">
							<div class="box box-primary">
								<div class="box-header with-border">
									<h3 class="box-title">
										<la:message key="labels.diagnostic_logs" />
									</h3>
								</div>
								<!-- /.box-header -->
								<div class="box-footer">
									<button type="submit" class="btn btn-primary"
										name="downloadLogs"
										value="<la:message key="labels.download_diagnostic_logs_button"/>">
										<i class="fa fa-arrow-circle-right"></i>
										<la:message key="labels.download_diagnostic_logs_button" />
									</button>
								</div>
								<!-- /.box-footer -->
							</div>
							<!-- /.box -->
						</div>
					</la:form>
				</div>
			</section>
		</div>
		<jsp:include page="/WEB-INF/view/common/admin/footer.jsp"></jsp:include>
	</div>
	<jsp:include page="/WEB-INF/view/common/admin/foot.jsp"></jsp:include>
</body>
</html>
