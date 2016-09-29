$(function() {
	var SUBSTR_START = 0;
	var SUBSTR_END = 2;
	var SEP_CHAR = '-'
	var sepLangCode = 'en';
	var browserLang = (window.navigator.languages && window.navigator.languages[0])
			|| window.navigator.language
			|| window.navigator.userLanguage
			|| window.navigator.browserLanguage;

	if (browserLang) {
		sepLangCode = browserLang.split(SEP_CHAR);
	};

	$.validate({
		modules : 'html5',
		lang : sepLangCode,
	});
});