<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<h3 id="searchOptionsLabel">
	<la:message key="labels.search_options" />
</h3>
<div>
	<fieldset class="form-group">
		<legend><la:message key="labels.index_num" /></legend>
		<label for="contentNum"><la:message key="labels.index_num" /></label>
		<la:select property="num" styleId="numSearchOption"
			styleClass="form-control">
			<option value="10">
				<la:message key="labels.search_result_select_num" />
			</option>
			<la:option value="10">10</la:option>
			<la:option value="20">20</la:option>
			<la:option value="30">30</la:option>
			<la:option value="40">40</la:option>
			<la:option value="50">50</la:option>
			<la:option value="100">100</la:option>
		</la:select>
	</fieldset>
	<fieldset class="form-group">
		<legend><la:message key="labels.index_sort" /></legend>
		<label for="contentSort"><la:message key="labels.index_sort" /></label>
		<la:select property="sort" styleId="sortSearchOption"
			styleClass="form-control">
			<option value="">
				<la:message key="labels.search_result_select_sort" />
			</option>
			<la:option value="score.desc">
				<la:message key="labels.search_result_sort_score_desc" />
			</la:option>
			<la:option value="filename.asc">
				<la:message key="labels.search_result_sort_filename_asc" />
			</la:option>
			<la:option value="filename.desc">
				<la:message key="labels.search_result_sort_filename_desc" />
			</la:option>
			<la:option value="created.asc">
				<la:message key="labels.search_result_sort_created_asc" />
			</la:option>
			<la:option value="created.desc">
				<la:message key="labels.search_result_sort_created_desc" />
			</la:option>
			<la:option value="content_length.asc">
				<la:message key="labels.search_result_sort_content_length_asc" />
			</la:option>
			<la:option value="content_length.desc">
				<la:message key="labels.search_result_sort_content_length_desc" />
			</la:option>
			<la:option value="last_modified.asc">
				<la:message key="labels.search_result_sort_last_modified_asc" />
			</la:option>
			<la:option value="last_modified.desc">
				<la:message key="labels.search_result_sort_last_modified_desc" />
			</la:option>
			<c:if test="${searchLogSupport}">
				<la:option value="click_count.asc">
					<la:message key="labels.search_result_sort_click_count_asc" />
				</la:option>
				<la:option value="click_count.desc">
					<la:message key="labels.search_result_sort_click_count_desc" />
				</la:option>
			</c:if>
			<c:if test="${favoriteSupport}">
				<la:option value="favorite_count.asc">
					<la:message key="labels.search_result_sort_favorite_count_asc" />
				</la:option>
				<la:option value="favorite_count.desc">
					<la:message key="labels.search_result_sort_favorite_count_desc" />
				</la:option>
			</c:if>
		</la:select>
	</fieldset>
	<fieldset class="form-group">
		<legend><la:message key="labels.index_lang" /></legend>
		<label for="contentLang"><la:message key="labels.index_lang" /></label>
		<la:select property="lang" styleId="langSearchOption" multiple="true"
			styleClass="form-control">
			<c:forEach var="item" items="${langItems}">
				<la:option value="${f:u(item.value)}">${f:h(item.label)}</la:option>
			</c:forEach>
		</la:select>
	</fieldset>
	<c:if test="${displayLabelTypeItems}">
		<fieldset class="form-group">
			<legend><la:message key="labels.index_label" /></legend>
			<label for="contentLabelType"><la:message
					key="labels.index_label" /></label>
			<la:select property="fields.label" styleId="labelTypeSearchOption"
				multiple="true" styleClass="form-control">
				<c:forEach var="item" items="${labelTypeItems}">
					<la:option value="${f:u(item.value)}">${f:h(item.label)}</la:option>
				</c:forEach>
			</la:select>
		</fieldset>
	</c:if>
</div>
