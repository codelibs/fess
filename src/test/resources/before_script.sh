#!/bin/bash
set -xuo pipefail

temp_log_file=/tmp/fess-build.$$
unzip target/releases/fess-*.zip > ${temp_log_file} 2>&1
tail ${temp_log_file}

# bin/fess.in.sh points at http://localhost:9200 by default, which is the service container.
export FESS_DICTIONARY_PATH="${FESS_DICTIONARY_PATH:-/usr/share/opensearch/config/dictionary}"
./fess-*/bin/fess > ${temp_log_file} 2>&1 &

temp_json_file=/tmp/fess-log.$$
touch ${temp_json_file}
error_count=0
while true ; do
  status=$(curl -w '%{http_code}\n' -s -o ${temp_json_file} "http://localhost:8080/api/v2/health")
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
cd fess-testdata
git checkout f19176ab1b7ddc0a40393a8cbbb8d1c17b27c3ce
cd ..
popd >/dev/null

cat ${temp_log_file} ./fess-*/logs/*.log
curl -s "http://localhost:9200/_cat/indices?v"
curl -s "http://localhost:8080/"

