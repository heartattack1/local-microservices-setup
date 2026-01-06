package com.example.springapp.service;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaMessageService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaMessageService.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topicName;

    public KafkaMessageService(
            KafkaTemplate<String, String> kafkaTemplate,
            @Value("${app.kafka.topic:demo}") String topicName) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
    }

    public void sendHeartbeat() {
        String payload = "heartbeat-" + Instant.now();
        kafkaTemplate.send(topicName, payload)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        logger.warn("Failed to send Kafka message", ex);
                    } else {
                        logger.info("Sent Kafka message to topic {} partition {} offset {}", topicName,
                                result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
                    }
                });
    }
}
