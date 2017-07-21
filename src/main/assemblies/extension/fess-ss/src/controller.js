import FessJQuery from 'jquery';

export default class {
  constructor(FessView, FessModel) {
    this.FessView = FessView;
    this.FessModel = FessModel;
    this.fessUrl = FessJQuery('script#fess-ss').attr('fess-url');
    this.fessContextPath = this.fessUrl.slice(0, this.fessUrl.indexOf('/json'));
  }

  start() {
    this.FessView.init();
    this.FessView.renderForm();
    this._bindForm();
  }

  _bindForm() {
    var $cls = this;
    FessJQuery('.fessWrapper form').submit(function(){
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
      var keyword = FessJQuery('.fessWrapper #contentQuery').val();
      if (keyword.length > 0) {
        params.q = keyword;
      } else {
        params.q = '*:*';
      }
    }

    var sort = FessJQuery(".fessWrapper select.sort").val();
    if (sort !== undefined) {
      params.sort = sort;
    } else {
      params.sort = 'score.desc';
    }

    var $cls = this;
    this.FessModel.search(this.fessUrl, params).then(function(data){
      var response = data.response;
      if (response.record_count > 0) {
        $cls.FessView.renderResult($cls.fessContextPath, data.response, params);
        $fessResult.css('display', 'block');
        $cls._bindPagination(data.response);
      } else {
        $cls.FessView.renderNoResult(data.response, params);
        $fessResult.css('display', 'block');
      }
    }, function(data) {
      console.log("search error: " + JSON.stringify(data));
      $cls.FessView.renderNoResult(data.response, params);
      $fessResult.css('display', 'block');
    });
  }
}
