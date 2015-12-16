@echo off
SETLOCAL

TITLE Fess Service 10.0.0-SNAPSHOT

if NOT DEFINED JAVA_HOME goto err

set SCRIPT_DIR=%~dp0
for %%I in ("%SCRIPT_DIR%..") do set FESS_HOME=%%~dpfI

rem Detect JVM version to figure out appropriate executable to use
if not exist "%JAVA_HOME%\bin\java.exe" (
echo JAVA_HOME points to an invalid Java installation (no java.exe found in "%JAVA_HOME%"^). Exiting...
goto:eof
)
"%JAVA_HOME%\bin\java" -version 2>&1 | "%windir%\System32\find" "64-Bit" >nul:

if errorlevel 1 goto x86
set EXECUTABLE=%FESS_HOME%\bin\fess-service-x64.exe
set SERVICE_ID=fess-service-x64
set ARCH=64-bit
goto checkExe

:x86
set EXECUTABLE=%FESS_HOME%\bin\fess-service-x86.exe
set SERVICE_ID=fess-service-x86
set ARCH=32-bit

:checkExe
if EXIST "%EXECUTABLE%" goto okExe
echo fess-service-(x86|x64).exe was not found...

:okExe
set FESS_VERSION=10.0.0-SNAPSHOT

if "%LOG_DIR%" == "" set LOG_DIR=%FESS_HOME%\logs

if "x%1x" == "xx" goto displayUsage
set SERVICE_CMD=%1
shift
if "x%1x" == "xx" goto checkServiceCmd
set SERVICE_ID=%1

:checkServiceCmd

if "%LOG_OPTS%" == "" set LOG_OPTS=--LogPath "%LOG_DIR%" --LogPrefix "%SERVICE_ID%" --StdError auto --StdOutput auto

if /i %SERVICE_CMD% == install goto doInstall
if /i %SERVICE_CMD% == remove goto doRemove
if /i %SERVICE_CMD% == start goto doStart
if /i %SERVICE_CMD% == stop goto doStop
if /i %SERVICE_CMD% == manager goto doManagment
echo Unknown option "%SERVICE_CMD%"

:displayUsage
echo.
echo Usage: service.bat install^|remove^|start^|stop^|manager [SERVICE_ID]
goto:eof

:doStart
"%EXECUTABLE%" //ES//%SERVICE_ID% %LOG_OPTS%
if not errorlevel 1 goto started
echo Failed starting '%SERVICE_ID%' service
goto:eof
:started
echo The service '%SERVICE_ID%' has been started
goto:eof

:doStop
"%EXECUTABLE%" //SS//%SERVICE_ID% %LOG_OPTS%
if not errorlevel 1 goto stopped
echo Failed stopping '%SERVICE_ID%' service
goto:eof
:stopped
echo The service '%SERVICE_ID%' has been stopped
goto:eof

:doRemove
rem Remove the service
"%EXECUTABLE%" //DS//%SERVICE_ID% %LOG_OPTS%
if not errorlevel 1 goto removed
echo Failed removing '%SERVICE_ID%' service
goto:eof
:removed
echo The service '%SERVICE_ID%' has been removed
goto:eof

:doInstall
echo Installing service      :  "%SERVICE_ID%"
echo Using JAVA_HOME (%ARCH%):  "%JAVA_HOME%"

rem Check JVM server dll first
set JVM_DLL=%JAVA_HOME%\jre\bin\server\jvm.dll
if exist "%JVM_DLL%" goto foundJVM

rem Check 'server' JRE (JRE installed on Windows Server)
set JVM_DLL=%JAVA_HOME%\bin\server\jvm.dll
if exist "%JVM_DLL%" goto foundJVM

rem Fallback to 'client' JRE
set JVM_DLL=%JAVA_HOME%\bin\client\jvm.dll

if exist "%JVM_DLL%" (
echo Warning: JAVA_HOME points to a JRE and not JDK installation; a client (not a server^) JVM will be used...
) else (
echo JAVA_HOME points to an invalid Java installation (no jvm.dll found in "%JAVA_HOME%"^). Existing...
goto:eof
)

