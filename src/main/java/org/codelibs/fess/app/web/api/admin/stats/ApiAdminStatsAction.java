/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.codelibs.fess.app.web.api.admin.stats;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.ApiResult.ApiStatsResponse;
import org.codelibs.fess.app.web.api.admin.FessApiAdminAction;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.JsonResponse;
import org.opensearch.action.admin.cluster.health.ClusterHealthResponse;
import org.opensearch.monitor.jvm.JvmStats;
import org.opensearch.monitor.jvm.JvmStats.BufferPool;
import org.opensearch.monitor.jvm.JvmStats.Classes;
import org.opensearch.monitor.jvm.JvmStats.GarbageCollectors;
import org.opensearch.monitor.jvm.JvmStats.Mem;
import org.opensearch.monitor.jvm.JvmStats.Threads;
import org.opensearch.monitor.os.OsProbe;
import org.opensearch.monitor.os.OsStats;
import org.opensearch.monitor.process.ProcessProbe;

/**
 * API action for admin statistics management.
 *
 * @author shinsuke
 */
public class ApiAdminStatsAction extends FessApiAdminAction {

    // ===================================================================================
    //                                                                           Constructor
    //                                                                           ===========

    /**
     * Default constructor.
     */
    public ApiAdminStatsAction() {
        // Default constructor
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============

    /**
     * Retrieves system statistics including JVM, OS, process, engine, and filesystem information.
     *
     * @return JSON response containing system statistics
     */
    // GET /api/admin/stats
    @Execute
    public JsonResponse<ApiResult> index() {
        final HashMap<String, Object> stats = new HashMap<>();
        stats.put("jvm", getJvmObj());
        stats.put("os", getOsObj());
        stats.put("process", getProcessObj());
        stats.put("engine", getEngineObj());
        stats.put("fs", getFsObj());
        return asJson(new ApiStatsResponse().stats(stats).status(ApiResult.Status.OK).result());
    }

    private FsObj[] getFsObj() {
        return Arrays.stream(File.listRoots()).map(f -> {
            final FsObj fsObj = new FsObj();
            fsObj.path = f.getAbsolutePath();
            fsObj.free = f.getFreeSpace();
            fsObj.total = f.getTotalSpace();
            fsObj.usable = f.getUsableSpace();
            fsObj.used = fsObj.total - fsObj.usable;
            fsObj.percent = (short) (100 * fsObj.used / fsObj.total);
            return fsObj;
        }).toArray(n -> new FsObj[n]);
    }

    private JvmObj getJvmObj() {
        final JvmObj jvmObj = new JvmObj();
        final JvmStats jvmStats = JvmStats.jvmStats();
        final Mem mem = jvmStats.getMem();
        final JvmMemoryObj jvmMemoryObj = new JvmMemoryObj();
        jvmObj.memory = jvmMemoryObj;
        final JvmMemoryHeapObj jvmMemoryHeapObj = new JvmMemoryHeapObj();
        jvmMemoryObj.heap = jvmMemoryHeapObj;
        jvmMemoryHeapObj.used = mem.getHeapUsed().getBytes();
        jvmMemoryHeapObj.committed = mem.getHeapCommitted().getBytes();
        jvmMemoryHeapObj.max = mem.getHeapMax().getBytes();
        jvmMemoryHeapObj.percent = mem.getHeapUsedPercent();
        final JvmMemoryNonHeapObj jvmMemoryNonHeapObj = new JvmMemoryNonHeapObj();
        jvmMemoryObj.nonHeap = jvmMemoryNonHeapObj;
        jvmMemoryNonHeapObj.used = mem.getNonHeapUsed().getBytes();
        jvmMemoryNonHeapObj.committed = mem.getNonHeapCommitted().getBytes();
        final List<BufferPool> bufferPools = jvmStats.getBufferPools();
        jvmObj.pools = bufferPools.stream().map(p -> {
            final JvmPoolObj jvmPoolObj = new JvmPoolObj();
            jvmPoolObj.key = p.getName();
            jvmPoolObj.count = p.getCount();
            jvmPoolObj.used = p.getUsed().getBytes();
            jvmPoolObj.capacity = p.getTotalCapacity().getBytes();
            return jvmPoolObj;
        }).toArray(n -> new JvmPoolObj[n]);
        final GarbageCollectors gc = jvmStats.getGc();
        jvmObj.gc = Arrays.stream(gc.getCollectors()).map(c -> {
            final JvmGcObj jvmGcObj = new JvmGcObj();
            jvmGcObj.key = c.getName();
            jvmGcObj.count = c.getCollectionCount();
            jvmGcObj.time = c.getCollectionTime().getMillis();
            return jvmGcObj;
        }).toArray(n -> new JvmGcObj[n]);
        final Threads threads = jvmStats.getThreads();
        final JvmThreadsObj jvmThreadsObj = new JvmThreadsObj();
        jvmObj.threads = jvmThreadsObj;
        jvmThreadsObj.count = threads.getCount();
        jvmThreadsObj.peak = threads.getPeakCount();
        final Classes classes = jvmStats.getClasses();
        final JvmClassesObj jvmClassesObj = new JvmClassesObj();
        jvmObj.classes = jvmClassesObj;
        jvmClassesObj.loaded = classes.getLoadedClassCount();
        jvmClassesObj.total_loaded = classes.getTotalLoadedClassCount();
        jvmClassesObj.unloaded = classes.getUnloadedClassCount();
        jvmObj.uptime = jvmStats.getUptime().getMillis();
        return jvmObj;
    }

    private ProcessObj getProcessObj() {
        final ProcessObj processObj = new ProcessObj();
        final ProcessProbe processProbe = ProcessProbe.getInstance();
        final ProcessFileDescriptorObj processFileDescriptorObj = new ProcessFileDescriptorObj();
        processObj.fileFescriptor = processFileDescriptorObj;
        processFileDescriptorObj.open = processProbe.getOpenFileDescriptorCount();
        processFileDescriptorObj.max = processProbe.getMaxFileDescriptorCount();
        final ProcessCpuObj processCpuObj = new ProcessCpuObj();
        processObj.cpu = processCpuObj;
        processCpuObj.percent = processProbe.getProcessCpuPercent();
        processCpuObj.total = processProbe.getProcessCpuTotalTime();
        final ProcessVirtualMemoryObj processVirtualMemoryObj = new ProcessVirtualMemoryObj();
        processObj.virtualMemory = processVirtualMemoryObj;
        processVirtualMemoryObj.total = processProbe.getTotalVirtualMemorySize();
        return processObj;
    }

    private OsObj getOsObj() {
        final OsObj osObj = new OsObj();
        final OsProbe osProbe = OsProbe.getInstance();
        final OsMemoryObj osMemoryObj = new OsMemoryObj();
        osObj.memory = osMemoryObj;
        final OsMemoryPhysicalObj osMemoryPhysicalObj = new OsMemoryPhysicalObj();
        osMemoryObj.physical = osMemoryPhysicalObj;
        osMemoryPhysicalObj.free = osProbe.getFreePhysicalMemorySize();
        osMemoryPhysicalObj.total = osProbe.getTotalPhysicalMemorySize();
        final OsMemorySwapSpaceObj osMemorySwapSpaceObj = new OsMemorySwapSpaceObj();
        osMemoryObj.swapSpace = osMemorySwapSpaceObj;
        osMemorySwapSpaceObj.free = osProbe.getFreeSwapSpaceSize();
        osMemorySwapSpaceObj.total = osProbe.getTotalSwapSpaceSize();
        final OsCpuObj osCpuObj = new OsCpuObj();
        osObj.cpu = osCpuObj;
        osCpuObj.percent = osProbe.getSystemCpuPercent();
        final OsStats osStats = osProbe.osStats();
        osObj.loadAverages = osStats.getCpu().getLoadAverage();
        return osObj;
    }

    private EngineObj getEngineObj() {
        final EngineObj engineObj = new EngineObj();
        try {
            final SearchEngineClient esClient = ComponentUtil.getSearchEngineClient();
            final ClusterHealthResponse response =
                    esClient.admin().cluster().prepareHealth().execute().actionGet(fessConfig.getIndexHealthTimeout());
            engineObj.clusterName = response.getClusterName();
            engineObj.numberOfNodes = response.getNumberOfNodes();
            engineObj.numberOfDataNodes = response.getNumberOfDataNodes();
            engineObj.activePrimaryShards = response.getActivePrimaryShards();
            engineObj.activeShards = response.getActiveShards();
            engineObj.activeShardsPercent = response.getActiveShardsPercent();
            engineObj.relocatingShards = response.getRelocatingShards();
            engineObj.initializingShards = response.getInitializingShards();
            engineObj.unassignedShards = response.getUnassignedShards();
            engineObj.delayedUnassignedShards = response.getDelayedUnassignedShards();
            engineObj.numberOfPendingTasks = response.getNumberOfPendingTasks();
            engineObj.numberOfInFlightFetch = response.getNumberOfInFlightFetch();
            engineObj.status = response.getStatus().name().toLowerCase(Locale.ROOT);
        } catch (final Exception e) {
            engineObj.status = "red";
            engineObj.exception = e.getMessage();
        }
        return engineObj;
    }

    /**
     * Data transfer object representing filesystem statistics.
     */
    public static class FsObj {
        /**
         * Default constructor.
         */
        public FsObj() {
            // Default constructor
        }

        /** Filesystem usage percentage */
        public short percent;
        /** Used space in bytes */
        public long used;
        /** Filesystem path */
        public String path;
        /** Free space in bytes */
        public long free;
        /** Total space in bytes */
        public long total;
        /** Usable space in bytes */
        public long usable;
    }

    /**
     * Data transfer object representing search engine cluster statistics.
     */
    public static class EngineObj {
        /**
         * Default constructor.
         */
        public EngineObj() {
            // Default constructor
        }

        /** Exception message if any error occurred */
        public String exception;
        /** Cluster health status */
        public String status;
        /** Number of in-flight fetch operations */
        public int numberOfInFlightFetch;
        /** Number of pending tasks */
        public int numberOfPendingTasks;
        /** Number of delayed unassigned shards */
        public int delayedUnassignedShards;
        /** Number of unassigned shards */
        public int unassignedShards;
        /** Number of initializing shards */
        public int initializingShards;
        /** Number of relocating shards */
        public int relocatingShards;
        /** Percentage of active shards */
        public double activeShardsPercent;
        /** Number of active shards */
        public int activeShards;
        /** Number of active primary shards */
        public int activePrimaryShards;
        /** Number of data nodes */
        public int numberOfDataNodes;
        /** Total number of nodes */
        public int numberOfNodes;
        /** Cluster name */
        public String clusterName;
    }

    /**
     * Data transfer object representing JVM statistics.
     */
    public static class JvmObj {
        /**
         * Default constructor.
         */
        public JvmObj() {
            // Default constructor
        }

        /** JVM memory statistics */
        public JvmMemoryObj memory;
        /** JVM buffer pool statistics */
        public JvmPoolObj[] pools;
        /** JVM garbage collection statistics */
        public JvmGcObj[] gc;
        /** JVM thread statistics */
        public JvmThreadsObj threads;
        /** JVM class loading statistics */
        public JvmClassesObj classes;
        /** JVM uptime in milliseconds */
        public long uptime;
    }

    /**
     * Data transfer object representing JVM memory statistics.
     */
    public static class JvmMemoryObj {
        /**
         * Default constructor.
         */
        public JvmMemoryObj() {
            // Default constructor
        }

        /** Heap memory statistics */
        public JvmMemoryHeapObj heap;
        /** Non-heap memory statistics */
        public JvmMemoryNonHeapObj nonHeap;
    }

    /**
     * Data transfer object representing JVM heap memory statistics.
     */
    public static class JvmMemoryHeapObj {
        /**
         * Default constructor.
         */
        public JvmMemoryHeapObj() {
            // Default constructor
        }

        /** Used heap memory in bytes */
        public long used;
        /** Committed heap memory in bytes */
        public long committed;
        /** Maximum heap memory in bytes */
        public long max;
        /** Heap memory usage percentage */
        public short percent;
    }

    /**
     * Data transfer object representing JVM non-heap memory statistics.
     */
    public static class JvmMemoryNonHeapObj {
        /**
         * Default constructor.
         */
        public JvmMemoryNonHeapObj() {
            // Default constructor
        }

        /** Used non-heap memory in bytes */
        public long used;
        /** Committed non-heap memory in bytes */
        public long committed;
        /** Maximum non-heap memory in bytes */
        public long max;
        /** Non-heap memory usage percentage */
        public short percent;
    }

    /**
     * Data transfer object representing JVM buffer pool statistics.
     */
    public static class JvmPoolObj {
        /**
         * Default constructor.
         */
        public JvmPoolObj() {
            // Default constructor
        }

        /** Buffer pool name */
        public String key;
        /** Number of buffers */
        public long count;
        /** Used memory in bytes */
        public long used;
        /** Total capacity in bytes */
        public long capacity;
    }

    /**
     * Data transfer object representing JVM garbage collection statistics.
     */
    public static class JvmGcObj {
        /**
         * Default constructor.
         */
        public JvmGcObj() {
            // Default constructor
        }

        /** Garbage collector name */
        public String key;
        /** Number of collections */
        public long count;
        /** Total collection time in milliseconds */
        public long time;
    }

    /**
     * Data transfer object representing JVM thread statistics.
     */
    public static class JvmThreadsObj {
        /**
         * Default constructor.
         */
        public JvmThreadsObj() {
            // Default constructor
        }

        /** Current number of threads */
        public int count;
        /** Peak number of threads */
        public int peak;
    }

    /**
     * Data transfer object representing JVM class loading statistics.
     */
    public static class JvmClassesObj {
        /**
         * Default constructor.
         */
        public JvmClassesObj() {
            // Default constructor
        }

        /** Currently loaded classes */
        public long loaded;
        /** Total classes loaded since JVM start */
        public long total_loaded;
        /** Total classes unloaded */
        public long unloaded;
    }

    /**
     * Data transfer object representing process statistics.
     */
    public static class ProcessObj {
        /**
         * Default constructor.
         */
        public ProcessObj() {
            // Default constructor
        }

        public ProcessFileDescriptorObj fileFescriptor;
        public ProcessCpuObj cpu;
        public ProcessVirtualMemoryObj virtualMemory;
    }

    /**
     * Data transfer object representing process file descriptor statistics.
     */
    public static class ProcessFileDescriptorObj {
        /**
         * Default constructor.
         */
        public ProcessFileDescriptorObj() {
            // Default constructor
        }

        public long open;
        public long max;
    }

    /**
     * Data transfer object representing process CPU statistics.
     */
    public static class ProcessCpuObj {
        /**
         * Default constructor.
         */
        public ProcessCpuObj() {
            // Default constructor
        }

        public short percent;
        public long total;
    }

    /**
     * Data transfer object representing process virtual memory statistics.
     */
    public static class ProcessVirtualMemoryObj {
        /**
         * Default constructor.
         */
        public ProcessVirtualMemoryObj() {
            // Default constructor
        }

        public long total;
    }

    /**
     * Data transfer object representing operating system statistics.
     */
    public static class OsObj {
        /**
         * Default constructor.
         */
        public OsObj() {
            // Default constructor
        }

        public OsMemoryObj memory;
        public OsCpuObj cpu;
        public double[] loadAverages;
    }

    /**
     * Data transfer object representing OS memory statistics.
     */
    public static class OsMemoryObj {
        /**
         * Default constructor.
         */
        public OsMemoryObj() {
            // Default constructor
        }

        public OsMemoryPhysicalObj physical;
        public OsMemorySwapSpaceObj swapSpace;
    }

    /**
     * Data transfer object representing OS physical memory statistics.
     */
    public static class OsMemoryPhysicalObj {
        /**
         * Default constructor.
         */
        public OsMemoryPhysicalObj() {
            // Default constructor
        }

        public long free;
        public long total;
    }

    /**
     * Data transfer object representing OS swap space statistics.
     */
    public static class OsMemorySwapSpaceObj {
        /**
         * Default constructor.
         */
        public OsMemorySwapSpaceObj() {
            // Default constructor
        }

        public long free;
        public long total;
    }

    /**
     * Data transfer object representing OS CPU statistics.
     */
    public static class OsCpuObj {
        /**
         * Default constructor.
         */
        public OsCpuObj() {
            // Default constructor
        }

        public short percent;
    }
}
