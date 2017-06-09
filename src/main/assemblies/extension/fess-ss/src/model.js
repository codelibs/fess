import FessJQuery from 'jquery';

export default class {
  constructor() {
  }

  search(url, params) {
    return new Promise(function(resolve, reject) {
      FessJQuery.ajax({
        url: url,
        type: "GET",
        dataType: "jsonp",
        data: params
      }).done(function(data){
        resolve(data);
      }).fail(function(data){
        reject(data);
      });
    });
  }
}
