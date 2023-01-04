/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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
import org.codelibs.fess.es.client.SearchEngineClient;
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
 * @author shinsuke
 */
public class ApiAdminStatsAction extends FessApiAdminAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============

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

    public static class FsObj {
        public short percent;
        public long used;
        public String path;
        public long free;
        public long total;
        public long usable;
    }

    public static class EngineObj {
        public String exception;
        public String status;
        public int numberOfInFlightFetch;
        public int numberOfPendingTasks;
        public int delayedUnassignedShards;
        public int unassignedShards;
        public int initializingShards;
        public int relocatingShards;
        public double activeShardsPercent;
        public int activeShards;
        public int activePrimaryShards;
        public int numberOfDataNodes;
        public int numberOfNodes;
        public String clusterName;
    }

    public static class JvmObj {
        public JvmMemoryObj memory;
        public JvmPoolObj[] pools;
        public JvmGcObj[] gc;
        public JvmThreadsObj threads;
        public JvmClassesObj classes;
        public long uptime;
    }

    public static class JvmMemoryObj {
        public JvmMemoryHeapObj heap;
        public JvmMemoryNonHeapObj nonHeap;
    }

    public static class JvmMemoryHeapObj {
        public long used;
        public long committed;
        public long max;
        public short percent;
    }

    public static class JvmMemoryNonHeapObj {
        public long used;
        public long committed;
        public long max;
        public short percent;
    }

    public static class JvmPoolObj {
        public String key;
        public long count;
        public long used;
        public long capacity;
    }

    public static class JvmGcObj {
        public String key;
        public long count;
        public long time;
    }

    public static class JvmThreadsObj {
        public int count;
        public int peak;
    }

    public static class JvmClassesObj {
        public long loaded;
        public long total_loaded;
        public long unloaded;
    }

    public static class ProcessObj {
        public ProcessFileDescriptorObj fileFescriptor;
        public ProcessCpuObj cpu;
        public ProcessVirtualMemoryObj virtualMemory;
    }

    public static class ProcessFileDescriptorObj {
        public long open;
        public long max;
    }

    public static class ProcessCpuObj {
        public short percent;
        public long total;
    }

    public static class ProcessVirtualMemoryObj {
        public long total;
    }

    public static class OsObj {
        public OsMemoryObj memory;
        public OsCpuObj cpu;
        public double[] loadAverages;
    }

    public static class OsMemoryObj {
        public OsMemoryPhysicalObj physical;
        public OsMemorySwapSpaceObj swapSpace;
    }

    public static class OsMemoryPhysicalObj {
        public long free;
        public long total;
    }

    public static class OsMemorySwapSpaceObj {
        public long free;
        public long total;
    }

    public static class OsCpuObj {
        public short percent;
    }
}
