<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><tiles:insert template="/WEB-INF/view/common/admin/layout.jsp"
	flush="true">
	<tiles:put name="title">
		<bean:message key="labels.crawling_session_configuration" />
	</tiles:put>
	<tiles:put name="header" value="/WEB-INF/view/common/admin/header.jsp" />
	<tiles:put name="footer" value="/WEB-INF/view/common/admin/footer.jsp" />
	<tiles:put name="menu" value="/WEB-INF/view/common/admin/menu.jsp" />
	<tiles:put name="menuType" value="crawlingSession" />
	<tiles:put name="headerScript" type="string"></tiles:put>
	<tiles:put name="body" type="string">


		<h3>
			<bean:message key="labels.crawling_session_title" />
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
		<s:form>
			<div class="row">
				<div class="span12">
					<label for="sessionIdSearchBtn"><bean:message
							key="labels.crawling_session_session_id_search" /> </label>
					<html:text styleId="sessionIdSearchBtn"
						property="searchParams.sessionId"></html:text>
					<input type="submit" class="btn mini primary" name="search"
						value="<bean:message key="labels.crawling_session_search"/>" /> <input
						type="submit" class="btn small" name="reset"
						value="<bean:message key="labels.crawling_session_reset"/>" />
				</div>
			</div>
		</s:form>
		<%-- Search: END --%>

		<%-- List: BEGIN --%>
		<div class="row" style="margin-top: 5px;">
			<c:if test="${crawlingSessionPager.allRecordCount == 0}">
				<p class="alert-message warning">
					<bean:message key="labels.list_could_not_find_crud_table" />
				</p>
			</c:if>
			<c:if test="${crawlingSessionPager.allRecordCount > 0}">

				<div class="span12">
					<table class="bordered-table zebra-striped">
						<thead>
							<tr>
								<th style="text-align: center; width: 150px;"><bean:message
										key="labels.crawling_session_session_id" /></th>
								<th style="text-align: center; width: 180px;"><bean:message
										key="labels.crawling_session_created_time" /></th>
								<th style="text-align: center; width: 180px;"><bean:message
										key="labels.crawling_session_expired_time" /></th>

								<th style="text-align: center; width: 100px;">&nbsp;</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="data" varStatus="s"
								items="${crawlingSessionItems}">
								<tr class="${s.index % 2 == 0 ? 'row1' : 'row2'}">
									<td style="text-align: center;"><html:link
											href="${f:url('/admin/searchList/search')}?query=segment:${f:u(data.sessionId)}">${f:h(data.sessionId)}</html:link></td>
									<td style="text-align: center;">${f:h(data.createdTime)}</td>
									<td style="text-align: center;">
										<c:if test="${data.expiredTime==null}"><bean:message key="labels.none" /></c:if>
										<c:if test="${data.expiredTime!=null}">${f:h(data.expiredTime)}</c:if>
									</td>


									<td  style="text-align: center;"><s:link
											href="confirmpage/4/${f:u(data.id)}">
											<bean:message key="labels.crawling_session_link_details" />
										</s:link> <s:link href="deletepage/3/${f:u(data.id)}">
											<bean:message key="labels.crawling_session_link_delete" />
										</s:link></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>

				<div class="span12 center">
					<script>
			<!--
				function confirmToDeleteAll() {
					if (confirm('<bean:message key="labels.crawling_session_delete_all_confirmation"/>')) {
						return true;
					} else {
						return false;
					}
				}
			// -->
			</script>
					<s:link href="deleteall" onclick="return confirmToDeleteAll();"
						styleClass="btn">
						<bean:message key="labels.crawling_session_delete_all_link" />
					</s:link>
				</div>

				<%-- Page Navigation: BEGIN --%>
				<div class="row center">
					<div class="pagination">
						<ul>
							<c:if test="${crawlingSessionPager.existPrePage}">
								<li class="prev"><s:link
										href="list/${crawlingSessionPager.currentPageNumber - 1}">
										<bean:message key="labels.crud_link_prev_page" />
									</s:link></li>
							</c:if>
							<c:if test="${!crawlingSessionPager.existPrePage}">
								<li class="prev disabled"><a href="#"><bean:message
											key="labels.crud_link_prev_page" /></a></li>
							</c:if>
							<c:forEach var="p" varStatus="s"
								items="${crawlingSessionPager.pageNumberList}">
								<li
									<c:if test="${p == crawlingSessionPager.currentPageNumber}">class="active"</c:if>>
									<s:link href="list/${p}">${p}</s:link>
								</li>
							</c:forEach>
							<c:if test="${crawlingSessionPager.existNextPage}">
								<li class="next"><s:link
										href="list/${crawlingSessionPager.currentPageNumber + 1}">
										<bean:message key="labels.crud_link_next_page" />
									</s:link></li>
							</c:if>
							<c:if test="${!crawlingSessionPager.existNextPage}">
								<li class="next disabled"><a href="#"><bean:message
											key="labels.crud_link_next_page" /></a></li>
							</c:if>
						</ul>
					</div>
					<div class="span12">
						<span><bean:message key="labels.pagination_page_guide_msg"
								arg0="${f:h(crawlingSessionPager.currentPageNumber)}"
								arg1="${f:h(crawlingSessionPager.allPageCount)}"
								arg2="${f:h(crawlingSessionPager.allRecordCount)}" /></span>
					</div>
					<%-- Page Navigation: END --%>

				</div>
			</c:if>
		</div>
		<%-- List: END --%>


	</tiles:put>
</tiles:insert>

