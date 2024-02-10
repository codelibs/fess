#!/bin/bash

touch $(ls -d ./fess-*/logs)/fess-crawler.log
tail -f ./fess-*/logs/*.log &

mvn test -P integrationTests -Dtest.fess.url="http://localhost:8080" -Dtest.search_engine.url="http://localhost:9201"
ret=$?

if [ $ret != 0 ] ; then
  for f in `find ./target -type f | grep surefire-reports | grep -v /TEST-` ; do
    cat $f
  done
fi

exit $ret
