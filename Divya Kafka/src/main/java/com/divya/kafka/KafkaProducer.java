 package com.divya.kafka;

 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.beans.factory.annotation.Value;
 import org.springframework.kafka.core.KafkaTemplate;
 import org.springframework.stereotype.Component;

 import lombok.extern.slf4j.Slf4j;

 @Slf4j
 @Component
 public class KafkaProducer {
 @Value("${spring.kafka.topic.name}")
 private String topicName;

 @Autowired
 private KafkaTemplate<String, String> kafkaTemplate;

 public void sendMessage(String message) {
 log.info("=========KafkaProducer=={}", message);
 kafkaTemplate.send(topicName, message);
 }
 }
