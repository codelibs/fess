$(function() {
	var SEP_CHAR = '-'
	var langCode;
	var browserLang = (window.navigator.languages && window.navigator.languages[0])
			|| window.navigator.language
			|| window.navigator.userLanguage
			|| window.navigator.browserLanguage;

	if (browserLang) {
		langCode = browserLang.split(SEP_CHAR)[0];
	} else {
		langCode = 'en';
	}

	$.validate({
		modules : 'html5',
		lang : langCode,
	});
});