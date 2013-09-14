@echo off

setlocal
%~d0
cd %~p0
call _project.bat

echo /nnnnnnnnnnnnnnnnnnnnnnn
echo ...Calling the JDBC task
echo nnnnnnnnnn/
call %DBFLUTE_HOME%\etc\cmd\_df-jdbc.cmd %MY_PROPERTIES_PATH%

if "%pause_at_end%"=="y" (
  pause
)
