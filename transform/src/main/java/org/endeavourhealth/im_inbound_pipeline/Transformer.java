package org.endeavourhealth.im_inbound_pipeline;

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
  String className = "org.endeavourhealth.im_inbound_pipeline.Transformer";

  protected Transformer() throws ClassNotFoundException {
    functions.add(FunctionUtils.wrapStaticMethod("uuidToIri", className, "uuidToIri"));
    functions.add(FunctionUtils.wrapStaticMethod("newUUIDIri", className, "newUUIDIri"));
    functions.add(FunctionUtils.wrapStaticMethod("formatDate", className, "formatDate"));

  }

  public JsonNode transform(JsonNode inputJson, String org, String type) {
    return transformJson(inputJson, loadTransformation(org, type));
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

  private JsonNode transformJson(JsonNode inputJson, String transformFile) {
    if (inputJson == null)
      return null;
    Expression jslt = Parser.compileString(transformFile, functions);
    return jslt.apply(inputJson);
  }

  public static String newUUIDIri(String namespace) {
    String uuid = UUID.randomUUID().toString();
    return uuidToIri(uuid, namespace);
  }

  public static String uuidToIri(String uuid, String namespace) {
    return namespace + (uuid.replace("{", "").replace("}", ""));
  }

  public static String formatDate(String date) {
    DateTimeFormatter incomingFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    LocalDateTime parsedDate = LocalDate.parse(date, incomingFormatter).atStartOfDay();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    return parsedDate.format(formatter) + "Z";
  }
}
