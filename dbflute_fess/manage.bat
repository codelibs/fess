@echo off

setlocal
%~d0
cd %~p0
call _project.bat

:: tilde to remove double quotation
set FIRST_ARG=%~1
if "%FIRST_ARG%"=="" set FIRST_ARG=""
set SECOND_ARG=%2
if "%SECOND_ARG%"=="" set SECOND_ARG=""

call %DBFLUTE_HOME%\etc\cmd\_df-manage.cmd %MY_PROPERTIES_PATH% "%FIRST_ARG%" %SECOND_ARG%

if "%pause_at_end%"=="y" (
  pause
)
