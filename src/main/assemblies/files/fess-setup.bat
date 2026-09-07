@echo off

REM Installs the artifacts Fess needs but does not bundle -- OpenSearch and its Fess plugins.
REM Run it once before starting Fess for the first time:
REM
REM     bin\fess-setup install opensearch

SETLOCAL

SET FESS_HOME=%~dp0..

IF DEFINED JAVA_HOME GOTO javahome
FOR %%I IN (java.exe) DO SET JAVA_EXE=%%~$PATH:I
IF DEFINED JAVA_EXE GOTO run
ECHO Could not find any executable java binary. Please install java in your PATH or set JAVA_HOME 1>&2
EXIT /B 1

:javahome
SET JAVA_EXE=%JAVA_HOME%\bin\java.exe
IF EXIST "%JAVA_EXE%" GOTO run
ECHO JAVA_HOME is set to %JAVA_HOME% but %JAVA_EXE% does not exist 1>&2
EXIT /B 1

:run
"%JAVA_EXE%" -jar "%FESS_HOME%\bin\fess-setup.jar" %*
SET EXIT_CODE=%ERRORLEVEL%

ENDLOCAL & EXIT /B %EXIT_CODE%
