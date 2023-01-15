@echo off
SETLOCAL enabledelayedexpansion

TITLE Fess Service

if NOT DEFINED JAVA_HOME goto err

set SCRIPT_DIR=%~dp0
for %%I in ("%SCRIPT_DIR%..") do set FESS_HOME=%%~dpfI

rem Detect JVM version to figure out appropriate executable to use
if not exist "%JAVA_HOME%\bin\java.exe" (
echo JAVA_HOME points to an invalid Java installation (no java.exe found in "%JAVA_HOME%"^). Exiting...
goto:eof
)

"%JAVA_HOME%\bin\java" -Xmx50M -version > nul 2>&1

if errorlevel 1 (
	echo Warning: Could not start JVM to detect version, defaulting to x86:
	goto x86
)

"%JAVA_HOME%\bin\java" -Xmx50M -version 2>&1 | "%windir%\System32\find" "64-Bit" >nul:

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

:doManagment
set EXECUTABLE_MGR=%FESS_HOME%\bin\fess-service-mgr.exe
"%EXECUTABLE_MGR%" //ES//%SERVICE_ID%
if not errorlevel 1 goto managed
echo Failed starting service manager for '%SERVICE_ID%'
goto:eof
:managed
echo Successfully started service manager for '%SERVICE_ID%'.
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
if exist "%JAVA_HOME%"\jre\bin\server\jvm.dll (
	set JVM_DLL=\jre\bin\server\jvm.dll
	goto foundJVM
)

rem Check 'server' JRE (JRE installed on Windows Server)
if exist "%JAVA_HOME%"\bin\server\jvm.dll (
	set JVM_DLL=\bin\server\jvm.dll
	goto foundJVM
)

rem Fallback to 'client' JRE
if exist "%JAVA_HOME%"\bin\client\jvm.dll (
	set JVM_DLL=\bin\client\jvm.dll
	echo Warning: JAVA_HOME points to a JRE and not JDK installation; a client (not a server^) JVM will be used...
) else (
	echo JAVA_HOME points to an invalid Java installation (no jvm.dll found in "%JAVA_HOME%"^). Exiting...
	goto:eof
)

:foundJVM
if "%FESS_MIN_MEM%" == "" set FESS_MIN_MEM=256m
if "%FESS_MAX_MEM%" == "" set FESS_MAX_MEM=1g

if NOT "%FESS_HEAP_SIZE%" == "" set FESS_MIN_MEM=%FESS_HEAP_SIZE%
if NOT "%FESS_HEAP_SIZE%" == "" set FESS_MAX_MEM=%FESS_HEAP_SIZE%

call:convertxm %FESS_MIN_MEM% JVM_XMS
call:convertxm %FESS_MAX_MEM% JVM_XMX

CALL "%FESS_HOME%\bin\fess.in.bat"

rem thread stack size
set JVM_SS=256

set FESS_PARAMS=-Dfess;-Dfess.home="%FESS_HOME%";-Dfess.es.dir="%SEARCH_ENGINE_HOME%";-Dfess.home="%FESS_HOME%";-Dfess.context.path="/";-Dfess.port=8080;-Dfess.webapp.path="%FESS_HOME%\app";-Dfess.temp.path="%FESS_HOME%\temp";-Dfess.log.name="%APP_NAME%";-Dfess.log.path="%FESS_HOME%\logs";-Dfess.log.level=warn;-Dlasta.env=web;-Dtomcat.config.path=tomcat_config.properties

set JVM_OPTS=-XX:+UseG1GC%FESS_JAVA_OPTS: =;%

if "%FESS_START_TYPE%" == "" set FESS_START_TYPE=manual
if "%FESS_STOP_TIMEOUT%" == "" set FESS_STOP_TIMEOUT=0

if "%SERVICE_DISPLAY_NAME%" == "" set SERVICE_DISPLAY_NAME=Fess (%SERVICE_ID%)
if "%SERVICE_DESCRIPTION%" == "" set SERVICE_DESCRIPTION=Fess Service - https://github.com/codelibs/fess

if not "%SERVICE_USERNAME%" == "" (
	if not "%SERVICE_PASSWORD%" == "" (
		set SERVICE_PARAMS=%SERVICE_PARAMS% --ServiceUser "%SERVICE_USERNAME%" --ServicePassword "%SERVICE_PASSWORD%"
	)
)

 "%EXECUTABLE%" //IS//%SERVICE_ID% --Startup %FESS_START_TYPE% --StopTimeout %FESS_STOP_TIMEOUT% --StartClass org.codelibs.fess.FessBoot --StopClass org.codelibs.fess.FessBoot --StartMethod main --StopMethod shutdown --Classpath "%FESS_CLASSPATH%" --JvmSs %JVM_SS% --JvmMs %JVM_XMS% --JvmMx %JVM_XMX% --JvmOptions %JVM_OPTS% ++JvmOptions %FESS_PARAMS% %LOG_OPTS% --PidFile "%SERVICE_ID%.pid" --DisplayName "%SERVICE_DISPLAY_NAME%" --Description "%SERVICE_DESCRIPTION%" --Jvm "%%JAVA_HOME%%%JVM_DLL%" --StartMode jvm --StopMode jvm --StartPath "%FESS_HOME%" %SERVICE_PARAMS% ++StartParams start

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
