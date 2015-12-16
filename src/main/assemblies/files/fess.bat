@echo off

SETLOCAL enabledelayedexpansion
TITLE Fess 10.0.0-SNAPSHOT

SET params='%*'

:loop
FOR /F "usebackq tokens=1* delims= " %%A IN (!params!) DO (
    SET current=%%A
    SET params='%%B'
	SET silent=N
	
	IF "!current!" == "-s" (
		SET silent=Y
	)
	IF "!current!" == "--silent" (
		SET silent=Y
	)	
	
	IF "!silent!" == "Y" (
		SET nopauseonerror=Y
	) ELSE (
	    IF "x!newparams!" NEQ "x" (
	        SET newparams=!newparams! !current!
        ) ELSE (
            SET newparams=!current!
        )
	)
	
    IF "x!params!" NEQ "x" (
		GOTO loop
	)
)

SET HOSTNAME=%COMPUTERNAME%

CALL "%~dp0fess.in.bat"
IF ERRORLEVEL 1 (
	IF NOT DEFINED nopauseonerror (
		PAUSE
	)
	EXIT /B %ERRORLEVEL%
)

set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Dfess.home=%FESS_HOME%
set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Dfess.context.path=/
set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Dfess.port=8080
set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Dfess.webapp.path=%FESS_HOME%\app
set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Dfess.temp.path=%FESS_HOME%\temp
set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Dfess.log.name=%APP_NAME%
set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Dfess.log.path=%FESS_HOME%\logs
set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Dfess.log.level=warn
set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Dlasta.env=web
set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Dtomcat.config.path=tomcat_config.properties

"%JAVA_HOME%\bin\java" %JAVA_OPTS% %FESS_JAVA_OPTS% %FESS_PARAMS% !newparams! -cp "%FESS_CLASSPATH%" "org.codelibs.fess.FessBoot"

ENDLOCAL
