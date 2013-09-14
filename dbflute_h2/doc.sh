#!/bin/bash

cd `dirname $0`
. _project.sh

echo "/nnnnnnnnnnnnnnnnnnnnnnnnnnn"
echo "...Calling the Document task"
echo "nnnnnnnnnn/"
sh $DBFLUTE_HOME/etc/cmd/_df-doc.sh $MY_PROPERTIES_PATH
taskReturnCode=$?

if [ $taskReturnCode -ne 0 ];then
  exit $taskReturnCode;
fi
