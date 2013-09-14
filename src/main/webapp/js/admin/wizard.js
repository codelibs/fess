$(function(){
	var scheduleEnabledCheckbox = $('input[name=scheduleEnabled]');
	if(scheduleEnabledCheckbox.is(':checked')) {
		// unscheduled
		$(".unscheduled").hide();
	} else {
		// scheduled
		$(".scheduled").hide();
	}

	scheduleEnabledCheckbox.click(function(){
		if(scheduleEnabledCheckbox.is(':checked')){
			// scheduled
			$(".unscheduled").hide();
			$(".scheduled").show();
		} else {
			// unscheduled
			$(".scheduled").hide();
			$(".unscheduled").show();
		}
	});
});
