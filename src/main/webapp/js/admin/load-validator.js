$(function() {
	var SUBSTR_START = 0;
	var SUBSTR_END = 2;
	var twoCharLangCode = 'en';
	var browserLang = (window.navigator.languages && window.navigator.languages[0])
			|| window.navigator.language
			|| window.navigator.userLanguage
			|| window.navigator.browserLanguage;

	if (SUBSTR_END < browserLang.length) {
		twoCharLangCode = browserLang.substr(SUBSTR_START, SUBSTR_END);
	};

	$.validate({
		modules : 'html5',
		lang : twoCharLangCode,
	});
});