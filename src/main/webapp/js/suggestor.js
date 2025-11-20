/**
 * Suggestor - jQuery plugin for search autocomplete functionality
 * Provides real-time search suggestions with keyboard and mouse navigation
 */
(function ($) {
  "use strict";

  // Key code constants for better readability
  var KEY_CODES = {
    BACKSPACE: 8,
    ENTER: 13,
    SPACE: 32,
    UP: 38,
    DOWN: 40,
    DELETE: 46,
    NUM_0: 48,
    NUM_9: 57,
    A: 65,
    Z: 90,
    NUMPAD_0: 96,
    NUMPAD_9: 105,
    SEMICOLON: 186,
    EQUALS: 187,
    DASH: 189,
    FORWARD_SLASH: 191,
    GRAVE: 192,
    OPEN_BRACKET: 219,
    BACK_SLASH: 220,
    CLOSE_BRACKET: 221,
    SINGLE_QUOTE: 222
  };

  // UI constants
  var VERTICAL_SPACING = 6; // Pixels between input field and suggestion box

  $.fn.suggestor = function (setting) {
    var $boxElement,
      $textArea,
      inputText = "",
      isFocusList = false,
      listNum = 0,
      listSelNum = 0,
      isMouseHover = false,
      started = false,
      debounceTimer = null,

      settingMinTerm = 1,
      settingAjaxInfo,
      settingAdjustWidthVal,
      $settingSearchForm,
      listSelectedCssInfo,
      listDeselectedCssInfo,
      boxCssInfo,

      suggestingSts = false,

      /**
       * Helper function to check if a keycode is a valid input key
       */
      isInputKey = function (keyCode) {
        return (
          (keyCode >= KEY_CODES.NUM_0 && keyCode <= KEY_CODES.NUM_9) ||
          (keyCode >= KEY_CODES.A && keyCode <= KEY_CODES.Z) ||
          (keyCode >= KEY_CODES.NUMPAD_0 && keyCode <= KEY_CODES.NUMPAD_9) ||
          (keyCode >= KEY_CODES.SEMICOLON && keyCode <= KEY_CODES.SINGLE_QUOTE) ||
          keyCode === KEY_CODES.BACKSPACE ||
          keyCode === KEY_CODES.SPACE ||
          keyCode === KEY_CODES.DELETE
        );
      },

      /**
       * Helper function to apply CSS styles to list items
       */
      applyListItemStyle = function ($item, isSelected) {
        if (isSelected) {
          if (typeof listSelectedCssInfo === "undefined") {
            $item.css("background-color", "#e5e5e5");
          } else {
            $item.css(listSelectedCssInfo);
          }
        } else {
          if (typeof listDeselectedCssInfo !== "undefined") {
            $item.css(listDeselectedCssInfo);
          } else {
            var bgColor = "#ffffff";
            if (
              typeof boxCssInfo !== "undefined" &&
              typeof boxCssInfo["background-color"] !== "undefined"
            ) {
              bgColor = boxCssInfo["background-color"];
            }
            $item.css("background-color", bgColor);
          }
        }
      },

      /**
       * Helper function to safely escape HTML to prevent XSS
       */
      escapeHtml = function (text) {
        return $("<div>").text(text).html();
      },

      suggestor = {
        /**
         * Initialize the suggestor plugin
         */
        init: function ($element, setting) {
          var suggestor;

          suggestingSts = false;
          $boxElement = $("<div/>");
          $boxElement.addClass("suggestorBox");
          $boxElement.attr({
            role: "listbox",
            "aria-label": "Search suggestions"
          });

          // Apply default or custom CSS styles
          $boxElement.css({
            display: "none",
            position: "absolute",
            "text-align": "left",
            "font-size": $element.css("font-size")
          });

          if (typeof setting.boxCssInfo === "undefined") {
            $boxElement.css({
              border: "1px solid #cccccc",
              "box-shadow":
                "0 1px 1px 0px rgba(0, 0, 0, 0.1), 0 3px 2px 0px rgba(82, 168, 236, 0.2)",
              "background-color": "#fff"
            });
          } else {
            $boxElement.css(setting.boxCssInfo);
          }

          $textArea = $element;
          $textArea.attr({
            autocomplete: "off",
            "aria-autocomplete": "list",
            "aria-haspopup": "listbox",
            "aria-expanded": "false"
          });

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

          $boxElement.on("mouseenter", function () {
            isMouseHover = true;
          });
          $boxElement.on("mouseleave", function () {
            isMouseHover = false;
          });

          this.resize();
          suggestor = this;
          $(window).resize(function () {
            suggestor.resize();
          });

          $("body").append($boxElement);
        },

        /**
         * Fetch and display suggestions based on current input
         */
        suggest: function () {
          suggestingSts = true;

          this.resize();

          var suggestor = this;
          inputText = $textArea.val();

          listNum = 0;
          listSelNum = 0;

          if (inputText.length < settingMinTerm) {
            this.hideSuggestionBox();
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
            .done(function (obj) {
              suggestor.createAutoCompleteList(obj);
            })
            .fail(function (xhr, status, error) {
              if (console && console.error) {
                console.error("Suggestion request failed:", status, error);
              }
              suggestor.hideSuggestionBox();
              suggestingSts = false;
            });
        },

        /**
         * Hide suggestion box and update ARIA attributes
         */
        hideSuggestionBox: function () {
          $boxElement.css("display", "none");
          $textArea.attr("aria-expanded", "false");
        },

        /**
         * Show suggestion box and update ARIA attributes
         */
        showSuggestionBox: function () {
          $boxElement.css("display", "block");
          $textArea.attr("aria-expanded", "true");
        },

        /**
         * Create and display the autocomplete suggestion list
         */
        createAutoCompleteList: function (obj) {
          if (typeof obj.record_count === "undefined") {
            this.hideSuggestionBox();
            return;
          }

          var hits = obj.data,
            suggestor = this,
            reslist,
            $olEle,
            str,
            $liEle,
            seenTexts,
            i, j;

          listNum = 0;
          if (typeof hits !== "undefined" && hits.length > 0) {
            reslist = [];
            for (i = 0; i < hits.length; i++) {
              reslist.push(hits[i].text);
            }

            $olEle = $("<ol/>");
            $olEle.css({
              "list-style": "none",
              padding: "0",
              margin: "2px"
            });

            // Use a Set to track seen texts for O(n) duplicate checking
            seenTexts = {};

            for (
              j = 0;
              j < reslist.length && listNum < settingAjaxInfo.num;
              j++
            ) {
              str = reslist[j];

              // Check for duplicates using Set lookup
              if (!seenTexts[str]) {
                seenTexts[str] = true;
                $liEle = $("<li/>");
                $liEle.text(str); // Use text() instead of html() to prevent XSS
                $liEle.attr({
                  role: "option",
                  "aria-selected": "false"
                });
                $liEle.css("padding", "2px");

                // Click handler
                $liEle.on("click", function () {
                  var text = $(this).text();
                  suggestor.fixList();
                  $textArea.val(text);
                  if (typeof $settingSearchForm !== "undefined") {
                    $settingSearchForm.submit();
                  }
                });

                // Mouse enter handler
                $liEle.on("mouseenter", function () {
                  var $items = $(this).closest("ol").children("li");
                  listSelNum = $items.index(this) + 1;
                  $items.each(function (i) {
                    var isSelected = i === listSelNum - 1;
                    applyListItemStyle($(this), isSelected);
                    $(this).attr("aria-selected", isSelected ? "true" : "false");
                  });
                });

                // Mouse leave handler
                $liEle.on("mouseleave", function () {
                  var $this = $(this);
                  var currentIndex = $this.closest("ol").children("li").index(this) + 1;
                  if (listSelNum === currentIndex) {
                    applyListItemStyle($this, false);
                    $this.attr("aria-selected", "false");
                    listSelNum = 0;
                  }
                });

                $olEle.append($liEle);
                listNum++;
              }
            }

            if (listNum > 0 && $textArea.val().length >= settingMinTerm) {
              $boxElement.html("");
              $boxElement.append($olEle);
              this.showSuggestionBox();
            } else {
              this.hideSuggestionBox();
            }
          } else {
            this.hideSuggestionBox();
          }
          this.resize();

          suggestingSts = false;
        },

        /**
         * Navigate through suggestion list using keyboard
         */
        selectlist: function (direction) {
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

          // Wrap around at boundaries
          if (listSelNum < 0) {
            listSelNum = listNum;
          } else if (listSelNum > listNum) {
            listSelNum = 0;
          }

          var $items = $boxElement.children("ol").children("li");
          $items.each(function (i) {
            var $item = $(this);
            var isSelected = i === listSelNum - 1;
            applyListItemStyle($item, isSelected);
            $item.attr("aria-selected", isSelected ? "true" : "false");
            if (isSelected) {
              $textArea.val($item.text());
            }
          });

          if (listSelNum === 0) {
            $textArea.val(inputText);
          }
        },

        /**
         * Confirm the selected suggestion
         */
        fixList: function () {
          if (listSelNum > 0) {
            var $selectedItem = $(
              $boxElement.children("ol").children("li").get(listSelNum - 1)
            );
            $textArea.val($selectedItem.text());
          }
          inputText = $textArea.val();

          isFocusList = false;
          this.hideSuggestionBox();
          listNum = 0;
        },

        /**
         * Resize and position the suggestion box
         */
        resize: function () {
          var offset = $textArea.offset();
          $boxElement.css({
            top: offset.top + $textArea.outerHeight() + VERTICAL_SPACING,
            left: offset.left,
            height: "auto",
            width: "auto"
          });

          var minWidth = $textArea.outerWidth() + settingAdjustWidthVal;
          if ($boxElement.width() < minWidth) {
            $boxElement.width(minWidth);
          }
        }
      };

    suggestor.init($(this), setting);

    // Keydown event handler
    $(this).on("keydown", function (e) {
      if (isInputKey(e.keyCode)) {
        started = true;
        isFocusList = false;
      } else if (e.keyCode === KEY_CODES.UP) {
        if ($boxElement.css("display") !== "none") {
          e.preventDefault();
        }
        suggestor.selectlist("up");
      } else if (e.keyCode === KEY_CODES.DOWN) {
        if ($boxElement.css("display") === "none") {
          suggestor.suggest();
        } else {
          suggestor.selectlist("down");
        }
      } else if (e.keyCode === KEY_CODES.ENTER) {
        if (isFocusList) {
          suggestor.fixList();
        }
      }
    });

    // Keyup event handler
    $(this).on("keyup", function (e) {
      if (isInputKey(e.keyCode)) {
        started = true;
        isFocusList = false;
      }
    });

    // Blur event handler
    $(this).on("blur", function () {
      if (!isMouseHover) {
        suggestor.fixList();
      }
    });

    // Input event handler with debouncing
    $(this).on("input", function () {
      if ($textArea.val() !== inputText) {
        if (!isFocusList && started && !suggestingSts) {
          // Clear existing timer and set new one to debounce rapid inputs
          clearTimeout(debounceTimer);
          debounceTimer = setTimeout(function () {
            suggestor.suggest();
          }, 300); // 300ms delay for debouncing
        }
      }
    });

    return this;
  };
})(jQuery);
