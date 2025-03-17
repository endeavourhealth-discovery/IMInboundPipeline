package org.endeavourhealth.pipeline.inbound.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.endeavourhealth.pipeline.inbound.Transformer;
import org.endeavourhealth.pipeline.inbound.helpers.DBConnectionManager;
import org.endeavourhealth.pipeline.inbound.model.Event;
import org.endeavourhealth.pipeline.inbound.model.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.util.Map;
import java.util.UUID;

@Service
public class DataListener {

  private static final Logger LOG = LoggerFactory.getLogger(DataListener.class);
  private static final ObjectMapper objectMapper = new ObjectMapper();
  Transformer transformer = new Transformer();
  private Connection connection;

  public DataListener() throws ClassNotFoundException {
  }

  @RabbitListener(queues = "#{rabbitMQConfig.getQueue()}")
  public void handleDataMessages(Message message) throws Exception {
    LOG.debug("Received data message: {}", message);
    String messageBody = new String(message.getBody());
    LOG.debug("Received data body: {}", messageBody);
    JsonNode dataNode = objectMapper.readTree(messageBody);
    try {
      Map<String, Object> headers = message.getMessageProperties().getHeaders();
      transformer.loadTransformation(headers.get("publisher").toString(), headers.get("datatype").toString());
      JsonNode entities = transformer.transform(dataNode).get("entities");
      String category = headers.get("category").toString();
      saveToDB(entities, category);
      LOG.debug("Filed to DB");
    } catch (Exception e) {
      LOG.error("{}", e.toString());
      System.exit(1);
    }
  }

  private void saveToDB(JsonNode entities, String category) throws Exception {
    if (connection == null) connection = DBConnectionManager.getConnection();
    if (entities.isArray()) {
      for (JsonNode jsonNode : entities) {
        if ("EVENT".equals(category)) {
          Event event = new Event();
          event.setId(UUID.fromString(jsonNode.get("@id").asText()));
          event.setJson(jsonNode.toString());
          DBConnectionManager.fileEvent(event);
        } else if ("INSTANCE".equals(category)) {
          Instance instance = new Instance();
          instance.setId(UUID.fromString(jsonNode.get("@id").asText()));
          instance.setJson(jsonNode.toString());
          DBConnectionManager.fileInstance(instance);
        } else {
          throw new IllegalArgumentException("Provided category header '" + category + "' is invalid");
        }
      }
    }
  }
}
