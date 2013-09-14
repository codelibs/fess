#!/bin/bash

# the following is in fess-server/build.xml.

FILE=dbflute_oracle/schema/project-schema-fess.xml

perl -pi -e 's/name="DATA_CONFIG_TO_B_TYPE"/javaName="DataConfigToBrowserTypeMapping" name="DATA_CONFIG_TO_B_TYPE"/' $FILE
perl -pi -e 's/name="DATA_CONFIG_TO_LABEL_TYPE"/javaName="DataConfigToLabelTypeMapping" name="DATA_CONFIG_TO_LABEL_TYPE"/' $FILE
perl -pi -e 's/name="DATA_CONFIG_TO_ROLE_TYPE"/javaName="DataConfigToRoleTypeMapping" name="DATA_CONFIG_TO_ROLE_TYPE"/' $FILE
perl -pi -e 's/name="FILE_CONFIG_TO_B_TYPE"/javaName="FileConfigToBrowserTypeMapping" name="FILE_CONFIG_TO_B_TYPE"/' $FILE
perl -pi -e 's/name="FILE_CONFIG_TO_LABEL_TYPE"/javaName="FileConfigToLabelTypeMapping" name="FILE_CONFIG_TO_LABEL_TYPE"/' $FILE
perl -pi -e 's/name="FILE_CONFIG_TO_ROLE_TYPE"/javaName="FileConfigToRoleTypeMapping" name="FILE_CONFIG_TO_ROLE_TYPE"/' $FILE
perl -pi -e 's/name="LABEL_TYPE_TO_ROLE_TYPE"/javaName="LabelTypeToRoleTypeMapping" name="LABEL_TYPE_TO_ROLE_TYPE"/' $FILE
perl -pi -e 's/name="WEB_CONFIG_TO_B_TYPE"/javaName="WebConfigToBrowserTypeMapping" name="WEB_CONFIG_TO_B_TYPE"/' $FILE
perl -pi -e 's/name="WEB_CONFIG_TO_LABEL_TYPE"/javaName="WebConfigToLabelTypeMapping" name="WEB_CONFIG_TO_LABEL_TYPE"/' $FILE
perl -pi -e 's/name="WEB_CONFIG_TO_ROLE_TYPE"/javaName="WebConfigToRoleTypeMapping" name="WEB_CONFIG_TO_ROLE_TYPE"/' $FILE

perl -pi -e 's/dbType="FLOAT" javaType="java.math.BigDecimal"/dbType="FLOAT" javaType="Float"/g' $FILE
