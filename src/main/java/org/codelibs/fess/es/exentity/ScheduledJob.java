package org.codelibs.fess.es.exentity;

import org.codelibs.fess.Constants;
import org.codelibs.fess.es.bsentity.BsScheduledJob;
import org.codelibs.fess.job.TriggeredJob;
import org.codelibs.fess.util.ComponentUtil;

/**
 * @author FreeGen
 */
public class ScheduledJob extends BsScheduledJob {

    private static final long serialVersionUID = 1L;

    public boolean isLoggingEnabled() {
        return Constants.T.equals(getJobLogging());
    }

    public boolean isCrawlerJob() {
        return Constants.T.equals(getCrawler());
    }

    public boolean isEnabled() {
        return Constants.T.equals(getAvailable());
    }

    public boolean isRunning() {
        return ComponentUtil.getJobHelper().getJobExecutoer(getId()) != null;
    }

    public void start() {
        final ScheduledJob scheduledJob = this;
        new Thread(() -> new TriggeredJob().execute(scheduledJob)).start();
    }

    @Override
    public String getId() {
        return asDocMeta().id();
    }

    @Override
    public void setId(String id) {
        asDocMeta().id(id);
    }

    public Long getVersionNo() {
        return asDocMeta().version();
    }

    public void setVersionNo(Long version) {
        asDocMeta().version(version);
    }
}
