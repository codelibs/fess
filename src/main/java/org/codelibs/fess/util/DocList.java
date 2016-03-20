package org.codelibs.fess.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class DocList extends ArrayList<Map<String, Object>> {

    private static final long serialVersionUID = 1L;

    private long contentSize = 0;

    private long processingTime = 0;

    public void clear() {
        super.clear();
        contentSize = 0;
        processingTime = 0;
    }

    public long getContentSize() {
        return contentSize;
    }

    public void addContentSize(long contentSize) {
        this.contentSize += contentSize;
    }

    public long getProcessingTime() {
        return processingTime;
    }

    public void addProcessingTime(long processingTime) {
        this.processingTime += processingTime;
    }

    @Override
    public String toString() {
        return "DocList [contentSize=" + contentSize + ", processingTime=" + processingTime + ", elementData="
                + Arrays.toString(toArray(new Map[size()])) + "]";
    }

}
