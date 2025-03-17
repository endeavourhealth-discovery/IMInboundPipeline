package org.endeavourhealth.pipeline.inbound.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.endeavourhealth.pipeline.inbound.Transformer;
import org.endeavourhealth.pipeline.inbound.config.DBConfig;
import org.endeavourhealth.pipeline.inbound.helpers.DBConnectionManager;
import org.endeavourhealth.pipeline.inbound.model.Event;
import org.endeavourhealth.pipeline.inbound.model.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.util.Map;
import java.util.UUID;

@Service
public class DataListener {

  private static final Logger LOG = LoggerFactory.getLogger(DataListener.class);
  private static final ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  private EventService eventService;
  @Autowired
  private InstanceService instanceService;

  private Connection connection;
  private FilingOutcomeSender filingOutcomeSender;

  public DataListener(FilingOutcomeSender filingOutcomeSender) {
    this.filingOutcomeSender = filingOutcomeSender;
  }

  @RabbitListener(queues = "#{rabbitMQConfig.getQueue()}")
  public void handleDataMessages(Message message) throws Exception {
    LOG.debug("Received data message: {}", message);
    String messageBody = new String(message.getBody());
    LOG.debug("Received data body: {}", messageBody);
    if ("EOF".equals(messageBody)) {
      filingOutcomeSender.sendMessage(message);
    } else {
      JsonNode dataNode = objectMapper.readTree(messageBody);
      try {
        Map<String, Object> headers = message.getMessageProperties().getHeaders();
        Transformer transformer = new Transformer(headers.get("publisher").toString(), headers.get("datatype").toString());
        JsonNode transformedDataNode = transformer.transform(dataNode);
        JsonNode entities = transformedDataNode.get("entities");
        String category = headers.get("category").toString();
        saveToDB(entities, category);
        LOG.debug("Filed to DB");
      } catch (Exception e) {
        LOG.error("{}", e.toString());
        System.exit(1);
      }
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
          //eventService.create(event);
        } else if ("INSTANCE".equals(category)) {
          Instance instance = new Instance();
          instance.setId(UUID.fromString(jsonNode.get("@id").asText()));
          instance.setJson(jsonNode.toString());
          DBConnectionManager.fileInstance(instance);
          ///instanceService.create(instance);
        } else {
          throw new IllegalArgumentException("Provided category header '" + category + "' is invalid");
        }
      }
    }
  }
}
