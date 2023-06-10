package com.divya.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.divya.kafka.KafkaProducer;

@RestController
@RequestMapping("/kafka")
public class Controller {

  @Autowired
  private KafkaProducer kafkaProducer;

  // in producer side always store the events preferably in mongodb for future
  // purpose
  @PostMapping("/produce")
  public String sendToKafka(@RequestBody String message) {
    kafkaProducer.sendMessage(message);
    return "sucess";
  }

}
