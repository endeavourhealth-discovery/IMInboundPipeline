package org.endeavourhealth.pipeline.inbound.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
//import org.endeavourhealth.pipeline.inbound.transform.Transformer;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class DataListener {

  @RabbitListener(queues = "#{rabbitMQConfig.getQueue()}")
  public void handleDataMessages(Message message) throws IOException, ClassNotFoundException {
    System.out.println("Received data message: " + message);
    ObjectMapper mapper = new ObjectMapper();
    JsonNode dataNode = mapper.readTree(message.getBody());
    System.out.println(dataNode);
    System.out.println(message.getMessageProperties().getHeaders().get("publisher").toString());
    System.out.println(message.getMessageProperties().getHeaders().get("datatype").toString());
    //Transformer transformer = new Transformer(message.getMessageProperties().getHeaders().get("publisher").toString(), message.getMessageProperties().getHeaders().get("datatype").toString());
    //JsonNode transformedDataNode = transformer.transform(dataNode);
    //System.out.println(transformedDataNode);
    //    TODO store in db
  }
}
