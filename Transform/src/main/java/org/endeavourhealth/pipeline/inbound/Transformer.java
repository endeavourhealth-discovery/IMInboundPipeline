package org.endeavourhealth.pipeline.inbound;

import com.fasterxml.jackson.databind.JsonNode;
import com.schibsted.spt.data.jslt.Expression;
import com.schibsted.spt.data.jslt.Function;
import com.schibsted.spt.data.jslt.FunctionUtils;
import com.schibsted.spt.data.jslt.Parser;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Transformer {
  private static final Logger LOG = LoggerFactory.getLogger(Transformer.class);
  HashMap<String, Expression> fileCache = new HashMap<>();
  Collection<Function> functions = new ArrayList<>();
  String className = this.getClass().getName();
  Expression jslt;

  public void setFunctions() {
    if (functions.isEmpty()) {
      try {
        functions.add(FunctionUtils.wrapStaticMethod("formatUuid", className, "formatUuid"));
        functions.add(FunctionUtils.wrapStaticMethod("newUuid", className, "newUuid"));
        functions.add(FunctionUtils.wrapStaticMethod("formatDate", className, "formatDate"));
      } catch (ClassNotFoundException e) {
        LOG.error(e.getMessage());
      }
    }
  }

  public void loadTransformation(String organisation, String type) throws NullPointerException {
    String transformName = organisation + type;
    String file = "";
    if (fileCache.containsKey(transformName)) {
      jslt = fileCache.get(transformName);
    } else {
      String transformFileName = transformName + ".jslt";
      try {
        file = Files.readString(Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource(transformFileName)).toURI()));
      } catch (URISyntaxException | IOException | NullPointerException e) {
        LOG.error("Cannot find transform {} does not exist", transformFileName);
        System.exit(1);
      }
      LOG.debug("Loading functions: {}", className);
      setFunctions();
      LOG.debug("Instantiate JSLT");
      if (file != null) jslt = Parser.compileString(file, functions);
      fileCache.put(transformName, jslt);
    }
  }

  public JsonNode transform(JsonNode inputJson) {
    if (inputJson == null)
      return null;
    return jslt.apply(inputJson);
  }

  public static String newUuid() {
    return formatUuid(UUID.randomUUID().toString(), "new UUID");
  }

  public static String formatUuid(String uuid, String keyInfo) {
    if (uuid == null) {
      LOG.error("UUID for {} is null", keyInfo);
      return "NULL";
    }
    return uuid.replace("{", "").replace("}", "").toLowerCase();
  }

  public static String formatDate(String format, String date) {
    if (date == null) {
      LOG.error("Date is null");
      return "NULL";
    }
    if (date.isEmpty())
      return null;

    DateTimeFormatter incomingFormatter = DateTimeFormatter.ofPattern(format);
    LocalDateTime parsedDate = LocalDate.parse(date, incomingFormatter).atStartOfDay();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    return parsedDate.format(formatter) + "Z";
  }
}
