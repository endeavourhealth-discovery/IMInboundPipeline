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

  public static String stripQuotes(String s) {
    if (s == null) return null;

    if (s.length() > 1 && s.startsWith("\"") && s.endsWith("\"")) {
      return s.substring(1, s.length() - 1);
    }
    return s;
  }

  public static char getSeparatorFromFileName(String fileName) {
    if (fileName.endsWith(".csv")) return ',';
    else if (fileName.endsWith(".tsv")) return '\t';
    else throw new IllegalArgumentException("File type not supported: " + fileName);
  }
}
