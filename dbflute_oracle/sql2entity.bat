@echo off

setlocal
%~d0
cd %~p0
call _project.bat

echo /nnnnnnnnnnnnnnnnnnnnnnnnnnnnn
echo ...Calling the Sql2Entity task
echo nnnnnnnnnn/
call %DBFLUTE_HOME%\etc\cmd\_df-sql2entity.cmd %MY_PROPERTIES_PATH% %1

if "%pause_at_end%"=="y" (
  pause
)
