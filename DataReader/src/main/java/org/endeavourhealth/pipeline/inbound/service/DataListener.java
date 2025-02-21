package org.endeavourhealth.pipeline.inbound.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.endeavourhealth.pipeline.inbound.Transformer;
import org.endeavourhealth.pipeline.inbound.database.DBService;
import org.endeavourhealth.pipeline.inbound.model.DBEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class DataListener {

  private static final Logger LOG = LoggerFactory.getLogger(DataListener.class);
  private static final ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  private DBService dbService;

  @RabbitListener(queues = "#{rabbitMQConfig.getQueue()}")
  public void handleDataMessages(Message message) throws IOException {
    System.out.println("Received data message: " + message);
    JsonNode dataNode = objectMapper.readTree(message.getBody());
    try {
      String publisher = message.getMessageProperties().getHeaders().get("publisher").toString();
      String datatype = message.getMessageProperties().getHeaders().get("datatype").toString();
      Transformer transformer = new Transformer(publisher, datatype);
      JsonNode transformedDataNode = transformer.transform(dataNode);
      System.out.println(transformedDataNode.toPrettyString());
      DBEntry dbEntry = new DBEntry();
      dbEntry.setId(ThreadLocalRandom.current().nextInt(1, 1000000));      dbEntry.setOrganisation(publisher);
      dbEntry.setData(transformedDataNode.toPrettyString());
      dbService.create(dbEntry);
      System.out.println("Filed to DB");
    } catch (Exception e) {
      System.out.println("Error parsing data message: " + e.getMessage());
    }
  }
}
