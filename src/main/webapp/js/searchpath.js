// Search Path extension
$("[data-toggle='control-search-path']").click(function (e) {
  e.preventDefault();
  var target = $(this).attr("data-target") || $(this).attr("href");
  if (target) {
    $(target).toggleClass("active");
  }
});

$('#searchPathFieldClear').click(function (e) {
  $('#searchPathField').val('');
});


$(function () {
  var query = (location.search.split('q' + '=')[1] || '').split('&')[0];
  var searchPathTerms = [];

  if (query.indexOf("inurl") > 0) {
    var inurlEncodedMatches = query.match(/inurl%3A%22(.*?)%22/g) || [""];
    inurlEncodedMatches.forEach(function (item, idx) {
      var inurl = decodeURIComponent(item)
      // console.log("In URL: " + inurl);

      var searchPathTerm = (inurl.match("\\inurl:\"(.*?)\\\"") || [""])[1];
      // console.log("Search Path Term: " + searchPathTerm);

      //Replace inurl in query
      $('#query').val($('#query').val().replace('inurl:\"' + searchPathTerm + '\"', '').trim())
      searchPathTerms.push(searchPathTerm);
    });

    //Activate search path field
    $('#searchPath').toggleClass("active", true);
    $('#searchPathField').val(searchPathTerms.join(" "));
  }
});

$('#searchForm').submit(function () {
  var searchPath = $('#searchPathField').val();
  if (searchPath != "") {
    $('#query').css('color', 'white');
    $('#contentQuery').css('color', 'white');
    $('#searchPathField').css('color', 'white');

    var searchPathTerms = searchPath.split(" ");
    searchPathTerms.forEach(function (item, idx) {
      $('#query').val($('#query').val() + " inurl:\"" + item + "\"");
      $('#contentQuery').val($('#contentQuery').val() + " inurl:\"" + item + "\"");
    })
  }
});
