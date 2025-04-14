package org.endeavourhealth.pipeline.inbound.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.endeavourhealth.pipeline.inbound.Transformer;
import org.endeavourhealth.pipeline.inbound.helpers.DBConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Map;

@Service
public class DataListener {

  private static final Logger LOG = LoggerFactory.getLogger(DataListener.class);
  private static final ObjectMapper objectMapper = new ObjectMapper();
  Transformer transformer = new Transformer();
  private FilingOutcomeSender filingOutcomeSender;

  public DataListener(FilingOutcomeSender filingOutcomeSender) {
    this.filingOutcomeSender = filingOutcomeSender;
  }

  @RabbitListener(queues = "#{rabbitMQConfig.getQueue()}")
  public void handleDataMessages(Message message) throws JsonProcessingException {
    LOG.debug("Received data message: {}", message);
    String messageBody = new String(message.getBody());
    LOG.debug("Received data body: {}", messageBody);
    if ("EOF".equals(messageBody)) {
      filingOutcomeSender.sendMessage(message);
    } else {
      JsonNode dataNode = objectMapper.readTree(messageBody);
      try {
        Map<String, Object> headers = message.getMessageProperties().getHeaders();
        transformer.loadTransformation(headers.get("publisher").toString(), headers.get("datatype").toString());
        JsonNode entities = transformer.transform(dataNode).get("entities");
        String category = headers.get("category").toString();
        saveToDB(entities, category, headers.get("datatype").toString());
        LOG.debug("Filed to DB");
      } catch (Exception e) {
        LOG.error("{}", e.toString());
        System.exit(1);
      }
    }
  }

  private void saveToDB(JsonNode entities, String category, String datatype) throws SQLException {
    if (entities.isArray()) {
      for (JsonNode jsonNode : entities) {
        DBConnectionManager.fileEntity(category, jsonNode, datatype);
      }
    }
  }
}
