package io.github.jksinghpro.retrial.core;

import java.io.IOException;

/**
 * Interface implemented by retriable service
 */
public interface RetriableService {

  public static class RetryContext {

    private int retryCount;

    private long retryWait;

    public RetryContext() {
    }

    public RetryContext(int retryCount, long retryWait) {
      this.retryCount = retryCount;
      this.retryWait = retryWait;
    }

    public int getRetryCount() {
      return retryCount;
    }

    public void setRetryCount(int retryCount) {
      this.retryCount = retryCount;
    }

    public long getRetryWait() {
      return retryWait;
    }

    public void setRetryWait(long retryWait) {
      this.retryWait = retryWait;
    }

  }

  void connect() throws IOException;

  void initialise(RetryContext context) throws IOException;

  int getRetryCount();

  long getRetryWait();

}
