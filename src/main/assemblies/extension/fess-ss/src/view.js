import FessJQuery from 'jquery';
import formTemplate from '!handlebars-loader!./templates/fess-form.hbs';
import formOnlyTemplate from '!handlebars-loader!./templates/fess-form-only.hbs';
import resultTemplate from '!handlebars-loader!./templates/fess-result.hbs';
import noResultTemplate from '!handlebars-loader!./templates/fess-no-result.hbs';

export default class {
  constructor(FessMessages) {
    this.FessMessages = FessMessages;
    this.IMG_LOADING_DELAY = 200;
    this.IMG_LOADING_MAX = 0;
  }

  init() {
    {
      var $fessWrapper = FessJQuery('<div/>');
      $fessWrapper.addClass('fessWrapper');
      FessJQuery('fess\\:search').replaceWith($fessWrapper);

      var $fessForm = FessJQuery('<div/>');
      $fessForm.addClass('fessForm');
      $fessWrapper.append($fessForm);

      var $fessResult = FessJQuery('<div/>');
      $fessResult.addClass('fessResult');
      $fessResult.css('display', 'none');
      $fessWrapper.append($fessResult);
    }

    {
      var $fessFormWrapper = FessJQuery('<div/>');
      $fessFormWrapper.addClass('fessWrapper');

      var $fessFormOnly = FessJQuery('<div/>');
      $fessFormOnly.addClass('fessFormOnly');
      $fessFormWrapper.append($fessFormOnly);

      FessJQuery('fess\\:search-form-only').replaceWith($fessFormWrapper);
    }

    {
      var $fessResultWrapper = FessJQuery('<div/>');
      $fessResultWrapper.addClass('fessWrapper');

      var $fessResultOnly = FessJQuery('<div/>');
      $fessResultOnly.addClass('fessResult');
      $fessResultOnly.css('display', 'none');
      $fessResultWrapper.append($fessResultOnly);

      FessJQuery('fess\\:search-result-only').replaceWith($fessResultWrapper);
    }
  }

  renderForm(searchPagePath) {
    var $fessForm = FessJQuery('.fessWrapper .fessForm');
    var $fessFormOnly = FessJQuery('.fessWrapper .fessFormOnly');
    if ($fessForm.length > 0) {
      var html = formTemplate();
      $fessForm.html(this.FessMessages.render(html, {}));
    }
    if ($fessFormOnly.length > 0) {
      var html = formOnlyTemplate();
      $fessFormOnly.html(this.FessMessages.render(html, {}));
      FessJQuery('.fessWrapper .fessFormOnly form').attr('action', searchPagePath);
    }
  }

  renderResult(contextPath, response, params) {
    response['context_path'] = contextPath;
    var $fessResult = FessJQuery('.fessWrapper .fessResult');
    var html = resultTemplate(response);
    $fessResult.html(this.FessMessages.render(html, response));
    var $pagination = this._createPagination(response.record_count, response.page_size, response.page_number, params);
    FessJQuery('.fessWrapper .paginationNav').append($pagination);
    if (FessJQuery('.fessWrapper .fessForm').length > 0) {
      FessJQuery('.fessWrapper select.sort').val(params.sort);
    } else {
      FessJQuery('.fessWrapper .fessResult table .order').css('display', 'none');
    }
    this._loadThumbnail(contextPath);
  }

  renderNoResult(response, params) {
    var $fessResult = FessJQuery('.fessWrapper .fessResult');
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
      var pageWidth = function() {
        var width;
        if (window.matchMedia('( max-width : 47.9em)').matches) {
          width = 2;
        } else {
          width = 5;
        }
        return width;
      }();
      var allPageNum = Math.floor((recordCount - 1) / pageSize) + 1;
      var info = {};
      info.current = currentPage;
      info.min = (currentPage - pageWidth) > 0 ? currentPage - pageWidth : 1;
      info.max = (currentPage + pageWidth) < allPageNum ? currentPage + pageWidth : allPageNum;
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

  _loadThumbnail(contextPath) {
    var $cls = this;
    var loadImage = function(img, url, limit) {
      var imgData = new Image();
      imgData.onload = function() {
        FessJQuery(img).css('background-image', '');
        FessJQuery(img).attr('src', url);
      };
      imgData.onerror = function() {
        if (limit > 0) {
          setTimeout(function() {
            loadImage(img, url, --limit);
          }, $cls.IMG_LOADING_DELAY);
        } else {
          FessJQuery(img).parent().css('display', 'none');
        }
        imgData = null;
      };
      imgData.src = url;
    };

    FessJQuery('.fessWrapper .fessResult img.thumbnail').each(function() {
      FessJQuery(this).css('background-image', 'url("' + contextPath + '/images/loading.gif")');
      loadImage(this, FessJQuery(this).attr('data-src'), $cls.IMG_LOADING_MAX);
    });
  }
}
