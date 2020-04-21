package jk.util.retrial.core;

import java.io.IOException;


public abstract class AbtractRetriableService implements RetriableService {

    private RetryContext context;

    public void initialise(RetryContext context) throws IOException {
        this.context = context;
    }

    public int getRetryCount() {
        return context.getRetryCount();
    }

    public long getRetryWait() {
        return context.getRetryWait();
    }

    public RetryContext getContext() {
        return context;
    }

    public void setContext(RetryContext context) {
        this.context = context;
    }


}
