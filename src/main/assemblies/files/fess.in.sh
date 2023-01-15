#!/bin/sh

FESS_CLASSPATH=$FESS_HOME/lib/classes

# JAVA_OPTS is not a built-in JVM mechanism but some people think it is so we
# warn them that we are not observing the value of $JAVA_OPTS
if [ ! -z "$JAVA_OPTS" ]; then
  echo -n "warning: ignoring JAVA_OPTS=$JAVA_OPTS; "
  echo "pass JVM parameters via FESS_JAVA_OPTS"
fi

if [ "x$FESS_MIN_MEM" = "x" ]; then
    FESS_MIN_MEM=256m
fi
if [ "x$FESS_MAX_MEM" = "x" ]; then
    FESS_MAX_MEM=2g
fi
if [ "x$FESS_HEAP_SIZE" != "x" ]; then
    FESS_MIN_MEM=$FESS_HEAP_SIZE
    FESS_MAX_MEM=$FESS_HEAP_SIZE
fi

# External opensearch cluster
#SEARCH_ENGINE_HTTP_URL=http://localhost:9200
#FESS_DICTIONARY_PATH=/var/lib/opensearch/config/

# SSL truststore for certificate validation over https
#FESS_JAVA_OPTS="$FESS_JAVA_OPTS -Djavax.net.ssl.trustStore=/tech/elastic/config/truststore.jks"
#FESS_JAVA_OPTS="$FESS_JAVA_OPTS -Djavax.net.ssl.trustStorePassword=changeit"

# min and max heap sizes should be set to the same value to avoid
# stop-the-world GC pauses during resize, and so that we can lock the
# heap in memory on startup to prevent any of it from being swapped
# out.
FESS_JAVA_OPTS="$FESS_JAVA_OPTS -Xms${FESS_MIN_MEM}"
FESS_JAVA_OPTS="$FESS_JAVA_OPTS -Xmx${FESS_MAX_MEM}"
FESS_JAVA_OPTS="$FESS_JAVA_OPTS -XX:MaxMetaspaceSize=256m -XX:CompressedClassSpaceSize=32m"

# new generation
if [ "x$FESS_HEAP_NEWSIZE" != "x" ]; then
    FESS_JAVA_OPTS="$FESS_JAVA_OPTS -Xmn${FESS_HEAP_NEWSIZE}"
fi

# set to headless, just in case
FESS_JAVA_OPTS="$FESS_JAVA_OPTS -Djava.awt.headless=true"

# maximum # keep-alive connections to maintain at once
FESS_JAVA_OPTS="$FESS_JAVA_OPTS -Dhttp.maxConnections=20"

# Force the JVM to use IPv4 stack
if [ "x$FESS_USE_IPV4" != "x" ]; then
  FESS_JAVA_OPTS="$FESS_JAVA_OPTS -Djava.net.preferIPv4Stack=true"
fi

FESS_JAVA_OPTS="$FESS_JAVA_OPTS -Djna.nosys=true"
FESS_JAVA_OPTS="$FESS_JAVA_OPTS -Djdk.io.permissionsUseCanonicalPath=true"

FESS_JAVA_OPTS="$FESS_JAVA_OPTS -XX:+UseG1GC"
FESS_JAVA_OPTS="$FESS_JAVA_OPTS -XX:InitiatingHeapOccupancyPercent=75"

FESS_JAVA_OPTS="$FESS_JAVA_OPTS -Dio.netty.noUnsafe=true"
FESS_JAVA_OPTS="$FESS_JAVA_OPTS -Dio.netty.noKeySetOptimization=true"
FESS_JAVA_OPTS="$FESS_JAVA_OPTS -Dio.netty.recycler.maxCapacityPerThread=0"

FESS_JAVA_OPTS="$FESS_JAVA_OPTS -Dlog4j.shutdownHookEnabled=false"
FESS_JAVA_OPTS="$FESS_JAVA_OPTS -Dlog4j2.disable.jmx=true"
FESS_JAVA_OPTS="$FESS_JAVA_OPTS -Dlog4j2.formatMsgNoLookups=true"
FESS_JAVA_OPTS="$FESS_JAVA_OPTS -Dlog4j.skipJansi=true"

