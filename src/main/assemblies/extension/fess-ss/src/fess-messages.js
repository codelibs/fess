export default class {
  constructor() {
    this.messages = {
      en: {
        'form.search.button': 'Search',
        'form.input.placeholder': '',
        'result.number': 'results',
        'result.second': 'second',
        'result.order': 'Order',
        'result.order.score': 'Relevance',
        'result.order.last_modified': 'Last modified',
        'result.pagination.prev': 'prev',
        'result.pagination.next': 'next',
        'result.did_not_match': 'Your search - <b>{{q}}</b> - did not match any documents.'
      },
      ja: {
        'form.search.button': '検索',
        'form.input.placeholder': '',
        'result.number': '件',
        'result.second': '秒',
        'result.order': '表示順',
        'result.order.score': '関連度',
        'result.order.last_modified': '更新日時',
        'result.pagination.prev': '前へ',
        'result.pagination.next': '次へ',
        'result.did_not_match': '<b>{{q}}</b>に一致する情報は見つかりませんでした。'
      }
    }
  }
  getLanguage() {
    var lang = window.navigator.userLanguage || window.navigator.language || window.navigator.browserLanguage  || 'en';
    if (lang.indexOf('-') > 0) {
      lang = lang.substr(0, lang.indexOf('-'));
    }
    return lang;
  }
  getMessage(key, vars) {
    var language = this.getLanguage();
    if (this.messages[language] === undefined) {
      language = 'en';
    }
    var message = this.messages[language][key];
    if (message === undefined) {
      console.log('Invalid message key:' + key);
      return '';
    }

    for (var key in vars) {
      if (typeof vars[key] == 'string' || typeof vars == 'string') {
        var reg = new RegExp('{{' + key + '}}', 'g');
        message = message.replace(reg, vars[key]);
      }
    }
    //var reg = new RegExp('{{[^{}]*}}', 'g');
    //message = message.replace(reg, '');
    return message;
  }
  render(html, vars) {
    var language = this.getLanguage();
    if (this.messages[language] === undefined) {
      language = 'en';
    }
    var tmpHtml = html;
    var messages = this.messages[language];
    for(var key in messages) {
      var reg = new RegExp('{' + key + '}', 'g');
      tmpHtml = tmpHtml.replace(reg, this.getMessage(key, vars));
    }
    return tmpHtml;
  }
}
