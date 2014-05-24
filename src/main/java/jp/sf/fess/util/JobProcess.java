package jp.sf.fess.util;

import jp.sf.fess.Constants;

public class JobProcess {
    protected Process process;

    protected InputStreamThread inputStreamThread;

    public JobProcess(final Process process) {
        this.process = process;
        inputStreamThread = new InputStreamThread(process.getInputStream(),
                Constants.UTF_8);
    }

    public Process getProcess() {
        return process;
    }

    public InputStreamThread getInputStreamThread() {
        return inputStreamThread;
    }

}
