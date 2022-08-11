#!/bin/bash

TMP_FILE=/tmp/fess-build.$$
unzip target/releases/fess-*.zip 2>&1 > $TMP_FILE
tail $TMP_FILE

./fess-*/bin/fess 2>&1 > $TMP_FILE &

pushd /tmp
git clone https://github.com/codelibs/fess-testdata.git
popd

tail $TMP_FILE
touch `ls -d ./fess-*/logs`/fess-crawler.log
tail -f ./fess-*/logs/*.log &

counter=0
ret=1
while [ $ret != 0 -a $counter != 180 ] ; do
  echo "Ping Fess... $counter"
  curl -v "localhost:8080/json/?type=ping"
  ret=$?
  sleep 1
  counter=$((counter + 1))
done

