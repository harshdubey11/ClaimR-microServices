package com.claimr.email_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public DefaultErrorHandler kafkaErrorHandler() {

        FixedBackOff backOff = new FixedBackOff(2000L, 2L);

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
                (record, exception) -> {
                    System.err.println(
                            "Email notification failed after retries. " +
                                    "Topic: " + record.topic() +
                                    ", Offset: " + record.offset());
                    exception.printStackTrace();
                },
                backOff);

        return errorHandler;
    }
}
