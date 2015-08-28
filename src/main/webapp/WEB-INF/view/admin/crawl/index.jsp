<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Fess | <la:message key="labels.crawler_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin2/head.jsp"></jsp:include>
</head>
<body class="skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin2/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin2/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="system" />
			<jsp:param name="menuType" value="crawl" />
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
				<la:form>
					<div class="row">
						<div class="col-md-12">
							<div class="box">
								<%-- Box Header --%>
								<%-- Box Body --%>
								<div class="box-body">
									<%-- Message --%>
									<div>
										<la:info id="msg" message="true">
											<div class="alert-message info">
												${msg}
											</div>
										</la:info>
										<la:errors />
									</div>

									<%-- Form Fields --%>
									<div class="form-group">
											<label for="searchLog"><la:message key="labels.search_log_enabled" /></label>
											<div styleClass="form-inline" >
													<la:checkbox property="searchLog" />
													<la:message key="labels.enabled"/>
											</div>
									</div>
									<div class="form-group">
											<label for="userInfo"><la:message key="labels.user_info_enabled" /></label>
											<div styleClass="form-inline" >
													<la:checkbox property="userInfo" />
													<la:message key="labels.enabled"/>
											</div>
									</div>
									<div class="form-group">
											<label for="userFavorite"><la:message key="labels.user_favorite_enabled" /></label>
											<div styleClass="form-inline" >
													<la:checkbox property="userFavorite" />
													<la:message key="labels.enabled"/>
											</div>
									</div>
									<div class="form-group">
											<label for="appendQueryParameter"><la:message key="labels.append_query_param_enabled" /></label>
											<div styleClass="form-inline" >
													<la:checkbox property="appendQueryParameter" />
													<la:message key="labels.enabled"/>
											</div>
									</div>
									<div class="form-group">
											<label for="webApiXml"><la:message key="labels.web_api_xml_enabled" /></label>
											<div styleClass="form-inline" >
													<la:checkbox property="webApiXml" />
													<la:message key="labels.enabled"/>
											</div>
									</div>
									<div class="form-group">
											<label for="webApiJson"><la:message key="labels.web_api_json_enabled" /></label>
											<div styleClass="form-inline" >
													<la:checkbox property="webApiJson" />
													<la:message key="labels.enabled"/>
											</div>
									</div>
									<div class="form-group">
											<label for="defaultLabelValue"><la:message key="labels.default_label_value" /></label>
											<la:textarea property="defaultLabelValue" styleClass="form-control" />
									</div>
									<div class="form-group">
											<label for="supportedSearch"><la:message key="labels.supported_search_feature" /></label>
											<div class="form-inline">
													<la:select property="supportedSearch" styleClass="form-control">
															<c:forEach var="item" items="${supportedSearchItems}">
																	<la:option value="${f:u(item.value)}">${f:h(item.label)}</la:option>
															</c:forEach>
													</la:select>
											</div>
									</div>
									<div class="form-group">
											<label for="hotSearchWord"><la:message key="labels.hot_search_word_enabled" /></label>
											<div styleClass="form-inline" >
													<la:checkbox property="hotSearchWord" />
													<la:message key="labels.enabled"/>
											</div>
									</div>
									<div class="form-group">
											<label for="purgeSearchLogDay"><la:message key="labels.purge_search_log_day" /></label>
											<div class="form-inline">
													<la:text property="purgeSearchLogDay" styleClass="form-control" />
											</div>
									</div>
									<div class="form-group">
											<label for="purgeJobLogDay"><la:message key="labels.purge_job_log_day" /></label>
											<div class="form-inline">
													<la:text property="purgeJobLogDay" styleClass="form-control" />
											</div>
									</div>
									<div class="form-group">
											<label for="purgeUserInfoDay"><la:message key="labels.purge_user_info_day" /></label>
											<div class="form-inline">
													<la:text property="purgeUserInfoDay" styleClass="form-control" />
											</div>
									</div>
									<div class="form-group">
											<label for="purgeByBots"><la:message key="labels.purge_by_bots" /></label>
											<la:text property="purgeByBots" styleClass="form-control" />
									</div>
									<div class="form-group">
											<label for="notificationTo"><la:message key="labels.notification_to" /></label>
											<la:text property="notificationTo" styleClass="form-control" />
									</div>
									<div class="form-group">
											<label for="csvFileEncoding"><la:message key="labels.csv_file_encoding" /></label>
											<div class="form-inline">
													<la:text property="csvFileEncoding" styleClass="form-control" />
											</div>
									</div>
									<div class="form-group">
											<label for="diffCrawling"><la:message key="labels.diff_crawling" /></label>
											<div styleClass="form-inline" >
													<la:checkbox property="diffCrawling" />
													<la:message key="labels.enabled"/>
											</div>
									</div>
									<div class="form-group">
											<label for="useAclAsRole"><la:message key="labels.use_acl_as_role" /></label>
											<div styleClass="form-inline" >
													<la:checkbox property="useAclAsRole" />
													<la:message key="labels.enabled"/>
											</div>
									</div>
									<div class="form-group">
											<label for="serverRotation"><la:message key="labels.server_rotation" /></label>
											<div styleClass="form-inline" >
													<la:checkbox property="serverRotation" />
													<la:message key="labels.enabled"/>
											</div>
									</div>
									<div class="form-group">
											<label for="crawlingThreadCount"><la:message key="labels.crawling_thread_count" /></label>
											<div class="form-inline">
													<la:text property="crawlingThreadCount" styleClass="form-control" />
											</div>
									</div>
									<div class="form-group">
											<label for="dayForCleanup"><la:message key="labels.day_for_cleanup" /></label>
											<div class="form-inline">
													<la:select property="dayForCleanup" styleClass="form-control">
															<la:option value="-1"><la:message key="labels.none"/></la:option>
															<c:forEach var="d" items="${dayItems}">
																	<la:option value="${f:h(d)}">${f:h(d)}</la:option>
															</c:forEach>
													</la:select>
													<la:message key="labels.day"/>
											</div>
									</div>
									<div class="form-group">
											<label for="ignoreFailureType"><la:message key="labels.ignore_failure_type" /></label>
											<la:text property="ignoreFailureType" styleClass="form-control" />
									</div>
									<div class="form-group">
											<label for="failureCountThreshold"><la:message key="labels.failure_count_threshold" /></label>
											<div class="form-inline">
													<la:text property="failureCountThreshold" styleClass="form-control" />
											</div>
									</div>
									<div class="form-group">
											<label for="suggestSearchLog"><la:message key="labels.suggest_search_log_enabled" /></label>
											<div styleClass="form-inline" >
													<la:checkbox property="suggestSearchLog" />
													<la:message key="labels.enabled"/>
											</div>
									</div>
									<div class="form-group">
											<label for="purgeSuggestSearchLogDay"><la:message key="labels.purge_suggest_search_log_day" /></label>
											<div class="form-inline">
													<la:text property="purgeSuggestSearchLogDay" styleClass="form-control" />
											</div>
									</div>

								</div>
								<%-- Box Footer --%>
								<div class="box-footer">
										<input type="submit" class="btn small btn-primary" name="update"
													 value="<la:message key="labels.crawl_button_update"/>"
										/>
								</div>
							</div>
						</div>
					</div>
				</la:form>

			</section>
		</div>

		<jsp:include page="/WEB-INF/view/common/admin2/footer.jsp"></jsp:include>
	</div>
	<jsp:include page="/WEB-INF/view/common/admin2/foot.jsp"></jsp:include>
</body>
</html>
