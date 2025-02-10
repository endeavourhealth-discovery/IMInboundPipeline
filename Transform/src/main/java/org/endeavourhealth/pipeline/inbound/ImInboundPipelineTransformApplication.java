package org.endeavourhealth.pipeline.inbound;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.json.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ImInboundPipelineTransformApplication {

  public static void main(String[] args) throws Exception {
    List<JsonNode> values = csvToJson("file nme here");
    Transformer transformer = new Transformer();
    for (JsonNode value : values) {
      JsonNode node = transformer.transform(value, "Emis", "Organisation");
      System.out.println(node.toPrettyString());
    }
    //SpringApplication.run(ImInboundPipelineTransformApplication.class, args);
  }

  static List<JsonNode> csvToJson(String file) {
    List<JsonNode> jsonValues = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
      String line = null;
      String[] headers = null;
      while ((line = br.readLine()) != null) {
        String[] values = line.split(",");
        if (headers == null) {
          headers = values;
        } else {
          JSONObject jsonObject = new JSONObject();
          for (int i = 0; i < headers.length; i++) {
            jsonObject.put(headers[i], values[i]);
          }
          ObjectMapper objectMapper = new ObjectMapper();
          jsonValues.add(objectMapper.readTree(jsonObject.toString()));
        }
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return jsonValues;
  }
}
