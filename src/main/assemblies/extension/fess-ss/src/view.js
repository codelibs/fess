import FessJQuery from 'jquery';
import formTemplate from '!handlebars-loader!./templates/fess-form.hbs';
import resultTemplate from '!handlebars-loader!./templates/fess-result.hbs';
import noResultTemplate from '!handlebars-loader!./templates/fess-no-result.hbs';

export default class {
  constructor(FessMessages) {
    this.FessMessages = FessMessages;
  }

  init() {
    var $fessWrapper = FessJQuery('<div/>');
    $fessWrapper.addClass('fessWrapper');
    $fessWrapper.css('padding', '10px');
    FessJQuery('fess\\:search').replaceWith($fessWrapper);

    var $fessForm = FessJQuery('<div/>');
    $fessForm.attr('id', 'fessForm');
    $fessWrapper.append($fessForm);

    var $fessResult = FessJQuery('<div/>');
    $fessResult.attr('id', 'fessResult');
    $fessResult.css('display', 'none');
    $fessWrapper.append($fessResult);
  }

  renderForm() {
    var $fessForm = FessJQuery('.fessWrapper #fessForm');
    var html = formTemplate(this.css);
    $fessForm.html(this.FessMessages.render(html, {}));
  }

  renderResult(response, params) {
    var $fessResult = FessJQuery('.fessWrapper #fessResult');
    var html = resultTemplate(response);
    $fessResult.html(this.FessMessages.render(html, response));
    var $pagination = this._createPagination(response.record_count, response.page_size, response.page_number, params);
    FessJQuery('.fessWrapper .paginationNav').append($pagination);
    FessJQuery('.fessWrapper select.sort').val(params.sort);
  }

  renderNoResult(response, params) {
    var $fessResult = FessJQuery('.fessWrapper #fessResult');
    var html = noResultTemplate(response);
    $fessResult.html(this.FessMessages.render(html, response));
  }

  _createPagination(recordCount, pageSize, currentPage, params) {
    var $cls = this;

    var $pagination = FessJQuery('<ul/>');
    $pagination.addClass('pagination');

    var calc_start_pos = function(page, pageSize) {
      return (pageSize * (page - 1));
    }

    var paginationInfo = (function(){
      var allPageNum = Math.floor(recordCount / pageSize) + 1;
      var info = {};
      info.current = currentPage;
      info.min = (currentPage - 5) > 0 ? currentPage - 5 : 1;
      info.max = (currentPage + 5) < allPageNum ? currentPage + 5 : allPageNum;
      return info;
    })();

    var $prev = (function(){
      var $li = FessJQuery('<li/>');
      $li.addClass('prev');
      $li.attr('aria-label', 'Previous');
      $li.attr('page', paginationInfo.current - 1);
      $li.html($cls.FessMessages.render('<a><span aria-hidden="true">&laquo;</span> <span class="sr-only">{result.pagination.prev}</span></a>', {}));
      if (currentPage > 1) {
        $li.css('cursor', 'pointer');
      } else {
        $li.addClass('disabled');
      }
      return $li;
    })();
    $pagination.append($prev);

    for (var i=paginationInfo.min;i<=paginationInfo.max;i++) {
      var $li = FessJQuery('<li/>');
      if (i == paginationInfo.current) {
        $li.addClass('active');
      }
      $li.css('cursor', 'pointer');
      $li.html('<a>' + i + '</a>');
      $li.attr('page', i);
      $pagination.append($li);
    }

    var $next = (function(){
      var $li = FessJQuery('<li/>');
      $li.addClass('next');
      $li.attr('aria-label', 'Next');
      $li.attr('page', paginationInfo.current + 1);
      $li.html($cls.FessMessages.render('<a><span class="sr-only">{result.pagination.next}</span><span aria-hidden="true">&raquo;</span></a>', {}));
      if (paginationInfo.current < paginationInfo.max) {
        $li.css('cursor', 'pointer');
      } else {
        $li.addClass('disabled');
      }
      return $li;
    })();
    $pagination.append($next);

    return $pagination;
  }

}
