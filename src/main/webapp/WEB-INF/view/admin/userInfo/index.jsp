<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><tiles:insert template="/WEB-INF/view/common/admin/layout.jsp"
	flush="true">
	<tiles:put name="title">
		<bean:message key="labels.user_info_configuration" />
	</tiles:put>
	<tiles:put name="header" value="/WEB-INF/view/common/admin/header.jsp" />
	<tiles:put name="footer" value="/WEB-INF/view/common/admin/footer.jsp" />
	<tiles:put name="menu" value="/WEB-INF/view/common/admin/menu.jsp" />
	<tiles:put name="menuType" value="userInfo" />
	<tiles:put name="headerScript" type="string"></tiles:put>
	<tiles:put name="body" type="string">

		<h3>
			<bean:message key="labels.user_info_title" />
		</h3>
		<%-- Message: BEGIN --%>
		<div>
			<html:messages id="msg" message="true">
				<div class="alert-message info"><bean:write name="msg" ignore="true" /></div>
			</html:messages>
			<html:errors />
		</div>
		<%-- Message: END --%>

<%-- Search: BEGIN --%>
<div>
<s:form>
<table class="bordered-table zebra-striped">
	<tbody>
		<th><bean:message key="labels.user_info_search_code" /></th>
		<td><html:text property="searchParams.code"></html:text></td>
	</tbody>
</table>
<div class="row">
	<div class="span8 offset4">
				<input class="btn mini primary" type="submit" name="search" value="<bean:message key="labels.crud_button_search"/>"/>
				<input class="btn small" type="submit" name="reset" value="<bean:message key="labels.crud_button_reset"/>"/>
	</div>
</div>
</s:form>
</div>
<%-- Search: END --%>

<%-- List: BEGIN --%>
<div class="list-table" style="margin-top: 5px;">
			<c:if test="${userInfoPager.allRecordCount == 0}">
				<p class="alert-message warning">
					<bean:message key="labels.list_could_not_find_crud_table" />
				</p>
			</c:if>
			<c:if test="${userInfoPager.allRecordCount > 0}">
<table class="bordered-table zebra-striped">
	<thead>
		<tr>
				<th style="text-align: center;"><bean:message key="labels.user_info_list_code" /></th>
				<th style="text-align: center;"><bean:message key="labels.user_info_list_lastupdated" /></th>
			<th style="text-align: center;">&nbsp;</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="data" varStatus="s" items="${userInfoItems}">
		<tr class="${s.index % 2 == 0 ? 'row1' : 'row2'}">
			<td style="width:200px;word-break:break-all;">${f:h(data.code)}</td>
			<td style="text-align: center; width: 100px;">${f:h(data.updatedTime)}</td>
			<td style="text-align: center; width: 100px;">
				<s:link href="../searchLog/search?searchParams.userCode=${f:u(data.code)}"><bean:message key="labels.user_info_search_log_link"/></s:link>
				<s:link href="../favoriteLog/search?searchParams.userCode=${f:u(data.code)}"><bean:message key="labels.user_info_favorite_log_link"/></s:link>
				<s:link href="deletepage/3/${f:u(data.id)}"><bean:message key="labels.crud_link_delete"/></s:link>
			</td>
		</tr>
		</c:forEach>
	</tbody>
</table>

				<div class="span12 center">
					<script>
				<!--
					function confirmToDeleteAll() {
						if (confirm('<bean:message key="labels.user_info_delete_all_confirmation"/>')) {
							return true;
						} else {
							return false;
						}
					}
				// -->
				</script>
					<s:link href="deleteall" onclick="return confirmToDeleteAll();"
						styleClass="btn">
						<bean:message key="labels.user_info_delete_all_link" />
					</s:link>
				</div>


<%-- Page Navigation: BEGIN --%>
				<div class="row center">
					<div class="pagination">
						<ul>
							<c:if test="${userInfoPager.existPrePage}">
								<li class="prev"><s:link
										href="list/${userInfoPager.currentPageNumber - 1}">
										<bean:message key="labels.crud_link_prev_page" />
									</s:link></li>
							</c:if>
							<c:if test="${!userInfoPager.existPrePage}">
								<li class="prev disabled"><a href="#"><bean:message
											key="labels.crud_link_prev_page" /></a></li>
							</c:if>
							<c:forEach var="p" varStatus="s"
								items="${userInfoPager.pageNumberList}">
								<li
									<c:if test="${p == userInfoPager.currentPageNumber}">class="active"</c:if>>
									<s:link href="list/${p}">${p}</s:link>
								</li>
							</c:forEach>
							<c:if test="${userInfoPager.existNextPage}">
								<li class="next"><s:link
										href="list/${userInfoPager.currentPageNumber + 1}">
										<bean:message key="labels.crud_link_next_page" />
									</s:link></li>
							</c:if>
							<c:if test="${!userInfoPager.existNextPage}">
								<li class="next disabled"><a href="#"><bean:message
											key="labels.crud_link_next_page" /></a></li>
							</c:if>
						</ul>
					</div>
					<div>
						<span><bean:message key="labels.pagination_page_guide_msg"
								arg0="${f:h(userInfoPager.currentPageNumber)}"
								arg1="${f:h(userInfoPager.allPageCount)}"
								arg2="${f:h(userInfoPager.allRecordCount)}" /></span>
					</div>
				</div>
</c:if>
<%-- Page Navigation: END --%>
</div>
<%-- List: END --%>

	</tiles:put>
</tiles:insert>
