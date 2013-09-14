#!/bin/bash

cd `dirname $0`
. _project.sh

echo "/nnnnnnnnnnnnnnnnnnnnnnnnnnn"
echo "...Calling the Generate task"
echo "nnnnnnnnnn/"
sh $DBFLUTE_HOME/etc/cmd/_df-generate.sh $MY_PROPERTIES_PATH
taskReturnCode=$?

if [ $taskReturnCode -ne 0 ];then
  exit $taskReturnCode;
fi
