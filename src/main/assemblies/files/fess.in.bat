@echo off

if DEFINED JAVA_HOME goto cont

:err
ECHO JAVA_HOME environment variable must be set! 1>&2
EXIT /B 1 

:cont
set SCRIPT_DIR=%~dp0
for %%I in ("%SCRIPT_DIR%..") do set FESS_HOME=%%~dpfI


REM ***** JAVA options *****

if "%FESS_MIN_MEM%" == "" (
set FESS_MIN_MEM=256m
)

if "%FESS_MAX_MEM%" == "" (
set FESS_MAX_MEM=1g
)

if NOT "%FESS_HEAP_SIZE%" == "" (
set FESS_MIN_MEM=%FESS_HEAP_SIZE%
set FESS_MAX_MEM=%FESS_HEAP_SIZE%
)

REM min and max heap sizes should be set to the same value to avoid
REM stop-the-world GC pauses during resize, and so that we can lock the
REM heap in memory on startup to prevent any of it from being swapped
REM out.
set JAVA_OPTS=%JAVA_OPTS% -Xms%FESS_MIN_MEM% -Xmx%FESS_MAX_MEM%

REM new generation
if NOT "%FESS_HEAP_NEWSIZE%" == "" (
set JAVA_OPTS=%JAVA_OPTS% -Xmn%FESS_HEAP_NEWSIZE%
)

REM max direct memory
if NOT "%FESS_DIRECT_SIZE%" == "" (
set JAVA_OPTS=%JAVA_OPTS% -XX:MaxDirectMemorySize=%FESS_DIRECT_SIZE%
)

REM set to headless, just in case
set JAVA_OPTS=%JAVA_OPTS% -Djava.awt.headless=true

REM Force the JVM to use IPv4 stack
if NOT "%FESS_USE_IPV4%" == "" (
set JAVA_OPTS=%JAVA_OPTS% -Djava.net.preferIPv4Stack=true
)

set JAVA_OPTS=%JAVA_OPTS% -XX:+UseParNewGC
set JAVA_OPTS=%JAVA_OPTS% -XX:+UseConcMarkSweepGC

set JAVA_OPTS=%JAVA_OPTS% -XX:CMSInitiatingOccupancyFraction=75
set JAVA_OPTS=%JAVA_OPTS% -XX:+UseCMSInitiatingOccupancyOnly

if NOT "%FESS_USE_GC_LOGGING%" == "" set JAVA_OPTS=%JAVA_OPTS% -XX:+PrintGCDetails
if NOT "%FESS_USE_GC_LOGGING%" == "" set JAVA_OPTS=%JAVA_OPTS% -XX:+PrintGCTimeStamps
if NOT "%FESS_USE_GC_LOGGING%" == "" set JAVA_OPTS=%JAVA_OPTS% -XX:+PrintGCDateStamps
if NOT "%FESS_USE_GC_LOGGING%" == "" set JAVA_OPTS=%JAVA_OPTS% -XX:+PrintClassHistogram
if NOT "%FESS_USE_GC_LOGGING%" == "" set JAVA_OPTS=%JAVA_OPTS% -XX:+PrintTenuringDistribution
if NOT "%FESS_USE_GC_LOGGING%" == "" set JAVA_OPTS=%JAVA_OPTS% -XX:+PrintGCApplicationStoppedTime
if NOT "%FESS_USE_GC_LOGGING%" == "" set JAVA_OPTS=%JAVA_OPTS% -Xloggc:%FESS_HOME%/logs/gc.log

REM Causes the JVM to dump its heap on OutOfMemory.
set JAVA_OPTS=%JAVA_OPTS% -XX:+HeapDumpOnOutOfMemoryError
REM The path to the heap dump location, note directory must exists and have enough
REM space for a full heap dump.
REM JAVA_OPTS=%JAVA_OPTS% -XX:HeapDumpPath=%FESS_HOME%/logs/heapdump.hprof

REM Disables explicit GC
set JAVA_OPTS=%JAVA_OPTS% -XX:+DisableExplicitGC

REM Ensure UTF-8 encoding by default (e.g. filenames)
set JAVA_OPTS=%JAVA_OPTS% -Dfile.encoding=UTF-8

REM Use Groovy ClassValue
set JAVA_OPTS=%JAVA_OPTS% -Dgroovy.use.classvalue=true

REM Application Configuration
set APP_NAME=fess
set ES_HOME=%FESS_HOME%/es

set FESS_CLASSPATH=%FESS_HOME%\lib\classes
set FESS_JAVA_OPTS=-Dfess %FESS_JAVA_OPTS%
set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Des-foreground=yes
set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Dfess.home="%FESS_HOME%"
set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Dfess.es.dir="%ES_HOME%"
set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Dfess.context.path=/
set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Dfess.port=8080
set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Dfess.webapp.path=%FESS_HOME%\app
set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Dfess.temp.path=%FESS_HOME%\temp
set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Dfess.log.name=%APP_NAME%
set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Dfess.log.path=%FESS_HOME%\logs
set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Dfess.log.level=warn
set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Dlasta.env=web
set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Dtomcat.config.path=tomcat_config.properties

REM External elasticsearch cluster
REM FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Dfess.es.http_address=http://localhost:9200
REM FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Dfess.es.transport_addresses=localhost:9300
REM FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Dfess.dictionary.path=%ES_HOME%\data\
