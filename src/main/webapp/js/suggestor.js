(function($) {
  $.fn.suggestor = function(setting) {
    var $boxElement,
        $textArea,
        inputText = "",
        isFocusList = false,
        listNum = 0,
        listSelNum = 0,
        isMouseHover = false,
        started = false,
        interval = 5,

        settingMinTerm = 1,
        settingAjaxInfo,
        settingAdjustWidthVal,
        $settingSearchForm,
        listSelectedCssInfo,
        listDeselectedCssInfo,
        boxCssInfo,

        suggestingSts = false,

        suggestor = {
          init: function($element, setting) {
            var suggestor;

            suggestingSts = false;
            $boxElement = $("<div/>");
            $boxElement.addClass("suggestorBox");

            //style sheet
            $boxElement.css("display", "none");
            $boxElement.css("position", "absolute");
            $boxElement.css("text-align", "left");
            $boxElement.css("font-size", $element.css("font-size"));
            if (typeof setting.boxCssInfo === "undefined") {
              $boxElement.css("border", "1px solid #cccccc");
              $boxElement.css(
                "-webkit-box-shadow",
                "0 3px 2px 0px rgba(0, 0, 0, 0.1), 0 3px 2px 0px rgba(236, 236, 236, 0.6)"
              );
              $boxElement.css(
                "-moz-box-shadow",
                "0 3px 2px 0px rgba(0, 0, 0, 0.1), 0 3px 2px 0px rgba(236, 236, 236, 0.6)"
              );
              $boxElement.css(
                "box-shadow",
                "0 3px 2px 0px rgba(0, 0, 0, 0.1), 0 3px 2px 0px rgba(236, 236, 236, 0.6)"
              );
              $boxElement.css("background-color", "#fff");
            } else {
              $boxElement.css(setting.boxCssInfo);
            }

            $textArea = $element;
            $textArea.attr("autocomplete", "off");

            isFocusList = false;
            inputText = $textArea.val();

            //settings
            settingAjaxInfo = setting.ajaxinfo;
            settingMinTerm = setting.minterm;
            $settingSearchForm = setting.searchForm;
            listSelectedCssInfo = setting.listSelectedCssInfo;
            listDeselectedCssInfo = setting.listDeselectedCssInfo;
            settingAdjustWidthVal = setting.adjustWidthVal;

            boxCssInfo = setting.boxCssInfo;

            $boxElement.hover(
              function() {
                isMouseHover = true;
              },
              function() {
                isMouseHover = false;
              }
            );

            this.resize();
            suggestor = this;
            $(window).resize(function() {
              suggestor.resize();
            });

            $("body").append($boxElement);
          },

          suggest: function() {
            suggestingSts = true;

            this.resize();

            var suggestor = this;
            inputText = $textArea.val();

            listNum = 0;
            listSelNum = 0;

            if (inputText.length < settingMinTerm) {
              $boxElement.css("display", "none");
              suggestingSts = false;
              return;
            }

            $.ajax({
              url: settingAjaxInfo.url,
              type: "get",
              dataType: "json",
              cache: false,
              data: {
                q: $textArea.val(),
                field: settingAjaxInfo.fn,
                num: settingAjaxInfo.num * 2,
                lang: settingAjaxInfo.lang
              },
              traditional: true
            })
              .done(function(obj) {
                suggestor.createAutoCompleteList(obj);
              })
              .fail(function(a, obj, b) {
                suggestingSts = false;
                return;
              });
          },

          createAutoCompleteList: function(obj) {
            if (typeof obj.record_count === "undefined") {
              $boxElement.css("display", "none");
              return;
            }

            var hits = obj.data,
                suggestor = this,
                reslist,
                $olEle,
                str,
                chkCorrectWord,
                $tmpli,
                $liEle,
                i, j, k;

            listNum = 0;
            if (typeof hits !== "undefined") {
              reslist = [];
              for (i = 0; i < hits.length; i++) {
                reslist.push(hits[i].text);
              }
              $olEle = $("<ol/>");
              $olEle.css("list-style", "none");
              $olEle.css("padding", "0");
              $olEle.css("margin", "2px");

              for (
                j = 0;
                j < reslist.length && listNum < settingAjaxInfo.num;
                j++
              ) {
                str = reslist[j];
                chkCorrectWord = true;

                $tmpli = $($olEle.children("li"));
                for (k = 0; k < $tmpli.length; k++) {
                  if (str === $($tmpli.get(k)).html()) {
                    chkCorrectWord = false;
                  }
                }

                if (chkCorrectWord) {
                  $liEle = $("<li/>");
                  $liEle.html(str);
                  $liEle.click(function() {
                    var str = $(this).html();
                    suggestor.fixList();
                    $textArea.val(str);
                    if (typeof $settingSearchForm !== "undefined") {
                      $settingSearchForm.submit();
                      //$settingSearchForm.trigger("submit");
                    }
                  });
                  $liEle.hover(
                    function() {
                      listSelNum =
                        $(this)
                          .closest("ol")
                          .children("li")
                          .index(this) + 1;
                      $(this)
                        .closest("ol")
                        .children("li")
                        .each(function(i) {
                          if (i === listSelNum - 1) {
                            if (typeof listSelectedCssInfo === "undefined") {
                              $(this).css("background-color", "#e5e5e5");
                            } else {
                              $(this).css(listSelectedCssInfo);
                            }
                          } else {
                            if (typeof listDeselectedCssInfo !== "undefined") {
                              $(this).css(listDeselectedCssInfo);
                            } else {
                              if (
                                typeof boxCssInfo === "undefined" ||
                                typeof boxCssInfo["background-color"] ===
                                  "undefined"
                              ) {
                                $(this).css("background-color", "#ffffff");
                              } else {
                                $(this).css(
                                  "background-color",
                                  boxCssInfo["background-color"]
                                );
                              }
                            }
                          }
                        });
                    },
                    function() {
                      if (
                        listSelNum ===
                        $(this)
                          .closest("ol")
                          .children("li")
                          .index(this) +
                          1
                      ) {
                        if (typeof listDeselectedCssInfo !== "undefined") {
                          $(this).css(listDeselectedCssInfo);
                        } else {
                          if (
                            typeof boxCssInfo === "undefined" ||
                            typeof boxCssInfo["background-color"] === "undefined"
                          ) {
                            $(this).css("background-color", "#ffffff");
                          } else {
                            $(this).css(
                              "background-color",
                              boxCssInfo["background-color"]
                            );
                          }
                        }
                        listSelNum = 0;
                      }
                    }
                  );

                  $liEle.css("padding", "2px");

                  $olEle.append($liEle);
                  listNum++;
                }
              }

              if (listNum > 0 && $textArea.val().length >= settingMinTerm) {
                $boxElement.html("");
                $boxElement.append($olEle);
                $boxElement.css("display", "block");
              } else {
                $boxElement.css("display", "none");
              }
            } else {
              $boxElement.css("display", "none");
            }
            this.resize();

            suggestingSts = false;
          },

          selectlist: function(direction) {
            if ($boxElement.css("display") === "none") {
              return;
            }

            if (direction === "down") {
              listSelNum++;
            } else if (direction === "up") {
              listSelNum--;
            } else {
              return;
            }

            isFocusList = true;

            if (listSelNum < 0) {
              listSelNum = listNum;
            } else if (listSelNum > listNum) {
              listSelNum = 0;
            }

            $boxElement
              .children("ol")
              .children("li")
              .each(function(i) {
                if (i === listSelNum - 1) {
                  if (typeof listSelectedCssInfo === "undefined") {
                    $(this).css("background-color", "#e5e5e5");
                  } else {
                    $(this).css(listSelectedCssInfo);
                  }
                  $textArea.val($(this).html());
                } else {
                  if (typeof listDeselectedCssInfo !== "undefined") {
                    $(this).css(listDeselectedCssInfo);
                  } else {
                    if (
                      typeof boxCssInfo === "undefined" ||
                      typeof boxCssInfo["background-color"] === "undefined"
                    ) {
                      $(this).css("background-color", "#ffffff");
                    } else {
                      $(this).css(
                        "background-color",
                        boxCssInfo["background-color"]
                      );
                    }
                  }
                }
              });
            if (listSelNum === 0) {
              $textArea.val(inputText);
            }
          },

          fixList: function() {
            if (listSelNum > 0) {
              $textArea.val(
                $(
                  $boxElement
                    .children("ol")
                    .children("li")
                    .get(listSelNum - 1)
                ).html()
              );
            }
            inputText = $textArea.val();

            isFocusList = false;
            $boxElement.css("display", "none");
            listNum = 0;
          },

          resize: function() {
            $boxElement.css("top", $textArea.offset().top + $textArea.height() + 6);
            $boxElement.css("left", $textArea.offset().left);
            $boxElement.css("height", "auto");
            $boxElement.css("width", "auto");
            if ($boxElement.width() < $textArea.width() + settingAdjustWidthVal) {
              $boxElement.width($textArea.width() + settingAdjustWidthVal);
            }
          }
        };

    suggestor.init($(this), setting);

    $(this).keydown(function(e) {
      if (
        (e.keyCode >= 48 && e.keyCode <= 90) ||
        (e.keyCode >= 96 && e.keyCode <= 105) ||
        (e.keyCode >= 186 && e.keyCode <= 226) ||
        e.keyCode === 8 ||
        e.keyCode === 32 ||
        e.keyCode === 46
      ) {
        started = true;
        isFocusList = false;
      } else if (e.keyCode === 38) {
        if ($boxElement.css("display") !== "none") {
          e.preventDefault();
        }
        suggestor.selectlist("up");
      } else if (e.keyCode === 40) {
        if ($boxElement.css("display") === "none") {
          suggestor.suggest();
        } else {
          suggestor.selectlist("down");
        }
      } else if (e.keyCode === 13) {
        if (isFocusList) {
          suggestor.fixList();
        }
      }
    });
    $(this).keyup(function(e) {
      if (
        (e.keyCode >= 48 && e.keyCode <= 90) ||
        (e.keyCode >= 96 && e.keyCode <= 105) ||
        (e.keyCode >= 186 && e.keyCode <= 226) ||
        e.keyCode === 8 ||
        e.keyCode === 32 ||
        e.keyCode === 46
      ) {
        started = true;
        isFocusList = false;
      } else if (e.keyCode === 38) {
        /*			if($boxElement.css("display") !== "none") {
				var strTmp = $textArea.val();
				$textArea.val("");
				$textArea.focus();
				$textArea.val(strTmp);
			} */
      }
    });
    $(this).blur(function() {
      if (!isMouseHover) {
        suggestor.fixList();
      }
    });

    //monitoring input field
    setInterval(function() {
      if (interval < 5) {
        interval = interval + 1;
      } else {
        if ($textArea.val() !== inputText) {
          if (!isFocusList && started && !suggestingSts) {
            //update if not selecting item in list
            suggestor.suggest();
            interval = 0;
          }
        }
      }
    }, 100);
  };
})(jQuery);
