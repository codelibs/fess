#!/bin/bash

cd `dirname $0`
cd ../../..
BASE_DIR=`pwd`

mvn test -P integrationTests -Dtest.fess.url="http://127.0.0.1:8080" -Dtest.es.url="http://127.0.0.1:9201"
ret=$?

if [ $ret != 0 ] ; then
  for f in `find $BASE_DIR -type f | grep surefire-reports | grep -v /TEST-` ; do
    cat $f
  done
fi

exit $ret
