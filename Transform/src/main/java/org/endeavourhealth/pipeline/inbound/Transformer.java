package org.endeavourhealth.pipeline.inbound;

import com.fasterxml.jackson.databind.JsonNode;
import com.schibsted.spt.data.jslt.Expression;
import com.schibsted.spt.data.jslt.Function;
import com.schibsted.spt.data.jslt.FunctionUtils;
import com.schibsted.spt.data.jslt.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Transformer {
  private static final Logger LOG = LoggerFactory.getLogger(Transformer.class);
  Collection<Function> functions = new ArrayList<>();
  String className = Transformer.class.getName();
  Expression jslt;

  protected Transformer(String org, String type) throws ClassNotFoundException {
    LOG.debug("Identify transformation file");
    String transformFile = loadTransformation(org, type);

    LOG.info("Loading functions: {}", className);
    functions.add(FunctionUtils.wrapStaticMethod("uuidToIri", className, "uuidToIri"));
    functions.add(FunctionUtils.wrapStaticMethod("newUUIDIri", className, "newUUIDIri"));
    functions.add(FunctionUtils.wrapStaticMethod("formatDate", className, "formatDate"));

    LOG.debug("Instantiate JSLT");
    jslt = Parser.compileString(transformFile, functions);


  }

  public JsonNode transform(JsonNode inputJson) {
    return transformJson(inputJson);
  }

  private void validateJson() {
    //TODO maybe?
  }

  private String loadTransformation(String organisation, String type) throws NullPointerException {
    String transformName = organisation + type + ".jslt";
    String fileName = "";
    try {
      fileName = Files.readString(Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource(transformName)).toURI()));
    } catch (URISyntaxException | IOException | NullPointerException e) {
      LOG.error("Cannot find transform {} does not exist", transformName);
    }
    return fileName;
  }

  private JsonNode transformJson(JsonNode inputJson) {
    if (inputJson == null)
      return null;
    return jslt.apply(inputJson);
  }

  public static String newUUIDIri(String namespace) {
    String uuid = UUID.randomUUID().toString();
    return uuidToIri(uuid, namespace);
  }

  public static String uuidToIri(String uuid, String namespace) {
    if (uuid == null) {
      LOG.error("UUID is null");
      return "NULL";
    }

    return namespace + (uuid.replace("{", "").replace("}", ""));
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
