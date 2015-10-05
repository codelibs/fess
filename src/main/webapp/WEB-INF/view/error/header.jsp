<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<form action="${contextPath}/search" method="get">
	<div class="navbar navbar-inverse navbar-fixed-top">
		<div class="navbar-inner">
			<div class="container">
				<a class="brand" href="${contextPath}/">
					<img src="${f:url('/images/logo-head.gif')}"
						alt="<la:message key="labels.header_brand_name" />" />
				</a>
				<div class="navbar-form pull-right">
					<input type="text" name="query" maxlength="1000" id="query" />
					<button class="btn medium btn-primary" type="submit" name="search"
						id="searchButton">
						<la:message key="labels.search" />
					</button>
					<a href="#searchOptions" role="button" class="btn"
						data-toggle="modal"><la:message
							key="labels.header_form_option_btn" /></a>
				</div>
			</div>
		</div>
	</div>
	<div class="modal hide fade" id="searchOptions">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal"
				aria-hidden="true">&times;</button>
			<h3>
				<la:message key="labels.search_options" />
			</h3>
		</div>
		<div class="modal-body">
			<fieldset>
				<div class="clearfix">
					<label for="contentNum"><la:message
							key="labels.index_num" /></label>
					<div class="input">
						<select name="num" id="contentNum"
							class="span4" style="display:block;">
							<option value="">
								<la:message key="labels.search_result_select_num" />
							</option>
							<option value="10">10</option>
							<option value="20">20</option>
							<option value="30">30</option>
							<option value="40">40</option>
							<option value="50">50</option>
							<option value="100">100</option>
						</select>
					</div>
				</div>
				<div class="clearfix">
					<label for="contentSort"><la:message
							key="labels.index_sort" /></label>
					<div class="input">
						<select name="sort" id="contentSort"
							class="span4" style="display:block;">
							<option value="">
								<la:message key="labels.search_result_select_sort" />
							</option>
							<option value="created.asc">
								<la:message key="labels.search_result_sort_created_asc" />
							</option>
							<option value="created.desc">
								<la:message key="labels.search_result_sort_created_desc" />
							</option>
							<option value="contentLength.asc">
								<la:message key="labels.search_result_sort_contentLength_asc" />
							</option>
							<option value="contentLength.desc">
								<la:message key="labels.search_result_sort_contentLength_desc" />
							</option>
							<option value="lastModified.asc">
								<la:message key="labels.search_result_sort_lastModified_asc" />
							</option>
							<option value="lastModified.desc">
								<la:message key="labels.search_result_sort_lastModified_desc" />
							</option>
						</select>
					</div>
				</div>
				<div class="clearfix">
					<label for="contentLang"><la:message
							key="labels.index_lang" /></label>
					<div class="input">
						<select name="lang"
							id="langSearchOption" multiple="true"
							class="span4">
							<c:forEach var="item" items="${langItems}">
								<option value="${f:u(item.value)}">
													${f:h(item.label)}
												</option>
							</c:forEach>
						</select>
					</div>
				</div>
				<c:if test="${displayLabelTypeItems}">
					<div class="clearfix">
						<label for="contentLabelType"><la:message
								key="labels.index_label" /></label>
						<div class="input">
							<select name="fields.label" id="contentLabelType"
								multiple="true" class="span4">
								<c:forEach var="item" items="${labelTypeItems}">
									<option value="${f:u(item.value)}">
														${f:h(item.label)}
													</option>
								</c:forEach>
							</select>
						</div>
					</div>
				</c:if>
			</fieldset>
		</div>
		<div class="modal-footer">
			<button class="btn" id="searchOptionsClearButton">
				<la:message key="labels.search_options_clear" />
			</button>
			<button class="btn" data-dismiss="modal" aria-hidden="true">
				<la:message key="labels.search_options_close" />
			</button>
		</div>
	</div>
</form>

