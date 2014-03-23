<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><tiles:insert template="/WEB-INF/view/common/admin/layout.jsp"
	flush="true">
	<tiles:put name="title">
		<bean:message key="labels.joblog_configuration" />
	</tiles:put>
	<tiles:put name="header" value="/WEB-INF/view/common/admin/header.jsp" />
	<tiles:put name="footer" value="/WEB-INF/view/common/admin/footer.jsp" />
	<tiles:put name="menu" value="/WEB-INF/view/common/admin/menu.jsp" />
	<tiles:put name="menuType" value="jobLog" />
	<tiles:put name="headerScript" type="string"></tiles:put>
	<tiles:put name="body" type="string">

		<h3>
			<bean:message key="labels.joblog_title_list" />
		</h3>

		<%-- Message: BEGIN --%>
		<div>
			<html:messages id="msg" message="true">
				<div class="alert-message info"><bean:write name="msg" ignore="true" /></div>
			</html:messages>
			<html:errors />
		</div>
		<%-- Message: END --%>

		<%-- List: BEGIN --%>
		<div class="list-table">
			<div>
				<ul class="pills">
					<li class="active"><a href="#"><bean:message
								key="labels.joblog_link_list" /></a></li>
				</ul>
			</div>

			<c:if test="${jobLogPager.allRecordCount == 0}">
				<p class="alert-message warning">
					<bean:message key="labels.list_could_not_find_crud_table" />
				</p>
			</c:if>
			<c:if test="${jobLogPager.allRecordCount > 0}">
				<table class="bordered-table zebra-striped">
					<thead>
						<tr>
							<th style="text-align: center;"><bean:message key="labels.joblog_jobName" /></th>
							<th style="text-align: center;"><bean:message key="labels.joblog_jobStatus" /></th>
							<th style="text-align: center;"><bean:message key="labels.joblog_startTime" /></th>
							<th style="text-align: center;"><bean:message key="labels.joblog_endTime" /></th>

							<th style="text-align: center; width: 100px;">&nbsp;</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="data" varStatus="s" items="${jobLogItems}">
							<tr class="${s.index % 2 == 0 ? 'row1' : 'row2'}">
								<td>${f:h(data.jobName)}</td>
								<td style="text-align: center;text-transform: uppercase;">${f:h(data.jobStatus)}</td>
								<td style="text-align: center;"><fmt:formatDate value="${data.startTime}" pattern="yyyy-MM-dd'T'HH:mm:ss" /></td>
								<td style="text-align: center;">
									<c:if test="${data.endTime!=null}"><fmt:formatDate value="${data.endTime}" pattern="yyyy-MM-dd'T'HH:mm:ss" /></c:if>
									<c:if test="${data.endTime==null}"><bean:message key="labels.none" /></c:if>
								</td>
								<td style="text-align: center;"><s:link
										href="confirmpage/4/${f:u(data.id)}">
										<bean:message key="labels.joblog_link_details" />
									</s:link> <s:link href="deletepage/3/${f:u(data.id)}">
										<bean:message key="labels.joblog_link_delete" />
									</s:link></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>

				<div class="span12 center">
<script>
<!--
function confirmToDeleteAll() {
	if (confirm('<bean:message key="labels.joblog_delete_all_confirmation"/>')) {
		return true;
	} else {
		return false;
	}
}
// -->
</script>
					<s:link href="deleteall" onclick="return confirmToDeleteAll();"
						styleClass="btn">
						<bean:message key="labels.joblog_delete_all_link" />
					</s:link>
				</div>


				<%-- Page Navigation: BEGIN --%>
				<div class="row center">
					<div class="pagination">
						<ul>
							<c:if test="${jobLogPager.existPrePage}">
								<li class="prev"><s:link
										href="list/${jobLogPager.currentPageNumber - 1}">
										<bean:message key="labels.joblog_link_prev_page" />
									</s:link></li>
							</c:if>
							<c:if test="${!jobLogPager.existPrePage}">
								<li class="prev disabled"><a href="#"><bean:message
											key="labels.joblog_link_prev_page" /> </a></li>
							</c:if>
							<c:forEach var="p" varStatus="s"
								items="${jobLogPager.pageNumberList}">
								<li
									<c:if test="${p == jobLogPager.currentPageNumber}">class="active"</c:if>>
									<s:link href="list/${p}">${p}</s:link>
								</li>
							</c:forEach>
							<c:if test="${jobLogPager.existNextPage}">
								<li class="next"><s:link
										href="list/${jobLogPager.currentPageNumber + 1}">
										<bean:message key="labels.joblog_link_next_page" />
									</s:link></li>
							</c:if>
							<c:if test="${!jobLogPager.existNextPage}">
								<li class="next disabled"><a href="#"><bean:message
											key="labels.joblog_link_next_page" /></a></li>
							</c:if>
						</ul>
					</div>
					<div>
						<span><bean:message key="labels.pagination_page_guide_msg"
								arg0="${f:h(jobLogPager.currentPageNumber)}"
								arg1="${f:h(jobLogPager.allPageCount)}"
								arg2="${f:h(jobLogPager.allRecordCount)}" /></span>
					</div>
				</div>
				<%-- Page Navigation: END --%>
			</c:if>
		</div>
		<%-- List: END --%>


	</tiles:put>
</tiles:insert>
