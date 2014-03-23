<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><tiles:insert template="/WEB-INF/view/common/admin/layout.jsp" flush="true">
	<tiles:put name="title"><bean:message key="labels.data_crawling_configuration" /></tiles:put>
	<tiles:put name="header" value="/WEB-INF/view/common/admin/header.jsp" />
	<tiles:put name="footer" value="/WEB-INF/view/common/admin/footer.jsp" />
	<tiles:put name="menu" value="/WEB-INF/view/common/admin/menu.jsp" />
	<tiles:put name="menuType" value="dataCrawlingConfig" />
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
		<h3><bean:message key="labels.data_crawling_title_confirm"/></h3>
			<div>
				<ul class="pills">
					<li><s:link href="index">
							<bean:message key="labels.data_crawling_link_list" />
						</s:link></li>
					<c:if test="${crudMode == 1}">
					<li class="active"><a href="#"><bean:message
								key="labels.data_crawling_link_create" /></a></li>
					</c:if>
					<c:if test="${crudMode == 2}">
					<li class="active"><a href="#"><bean:message
								key="labels.data_crawling_link_update" /></a></li>
					</c:if>
					<c:if test="${crudMode == 3}">
					<li class="active"><a href="#"><bean:message
								key="labels.data_crawling_link_delete" /></a></li>
					</c:if>
					<c:if test="${crudMode == 4}">
					<li class="active"><a href="#"><bean:message
								key="labels.data_crawling_link_confirm" /></a></li>
					</c:if>
				</ul>
			</div>
		<table class="bordered-table zebra-striped" style="width:500px;">
			<tbody>
                <c:if test="${id != null}"><tr>
                    <th style="width:150px;"><bean:message key="labels.id" /></th>
                    <td style="width:345px;word-break:break-all;">${f:h(id)}</td>
                </tr></c:if>
                <tr>
                    <th style="width:150px;"><bean:message key="labels.name"/></th>
                    <td style="width:345px;word-break:break-all;">${f:h(name)}<html:hidden property="name"/></td>
                </tr>
                <tr>
                    <th style="width:150px;"><bean:message key="labels.handler_name"/></th>
                	<td>
<c:forEach var="hn" varStatus="s" items="${handlerNameItems}">
<c:if test="${handlerName==hn.value}">
${f:h(hn.label)}<br/>
</c:if>
</c:forEach>
<html:hidden property="handlerName"/>
                	</td>
                </tr>
                <tr>
                    <th style="width:150px;"><bean:message key="labels.handler_parameter"/></th>
                    <td style="width:345px;word-break:break-all;">${f:br(f:h(handlerParameter))}<html:hidden property="handlerParameter"/></td>
                </tr>
                <tr>
                    <th style="width:150px;"><bean:message key="labels.handler_script"/></th>
                    <td style="width:345px;word-break:break-all;">${f:br(f:h(handlerScript))}<html:hidden property="handlerScript"/></td>
                </tr>
                <tr>
                    <th><bean:message key="labels.boost"/></th>
                    <td>${f:h(boost)}<html:hidden property="boost"/></td>
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
            <input type="submit" class="btn small" name="create" value="<bean:message key="labels.data_crawling_button_create"/>"/>
            <input type="submit" class="btn small" name="editagain" value="<bean:message key="labels.data_crawling_button_back"/>"/>
        </c:if>
        <c:if test="${crudMode == 2}">
            <input type="submit" class="btn small" name="update" value="<bean:message key="labels.data_crawling_button_update"/>"/>
            <input type="submit" class="btn small" name="editagain" value="<bean:message key="labels.data_crawling_button_back"/>"/>
        </c:if>
        <c:if test="${crudMode == 3}">
            <input type="submit" class="btn small" name="delete" value="<bean:message key="labels.data_crawling_button_delete"/>"/>
            <input type="submit" class="btn small" name="back" value="<bean:message key="labels.data_crawling_button_back"/>"/>
        </c:if>
        <c:if test="${crudMode == 4}">
            <input type="submit" class="btn small" name="back" value="<bean:message key="labels.data_crawling_button_back"/>"/>
            <input type="submit" class="btn small" name="editfromconfirm" value="<bean:message key="labels.data_crawling_button_edit"/>"/>
            <input type="submit" class="btn small" name="deletefromconfirm" value="<bean:message key="labels.data_crawling_button_delete"/>"/>
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
