#!/bin/bash
set -euo pipefail

temp_file=/tmp/fess-build.$$
unzip target/releases/fess-*.zip > ${temp_file} 2>&1
tail ${temp_file}

./fess-*/bin/fess > ${temp_file} 2>&1 &
fess_pid=$!

pushd /tmp >/dev/null
git clone https://github.com/codelibs/fess-testdata.git
popd >/dev/null

tail ${temp_file}
touch $(ls -d ./fess-*/logs)/fess-crawler.log
tail -f ./fess-*/logs/*.log &

response_file=/tmp/response.$$
counter=0
ret=1
while [[ $ret != 0 && $counter -lt 180 ]]; do
  echo "Ping Fess... $counter"
  if ! curl -o ${response_file} -v "localhost:8080/api/v1/health"; then
    ret=$?
  else
    ret=0
  fi
  sleep 1
  ((counter++))
done

cat ${response_file}
if ! grep green ${response_file} >/dev/null; then
  exit $?
fi

