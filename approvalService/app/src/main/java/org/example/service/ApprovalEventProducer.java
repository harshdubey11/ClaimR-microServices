package org.example.service;

import org.example.event.ApprovalStatusChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ApprovalEventProducer {

    private static final String TOPIC = "approval-status-changed";

    private final KafkaTemplate<String, ApprovalStatusChangedEvent> kafkaTemplate;

    private static final Logger log =
            LoggerFactory.getLogger(ApprovalEventProducer.class);

    public ApprovalEventProducer(
            KafkaTemplate<String, ApprovalStatusChangedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(ApprovalStatusChangedEvent event) {

        kafkaTemplate.send(
                TOPIC,
                event.getExpenseId().toString(),
                event
        ).whenComplete((result, exception) -> {
            if (exception != null) {
                log.error("Failed to publish approval event for expense {}",
                        event.getExpenseId(), exception);
            } else {
                log.info("Published approval event for expense {}",
                        event.getExpenseId());
            }
        });
    }
}