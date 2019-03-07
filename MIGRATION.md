## Migration From Other Enterprise Search Systems

### Google Search Appliance (GSA) / Google Mini

Fess provides a [Google Search Appliance](https://enterprise.google.com/search/products/gsa.html) (GSA) compatible API. To enable this API, set `web.api.gsa=true` to system.properties. This will enable an enpoint at `<Fess Server Name>:8080/gsa`. When a search query is sent to `<Fess Server Name>:8080/gsa/?q=QUERY`, a GSA compatible response will be returned

For the more details, see the implementation code in [GsaApiManager](/src/main/java/org/codelibs/fess/api/gsa/GsaApiManager.java).

### Google Site Search (GSS) / Google Custom Search (GSE)

[Fess Site Search](https://github.com/codelibs/fess-site-search) provides [scripts](https://fss-generator.codelibs.org/docs/manual) (see below) to help you migrate from GSS/CSE.
Using the, you can replace existing GSS/CSE JavaScript codes with:

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

An example of Fess Site Search in use is below:

![Fess Site Search](https://fess.codelibs.org/_images/fess-ss-1.png "Fess Site Search")


### More Reading

- [JSON API](https://fess.codelibs.org/12.5/user/json-response.html)
