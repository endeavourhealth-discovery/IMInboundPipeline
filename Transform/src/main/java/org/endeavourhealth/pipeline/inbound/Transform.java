package org.endeavourhealth.pipeline.inbound;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Transform {

  public static void main(String[] args) throws Exception {
    List<JsonNode> values = csvToJson("Z:\\SyntheticEmisData\\v8.0 schema test data\\bulk_95047_Admin_Organisation_20231017043213_F95EE3AF-0B9D-40EB-8B28-8E858EF0091F.csv");
    Transformer transformer = new Transformer("emis", "Organisation");
//    for (JsonNode value : values) {
    JsonNode value = values.get(0);
    JsonNode node = transformer.transform(value);
    System.out.println(node.toPrettyString());
//    }
  }

  static List<JsonNode> csvToJson(String file) {
    List<JsonNode> jsonValues = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
      String line = null;
      String[] headers = null;
      while ((line = br.readLine()) != null) {
        String[] values = Arrays.stream(line.split(",")).map(Transform::cleanCSV).toArray(String[]::new);
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

  static String cleanCSV(String data) {
    if (data == null) return null;
    if (data.isEmpty()) return data;

    if (data.length() > 1 && data.startsWith("\"") && data.endsWith("\"")) data = data.substring(1, data.length() - 1);

    return data;
  }
}
