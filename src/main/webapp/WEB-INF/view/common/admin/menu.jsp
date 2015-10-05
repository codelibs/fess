<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<tiles:useAttribute name="menuType" />
<div id="left">
	<h3>
		<la:message key="labels.menu_system" />
	</h3>
	<p>
		<c:if test="${menuType=='wizard'}">
			<span class="selected">
		</c:if>
		<la:link href="${contextPath}/admin/wizard/index">
			<la:message key="labels.menu.wizard" />
		</la:link>
		<c:if test="${menuType=='wizard'}">
			</span>
		</c:if>
		<br />
		<c:if test="${menuType=='crawl'}">
			<span class="selected">
		</c:if>
		<la:link href="${contextPath}/admin/crawl/index">
			<la:message key="labels.menu.crawl_config" />
		</la:link>
		<c:if test="${menuType=='crawl'}">
			</span>
		</c:if>
		<br />
		<c:if test="${menuType=='system'}">
			<span class="selected">
		</c:if>
		<la:link href="${contextPath}/admin/system/index">
			<la:message key="labels.menu.system_config" />
		</la:link>
		<c:if test="${menuType=='system'}">
			</span>
		</c:if>
		<br />
		<c:if test="${menuType=='document'}">
			<span class="selected">
		</c:if>
		<la:link href="${contextPath}/admin/document/index">
			<la:message key="labels.menu.document_config" />
		</la:link>
		<c:if test="${menuType=='document'}">
			</span>
		</c:if>
		<br />
		<c:if test="${menuType=='scheduledJob'}">
			<span class="selected">
		</c:if>
		<la:link href="${contextPath}/admin/scheduledJob/index">
			<la:message key="labels.menu.scheduled_job_config" />
		</la:link>
		<c:if test="${menuType=='scheduledJob'}">
			</span>
		</c:if>
		<br />
		<c:if test="${menuType=='design'}">
			<span class="selected">
		</c:if>
		<la:link href="${contextPath}/admin/design/index">
			<la:message key="labels.menu.design" />
		</la:link>
		<c:if test="${menuType=='design'}">
			</span>
		</c:if>
		<br />
		<c:if test="${menuType=='dict'}">
			<span class="selected">
		</c:if>
		<la:link href="${contextPath}/admin/dict/index">
			<la:message key="labels.menu.dict" />
		</la:link>
		<c:if test="${menuType=='dict'}">
			</span>
		</c:if>
		<br />
		<c:if test="${menuType=='data'}">
			<span class="selected">
		</c:if>
		<la:link href="${contextPath}/admin/data/index">
			<la:message key="labels.menu.data" />
		</la:link>
		<c:if test="${menuType=='data'}">
			</span>
		</c:if>
		<br />
	</p>
	<h3>
		<la:message key="labels.menu_crawl" />
	</h3>
	<p>
		<c:if test="${menuType=='webConfig'}">
			<span class="selected">
		</c:if>
		<la:link href="${contextPath}/admin/webConfig/index">
			<la:message key="labels.menu.web" />
		</la:link>
		<c:if test="${menuType=='webConfig'}">
			</span>
		</c:if>
		<br />
		<c:if test="${menuType=='fileConfig'}">
			<span class="selected">
		</c:if>
		<la:link href="${contextPath}/admin/fileConfig/index">
			<la:message key="labels.menu.file_system" />
		</la:link>
		<c:if test="${menuType=='fileConfig'}">
			</span>
		</c:if>
		<br />
		<c:if test="${menuType=='dataConfig'}">
			<span class="selected">
		</c:if>
		<la:link href="${contextPath}/admin/dataConfig/index">
			<la:message key="labels.menu.data_store" />
		</la:link>
		<c:if test="${menuType=='dataConfig'}">
			</span>
		</c:if>
		<br />
		<c:if test="${menuType=='labelType'}">
			<span class="selected">
		</c:if>
		<la:link href="${contextPath}/admin/labelType/index">
			<la:message key="labels.menu.label_type" />
		</la:link>
		<c:if test="${menuType=='labelType'}">
			</span>
		</c:if>
		<br />
		<c:if test="${menuType=='keyMatch'}">
			<span class="selected">
		</c:if>
		<la:link href="${contextPath}/admin/keyMatch/index">
			<la:message key="labels.menu.key_match" />
		</la:link>
		<c:if test="${menuType=='keyMatch'}">
			</span>
		</c:if>
		<br />
		<c:if test="${menuType=='boostDocumentRule'}">
			<span class="selected">
		</c:if>
		<la:link href="${contextPath}/admin/boostDocumentRule/index">
			<la:message key="labels.menu.boost_document_rule" />
		</la:link>
		<c:if test="${menuType=='boostDocumentRule'}">
			</span>
		</c:if>
		<br />
		<c:if test="${menuType=='pathMapping'}">
			<span class="selected">
		</c:if>
		<la:link href="${contextPath}/admin/pathMapping/index">
			<la:message key="labels.menu.path_mapping" />
		</la:link>
		<c:if test="${menuType=='pathMapping'}">
			</span>
		</c:if>
		<br />
		<c:if test="${menuType=='webAuthentication'}">
			<span class="selected">
		</c:if>
		<la:link href="${contextPath}/admin/webAuthentication/index">
			<la:message key="labels.menu.web_authentication" />
		</la:link>
		<c:if test="${menuType=='webAuthentication'}">
			</span>
		</c:if>
		<br />
		<c:if test="${menuType=='fileAuthentication'}">
			<span class="selected">
		</c:if>
		<la:link href="${contextPath}/admin/fileAuthentication/index">
			<la:message key="labels.menu.file_authentication" />
		</la:link>
		<c:if test="${menuType=='fileAuthentication'}">
			</span>
		</c:if>
		<br />
		<c:if test="${menuType=='requestHeader'}">
			<span class="selected">
		</c:if>
		<la:link href="${contextPath}/admin/requestHeader/index">
			<la:message key="labels.menu.request_header" />
		</la:link>
		<c:if test="${menuType=='requestHeader'}">
			</span>
		</c:if>
		<br />
		<c:if test="${menuType=='overlappingHost'}">
			<span class="selected">
		</c:if>
		<la:link href="${contextPath}/admin/overlappingHost/index">
			<la:message key="labels.menu.overlapping_host" />
		</la:link>
		<c:if test="${menuType=='overlappingHost'}">
			</span>
		</c:if>
		<br />
		<c:if test="${menuType=='roleType'}">
			<span class="selected">
		</c:if>
		<la:link href="${contextPath}/admin/roleType/index">
			<la:message key="labels.menu.role_type" />
		</la:link>
		<c:if test="${menuType=='roleType'}">
			</span>
		</c:if>
		<br />
	</p>
	<h3>
		<la:message key="labels.menu_suggest" />
	</h3>
	<p>
		<c:if test="${menuType=='suggestElevateWord'}">
			<span class="selected">
		</c:if>
		<la:link href="${contextPath}/admin/suggestElevateWord/index">
			<la:message key="labels.menu.suggest_elevate_word" />
		</la:link>
		<c:if test="${menuType=='suggestElevateWord'}">
			</span>
		</c:if>
		<br />
		<c:if test="${menuType=='suggestBadWord'}">
			<span class="selected">
		</c:if>
		<la:link href="${contextPath}/admin/suggestBadWord/index">
			<la:message key="labels.menu.suggest_bad_word" />
		</la:link>
		<c:if test="${menuType=='suggestBadWord'}">
			</span>
		</c:if>
		<br />
	</p>
	<h3>
		<la:message key="labels.menu_system_log" />
	</h3>
	<p>
		<c:if test="${menuType=='systemInfo'}">
			<span class="selected">
		</c:if>
		<la:link href="${contextPath}/admin/systemInfo/index">
			<la:message key="labels.menu.system_info" />
		</la:link>
		<c:if test="${menuType=='systemInfo'}">
			</span>
		</c:if>
		<br />
		<c:if test="${menuType=='jobLog'}">
			<span class="selected">
		</c:if>
		<la:link href="${contextPath}/admin/jobLog/index">
			<la:message key="labels.menu.jobLog" />
		</la:link>
		<c:if test="${menuType=='jobLog'}">
			</span>
		</c:if>
		<br />
		<c:if test="${menuType=='crawlingSession'}">
			<span class="selected">
		</c:if>
		<la:link href="${contextPath}/admin/crawlingSession/index">
			<la:message key="labels.menu.session_info" />
		</la:link>
		<c:if test="${menuType=='crawlingSession'}">
			</span>
		</c:if>
		<br />
		<c:if test="${menuType=='log'}">
			<span class="selected">
		</c:if>
		<la:link href="${contextPath}/admin/log/index">
			<la:message key="labels.menu.log" />
		</la:link>
		<c:if test="${menuType=='log'}">
			</span>
		</c:if>
		<br />
		<c:if test="${menuType=='failureUrl'}">
			<span class="selected">
		</c:if>
		<la:link href="${contextPath}/admin/failureUrl/index">
			<la:message key="labels.menu.failure_url" />
		</la:link>
		<c:if test="${menuType=='failureUrl'}">
			</span>
		</c:if>
		<br />
		<c:if test="${menuType=='searchList'}">
			<span class="selected">
		</c:if>
		<la:link href="${contextPath}/admin/searchList/index">
			<la:message key="labels.menu.search_list" />
		</la:link>
		<c:if test="${menuType=='searchList'}">
			</span>
		</c:if>
		<br />
	</p>
</div>
