kibana4 settings for fess
=====

Providing kibana settings file for monitoring search logs of fess.

## Install

1. Install and launch fess.
1. Install and launch kibana4.
1. Open kibana home [http://localhost:5601/](http://localhost:5601/).
1. Input "fess_log" to the textbox of Index name or pattern.
1. Set "requestedAt" to the Time-field name.
1. From the top of page, click *Settings*.
1. Click *Objects*.
1. Click *Import* and select 'fess_log.json' to import settings.
1. Click *Dashboard*.
1. Click *Load Saved Dashboard* and select 'fess_log' dashboard.
1. (Change the period from upper right if you want to do.)

