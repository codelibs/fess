## Migration From Other Enterprise Search Systems

### Google Search Apliance/Google Mini

Fess provides GSA-Compatible API.
To enable this API, set `web.api.gsa=true` to system.properties.
The endpoint is localhost:8080/gsa. 
Sending a search query to localhost:8080/gsa/?q=QUERY, GSA-compatible response is returned.

For the more details, see [GsaApiManager](https://github.com/codelibs/fess/blob/master/src/main/java/org/codelibs/fess/api/gsa/GsaApiManager.java) implementation.

### Google Site Search/Google Custom Search

Fess provides Site-Search script to help migrations from GSS/CSE.
Using Site-Search script, you can replace existing GSS/CSE JavaScript codes with:

```
<script>
  (function() {
    var fess = document.createElement('script');
    fess.type = 'text/javascript';
    fess.async = true;
    fess.src = '//<Fess Server Name>/js/ss/fess-ss.min.js';
    fess.charset = 'utf-8';
    fess.setAttribute('id', 'fess-ss');
    fess.setAttribute('fess-url', '//<Server Name>/json');
    var s = document.getElementsByTagName('script')[0];
    s.parentNode.insertBefore(fess, s);
  })();
</script>
<fess:search></fess:search>
```

Fess Site Search works as below:

![Fess Site Search](http://fess.codelibs.org/_images/fess-ss-1.png "Fess Site Search")


### Others

See [JSON API](http://fess.codelibs.org/11.0/user/json-response.html).
