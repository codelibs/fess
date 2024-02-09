#!/bin/bash
set -xuo pipefail

temp_file=/tmp/fess-build.$$
unzip target/releases/fess-*.zip > ${temp_file} 2>&1
tail ${temp_file}

./fess-*/bin/fess > ${temp_file} 2>&1 &

error_count=0
while true ; do
  status=$(curl -w '%{http_code}\n' -s -o /dev/null "http://localhost:8080/api/v1/health")
  if [[ x"${status}" != x200 ]] ; then
    error_count=$((error_count + 1))
  fi
  if [[ ${error_count} -ge 180 ]] ; then
    echo "Fess is not available."
    cat ${temp_file} ./fess-*/logs/*.log
    exit 1
  fi
  sleep 1
done

pushd /tmp >/dev/null
git clone https://github.com/codelibs/fess-testdata.git
popd >/dev/null

touch $(ls -d ./fess-*/logs)/fess-crawler.log
tail -f ${temp_file} ./fess-*/logs/*.log &

