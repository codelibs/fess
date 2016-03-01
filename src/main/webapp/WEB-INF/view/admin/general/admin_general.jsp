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
			<section class="content-header">
				<h1>
					<la:message key="labels.crawler_title_edit" />
				</h1>
			</section>
			<section class="content">
				<la:form action="/admin/general/" styleClass="form-horizontal">
					<div class="row">
						<div class="col-md-12">
							<div class="box box-warning">
								<div class="box-body">
									<div>
										<la:info id="msg" message="true">
											<div class="alert alert-info">${msg}</div>
										</la:info>
										<la:errors property="_global" />
									</div>
									<%-- System --%>
									<h4><la:message key="labels.general_menu_system" /></h4>
									<div class="form-group">
										<label for="webApiJson" class="col-sm-3 control-label"><la:message
												key="labels.web_api_json_enabled" /></label>
										<div class="col-sm-9">
											<la:errors property="webApiJson" />
											<div class="checkbox">
												<label> <la:checkbox property="webApiJson" /> <la:message
														key="labels.enabled" />
												</label>
											</div>
										</div>
									</div>
									<div class="form-group">
										<label for="loginRequired" class="col-sm-3 control-label"><la:message
												key="labels.login_required" /></label>
										<div class="col-sm-9">
											<la:errors property="loginRequired" />
											<div class="checkbox">
												<label> <la:checkbox property="loginRequired" /> <la:message
														key="labels.enabled" />
												</label>
											</div>
										</div>
									</div>
									<div class="form-group">
										<label for="defaultLabelValue" class="col-sm-3 control-label"><la:message
												key="labels.default_label_value" /></label>
										<div class="col-sm-9">
											<la:errors property="defaultLabelValue" />
											<la:textarea property="defaultLabelValue"
												styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="defaultSortValue" class="col-sm-3 control-label"><la:message
												key="labels.default_sort_value" /></label>
										<div class="col-sm-9">
											<la:errors property="defaultSortValue" />
											<la:textarea property="defaultSortValue"
												styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="popularWord" class="col-sm-3 control-label"><la:message
												key="labels.popular_word_word_enabled" /></label>
										<div class="col-sm-9">
											<la:errors property="popularWord" />
											<div class="checkbox">
												<label> <la:checkbox property="popularWord" /> <la:message
														key="labels.enabled" />
												</label>
											</div>
										</div>
									</div>
									<div class="form-group">
										<label for="csvFileEncoding" class="col-sm-3 control-label"><la:message
												key="labels.csv_file_encoding" /></label>
										<div class="col-sm-9">
											<la:errors property="csvFileEncoding" />
											<la:text property="csvFileEncoding" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="appendQueryParameter"
											class="col-sm-3 control-label"><la:message
												key="labels.append_query_param_enabled" /></label>
										<div class="col-sm-9">
											<la:errors property="appendQueryParameter" />
											<div class="checkbox">
												<label> <la:checkbox property="appendQueryParameter" />
													<la:message key="labels.enabled" />
												</label>
											</div>
										</div>
									</div>
									<div class="form-group">
										<label for="notificationTo" class="col-sm-3 control-label"><la:message
												key="labels.notification_to" /></label>
										<div class="col-sm-9">
											<la:errors property="notificationTo" />
											<div class="input-group">
												<la:text property="notificationTo" styleClass="form-control" />
												<span class="input-group-btn">
													<button type="submit" class="btn btn-default" name="sendmail" value="test">
														<la:message key="labels.send_testmail" />
													</button>
												</span>
											</div>
										</div>
									</div>
									<%-- Crawler --%>
									<h4><la:message key="labels.general_menu_crawler" /></h4>
									<div class="form-group">
										<label for="purgeByBots" class="col-sm-3 control-label"><la:message
												key="labels.purge_by_bots" /></label>
										<div class="col-sm-9">
											<la:errors property="purgeByBots" />
											<la:text property="purgeByBots" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="incrementalCrawling" class="col-sm-3 control-label"><la:message
												key="labels.incremental_crawling" /></label>
										<div class="col-sm-9">
											<la:errors property="incrementalCrawling" />
											<div class="checkbox">
												<label> <la:checkbox property="incrementalCrawling" /> <la:message
														key="labels.enabled" />
												</label>
											</div>
										</div>
									</div>
									<div class="form-group">
										<label for="crawlingThreadCount"
											class="col-sm-3 control-label"><la:message
												key="labels.crawling_thread_count" /></label>
										<div class="form-inline col-sm-9">
											<la:errors property="crawlingThreadCount" />
											<input type="number" name="crawlingThreadCount"
												value="${f:h(crawlingThreadCount)}" class="form-control"
												min="1" max="1000">
										</div>
									</div>
									<div class="form-group">
										<label for="dayForCleanup" class="col-sm-3 control-label"><la:message
												key="labels.day_for_cleanup" /></label>
										<div class="form-inline col-sm-9">
											<la:errors property="dayForCleanup" />
											<input type="number" name="dayForCleanup"
												value="${f:h(dayForCleanup)}" class="form-control"
												min="-1" max="3650">
											<la:message key="labels.day" />
										</div>
									</div>
									<div class="form-group">
										<label for="ignoreFailureType" class="col-sm-3 control-label"><la:message
												key="labels.ignore_failure_type" /></label>
										<div class="col-sm-9">
											<la:errors property="ignoreFailureType" />
											<la:text property="ignoreFailureType"
												styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="failureCountThreshold"
											class="col-sm-3 control-label"><la:message
												key="labels.failure_count_threshold" /></label>
										<div class="form-inline col-sm-9">
											<la:errors property="failureCountThreshold" />
											<input type="number" name="failureCountThreshold"
												value="${f:h(failureCountThreshold)}" class="form-control"
												min="-1" max="1000">
										</div>
									</div>
									<%-- Logging --%>
									<h4><la:message key="labels.general_menu_logging" /></h4>
									<div class="form-group">
										<label for="searchLog" class="col-sm-3 control-label"><la:message
												key="labels.search_log_enabled" /></label>
										<div class="col-sm-9">
											<la:errors property="searchLog" />
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
											<la:errors property="userInfo" />
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
											<la:errors property="userFavorite" />
											<div class="checkbox">
												<label> <la:checkbox property="userFavorite" /> <la:message
														key="labels.enabled" />
												</label>
											</div>
										</div>
									</div>
									<div class="form-group">
										<label for="purgeSearchLogDay" class="col-sm-3 control-label"><la:message
												key="labels.purge_search_log_day" /></label>
										<div class="form-inline col-sm-9">
											<la:errors property="purgeSearchLogDay" />
											<input type="number" name="purgeSearchLogDay"
												value="${f:h(purgeSearchLogDay)}" class="form-control"
												min="-1" max="3650">
											<la:message key="labels.day" />
										</div>
									</div>
									<div class="form-group">
										<label for="purgeJobLogDay" class="col-sm-3 control-label"><la:message
												key="labels.purge_job_log_day" /></label>
										<div class="form-inline col-sm-9">
											<la:errors property="purgeJobLogDay" />
											<input type="number" name="purgeJobLogDay"
												value="${f:h(purgeJobLogDay)}" class="form-control"
												min="-1" max="3650">
											<la:message key="labels.day" />
										</div>
									</div>
									<div class="form-group">
										<label for="purgeUserInfoDay" class="col-sm-3 control-label"><la:message
												key="labels.purge_user_info_day" /></label>
										<div class="form-inline col-sm-9">
											<la:errors property="purgeUserInfoDay" />
											<input type="number" name="purgeUserInfoDay"
												value="${f:h(purgeUserInfoDay)}" class="form-control"
												min="-1" max="3650">
											<la:message key="labels.day" />
										</div>
									</div>
									<%-- Suggest --%>
									<h4><la:message key="labels.general_menu_suggest" /></h4>
									<div class="form-group">
										<label for="suggestSearchLog" class="col-sm-3 control-label"><la:message
												key="labels.suggest_search_log_enabled" /></label>
										<div class="col-sm-9">
											<la:errors property="suggestSearchLog" />
											<div class="checkbox">
												<label> <la:checkbox property="suggestSearchLog" />
													<la:message key="labels.enabled" />
												</label>
											</div>
										</div>
									</div>
									<div class="form-group">
										<label for="suggestSearchLog" class="col-sm-3 control-label"><la:message
												key="labels.suggest_documents_enabled" /></label>
										<div class="col-sm-9">
											<la:errors property="suggestDocuments" />
											<div class="checkbox">
												<label> <la:checkbox property="suggestDocuments" />
													<la:message key="labels.enabled" />
												</label>
											</div>
										</div>
									</div>
									<div class="form-group">
										<label for="purgeSuggestSearchLogDay"
											class="col-sm-3 control-label"><la:message
												key="labels.purge_suggest_search_log_day" /></label>
										<div class="form-inline col-sm-9">
											<la:errors property="purgeSuggestSearchLogDay" />
											<input type="number" name="purgeSuggestSearchLogDay"
												value="${f:h(purgeSuggestSearchLogDay)}" class="form-control"
												min="-1" max="3650">
											<la:message key="labels.day" />
										</div>
									</div>
									<%-- LDAP --%>
									<h4><la:message key="labels.general_menu_ldap" /></h4>
									<div class="form-group">
										<label for="ldapProviderUrl"
											class="col-sm-3 control-label"><la:message
												key="labels.ldap_provider_url" /></label>
										<div class="col-sm-9">
											<la:errors property="ldapProviderUrl" />
											<la:text property="ldapProviderUrl"
												styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="ldapSecurityPrincipal"
											class="col-sm-3 control-label"><la:message
												key="labels.ldap_security_principal" /></label>
										<div class="col-sm-9">
											<la:errors property="ldapSecurityPrincipal" />
											<la:text property="ldapSecurityPrincipal"
												styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="ldapBaseDn"
											class="col-sm-3 control-label"><la:message
												key="labels.ldap_base_dn" /></label>
										<div class="col-sm-9">
											<la:errors property="ldapBaseDn" />
											<la:text property="ldapBaseDn"
												styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="ldapAccountFilter"
											class="col-sm-3 control-label"><la:message
												key="labels.ldap_account_filter" /></label>
										<div class="col-sm-9">
											<la:errors property="ldapAccountFilter" />
											<la:text property="ldapAccountFilter"
												styleClass="form-control" />
										</div>
									</div>
									<%-- Nortification --%>
									<h4><la:message key="labels.general_menu_notification" /></h4>
									<div class="form-group">
										<label for="notificationLogin"
											class="col-sm-3 control-label"><la:message
												key="labels.notification_login" /></label>
										<div class="col-sm-9">
											<la:errors property="notificationLogin" />
											<la:textarea property="notificationLogin"
												styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="notificationSearchTop"
											class="col-sm-3 control-label"><la:message
												key="labels.notification_search_top" /></label>
										<div class="col-sm-9">
											<la:errors property="notificationSearchTop" />
											<la:textarea property="notificationSearchTop"
												styleClass="form-control" />
										</div>
									</div>
								</div>
								<!-- /.box-body -->
								<div class="box-footer">
									<button type="submit" class="btn btn-warning" name="update"
										value="<la:message key="labels.crawl_button_update" />">
										<i class="fa fa-pencil"></i>
										<la:message key="labels.crawl_button_update" />
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
