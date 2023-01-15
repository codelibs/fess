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
set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Xms%FESS_MIN_MEM% -Xmx%FESS_MAX_MEM%
set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -XX:MaxMetaspaceSize=256m -XX:CompressedClassSpaceSize=32m

REM new generation
if NOT "%FESS_HEAP_NEWSIZE%" == "" (
set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Xmn%FESS_HEAP_NEWSIZE%
)

REM max direct memory
if NOT "%FESS_DIRECT_SIZE%" == "" (
set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -XX:MaxDirectMemorySize=%FESS_DIRECT_SIZE%
)

REM set to headless, just in case
set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Djava.awt.headless=true

REM maximum # keep-alive connections to maintain at once
set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Dhttp.maxConnections=20

REM Force the JVM to use IPv4 stack
if NOT "%FESS_USE_IPV4%" == "" (
set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Djava.net.preferIPv4Stack=true
)

set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Djna.nosys=true
set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Djdk.io.permissionsUseCanonicalPath=true

set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -XX:+UseG1GC
set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -XX:InitiatingHeapOccupancyPercent=75

set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Dio.netty.noUnsafe=true
set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Dio.netty.noKeySetOptimization=true
set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Dio.netty.recycler.maxCapacityPerThread=0

set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Dlog4j.shutdownHookEnabled=false
set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Dlog4j2.disable.jmx=true
set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Dlog4j2.formatMsgNoLookups=true
set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Dlog4j.skipJansi=true

REM SSL truststore for certificate validation over https
REM FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Djavax.net.ssl.trustStore=/tech/elastic/config/truststore.jks
REM FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Djavax.net.ssl.trustStorePassword=changeit

REM Causes the JVM to dump its heap on OutOfMemory.
REM set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -XX:+HeapDumpOnOutOfMemoryError
REM The path to the heap dump location, note directory must exists and have enough
REM space for a full heap dump.
REM FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -XX:HeapDumpPath=%FESS_HOME%/logs/heapdump.hprof

REM Disables explicit GC
set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -XX:+DisableExplicitGC

REM Ensure UTF-8 encoding by default (e.g. filenames)
set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Dfile.encoding=UTF-8

REM Application Configuration
set APP_NAME=fess
set SEARCH_ENGINE_HOME=%FESS_HOME%/es

if NOT "%FESS_USE_GC_LOGGING%" == "" set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Xlog:gc*,gc+age=trace,safepoint:file=%FESS_HOME%/logs/gc-%APP_NAME%.log:utctime,pid,tags:filecount=5,filesize=64m

set FESS_CLASSPATH=%FESS_HOME%\lib\classes
set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Dfess
set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Des-foreground=yes
set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Dfess.home="%FESS_HOME%"
set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Dfess.es.dir="%SEARCH_ENGINE_HOME%"
set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Dfess.context.path=/
set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Dfess.port=8080
set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Dfess.webapp.path=%FESS_HOME%\app
set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Dfess.temp.path=%FESS_HOME%\temp
set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Dfess.log.name=%APP_NAME%
set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Dfess.log.path=%FESS_HOME%\logs
set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Dfess.log.level=warn
set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Dlasta.env=web
set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Dtomcat.config.path=tomcat_config.properties

REM External opensearch cluster
REM set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Dfess.search_engine.http_address=http://localhost:9200
REM set FESS_JAVA_OPTS=%FESS_JAVA_OPTS% -Dfess.dictionary.path=%SEARCH_ENGINE_HOME%/config/

set GROOVY_TURN_OFF_JAVA_WARNINGS=true
