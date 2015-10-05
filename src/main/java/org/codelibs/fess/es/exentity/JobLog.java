package org.codelibs.fess.es.exentity;

import org.codelibs.fess.Constants;
import org.codelibs.fess.es.bsentity.BsJobLog;
import org.codelibs.fess.util.ComponentUtil;

/**
 * @author FreeGen
 */
public class JobLog extends BsJobLog {

    private static final long serialVersionUID = 1L;

    private ScheduledJob scheduledJob;

    public JobLog() {
    }

    public JobLog(final ScheduledJob scheduledJob) {
        this.scheduledJob = scheduledJob;
        setJobName(scheduledJob.getName());
        setScriptType(scheduledJob.getScriptType());
        setScriptData(scheduledJob.getScriptData());
        setTarget(scheduledJob.getTarget());
        setStartTime(ComponentUtil.getSystemHelper().getCurrentTimeAsLong());
        setJobStatus(Constants.RUNNING);
    }

    public ScheduledJob getScheduledJob() {
        return scheduledJob;
    }

    @Override
    public String getId() {
        return asDocMeta().id();
    }

    @Override
    public void setId(final String id) {
        asDocMeta().id(id);
    }

    public Long getVersionNo() {
        return asDocMeta().version();
    }

    public void setVersionNo(final Long version) {
        asDocMeta().version(version);
    }
}
