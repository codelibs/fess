package jp.sf.fess.ds;

import org.seasar.robot.RobotCrawlAccessException;

public class DataStoreCrawlingException extends RobotCrawlAccessException {

    private static final long serialVersionUID = 1L;

    private final String url;

    public DataStoreCrawlingException(final String url, final String message,
            final Exception e) {
        super(message, e);
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

}