# Causes the JVM to dump its heap on OutOfMemory.
#FESS_JAVA_OPTS="$FESS_JAVA_OPTS -XX:+HeapDumpOnOutOfMemoryError"
# The path to the heap dump location, note directory must exists and have enough
# space for a full heap dump.
#FESS_JAVA_OPTS="$FESS_JAVA_OPTS -XX:HeapDumpPath=$FESS_HOME/logs/heapdump.hprof"

# Disables explicit GC
FESS_JAVA_OPTS="$FESS_JAVA_OPTS -XX:+DisableExplicitGC"

# Ensure UTF-8 encoding by default (e.g. filenames)
FESS_JAVA_OPTS="$FESS_JAVA_OPTS -Dfile.encoding=UTF-8"

# Application Configuration
if [ "x$APP_NAME" = "x" ]; then
  APP_NAME=fess
fi
if [ "x$SEARCH_ENGINE_HOME" = "x" ]; then
  SEARCH_ENGINE_HOME=$FESS_HOME/es
fi
if [ "x$FESS_TEMP_PATH" = "x" ]; then
  FESS_TEMP_PATH=$FESS_HOME/temp
fi
if [ "x$FESS_LOG_PATH" = "x" ]; then
  FESS_LOG_PATH=$FESS_HOME/logs
fi
if [ "x$FESS_LOG_LEVEL" = "x" ]; then
  FESS_LOG_LEVEL=warn
fi
if [ "x$FESS_PORT" = "x" ]; then
  FESS_PORT=8080
fi
if [ "x$FESS_CONTEXT_PATH" = "x" ]; then
  FESS_CONTEXT_PATH=/
fi
if [ "x$FESS_USE_GC_LOGGING" != "x" ]; then
  FESS_JAVA_OPTS="$FESS_JAVA_OPTS -Xlog:gc*,gc+age=trace,safepoint:file=$FESS_LOG_PATH/gc-$APP_NAME.log:utctime,pid,tags:filecount=5,filesize=64m"
fi
FESS_JAVA_OPTS="$FESS_JAVA_OPTS -Dfess.home=$FESS_HOME"
FESS_JAVA_OPTS="$FESS_JAVA_OPTS -Dfess.context.path=$FESS_CONTEXT_PATH"
FESS_JAVA_OPTS="$FESS_JAVA_OPTS -Dfess.port=$FESS_PORT"
FESS_JAVA_OPTS="$FESS_JAVA_OPTS -Dfess.webapp.path=$FESS_HOME/app"
FESS_JAVA_OPTS="$FESS_JAVA_OPTS -Dfess.temp.path=$FESS_TEMP_PATH"
FESS_JAVA_OPTS="$FESS_JAVA_OPTS -Dfess.log.name=$APP_NAME"
FESS_JAVA_OPTS="$FESS_JAVA_OPTS -Dfess.log.path=$FESS_LOG_PATH"
FESS_JAVA_OPTS="$FESS_JAVA_OPTS -Dfess.log.level=$FESS_LOG_LEVEL"
FESS_JAVA_OPTS="$FESS_JAVA_OPTS -Dlasta.env=web"
FESS_JAVA_OPTS="$FESS_JAVA_OPTS -Dtomcat.config.path=tomcat_config.properties"
if [ "x$FESS_CONF_PATH" != "x" ]; then
  FESS_JAVA_OPTS="$FESS_JAVA_OPTS -Dfess.conf.path=$FESS_CONF_PATH"
fi
if [ "x$FESS_VAR_PATH" != "x" ]; then
  FESS_JAVA_OPTS="$FESS_JAVA_OPTS -Dfess.var.path=$FESS_VAR_PATH"
fi
if [ "x$SEARCH_ENGINE_HTTP_URL" != "x" ]; then
  FESS_JAVA_OPTS="$FESS_JAVA_OPTS -Dfess.search_engine.http_address=$SEARCH_ENGINE_HTTP_URL"
fi
if [ "x$FESS_DICTIONARY_PATH" != "x" ]; then
  FESS_JAVA_OPTS="$FESS_JAVA_OPTS -Dfess.dictionary.path=$FESS_DICTIONARY_PATH"
fi

GROOVY_TURN_OFF_JAVA_WARNINGS=true
