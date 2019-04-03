#!/bin/sh

FESS_CLASSPATH=$FESS_HOME/lib/classes

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

# External elasticsearch cluster
#ES_HTTP_URL=http://localhost:9200
#FESS_DICTIONARY_PATH=/var/lib/elasticsearch/config/

# SSL truststore for certificate validation over https
#JAVA_OPTS="$JAVA_OPTS -Djavax.net.ssl.trustStore=/tech/elastic/config/truststore.jks"
#JAVA_OPTS="$JAVA_OPTS -Djavax.net.ssl.trustStorePassword=changeit"

# min and max heap sizes should be set to the same value to avoid
# stop-the-world GC pauses during resize, and so that we can lock the
# heap in memory on startup to prevent any of it from being swapped
# out.
JAVA_OPTS="$JAVA_OPTS -Xms${FESS_MIN_MEM}"
JAVA_OPTS="$JAVA_OPTS -Xmx${FESS_MAX_MEM}"

# new generation
if [ "x$FESS_HEAP_NEWSIZE" != "x" ]; then
    JAVA_OPTS="$JAVA_OPTS -Xmn${FESS_HEAP_NEWSIZE}"
fi

# set to headless, just in case
JAVA_OPTS="$JAVA_OPTS -Djava.awt.headless=true"

# maximum # keep-alive connections to maintain at once
JAVA_OPTS="$JAVA_OPTS -Dhttp.maxConnections=20"

# Force the JVM to use IPv4 stack
if [ "x$FESS_USE_IPV4" != "x" ]; then
  JAVA_OPTS="$JAVA_OPTS -Djava.net.preferIPv4Stack=true"
fi

JAVA_OPTS="$JAVA_OPTS -Djna.nosys=true"
JAVA_OPTS="$JAVA_OPTS -Djdk.io.permissionsUseCanonicalPath=true"

JAVA_OPTS="$JAVA_OPTS -XX:+UseConcMarkSweepGC"

JAVA_OPTS="$JAVA_OPTS -XX:CMSInitiatingOccupancyFraction=75"
JAVA_OPTS="$JAVA_OPTS -XX:+UseCMSInitiatingOccupancyOnly"

JAVA_OPTS="$JAVA_OPTS -Dio.netty.noUnsafe=true"
JAVA_OPTS="$JAVA_OPTS -Dio.netty.noKeySetOptimization=true"
JAVA_OPTS="$JAVA_OPTS -Dio.netty.recycler.maxCapacityPerThread=0"

JAVA_OPTS="$JAVA_OPTS -Dlog4j.shutdownHookEnabled=false"
JAVA_OPTS="$JAVA_OPTS -Dlog4j2.disable.jmx=true"
JAVA_OPTS="$JAVA_OPTS -Dlog4j.skipJansi=true"

# GC logging options
if [ "x$FESS_USE_GC_LOGGING" != "x" ]; then
  JAVA_OPTS="$JAVA_OPTS -XX:+PrintGCDetails"
  JAVA_OPTS="$JAVA_OPTS -XX:+PrintGCTimeStamps"
  JAVA_OPTS="$JAVA_OPTS -XX:+PrintGCDateStamps"
  JAVA_OPTS="$JAVA_OPTS -XX:+PrintClassHistogram"
  JAVA_OPTS="$JAVA_OPTS -XX:+PrintTenuringDistribution"
  JAVA_OPTS="$JAVA_OPTS -XX:+PrintGCApplicationStoppedTime"
  JAVA_OPTS="$JAVA_OPTS -Xloggc:/var/log/elasticsearch/gc.log"
fi

# Causes the JVM to dump its heap on OutOfMemory.
JAVA_OPTS="$JAVA_OPTS -XX:+HeapDumpOnOutOfMemoryError"
# The path to the heap dump location, note directory must exists and have enough
# space for a full heap dump.
#JAVA_OPTS="$JAVA_OPTS -XX:HeapDumpPath=$FESS_HOME/logs/heapdump.hprof"

# Disables explicit GC
JAVA_OPTS="$JAVA_OPTS -XX:+DisableExplicitGC"

# Ensure UTF-8 encoding by default (e.g. filenames)
JAVA_OPTS="$JAVA_OPTS -Dfile.encoding=UTF-8"

# Use Groovy ClassValue
JAVA_OPTS="$JAVA_OPTS -Dgroovy.use.classvalue=true"

# Application Configuration
if [ "x$APP_NAME" = "x" ]; then
  APP_NAME=fess
fi
if [ "x$ES_HOME" = "x" ]; then
  ES_HOME=$FESS_HOME/es
fi
if [ "x$FESS_TEMP_PATH" = "x" ]; then
  FESS_TEMP_PATH=$FESS_HOME/temp
fi
if [ "x$FESS_LOG_PATH" = "x" ]; then
  FESS_LOG_PATH=$FESS_HOME/logs
fi
FESS_JAVA_OPTS="$FESS_JAVA_OPTS -Dfess.home=$FESS_HOME"
FESS_JAVA_OPTS="$FESS_JAVA_OPTS -Dfess.context.path=/"
FESS_JAVA_OPTS="$FESS_JAVA_OPTS -Dfess.port=8080"
FESS_JAVA_OPTS="$FESS_JAVA_OPTS -Dfess.webapp.path=$FESS_HOME/app"
FESS_JAVA_OPTS="$FESS_JAVA_OPTS -Dfess.temp.path=$FESS_TEMP_PATH"
FESS_JAVA_OPTS="$FESS_JAVA_OPTS -Dfess.log.name=$APP_NAME"
FESS_JAVA_OPTS="$FESS_JAVA_OPTS -Dfess.log.path=$FESS_LOG_PATH"
FESS_JAVA_OPTS="$FESS_JAVA_OPTS -Dfess.log.level=warn"
FESS_JAVA_OPTS="$FESS_JAVA_OPTS -Dlasta.env=web"
FESS_JAVA_OPTS="$FESS_JAVA_OPTS -Dtomcat.config.path=tomcat_config.properties"
if [ "x$FESS_CONF_PATH" != "x" ]; then
  FESS_JAVA_OPTS="$FESS_JAVA_OPTS -Dfess.conf.path=$FESS_CONF_PATH"
fi
if [ "x$FESS_VAR_PATH" != "x" ]; then
  FESS_JAVA_OPTS="$FESS_JAVA_OPTS -Dfess.var.path=$FESS_VAR_PATH"
fi
if [ "x$ES_HTTP_URL" != "x" ]; then
  FESS_JAVA_OPTS="$FESS_JAVA_OPTS -Dfess.es.http_address=$ES_HTTP_URL"
fi
if [ "x$FESS_DICTIONARY_PATH" != "x" ]; then
  FESS_JAVA_OPTS="$FESS_JAVA_OPTS -Dfess.dictionary.path=$FESS_DICTIONARY_PATH"
fi

