package org.redflag.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.redflag.configuration.AppConfig;
import org.redflag.dto.FeatureFlagUpdate;

import java.time.Duration;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class KafkaFeatureFlagConsumer {
    private final KafkaConsumer<String, String> kafkaConsumer;
    private final Map<String, Boolean> featureFlags;
    private final ExecutorService executorService;
    private final AtomicBoolean running;
    private final ObjectMapper objectMapper;


    public KafkaFeatureFlagConsumer(AppConfig appConfig) {
        this.featureFlags = new ConcurrentHashMap<>();
        this.running = new AtomicBoolean(true);
        this.executorService = Executors.newSingleThreadExecutor();
        this.objectMapper = new ObjectMapper();

        Properties consumerProperties = new Properties();
        consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, "feature-flag-consumer");
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProperties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");

        consumerProperties.put("security.protocol", "SASL_PLAINTEXT");
        consumerProperties.put("sasl.mechanism", "PLAIN");
        consumerProperties.put("sasl.jaas.config",
                "org.apache.kafka.common.security.plain.PlainLoginModule required " +
                        "username=\"" + appConfig.getSdkLogin() + "\" " +
                        "password=\"" + appConfig.getSdkPassword() + "\";");

        this.kafkaConsumer = new KafkaConsumer<>(consumerProperties);
        this.kafkaConsumer.subscribe(appConfig.getKafkaTopics());
    }



    public void startConsuming() {
        executorService.submit(() -> {
            while (running.get()) {
                try {
                    ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(1000));
                    for (ConsumerRecord<String, String> record : records) {
                        processRecord(record);
                    }
                } catch (Exception e) {
                    System.err.println("Error consuming Kafka message: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    private void processRecord(ConsumerRecord<String, String> record) {
        try {
            FeatureFlagUpdate update = objectMapper.readValue(record.value(), FeatureFlagUpdate.class);
            featureFlags.put(update.getFlagKey(), update.getFlagValue());
            System.out.println("Updated feature flag: " + update.getFlagKey() + " = " + update.getFlagValue());
        } catch (Exception e) {
            System.err.println("Failed to parse feature flag update: " + e.getMessage());
        }
    }

    public boolean getFlagValue(String flagKey) {
        return featureFlags.getOrDefault(flagKey, false);
    }

    public void shutdown() {
        running.set(false);
        kafkaConsumer.wakeup();
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        kafkaConsumer.close();
    }
}
