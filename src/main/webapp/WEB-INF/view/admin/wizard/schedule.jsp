<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><tiles:insert template="/WEB-INF/view/common/admin/layout.jsp" flush="true">
	<tiles:put name="title"><bean:message key="labels.wizard_title_configuration" /></tiles:put>
	<tiles:put name="header" value="/WEB-INF/view/common/admin/header.jsp" />
	<tiles:put name="footer" value="/WEB-INF/view/common/admin/footer.jsp" />
	<tiles:put name="menu" value="/WEB-INF/view/common/admin/menu.jsp" />
	<tiles:put name="menuType" value="wizard" />
	<tiles:put name="headerScript" type="string"><script type="text/javascript" src="${f:url('/js/admin/wizard.js')}"></script></tiles:put>
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

	<s:form>
	<div>
        <h3><bean:message key="labels.wizard_schedule_title"/></h3>
		<table class="bordered-table zebra-striped">
			<tbody>
				<tr>
					<th style="width:150px;">
						<bean:message key="labels.wizard_schedule_enabled"/>
					</th>
					<td>
						<html:checkbox property="scheduleEnabled"/><bean:message key="labels.enabled"/>
					</td>
				</tr>
				<tr class="scheduled">
					<th>
						<bean:message key="labels.wizard_schedule"/>
					</th>
					<td>
						<bean:message key="labels.wizard_schedule_month_prefix"/>
						<html:select property="scheduleMonth" styleClass="mini">
							<html:option value="*">*</html:option>
							<c:forEach begin="1" end="12" step="1" varStatus="status">
								<html:option value="${status.index}">${status.index}</html:option>
							</c:forEach>
						</html:select>
						<bean:message key="labels.wizard_schedule_month_suffix"/>
						<bean:message key="labels.wizard_schedule_date_prefix"/>
						<html:select property="scheduleDate" styleClass="mini">
							<html:option value="*">*</html:option>
							<c:forEach begin="1" end="31" step="1" varStatus="status">
								<html:option value="${status.index}">${status.index}</html:option>
							</c:forEach>
						</html:select>
						<bean:message key="labels.wizard_schedule_date_suffix"/>
						<bean:message key="labels.wizard_schedule_hour_prefix"/>
						<html:select property="scheduleHour" styleClass="mini">
							<html:option value="*">*</html:option>
							<c:forEach begin="0" end="23" step="1" varStatus="status">
								<html:option value="${status.index}">${status.index}</html:option>
							</c:forEach>
						</html:select>
						<bean:message key="labels.wizard_schedule_hour_suffix"/>
						<bean:message key="labels.wizard_schedule_min_prefix"/>
						<html:select property="scheduleMin" styleClass="mini">
							<html:option value="*">*</html:option>
							<c:forEach begin="0" end="59" step="1" varStatus="status">
								<html:option value="${status.index}">${status.index}</html:option>
							</c:forEach>
						</html:select>
						<bean:message key="labels.wizard_schedule_min_suffix"/>
						<html:select property="scheduleDay" styleClass="mini">
							<html:option value="?">?</html:option>
							<html:option value="SUN"><bean:message key="labels.wizard_schedule_day_sun"/></html:option>
							<html:option value="MON"><bean:message key="labels.wizard_schedule_day_mon"/></html:option>
							<html:option value="TUE"><bean:message key="labels.wizard_schedule_day_tue"/></html:option>
							<html:option value="WED"><bean:message key="labels.wizard_schedule_day_wed"/></html:option>
							<html:option value="THU"><bean:message key="labels.wizard_schedule_day_thu"/></html:option>
							<html:option value="FRI"><bean:message key="labels.wizard_schedule_day_fri"/></html:option>
							<html:option value="SAT"><bean:message key="labels.wizard_schedule_day_sat"/></html:option>
							<html:option value="MON-FRI"><bean:message key="labels.wizard_schedule_day_m_f"/></html:option>
							<html:option value="MON,WED,FRI"><bean:message key="labels.wizard_schedule_day_mwf"/></html:option>
							<html:option value="TUE,THU"><bean:message key="labels.wizard_schedule_day_tt"/></html:option>
						</html:select>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						<input type="submit" class="btn small" name="index" value="<bean:message key="labels.wizard_button_cancel"/>"/>
						<input type="submit" class="btn small" name="crawlingConfigForm" value="<bean:message key="labels.wizard_button_skip"/>"/>
						<input type="submit" class="btn small" name="schedule" value="<bean:message key="labels.wizard_button_next"/>"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</div>
	</s:form>

</div>

	</tiles:put>
</tiles:insert>
