import FessJQuery from 'jquery';

export default class {
  constructor(FessView, FessModel) {
    this.FessView = FessView;
    this.FessModel = FessModel;
    this.fessUrl = FessJQuery('script#fess-ss').attr('fess-url');
    this.fessContextPath = this.fessUrl.slice(0, this.fessUrl.indexOf('/json'));
    this.searchPagePath = FessJQuery('script#fess-ss').attr('fess-search-page-path');
    this.enableLabels = FessJQuery('script#fess-ss').attr('enable-labels') === 'true' ? true : false;
    this.labelsCache = null
    this.urlParams = function() {
      var params = {};
      var array = location.search.replace(/\?/g, '').split('&');
      array.forEach(function(param){
        var tpl = param.split('=');
        if (tpl.length == 1) {
          params[decodeURIComponent(tpl[0])] = '';
        } else if (tpl.length == 2) {
          params[decodeURIComponent(tpl[0])] = decodeURIComponent(tpl[1].replace(/\+/g, ' '));
        }
      })
      return params;
    }();
  }

  start() {
    this.FessView.init();
    this.FessView.renderForm(this.searchPagePath, this.urlParams);
    this._bindForm();
    if (this.urlParams.q !== undefined) {
      if (FessJQuery('.fessWrapper .fessForm form input').length > 0) {
        FessJQuery('.fessWrapper .fessForm form input').val(this.urlParams.q);
      }
      if (FessJQuery('.fessWrapper .fessFormOnly form input').length > 0) {
        FessJQuery('.fessWrapper .fessFormOnly form input').val(this.urlParams.q);
      }
      try {
        this._search({q: this.urlParams.q});
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
}
