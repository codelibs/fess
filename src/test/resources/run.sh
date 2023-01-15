#!/bin/bash

tail -f ./fess-*/logs/fess-crawler.log &

mvn test -P integrationTests -Dtest.fess.url="http://127.0.0.1:8080" -Dtest.search_engine.url="http://127.0.0.1:9201"
ret=$?

if [ $ret != 0 ] ; then
  for f in `find ./target -type f | grep surefire-reports | grep -v /TEST-` ; do
    cat $f
  done
fi

exit $ret
