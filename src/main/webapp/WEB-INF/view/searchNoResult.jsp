<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%-- query did not match any document --%>
<div id="result" class="row">
	<div class="span8 alert">
			<la:message key="labels.did_not_match" arg0="${f:h(displayQuery)}" />
	</div>
	<div class="span4"><%-- Side Content --%></div>
</div>
