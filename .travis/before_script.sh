#!/bin/sh

TMP_FILE=/tmp/fess-build.$$
unzip target/releases/fess-*.zip 2>&1 > $TMP_FILE
tail $TMP_FILE

./fess-*/bin/fess 2>&1 > $TMP_FILE &
sleep 3
tail $TMP_FILE
touch `ls -d ./fess-*`/logs/fess-crawler.log
tail -f ./fess-*/logs/*.log &

counter=0
ret=1
while [ $ret != 0 -a $counter != 60 ] ; do
  echo "Ping Fess... $counter"
  curl -v 127.0.0.1:8080/json/?type=ping
  #ret=$?
  ret=1
  sleep 5
  counter=`expr $counter + 1`
done

cd /tmp
git clone https://github.com/codelibs/fess-testdata.git
