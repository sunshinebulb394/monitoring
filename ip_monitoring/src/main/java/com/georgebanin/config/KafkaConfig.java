package com.georgebanin.config;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.kafka.client.consumer.KafkaConsumer;
import io.vertx.kafka.client.producer.KafkaProducer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Produces;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Singleton
@Slf4j
public class KafkaConfig {

    @Inject
    Vertx vertx;

    @Inject
    KafkaProps kafkaProps;

    @Produces
    @Named("KafkaProducer")
    public KafkaProducer<JsonObject, JsonObject> kafkaProducer() {
        Map<String, String> config = new HashMap<>();
        config.put("bootstrap.servers", kafkaProps.bootstrapServers());
        config.put("key.serializer", kafkaProps.keySerializer());
        config.put("value.serializer", kafkaProps.valueSerializer());
        config.put("acks", kafkaProps.acks());

        // use producer for interacting with Apache Kafka
        KafkaProducer<JsonObject, JsonObject> producer = KafkaProducer.create(vertx, config,JsonObject.class, JsonObject.class);
        return producer;

    }

    @Produces
    @Named("KafkaConsumer")
    public KafkaConsumer<String, String> kafkaConsumer() {
        Map<String, String> config = new HashMap<>();
        config.put("bootstrap.servers", kafkaProps.bootstrapServers());
        config.put("key.serializer", kafkaProps.keySerializer());
        config.put("value.serializer", kafkaProps.valueSerializer());
        config.put("group.id", kafkaProps.groupId());
        config.put("enable.auto.commit", kafkaProps.enableAutoCommit());

        // use producer for interacting with Apache Kafka
        KafkaConsumer<String, String> consumer = KafkaConsumer.create(vertx, config);
        return consumer;

    }

}
