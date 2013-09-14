#!/bin/bash

cd `dirname $0`
. _project.sh

echo "/nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn"
echo "...Calling the OutsiteSqlTest task"
echo "nnnnnnnnnn/"
sh $DBFLUTE_HOME/etc/cmd/_df-outside-sql-test.sh $MY_PROPERTIES_PATH $1
taskReturnCode=$?

if [ $taskReturnCode -ne 0 ];then
  exit $taskReturnCode;
fi
