package com.example.springapp.scheduler;

import com.example.springapp.service.KafkaMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class KafkaHeartbeatScheduler {

    private static final Logger logger = LoggerFactory.getLogger(KafkaHeartbeatScheduler.class);

    private final KafkaMessageService kafkaMessageService;

    public KafkaHeartbeatScheduler(KafkaMessageService kafkaMessageService) {
        this.kafkaMessageService = kafkaMessageService;
    }

    @Scheduled(fixedRateString = "${app.kafka.heartbeat-interval-ms:60000}")
    public void sendHeartbeat() {
        logger.info("Sending scheduled Kafka heartbeat");
        kafkaMessageService.sendHeartbeat();
    }
}
