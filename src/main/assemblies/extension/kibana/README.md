kibana4 settings for fess
=====

Providing example of kibana settings file for monitoring search logs of fess.

## Install

1. Install and launch Fess.
1. Install and launch kibana4.
1. Go to kibana home [http://localhost:5601/](http://localhost:5601/).
1. Input "fess_log" to the textbox of **Index name or pattern**.
1. Set "requestedAt" to the **Time-field name** and **Create**.
1. From the top of page, click **Settings**.
1. Click **Objects**.
1. Click **Import** and select "fess_log.json" to import example settings.
1. Click **Dashboard**.
1. Click **Load Saved Dashboard** and select "fess_log" dashboard.
1. (Change the period from upper right if you want to do.)

## FAQ

#### Q. Kibana can't connect Elasticsearch.

A. Please check `${KIBANA_HOME}/config/kibana.yml` and set correct Elasticsearch URL.

Example:
```
# The Elasticsearch instance to use for all your queries.
elasticsearch.url: "http://localhost:9201"
```

#### Q. I imported "fess_log.json" but no results found.

A. Is there any search logs? If it still does not resolve, it may be caused by TimeZone. Please check **Settings** -> **Advanced** -> **dateFormat:tz**.
