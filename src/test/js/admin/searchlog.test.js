// SPDX-License-Identifier: Apache-2.0
import { describe, it, expect, beforeAll } from "vitest";

let A;
beforeAll(async () => {
  await import("../../../main/webapp/js/admin/searchlog.js");
  A = window.FessSearchlogAnalytics;
});

const trend = {
  type: "line", unit: "mixed", selectable: true, x: ["2026-09-01", "2026-09-02"],
  series: [
    { key: "searches", name: "Searches", data: [10, 20], previous: [5, 8] },
    { key: "ctr", name: "CTR", data: [0.25, null], previous: null },
  ],
};

describe("formatValue", () => {
  it("formats by unit", () => {
    expect(A.formatValue(0.1234, "percent")).toBe("12.3%");
    expect(A.formatValue(123.6, "ms")).toBe("124 ms");
    expect(A.formatValue(2.345, "rank")).toBe("2.3");
    expect(A.formatValue(null, "count")).toBe("-");
  });
  it("resolves the unit of mixed series by key", () => {
    expect(A.unitOf(trend, "ctr")).toBe("percent");
    expect(A.unitOf(trend, "avgResponseTime")).toBe("ms");
    expect(A.unitOf(trend, "searches")).toBe("count");
    expect(A.unitOf({ unit: "ms" }, "p95")).toBe("ms");
  });
});

describe("buildOption", () => {
  it("shows only the selected series plus its previous period", () => {
    const opt = A.buildOption(trend, { selected: "searches", previousLabel: "Previous period" });
    expect(opt.xAxis.data).toEqual(["2026-09-01", "2026-09-02"]);
    expect(opt.series.map((s) => s.name)).toEqual(["Searches", "Searches (Previous period)"]);
    expect(opt.series[1].lineStyle.type).toBe("dashed");
    expect(opt.aria.enabled).toBe(true);
  });
  it("omits previous when absent and keeps nulls as gaps", () => {
    const opt = A.buildOption(trend, { selected: "ctr", previousLabel: "Prev" });
    expect(opt.series).toHaveLength(1);
    expect(opt.series[0].data).toEqual([0.25, null]);
  });
  it("draws all series of a non-selectable chart", () => {
    const chart = { type: "bar", unit: "count", x: ["1", "2"], series: [{ key: "clicks", name: "Clicks", data: [3, 1] }] };
    const opt = A.buildOption(chart, {});
    expect(opt.series[0].type).toBe("bar");
  });
  it("builds a pie from x and the first series", () => {
    const chart = { type: "pie", unit: "count", x: ["web", "json"], series: [{ key: "searches", name: "Searches", data: [7, 3] }] };
    const opt = A.buildOption(chart, {});
    expect(opt.series[0].type).toBe("pie");
    expect(opt.series[0].data).toEqual([{ name: "web", value: 7 }, { name: "json", value: 3 }]);
  });
});

describe("buildSparklineOption", () => {
  it("has no axes labels and one line", () => {
    const opt = A.buildSparklineOption(trend, "searches");
    expect(opt.series).toHaveLength(1);
    expect(opt.xAxis.show).toBe(false);
    expect(opt.yAxis.show).toBe(false);
  });
});

describe("init", () => {
  it("does nothing without echarts or data", () => {
    document.body.innerHTML = "<div></div>";
    expect(() => A.init()).not.toThrow();
  });
  it("initializes each chart container with echarts", () => {
    const calls = [];
    window.echarts = { init: (el) => ({ setOption: (o) => calls.push([el.dataset.chart, o]), resize() {} }) };
    document.body.innerHTML =
      '<script type="application/json" id="searchlog-analytics-data">' +
      JSON.stringify({ labels: { previous: "Prev" }, charts: { trend } }) +
      '</script><div class="searchlog-chart" data-chart="trend"></div>' +
      '<div class="searchlog-sparkline" data-chart="trend" data-series="ctr"></div>';
    A.init();
    expect(calls.map((c) => c[0])).toEqual(["trend", "trend"]);
    delete window.echarts;
  });
});

describe("init interactions", () => {
  const setup = (extraHtml) => {
    const calls = [];
    let resized = 0;
    window.echarts = {
      init: (el) => ({
        setOption: (o, replace) => calls.push({ chart: el.dataset.chart, option: o, replace }),
        resize: () => { resized++; },
      }),
    };
    document.body.innerHTML =
      '<script type="application/json" id="searchlog-analytics-data">' +
      JSON.stringify({ labels: { previous: "Prev" }, charts: { trend } }) +
      '</script><div class="searchlog-chart" data-chart="trend"></div>' + extraHtml;
    A.init();
    return { calls, resized: () => resized };
  };

  it("switches the selected series with the chart buttons", () => {
    const t = setup(
      '<button type="button" class="active" data-chart-target="trend" data-series="searches"></button>' +
      '<button type="button" data-chart-target="trend" data-series="ctr"></button>');
    const buttons = document.querySelectorAll("[data-chart-target]");
    buttons[1].click();
    const last = t.calls[t.calls.length - 1];
    expect(last.replace).toBe(true);
    expect(last.option.series.map((s) => s.name)).toEqual(["CTR"]);
    expect(buttons[0].classList.contains("active")).toBe(false);
    expect(buttons[1].classList.contains("active")).toBe(true);
    delete window.echarts;
  });

  it("resizes every chart on window resize", () => {
    const t = setup('<div class="searchlog-sparkline" data-chart="trend" data-series="searches"></div>');
    window.dispatchEvent(new Event("resize"));
    expect(t.resized()).toBe(2);
    delete window.echarts;
  });

  it("resizes every chart when a chart container resizes", () => {
    const observed = [];
    let notify = null;
    window.ResizeObserver = class {
      constructor(callback) { notify = callback; }
      observe(el) { observed.push(el); }
    };
    const t = setup('<div class="searchlog-sparkline" data-chart="trend" data-series="searches"></div>');
    expect(observed.map((el) => el.className)).toEqual(["searchlog-chart", "searchlog-sparkline"]);
    notify([]);
    expect(t.resized()).toBe(2);
    delete window.ResizeObserver;
    delete window.echarts;
  });

  it("ignores unknown chart names and invalid JSON", () => {
    const t = setup('<div class="searchlog-chart" data-chart="missing"></div>');
    expect(t.calls).toHaveLength(1);
    document.getElementById("searchlog-analytics-data").textContent = "{";
    expect(() => A.init()).not.toThrow();
    delete window.echarts;
  });

  it("toggles the custom range fields", () => {
    document.body.innerHTML =
      '<select id="searchlog-range"><option value="28d"></option><option value="custom"></option></select>' +
      '<div id="searchlog-custom-range" class="d-none"></div>';
    A.init();
    const range = document.getElementById("searchlog-range");
    const custom = document.getElementById("searchlog-custom-range");
    range.value = "custom";
    range.dispatchEvent(new Event("change"));
    expect(custom.classList.contains("d-none")).toBe(false);
    range.value = "28d";
    range.dispatchEvent(new Event("change"));
    expect(custom.classList.contains("d-none")).toBe(true);
  });
});
