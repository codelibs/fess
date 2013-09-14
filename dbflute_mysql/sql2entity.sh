#!/bin/bash

cd `dirname $0`
. _project.sh

echo "/nnnnnnnnnnnnnnnnnnnnnnnnnnnnn"
echo "...Calling the Sql2Entity task"
echo "nnnnnnnnnn/"
sh $DBFLUTE_HOME/etc/cmd/_df-sql2entity.sh $MY_PROPERTIES_PATH $1
taskReturnCode=$?

if [ $taskReturnCode -ne 0 ];then
  exit $taskReturnCode;
fi
