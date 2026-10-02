/*
 * Renders the search log analytics dashboard with Apache ECharts.
 * Chart data is embedded by the server as JSON in
 * <script type="application/json" id="searchlog-analytics-data">.
 */
(function(window, document) {
  "use strict";

  var PALETTE = ["#007bff", "#28a745", "#ffc107", "#dc3545", "#17a2b8", "#6c757d"];
  var MIXED_UNITS = {
    zeroHitRate : "percent",
    ctr : "percent",
    pagingRate : "percent",
    errorRate : "percent",
    avgResponseTime : "ms"
  };

  function unitOf(chart, key) {
    return chart.unit === "mixed" ? (MIXED_UNITS[key] || "count") : chart.unit;
  }

  function formatValue(v, unit) {
    if (v === null || v === undefined) {
      return "-";
    }
    if (unit === "percent") {
      return (v * 100).toFixed(1) + "%";
    }
    if (unit === "ms") {
      return Math.round(v) + " ms";
    }
    if (unit === "rank") {
      return v.toFixed(1);
    }
    return Number(v).toLocaleString();
  }

  function findSeries(chart, key) {
    var i;
    for (i = 0; i < chart.series.length; i++) {
      if (chart.series[i].key === key) {
        return chart.series[i];
      }
    }
    return chart.series[0];
  }

  function buildPieOption(chart) {
    var first = chart.series[0];
    return {
      aria : {
        enabled : true
      },
      color : PALETTE,
      tooltip : {
        trigger : "item"
      },
      legend : {
        bottom : 0
      },
      series : [ {
        type : "pie",
        radius : [ "40%", "70%" ],
        data : chart.x.map(function(name, i) {
          return {
            name : name,
            value : first.data[i]
          };
        })
      } ]
    };
  }

  function buildOption(chart, opts) {
    var options = opts || {};
    var previousLabel = options.previousLabel || "";
    var shown, series, unit, valueFormatter;
    if (chart.type === "pie") {
      return buildPieOption(chart);
    }
    shown = chart.selectable ? [ findSeries(chart, options.selected) ] : chart.series;
    unit = unitOf(chart, shown[0].key);
    valueFormatter = function(v) {
      return formatValue(v, unit);
    };
    series = [];
    shown.forEach(function(s) {
      series.push({
        name : s.name,
        type : chart.type,
        data : s.data,
        smooth : false,
        showSymbol : chart.x.length <= 31
      });
      if (Array.isArray(s.previous)) {
        series.push({
          name : s.name + " (" + previousLabel + ")",
          type : "line",
          data : s.previous,
          smooth : false,
          showSymbol : false,
          lineStyle : {
            type : "dashed"
          },
          itemStyle : {
            opacity : 0.6
          }
        });
      }
    });
    return {
      aria : {
        enabled : true
      },
      color : PALETTE,
      tooltip : {
        trigger : "axis",
        valueFormatter : valueFormatter
      },
      legend : {
        bottom : 0
      },
      grid : {
        left : 56,
        right : 16,
        top : 16,
        bottom : 48
      },
      xAxis : {
        type : "category",
        data : chart.x
      },
      yAxis : {
        type : "value",
        axisLabel : {
          formatter : valueFormatter
        }
      },
      series : series
    };
  }

  function buildSparklineOption(chart, key) {
    return {
      animation : false,
      color : PALETTE,
      grid : {
        left : 0,
        right : 0,
        top : 2,
        bottom : 2
      },
      xAxis : {
        type : "category",
        show : false,
        data : chart.x
      },
      yAxis : {
        type : "value",
        show : false
      },
      series : [ {
        type : "line",
        data : findSeries(chart, key).data,
        showSymbol : false,
        lineStyle : {
          width : 1.5
        },
        areaStyle : {
          opacity : 0.15
        }
      } ]
    };
  }

  function toArray(list) {
    return Array.prototype.slice.call(list);
  }

  function bindRangeToggle() {
    var range = document.getElementById("searchlog-range");
    var custom = document.getElementById("searchlog-custom-range");
    if (!range || !custom) {
      return;
    }
    range.addEventListener("change", function() {
      custom.classList.toggle("d-none", range.value !== "custom");
    });
  }

  function init() {
    var dataEl = document.getElementById("searchlog-analytics-data");
    var data, instances, selected, charts, previousLabel, observer;
    bindRangeToggle();
    if (!window.echarts || !dataEl) {
      return;
    }
    try {
      data = JSON.parse(dataEl.textContent);
    } catch (e) {
      return;
    }
    data = data || {};
    previousLabel = (data.labels && data.labels.previous) || "";
    instances = [];
    selected = {};
    charts = {};

    toArray(document.querySelectorAll(".searchlog-chart")).forEach(function(el) {
      var name = el.getAttribute("data-chart");
      var chart = data.charts && data.charts[name];
      var instance;
      if (!chart) {
        return;
      }
      instance = window.echarts.init(el);
      instance.setOption(buildOption(chart, {
        previousLabel : previousLabel
      }));
      charts[name] = instance;
      instances.push(instance);
    });

    toArray(document.querySelectorAll(".searchlog-sparkline")).forEach(function(el) {
      var chart = data.charts && data.charts[el.getAttribute("data-chart")];
      var instance;
      if (!chart) {
        return;
      }
      instance = window.echarts.init(el);
      instance.setOption(buildSparklineOption(chart, el.getAttribute("data-series")));
      instances.push(instance);
    });

    toArray(document.querySelectorAll("[data-chart-target]")).forEach(function(btn) {
      btn.addEventListener("click", function() {
        var name = btn.getAttribute("data-chart-target");
        var chart = data.charts && data.charts[name];
        if (!chart || !charts[name]) {
          return;
        }
        selected[name] = btn.getAttribute("data-series");
        toArray(document.querySelectorAll("[data-chart-target]")).forEach(function(other) {
          if (other.getAttribute("data-chart-target") === name) {
            other.classList.toggle("active", other === btn);
          }
        });
        charts[name].setOption(buildOption(chart, {
          selected : selected[name],
          previousLabel : previousLabel
        }), true);
      });
    });

    window.addEventListener("resize", resizeAll);

    // The containers also change width without a window resize event: when the sidebar is
    // toggled, and while its width transition runs after the window crosses the sidebar breakpoint.
    if (typeof window.ResizeObserver === "function") {
      observer = new window.ResizeObserver(resizeAll);
      toArray(document.querySelectorAll(".searchlog-chart, .searchlog-sparkline")).forEach(function(el) {
        observer.observe(el);
      });
    }

    function resizeAll() {
      instances.forEach(function(instance) {
        instance.resize();
      });
    }
  }

  var api = {
    formatValue : formatValue,
    unitOf : unitOf,
    buildOption : buildOption,
    buildSparklineOption : buildSparklineOption,
    init : init
  };
  window.FessSearchlogAnalytics = api;

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", init);
  } else {
    init();
  }
})(window, document);
