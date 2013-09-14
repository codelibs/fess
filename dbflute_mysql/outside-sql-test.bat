@echo off

setlocal
%~d0
cd %~p0
call _project.bat

echo /nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn
echo ...Calling the OutsiteSqlTest task
echo nnnnnnnnnn/
call %DBFLUTE_HOME%\etc\cmd\_df-outside-sql-test.cmd %MY_PROPERTIES_PATH% %1

if "%pause_at_end%"=="y" (
  pause
)
