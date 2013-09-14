<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><tiles:insert template="/WEB-INF/view/common/admin/layout.jsp" flush="true">
	<tiles:put name="title"><bean:message key="labels.file_crawling_configuration" /></tiles:put>
	<tiles:put name="header" value="/WEB-INF/view/common/admin/header.jsp" />
	<tiles:put name="footer" value="/WEB-INF/view/common/admin/footer.jsp" />
	<tiles:put name="menu" value="/WEB-INF/view/common/admin/menu.jsp" />
	<tiles:put name="menuType" value="fileCrawlingConfig" />
	<tiles:put name="headerScript" type="string"></tiles:put>
	<tiles:put name="body" type="string">

      <div id="main">

		<%-- Message: BEGIN --%>
		<div>
			<html:messages id="msg" message="true">
				<div class="alert-message info"><bean:write name="msg" ignore="true" /></div>
			</html:messages>
			<html:errors />
		</div>
		<%-- Message: END --%>

<%-- Confirm Form: BEGIN --%>
	<s:form>
		<html:hidden property="crudMode"/>
		<div>
<c:if test="${crudMode==2 || crudMode==3 || crudMode==4}">
<html:hidden property="id"/>
<html:hidden property="versionNo"/>
</c:if>
<html:hidden property="createdBy"/>
<html:hidden property="createdTime"/>
<html:hidden property="sortOrder"/>
		<h3><bean:message key="labels.file_crawling_title_confirm"/></h3>
			<div>
				<ul class="pills">
					<li><s:link href="index">
							<bean:message key="labels.file_crawling_link_list" />
						</s:link></li>
					<c:if test="${crudMode == 1}">
					<li class="active"><a href="#"><bean:message
								key="labels.file_crawling_link_create" /></a></li>
					</c:if>
					<c:if test="${crudMode == 2}">
					<li class="active"><a href="#"><bean:message
								key="labels.file_crawling_link_update" /></a></li>
					</c:if>
					<c:if test="${crudMode == 3}">
					<li class="active"><a href="#"><bean:message
								key="labels.file_crawling_link_delete" /></a></li>
					</c:if>
					<c:if test="${crudMode == 4}">
					<li class="active"><a href="#"><bean:message
								key="labels.file_crawling_link_confirm" /></a></li>
					</c:if>
				</ul>
			</div>
		<table class="bordered-table zebra-striped" style="width:500px;">
			<tbody>

					                <tr>
                    <th style="width:150px;"><bean:message key="labels.name"/></th>
                    <td style="width:345px;word-break:break-all;">${f:h(name)}<html:hidden property="name"/></td>
                </tr>
					                <tr>
                    <th style="width:150px;"><bean:message key="labels.paths"/></th>
                    <td style="width:345px;word-break:break-all;">${f:br(f:h(paths))}<html:hidden property="paths"/></td>
                </tr>
					                <tr>
                    <th style="width:150px;"><bean:message key="labels.included_paths"/></th>
                    <td style="width:345px;word-break:break-all;">${f:br(f:h(includedPaths))}<html:hidden property="includedPaths"/></td>
                </tr>
					                <tr>
                    <th style="width:150px;"><bean:message key="labels.excluded_paths"/></th>
                    <td style="width:345px;word-break:break-all;">${f:br(f:h(excludedPaths))}<html:hidden property="excludedPaths"/></td>
                </tr>
					                <tr>
                    <th style="width:150px;"><bean:message key="labels.included_doc_paths"/></th>
                    <td style="width:345px;word-break:break-all;">${f:br(f:h(includedDocPaths))}<html:hidden property="includedDocPaths"/></td>
                </tr>
					                <tr>
                    <th style="width:150px;"><bean:message key="labels.excluded_doc_paths"/></th>
                    <td style="width:345px;word-break:break-all;">${f:br(f:h(excludedDocPaths))}<html:hidden property="excludedDocPaths"/></td>
                </tr>
					                <tr>
                    <th style="width:150px;"><bean:message key="labels.config_parameter" /></th>
                    <td style="width:345px;word-break:break-all;">${f:br(f:h(configParameter))}<html:hidden property="configParameter"/></td>
                </tr>
					                <tr>
                    <th><bean:message key="labels.depth"/></th>
                    <td>${f:h(depth)}<html:hidden property="depth"/></td>
                </tr>
					                <tr>
                    <th><bean:message key="labels.max_access_count"/></th>
                    <td>${f:h(maxAccessCount)}<html:hidden property="maxAccessCount"/></td>
                </tr>
					                <tr>
                    <th><bean:message key="labels.number_of_thread"/></th>
                    <td>${f:h(numOfThread)}<html:hidden property="numOfThread"/></td>
                </tr>
					                <tr>
                    <th><bean:message key="labels.interval_time"/></th>
                    <td>${f:h(intervalTime)}<html:hidden property="intervalTime"/><bean:message key="labels.millisec"/></td>
                </tr>
					                <tr>
                    <th><bean:message key="labels.boost"/></th>
                    <td>${f:h(boost)}<html:hidden property="boost"/></td>
                </tr>
                <tr>
                	<th><bean:message key="labels.browser_type"/></th>
                	<td>
