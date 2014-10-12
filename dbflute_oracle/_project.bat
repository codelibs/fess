@echo off

set ANT_OPTS=-Xmx512m

set DBFLUTE_HOME=..\mydbflute\dbflute-1.0.5L

set MY_PROJECT_NAME=oracle

set MY_PROPERTIES_PATH=build.properties

if "%pause_at_end%"=="" set pause_at_end=y
