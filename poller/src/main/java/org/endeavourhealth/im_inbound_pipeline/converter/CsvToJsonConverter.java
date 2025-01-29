package org.endeavourhealth.im_inbound_pipeline.converter;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CsvToJsonConverter {

  public List<String> convertCsvToJsonLines(InputStream csvInputStream) {
    List<String> jsonLines = new ArrayList<>();

    try (BufferedReader br = new BufferedReader(new InputStreamReader(csvInputStream))) {
      String line;
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
          System.out.println(line + " -> " + jsonObject.toString());
          jsonLines.add(jsonObject.toString());
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return jsonLines;
  }
}
