#!/bin/bash

cd `dirname $0`
. _project.sh

echo "/nnnnnnnnnnnnnnnnnnnnnnn"
echo "...Calling the JDBC task"
echo "nnnnnnnnnn/"
sh $DBFLUTE_HOME/etc/cmd/_df-jdbc.sh $MY_PROPERTIES_PATH
taskReturnCode=$?

if [ $taskReturnCode -ne 0 ];then
  exit $taskReturnCode;
fi
