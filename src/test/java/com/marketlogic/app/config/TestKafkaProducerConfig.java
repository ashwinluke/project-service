package com.marketlogic.app.config;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

@TestConfiguration
@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
public class TestKafkaProducerConfig {

    @Autowired
    private KafkaProducer producer;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        kafkaTemplate.setDefaultTopic("ProjectPublish");
        return kafkaTemplate;
    }
}