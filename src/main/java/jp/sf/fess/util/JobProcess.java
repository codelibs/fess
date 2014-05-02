package jp.sf.fess.util;

import jp.sf.fess.Constants;

public class JobProcess {
    protected Process process;

    protected InputStreamThread inputStreamThread;

    public JobProcess(Process process) {
        this.process = process;
        this.inputStreamThread = new InputStreamThread(
                process.getInputStream(), Constants.UTF_8);
    }

    public Process getProcess() {
        return process;
    }

    public InputStreamThread getInputStreamThread() {
        return inputStreamThread;
    }

}
