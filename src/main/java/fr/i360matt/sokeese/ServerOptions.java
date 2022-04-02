package fr.i360matt.sokeese;

public class ServerOptions {

    protected int autoFLushDelay = 0;
    // In microsecond
    // -1 : no auto flush
    // 0 = instant
    // 1 = 1 microsecond
    public void setAutoFLushDelay(int autoFLushDelay) {
        this.autoFLushDelay = autoFLushDelay;
    }
    public int getAutoFLushDelay() {
        return autoFLushDelay;
    }

    protected int corePoolSize = Runtime.getRuntime().availableProcessors();
    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }
    public int getCorePoolSize() {
        return corePoolSize;
    }



}
