<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><tiles:insert template="/WEB-INF/view/common/admin/layout.jsp" flush="true">
	<tiles:put name="title"><bean:message key="labels.crawler_configuration" /></tiles:put>
	<tiles:put name="header" value="/WEB-INF/view/common/admin/header.jsp" />
	<tiles:put name="footer" value="/WEB-INF/view/common/admin/footer.jsp" />
	<tiles:put name="menu" value="/WEB-INF/view/common/admin/menu.jsp" />
	<tiles:put name="menuType" value="crawl" />
	<tiles:put name="headerScript" type="string"></tiles:put>
	<tiles:put name="body" type="string">

		<%-- Message: BEGIN --%>
		<div>
			<html:messages id="msg" message="true">
				<div class="alert-message info"><bean:write name="msg" ignore="true" /></div>
			</html:messages>
			<html:errors />
		</div>
		<%-- Message: END --%>

	<s:form>
	<div>
        <h3><bean:message key="labels.crawler_title_edit"/></h3>
		<table class="bordered-table zebra-striped">
			<tbody>
				<tr>
					<th style="width:200px;">
						<bean:message key="labels.schedule"/>
					</th>
					<td>
						<html:text property="cronExpression"/>
					</td>
				</tr>
				<tr>
					<th>
						<bean:message key="labels.search_log_enabled"/>
					</th>
					<td>
						<html:checkbox property="searchLog"/><bean:message key="labels.enabled"/>
					</td>
				</tr>
				<tr>
					<th>
						<bean:message key="labels.user_info_enabled"/>
					</th>
					<td>
						<html:checkbox property="userInfo"/><bean:message key="labels.enabled"/>
					</td>
				</tr>
				<tr>
					<th>
						<bean:message key="labels.user_favorite_enabled"/>
					</th>
					<td>
						<html:checkbox property="userFavorite"/><bean:message key="labels.enabled"/>
					</td>
				</tr>
				<tr>
					<th>
						<bean:message key="labels.append_query_param_enabled"/>
					</th>
					<td>
						<html:checkbox property="appendQueryParameter"/><bean:message key="labels.enabled"/>
					</td>
				</tr>
				<tr>
					<th>
						<bean:message key="labels.web_api_xml_enabled"/>
					</th>
					<td>
						<html:checkbox property="webApiXml"/><bean:message key="labels.enabled"/>
					</td>
				</tr>
				<tr>
					<th>
						<bean:message key="labels.web_api_json_enabled"/>
					</th>
					<td>
						<html:checkbox property="webApiJson"/><bean:message key="labels.enabled"/>
					</td>
				</tr>
				<tr>
					<th>
						<bean:message key="labels.web_api_suggest_enabled"/>
					</th>
					<td>
						<html:checkbox property="webApiSuggest"/><bean:message key="labels.enabled"/>
					</td>
				</tr>
				<tr>
					<th>
						<bean:message key="labels.web_api_analysis_enabled"/>
					</th>
					<td>
						<html:checkbox property="webApiAnalysis"/><bean:message key="labels.enabled"/>
					</td>
				</tr>
				<tr>
					<th>
						<bean:message key="labels.mobile_transcoder"/>
					</th>
					<td>
						<html:select property="mobileTranscoder">
							<c:forEach var="item" items="${mobileTranscoderItems}">
								<html:option value="${f:u(item.value)}">${f:h(item.label)}</html:option>
							</c:forEach>
						</html:select>
					</td>
				</tr>
				<tr>
					<th>
						<bean:message key="labels.default_label_value"/>
					</th>
					<td>
						<html:textarea property="defaultLabelValue" styleClass="width:98%"/>
					</td>
				</tr>
				<tr>
					<th>
						<bean:message key="labels.supported_search_feature"/>
					</th>
					<td>
						<html:select property="supportedSearch">
							<c:forEach var="item" items="${supportedSearchItems}">
								<html:option value="${f:u(item.value)}">${f:h(item.label)}</html:option>
							</c:forEach>
						</html:select>
					</td>
				</tr>
				<tr>
					<th>
						<bean:message key="labels.hot_search_word_enabled"/>
					</th>
					<td>
						<html:checkbox property="hotSearchWord"/><bean:message key="labels.enabled"/>
					</td>
				</tr>
				<tr>
					<th>
						<bean:message key="labels.purge_search_log_day"/>
					</th>
					<td>
						<html:text property="purgeSearchLogDay"  styleClass="small"/>
					</td>
				</tr>
				<tr>
					<th>
						<bean:message key="labels.purge_user_info_day"/>
					</th>
					<td>
						<html:text property="purgeUserInfoDay"  styleClass="small"/>
					</td>
				</tr>
				<tr>
					<th>
						<bean:message key="labels.purge_by_bots"/>
					</th>
					<td>
						<html:text property="purgeByBots" style="width:98%"/>
					</td>
				</tr>
				<tr>
					<th>
						<bean:message key="labels.notification_to"/>
					</th>
					<td>
						<html:text property="notificationTo" style="width:98%"/>
					</td>
				</tr>
				<tr>
					<th>
						<bean:message key="labels.csv_file_encoding"/>
					</th>
					<td>
						<html:text property="csvFileEncoding" styleClass="small"/>
					</td>
				</tr>
				<tr>
					<th>
						<bean:message key="labels.diff_crawling"/>
					</th>
					<td>
						<html:checkbox property="diffCrawling"/><bean:message key="labels.enabled"/>
					</td>
				</tr>
				<tr>
					<th>
						<bean:message key="labels.use_acl_as_role"/>
					</th>
					<td>
						<html:checkbox property="useAclAsRole"/><bean:message key="labels.enabled"/>
					</td>
				</tr>
				<tr>
					<th>
						<bean:message key="labels.optimize"/>
					</th>
					<td>
						<html:checkbox property="optimize"/><bean:message key="labels.enabled"/>
					</td>
				</tr>
				<tr>
					<th>
						<bean:message key="labels.commit"/>
					</th>
					<td>
						<html:checkbox property="commit"/><bean:message key="labels.enabled"/>
					</td>
				</tr>
				<tr>
					<th>
						<bean:message key="labels.server_rotation"/>
					</th>
					<td>
						<html:checkbox property="serverRotation"/><bean:message key="labels.enabled"/>
					</td>
				</tr>
				<tr>
					<th>
						<bean:message key="labels.commit_per_count"/>
					</th>
					<td>
						<html:text property="commitPerCount" styleClass="small"/>
					</td>
				</tr>
				<tr>
					<th>
						<bean:message key="labels.crawling_thread_count"/>
					</th>
					<td>
						<html:text property="crawlingThreadCount" styleClass="small"/>
					</td>
				</tr>
				<tr>
					<th>
						<bean:message key="labels.day_for_cleanup"/>
					</th>
					<td>
						<html:select property="dayForCleanup" styleClass="small">
							<html:option value="-1"><bean:message key="labels.none"/></html:option>
							<c:forEach var="d" items="${dayItems}">
								<html:option value="${f:h(d)}">${f:h(d)}</html:option>
							</c:forEach>
						</html:select>
						<bean:message key="labels.day"/>
					</td>
				</tr>
				<tr>
					<th>
						<bean:message key="labels.ignore_failure_type"/>
					</th>
					<td>
						<html:text property="ignoreFailureType" style="width:98%"/>
					</td>
				</tr>
				<tr>
					<th>
						<bean:message key="labels.failure_count_threshold"/>
					</th>
					<td>
						<html:text property="failureCountThreshold" styleClass="small"/>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						<input type="submit" class="btn small" name="update" value="<bean:message key="labels.crawl_button_update"/>"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</div>
	</s:form>


	</tiles:put>
</tiles:insert>
