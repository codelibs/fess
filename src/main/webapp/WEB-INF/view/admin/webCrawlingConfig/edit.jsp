<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><tiles:insert template="/WEB-INF/view/common/admin/layout.jsp"
	flush="true">
	<tiles:put name="title">
		<bean:message key="labels.web_crawling_configuration" />
	</tiles:put>
	<tiles:put name="header" value="/WEB-INF/view/common/admin/header.jsp" />
	<tiles:put name="footer" value="/WEB-INF/view/common/admin/footer.jsp" />
	<tiles:put name="menu" value="/WEB-INF/view/common/admin/menu.jsp" />
	<tiles:put name="menuType" value="webCrawlingConfig" />
	<tiles:put name="headerScript" type="string"></tiles:put>
	<tiles:put name="body" type="string">

		<h3>
			<bean:message key="labels.web_crawling_title_details" />
		</h3>


		<%-- Message: BEGIN --%>
		<div>
			<html:messages id="msg" message="true">
				<div class="alert-message info"><bean:write name="msg" ignore="true" /></div>
			</html:messages>
			<html:errors />
		</div>
		<%-- Message: END --%>

			<div>
				<ul class="pills">
					<li><s:link href="index">
							<bean:message key="labels.web_crawling_link_list" />
						</s:link></li>
					<c:if test="${crudMode == 1}">
					<li class="active"><a href="#"><bean:message
								key="labels.web_crawling_link_create" /></a></li>
					</c:if>
					<c:if test="${crudMode == 2}">
					<li class="active"><a href="#"><bean:message
								key="labels.web_crawling_link_update" /></a></li>
					</c:if>
					<c:if test="${crudMode == 3}">
					<li class="active"><a href="#"><bean:message
								key="labels.web_crawling_link_delete" /></a></li>
					</c:if>
					<c:if test="${crudMode == 4}">
					<li class="active"><a href="#"><bean:message
								key="labels.web_crawling_link_confirm" /></a></li>
					</c:if>
				</ul>
			</div>

		<%-- Edit Form: BEGIN --%>
		<s:form>
			<html:hidden property="crudMode" />
			<div>
				<c:if test="${crudMode==2}">
					<html:hidden property="id" />
					<html:hidden property="versionNo" />
				</c:if>
				<html:hidden property="createdBy" />
				<html:hidden property="createdTime" />
				<html:hidden property="sortOrder" />
				<table class="bordered-table zebra-striped">
					<tbody>

						<tr>
							<th style="width: 150px;"><bean:message key="labels.name" /></th>
							<td><html:text property="name" style="width:345px;" /></td>
						</tr>
						<tr>
							<th style="width: 150px;"><bean:message key="labels.urls" /></th>
							<td><html:textarea property="urls" style="width:345px;"
									rows="5" /></td>
						</tr>
						<tr>
							<th style="width: 150px;"><bean:message
									key="labels.included_urls" /></th>
							<td><html:textarea property="includedUrls"
									style="width:345px;" rows="5" /></td>
						</tr>
						<tr>
							<th style="width: 150px;"><bean:message
									key="labels.excluded_urls" /></th>
							<td><html:textarea property="excludedUrls"
									style="width:345px;" rows="5" /></td>
						</tr>
						<tr>
							<th style="width: 150px;"><bean:message
									key="labels.included_doc_urls" /></th>
							<td><html:textarea property="includedDocUrls"
									style="width:345px;" rows="5" /></td>
						</tr>
						<tr>
							<th style="width: 150px;"><bean:message
									key="labels.excluded_doc_urls" /></th>
							<td><html:textarea property="excludedDocUrls"
									style="width:345px;" rows="5" /></td>
						</tr>
						<tr>
							<th style="width: 150px;"><bean:message
									key="labels.config_parameter" /></th>
							<td><html:textarea property="configParameter"
									style="width:345px;" rows="5" /></td>
						</tr>
						<tr>
							<th><bean:message key="labels.depth" /></th>
							<td><html:text property="depth" size="5" /></td>
						</tr>
						<tr>
							<th><bean:message key="labels.max_access_count" /></th>
							<td><html:text property="maxAccessCount" size="10" /></td>
						</tr>
						<tr>
							<th><bean:message key="labels.user_agent" /></th>
							<td><html:text property="userAgent" style="width:345px;" /></td>
						</tr>
						<tr>
							<th><bean:message key="labels.number_of_thread" /></th>
							<td><html:text property="numOfThread" size="3" /></td>
						</tr>
						<tr>
							<th><bean:message key="labels.interval_time" /></th>
							<td><html:text property="intervalTime" size="6" />
								<bean:message key="labels.millisec" /></td>
						</tr>
						<tr>
							<th><bean:message key="labels.boost" /></th>
							<td><html:text property="boost" size="6" /></td>
						</tr>
						<tr>
							<th><bean:message key="labels.role_type" /></th>
							<td><html:select property="roleTypeIds" multiple="true"
									size="5" style="width:345px;">
									<c:forEach var="rt" varStatus="s" items="${roleTypeItems}">
										<html:option value="${f:u(rt.id)}">${f:h(rt.name)}</html:option>
									</c:forEach>
								</html:select></td>
						</tr>
						<tr>
							<th><bean:message key="labels.label_type" /></th>
							<td><html:select property="labelTypeIds" multiple="true"
									size="5" style="width:345px;">
									<c:forEach var="l" varStatus="s" items="${labelTypeItems}">
										<html:option value="${f:u(l.id)}">${f:h(l.name)}</html:option>
									</c:forEach>
								</html:select></td>
						</tr>
						<tr>
							<th><bean:message key="labels.available" /></th>
							<td><html:select property="available">
									<html:option value="T">
										<bean:message key="labels.enabled" />
									</html:option>
									<html:option value="F">
										<bean:message key="labels.disabled" />
									</html:option>
								</html:select></td>
						</tr>

					</tbody>
					<tfoot>
						<tr>
							<td colspan="2"><c:if test="${crudMode == 1}">
									<input type="submit" class="btn small" name="confirmfromcreate"
										value="<bean:message key="labels.web_crawling_button_create"/>" />
									<input type="submit" class="btn small" name="back"
										value="<bean:message key="labels.web_crawling_button_back"/>" />
								</c:if> <c:if test="${crudMode == 2}">
									<input type="submit" class="btn small" name="confirmfromupdate"
										value="<bean:message key="labels.web_crawling_button_confirm"/>" />
									<input type="submit" class="btn small" name="back"
										value="<bean:message key="labels.web_crawling_button_back"/>" />
								</c:if></td>
						</tr>
					</tfoot>
				</table>
			</div>
		</s:form>
		<%-- Edit Form: BEGIN --%>

	</tiles:put>
</tiles:insert>
