package org.endeavourhealth.pipeline.inbound.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class Utils {
  private static final Logger LOG = LoggerFactory.getLogger(Utils.class);

  public <T> T executeWithRetry(Supplier<T> operation, int maxRetries, int waitTime) throws InterruptedException {
    int attempt = 0;

    while (attempt < maxRetries) {
      try {
        return operation.get();
      } catch (Exception e) {
        attempt++;
        LOG.warn("Attempt {} failed: {}", attempt, e.getMessage());
        if (attempt == maxRetries) {
          System.out.println("All retries failed.");
          throw e;
        }
        LOG.warn("Waiting {} seconds before retrying...", waitTime);
        Thread.sleep(waitTime * 1000L);
      }
    }
    return null;
  }
}
