import FessJQuery from 'jquery';

export default class {
  constructor(FessView, FessModel) {
    this.FessView = FessView;
    this.FessModel = FessModel;
    this.fessUrl = FessJQuery('script#fess-ss').attr('fess-url');
    this.fessContextPath = this.fessUrl.slice(0, this.fessUrl.indexOf('/json'));
    this.searchPagePath = FessJQuery('script#fess-ss').attr('fess-search-page-path');
    this.enableLabels = FessJQuery('script#fess-ss').attr('enable-labels') === 'true' ? true : false;
    this.enableRelated = FessJQuery('script#fess-ss').attr('enable-related') === 'true' ? true : false;
    this.labelsCache = null
    this.urlParams = this._getParameters();
  }

  start() {
    this.FessView.init();
    this.FessView.renderForm(this.searchPagePath, this.urlParams);
    this._bindForm();
    if (this.urlParams.q !== undefined && this.urlParams.q.length > 0) {
      var query = this.urlParams.q[0];
      if (FessJQuery('.fessWrapper .fessForm form input').length > 0) {
        FessJQuery('.fessWrapper .fessForm form input').val(query);
      }
      if (FessJQuery('.fessWrapper .fessFormOnly form input').length > 0) {
        FessJQuery('.fessWrapper .fessFormOnly form input').val(query);
      }
      try {
        this._search({q: query});
      } catch (e) {
        console.log(e);
      }
    }
  }

  _bindForm() {
    var $cls = this;
    FessJQuery('.fessWrapper .fessForm form').submit(function(){
      try {
        var keyword = FessJQuery('#contentQuery', this).val();
        FessJQuery('.fessWrapper #contentQuery').val(keyword);
        $cls._search({});
      } catch (e) {
        console.log(e);
      }
      return false;
    });
  }

  _bindPagination(response) {
    var $cls = this;
    FessJQuery('.fessWrapper .pagination li').click(function(){
      var $this = FessJQuery(this);
      if ($this.hasClass('disabled')) {
        return false;
      }

      var off = $this.parents('.fessResult').offset();
      FessJQuery(window).scrollTop(off.top);

      var page = $this.attr('page');
      var params = {};
      params.start = response.page_size * (page - 1);
      $cls._search(params);
      return false;
    });
  }

    _search(params) {
    var $fessResult = FessJQuery('.fessWrapper .fessResult');
    $fessResult.css('display', 'none');

    if (params.q === undefined) {
      var keyword = '';
      if (FessJQuery('.fessWrapper .fessForm').length > 0) {
        keyword = FessJQuery('.fessWrapper .fessForm #contentQuery').val();
      } else if(FessJQuery('.fessWrapper .fessFormOnly').length > 0) {
        keyword = FessJQuery('.fessWrapper .fessFormOnly #contentQuery').val();
      }
      if (keyword.length > 0) {
        params.q = keyword;
      } else {
        params.q = '*:*';
      }
    }

    var sort = FessJQuery(".fessWrapper select.sort").val();
    if (sort !== undefined && sort !== '') {
      params.sort = sort;
    }
    var label = FessJQuery(".fessWrapper select.field-labels").val();
    if (label !== undefined && label !== '') {
      params['fields.label'] = label;
    }

    var $cls = this;
    this.FessModel.search(this.fessUrl, params).then(function(data){
      var searchResponse = data.response;
      if ($cls.enableLabels && $cls.labelsCache === null) {
        $cls.FessModel.getLabels($cls.fessUrl).then(function(data) {
          $cls.labelsCache = data.response.result;
          $cls._renderResult(searchResponse, params);
        }, function(data) {
          console.log("labels error: " + JSON.stringify(data));
          $cls._renderResult(searchResponse, params);
        });
      } else {
        $cls._renderResult(searchResponse, params);
      }
    }, function(data) {
      console.log("search error: " + JSON.stringify(data));
      $cls.FessView.renderNoResult(searchResponse, params);
      $fessResult.css('display', 'block');
    });
  }

  _renderResult(response, params) {
    if (this.enableLabels && this.labelsCache !== null) {
      response.labels = this.labelsCache;
    }

    if (!this.enableRelated) {
      delete response.related_query;
      delete response.related_content;
    }

    if (response.related_query !== undefined) {
      for (var i=0;i<response.related_query.length;i++) {
        var relatedQuery = response.related_query[i];
        response.related_query[i] = {"query": relatedQuery, "link": this._getRelatedQueryLink(relatedQuery)};
      }
    }

    var $fessResult = FessJQuery('.fessWrapper .fessResult');
    if (response.record_count > 0) {
      this.FessView.renderResult(this.fessContextPath, response, params);
      $fessResult.css('display', 'block');
      this._bindPagination(response);
    } else {
      this.FessView.renderNoResult(response, params);
      $fessResult.css('display', 'block');
    }
  }

  _getParameters() {
    var path = location.href.replace(/\?.*$/, '');
    var hash = '';
    var url = location.href;
    if (url.indexOf('#') != -1) {
      var array = url.split('#');
      url = array[0];
      hash = array[1];
    }

    var params = function(url) {
      var params = {};
      if (url.indexOf('?') != -1) {
        var array = url.split('?');
        var paramArray = array[1].split('&');
        paramArray.forEach(function(val, index, ar) {
          var tpl = val.split('=');
          var key = decodeURIComponent(tpl[0]);
          var value = '';
          if (tpl.length > 1) {
            value = decodeURIComponent(tpl[1]);
          }

          if (params[key] === undefined) {
            params[key] = [value];
          } else {
            params[key].push(value);
          }
        });
      }
      return params;
    }(url);

    params['fess_url_hash'] = hash;
    return params;
  }

  _getRelatedQueryLink(query) {
    var url = location.href.replace(/\?.*$/, '') + '?q=' + encodeURIComponent(query);
    var hash = this.urlParams['fess_url_hash'];
    Object.keys(this.urlParams).forEach(function(key) {
      if (key !== 'q' && key !== 'fess_url_hash') {
        this[key].forEach(function(val) {
          url = url + '&' + encodeURIComponent(key) + '=' + encodeURIComponent(val);
        });
      }
    }, this.urlParams);
    if (hash !== undefined && hash !== '') {
      url = url + '#' + hash;
    }
    return url;
  }
}
