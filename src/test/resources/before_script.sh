#!/bin/bash
set -xuo pipefail

temp_log_file=/tmp/fess-build.$$
unzip target/releases/fess-*.zip > ${temp_log_file} 2>&1
tail ${temp_log_file}

./fess-*/bin/fess > ${temp_log_file} 2>&1 &

temp_json_file=/tmp/fess-log.$$
touch ${temp_json_file}
error_count=0
while true ; do
  status=$(curl -w '%{http_code}\n' -s -o ${temp_json_file} "http://localhost:8080/api/v1/health")
  cat ${temp_json_file}
  if [[ x"${status}" = x200 ]] ; then
    break
  else
    error_count=$((error_count + 1))
  fi
  if [[ ${error_count} -ge 60 ]] ; then
    echo "Fess is not available."
    cat ${temp_log_file} ./fess-*/logs/*.log
    exit 1
  fi
  sleep 1
done

pushd /tmp >/dev/null
git clone https://github.com/codelibs/fess-testdata.git
popd >/dev/null

cat ${temp_log_file} ./fess-*/logs/*.log
curl -s "http://localhost:9201/_cat/indices?v"
curl -s "http://localhost:8080/"