<c:forEach var="bt" varStatus="s" items="${browserTypeItems}">
<c:forEach var="btid" varStatus="s" items="${browserTypeIds}">
<c:if test="${btid==bt.id}">
${f:h(bt.name)}<br/>
</c:if>
</c:forEach>
</c:forEach>
<html:select property="browserTypeIds" multiple="true" style="display:none;">
<c:forEach var="bt" varStatus="s" items="${browserTypeItems}">
	<html:option value="${f:u(bt.id)}">${f:h(bt.name)}</html:option>
</c:forEach>
</html:select>
                	</td>
                </tr>
                <tr>
                	<th><bean:message key="labels.role_type" /></th>
                	<td>
<c:forEach var="rt" varStatus="s" items="${roleTypeItems}">
<c:forEach var="rtid" varStatus="s" items="${roleTypeIds}">
<c:if test="${rtid==rt.id}">
${f:h(rt.name)}<br/>
</c:if>
</c:forEach>
</c:forEach>
<html:select property="roleTypeIds" multiple="true" style="display:none;">
<c:forEach var="rt" varStatus="s" items="${roleTypeItems}">
	<html:option value="${f:u(rt.id)}">${f:h(rt.name)}</html:option>
</c:forEach>
</html:select>
                	</td>
                </tr>                
                <tr>
                	<th><bean:message key="labels.label_type" /></th>
                	<td>
<c:forEach var="l" varStatus="s" items="${labelTypeItems}">
<c:forEach var="ltid" varStatus="s" items="${labelTypeIds}">
<c:if test="${ltid==l.id}">
${f:h(l.name)}<br/>
</c:if>
</c:forEach>
</c:forEach>
<html:select property="labelTypeIds" multiple="true" style="display:none;">
<c:forEach var="l" varStatus="s" items="${labelTypeItems}">
	<html:option value="${f:u(l.id)}">${f:h(l.name)}</html:option>
</c:forEach>
</html:select>
                	</td>
                </tr>                
				<tr>
                    <th><bean:message key="labels.available"/></th>
                    <td><html:hidden property="available"/>
<c:if test="${available=='T'}"><bean:message key="labels.enabled"/></c:if>
<c:if test="${available=='F'}"><bean:message key="labels.disabled"/></c:if>
					</td>
                </tr>
			     

			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
        <c:if test="${crudMode == 1}">
            <input type="submit" class="btn mini" name="create" value="<bean:message key="labels.file_crawling_button_create"/>"/>
            <input type="submit" class="btn mini" name="editagain" value="<bean:message key="labels.file_crawling_button_back"/>"/>
        </c:if>
        <c:if test="${crudMode == 2}">
            <input type="submit" class="btn mini" name="update" value="<bean:message key="labels.file_crawling_button_update"/>"/>
            <input type="submit" class="btn mini" name="editagain" value="<bean:message key="labels.file_crawling_button_back"/>"/>
        </c:if>
        <c:if test="${crudMode == 3}">
            <input type="submit" class="btn mini" name="delete" value="<bean:message key="labels.file_crawling_button_delete"/>"/>
            <input type="submit" class="btn mini" name="back" value="<bean:message key="labels.file_crawling_button_back"/>"/>
        </c:if>
        <c:if test="${crudMode == 4}">
            <input type="submit" class="btn mini" name="back" value="<bean:message key="labels.file_crawling_button_back"/>"/>
            <input type="submit" class="btn mini" name="editfromconfirm" value="<bean:message key="labels.file_crawling_button_edit"/>"/>
            <input type="submit" class="btn mini" name="deletefromconfirm" value="<bean:message key="labels.file_crawling_button_delete"/>"/>
        </c:if>
					</td>
				</tr>
			</tfoot>
		</table>
		</div>
	</s:form>
<%-- Confirm Form: BEGIN --%>

      </div>

	</tiles:put>
</tiles:insert>
