#!/bin/bash
set -xuo pipefail

temp_file=/tmp/fess-build.$$
unzip target/releases/fess-*.zip > ${temp_file} 2>&1
tail ${temp_file}

./fess-*/bin/fess > ${temp_file} 2>&1 &

counter=0
while [[ ! -f ./fess-*/logs/fess.log  && $counter -lt 30 ]]; do
  sleep 1
  ((counter++))
done

pushd /tmp >/dev/null
git clone https://github.com/codelibs/fess-testdata.git
popd >/dev/null

touch $(ls -d ./fess-*/logs)/fess-crawler.log
tail -f ${temp_file} ./fess-*/logs/*.log &

response_file=/tmp/response.$$
touch ${response_file}

counter=0
ret=1
while [[ $ret != 0 && $counter -lt 180 ]]; do
  echo "Ping Fess... $counter"
  curl -o ${response_file} -v "localhost:8080/api/v1/health"
  cat ${response_file}
  if grep green ${response_file} >/dev/null; then
    ret=0
  fi
  sleep 1
  ((counter++))
done