:foundJVM
if "%FESS_MIN_MEM%" == "" set FESS_MIN_MEM=256m
if "%FESS_MAX_MEM%" == "" set FESS_MAX_MEM=1g

if NOT "%FESS_HEAP_SIZE%" == "" set FESS_MIN_MEM=%FESS_HEAP_SIZE%
if NOT "%FESS_HEAP_SIZE%" == "" set FESS_MAX_MEM=%FESS_HEAP_SIZE%

call:convertxm %FESS_MIN_MEM% JVM_XMS
call:convertxm %FESS_MAX_MEM% JVM_XMX

REM java_opts might be empty - init to avoid tripping commons daemon (if the command starts with ;)
if "%JAVA_OPTS%" == "" set JAVA_OPTS=-XX:+UseParNewGC

CALL "%FESS_HOME%\bin\fess.in.bat"

rem thread stack size
set JVM_SS=256

set FESS_PARAMS=-Dfess;-Dfess.home="%FESS_HOME%";-Dfess.es.dir="%ES_HOME%";-Dfess.home="%FESS_HOME%";-Dfess.context.path="/";-Dfess.port=8080;-Dfess.webapp.path="%FESS_HOME%\app";-Dfess.temp.path="%FESS_HOME%\temp";-Dfess.log.name="%APP_NAME%";-Dfess.log.path="%FESS_HOME%\logs";-Dfess.log.level=warn;-Dlasta.env=web;-Dtomcat.config.path=tomcat_config.properties

set JVM_OPTS=%JAVA_OPTS: =;%

if not "%FESS_JAVA_OPTS%" == "" set JVM_FESS_JAVA_OPTS=%FESS_JAVA_OPTS: =#%
if not "%FESS_JAVA_OPTS%" == "" set JVM_OPTS=%JVM_OPTS%;%JVM_FESS_JAVA_OPTS%

if "%FESS_START_TYPE%" == "" set FESS_START_TYPE=manual
if "%FESS_STOP_TIMEOUT%" == "" set FESS_STOP_TIMEOUT=0

"%EXECUTABLE%" //IS//%SERVICE_ID% --Startup %FESS_START_TYPE% --StopTimeout %FESS_STOP_TIMEOUT% --StartClass org.codelibs.fess.FessBoot --StopClass org.codelibs.fess.FessBoot --StartMethod main --StopMethod shutdown --Classpath "%FESS_CLASSPATH%" --JvmSs %JVM_SS% --JvmMs %JVM_XMS% --JvmMx %JVM_XMX% --JvmOptions %JVM_OPTS% ++JvmOptions %FESS_PARAMS% %LOG_OPTS% --PidFile "%SERVICE_ID%.pid" --DisplayName "Fess %FESS_VERSION% (%SERVICE_ID%)" --Description "Fess %FESS_VERSION% Windows Service - https://github.com/codelibs/fess" --Jvm "%JVM_DLL%" --StartMode jvm --StopMode jvm --StartPath "%FESS_HOME%"

if not errorlevel 1 goto installed
echo Failed installing '%SERVICE_ID%' service
goto:eof

:installed
echo The service '%SERVICE_ID%' has been installed.
goto:eof

:err
echo JAVA_HOME environment variable must be set!
pause
goto:eof

rem ---
rem Function for converting Xm[s|x] values into MB which Commons Daemon accepts
rem ---
:convertxm
set value=%~1
rem extract last char (unit)
set unit=%value:~-1%
rem assume the unit is specified
set conv=%value:~0,-1%

if "%unit%" == "k" goto kilo
if "%unit%" == "K" goto kilo
if "%unit%" == "m" goto mega
if "%unit%" == "M" goto mega
if "%unit%" == "g" goto giga
if "%unit%" == "G" goto giga

rem no unit found, must be bytes; consider the whole value
set conv=%value%
rem convert to KB
set /a conv=%conv% / 1024
:kilo
rem convert to MB
set /a conv=%conv% / 1024
goto mega
:giga
rem convert to MB
set /a conv=%conv% * 1024
:mega
set "%~2=%conv%"
goto:eof

ENDLOCAL
