package org.codelibs.fess.job;

import org.codelibs.fess.util.ComponentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PurgeThumbnailJob {
    private static final Logger logger = LoggerFactory.getLogger(PurgeThumbnailJob.class);

    private long expiry;

    public String execute() {
        try {
            final long count = ComponentUtil.getThumbnailManager().purge(getExpiry());
            return "Deleted " + count + " thumbnail files.";
        } catch (final Exception e) {
            logger.error("Failed to purge user info.", e);
            return e.getMessage();
        }
    }

    public long getExpiry() {
        return expiry;
    }

    public PurgeThumbnailJob expiry(long expiry) {
        this.expiry = expiry;
        return this;
    }
}
