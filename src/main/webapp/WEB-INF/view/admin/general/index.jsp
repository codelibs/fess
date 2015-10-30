<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><la:message key="labels.admin_brand_title" /> | <la:message
		key="labels.crawler_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="system" />
			<jsp:param name="menuType" value="general" />
		</jsp:include>

		<div class="content-wrapper">

			<%-- Content Header --%>
			<section class="content-header">
				<h1>
					<la:message key="labels.crawler_title_edit" />
				</h1>
			</section>

			<section class="content">

				<%-- Form --%>
				<la:form styleClass="form-horizontal">
					<div class="row">
						<div class="col-md-12">
							<div class="box box-warning">
								<%-- Box Header --%>
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
										<label for="esHttpUrl" class="col-sm-3 control-label"><la:message
												key="labels.es_http_url" /></label>
										<div class="col-sm-9">
											<la:text property="esHttpUrl" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="searchLog" class="col-sm-3 control-label"><la:message
												key="labels.search_log_enabled" /></label>
										<div class="col-sm-9">
											<div class="checkbox">
												<label> <la:checkbox property="searchLog" /> <la:message
														key="labels.enabled" />
												</label>
											</div>
										</div>
									</div>
									<div class="form-group">
										<label for="userInfo" class="col-sm-3 control-label"><la:message
												key="labels.user_info_enabled" /></label>
										<div class="col-sm-9">
											<div class="checkbox">
												<label> <la:checkbox property="userInfo" /> <la:message
														key="labels.enabled" />
												</label>
											</div>
										</div>
									</div>
									<div class="form-group">
										<label for="userFavorite" class="col-sm-3 control-label"><la:message
												key="labels.user_favorite_enabled" /></label>
										<div class="col-sm-9">
											<div class="checkbox">
												<label> <la:checkbox property="userFavorite" /> <la:message
														key="labels.enabled" />
												</label>
											</div>
										</div>
									</div>
									<div class="form-group">
										<label for="appendQueryParameter"
											class="col-sm-3 control-label"><la:message
												key="labels.append_query_param_enabled" /></label>
										<div class="col-sm-9">
											<div class="checkbox">
												<label> <la:checkbox property="appendQueryParameter" />
													<la:message key="labels.enabled" />
												</label>
											</div>
										</div>
									</div>
									<div class="form-group">
										<label for="webApiXml" class="col-sm-3 control-label"><la:message
												key="labels.web_api_xml_enabled" /></label>
										<div class="col-sm-9">
											<div class="checkbox">
												<label> <la:checkbox property="webApiXml" /> <la:message
														key="labels.enabled" />
												</label>
											</div>
										</div>
									</div>
									<div class="form-group">
										<label for="webApiJson" class="col-sm-3 control-label"><la:message
												key="labels.web_api_json_enabled" /></label>
										<div class="col-sm-9">
											<div class="checkbox">
												<label> <la:checkbox property="webApiJson" /> <la:message
														key="labels.enabled" />
												</label>
											</div>
										</div>
									</div>
									<div class="form-group">
										<label for="defaultLabelValue" class="col-sm-3 control-label"><la:message
												key="labels.default_label_value" /></label>
										<div class="col-sm-9">
											<la:textarea property="defaultLabelValue"
												styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="supportedSearch" class="col-sm-3 control-label"><la:message
												key="labels.supported_search_feature" /></label>
										<div class="col-sm-9">
											<la:select property="supportedSearch"
												styleClass="form-control">
												<c:forEach var="item" items="${supportedSearchItems}">
													<la:option value="${f:u(item.value)}">${f:h(item.label)}</la:option>
												</c:forEach>
											</la:select>
										</div>
									</div>
									<div class="form-group">
										<label for="hotSearchWord" class="col-sm-3 control-label"><la:message
												key="labels.hot_search_word_enabled" /></label>
										<div class="col-sm-9">
											<div class="checkbox">
												<label> <la:checkbox property="hotSearchWord" /> <la:message
														key="labels.enabled" />
												</label>
											</div>
										</div>
									</div>
									<div class="form-group">
										<label for="purgeSearchLogDay" class="col-sm-3 control-label"><la:message
												key="labels.purge_search_log_day" /></label>
										<div class="col-sm-9">
											<la:text property="purgeSearchLogDay"
												styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="purgeJobLogDay" class="col-sm-3 control-label"><la:message
												key="labels.purge_job_log_day" /></label>
										<div class="col-sm-9">
											<la:text property="purgeJobLogDay" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="purgeUserInfoDay" class="col-sm-3 control-label"><la:message
												key="labels.purge_user_info_day" /></label>
										<div class="col-sm-9">
											<la:text property="purgeUserInfoDay"
												styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="purgeByBots" class="col-sm-3 control-label"><la:message
												key="labels.purge_by_bots" /></label>
										<div class="col-sm-9">
											<la:text property="purgeByBots" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="notificationTo" class="col-sm-3 control-label"><la:message
												key="labels.notification_to" /></label>
										<div class="col-sm-9">
											<la:text property="notificationTo" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="csvFileEncoding" class="col-sm-3 control-label"><la:message
												key="labels.csv_file_encoding" /></label>
										<div class="col-sm-9">
											<la:text property="csvFileEncoding" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="diffCrawling" class="col-sm-3 control-label"><la:message
												key="labels.diff_crawling" /></label>
										<div class="col-sm-9">
											<div class="checkbox">
												<label> <la:checkbox property="diffCrawling" /> <la:message
														key="labels.enabled" />
												</label>
											</div>
										</div>
									</div>
									<div class="form-group">
										<label for="useAclAsRole" class="col-sm-3 control-label"><la:message
												key="labels.use_acl_as_role" /></label>
										<div class="col-sm-9">
											<div class="checkbox">
												<label> <la:checkbox property="useAclAsRole" /> <la:message
														key="labels.enabled" />
												</label>
											</div>
										</div>
									</div>
									<div class="form-group">
										<label for="serverRotation" class="col-sm-3 control-label"><la:message
												key="labels.server_rotation" /></label>
										<div class="col-sm-9">
											<div class="checkbox">
												<label> <la:checkbox property="serverRotation" /> <la:message
														key="labels.enabled" />
												</label>
											</div>
										</div>
									</div>
									<div class="form-group">
										<label for="crawlingThreadCount"
											class="col-sm-3 control-label"><la:message
												key="labels.crawling_thread_count" /></label>
										<div class="col-sm-9">
											<la:text property="crawlingThreadCount"
												styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="dayForCleanup" class="col-sm-3 control-label"><la:message
												key="labels.day_for_cleanup" /></label>
										<div class="form-inline col-sm-9">
											<la:select property="dayForCleanup" styleClass="form-control">
												<la:option value="-1">
													<la:message key="labels.none" />
												</la:option>
												<c:forEach var="d" items="${dayItems}">
													<la:option value="${f:h(d)}">${f:h(d)}</la:option>
												</c:forEach>
											</la:select>
											<la:message key="labels.day" />
										</div>
									</div>
									<div class="form-group">
										<label for="ignoreFailureType" class="col-sm-3 control-label"><la:message
												key="labels.ignore_failure_type" /></label>
										<div class="col-sm-9">
											<la:text property="ignoreFailureType"
												styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="failureCountThreshold"
											class="col-sm-3 control-label"><la:message
												key="labels.failure_count_threshold" /></label>
										<div class="col-sm-9">
											<la:text property="failureCountThreshold"
												styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="suggestSearchLog" class="col-sm-3 control-label"><la:message
												key="labels.suggest_search_log_enabled" /></label>
										<div class="col-sm-9">
											<div class="checkbox">
												<label> <la:checkbox property="suggestSearchLog" />
													<la:message key="labels.enabled" />
												</label>
											</div>
										</div>
									</div>
									<div class="form-group">
										<label for="purgeSuggestSearchLogDay"
											class="col-sm-3 control-label"><la:message
												key="labels.purge_suggest_search_log_day" /></label>
										<div class="col-sm-9">
											<la:text property="purgeSuggestSearchLogDay"
												styleClass="form-control" />
										</div>
									</div>
								</div>

								<%-- Box Footer --%>
								<div class="box-footer">
									<button type="submit" class="btn btn-warning" name="update"
										value="<la:message key="labels.crawl_button_update" />">
										<la:message key="labels.crawl_button_update" />
									</button>
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
