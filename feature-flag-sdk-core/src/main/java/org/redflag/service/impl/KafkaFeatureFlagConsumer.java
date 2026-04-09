package org.redflag.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.redflag.configuration.AppConfig;
import org.redflag.dto.FeatureFlagUpdate;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
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

        this.kafkaConsumer = new KafkaConsumer<>(appConfig.getKafkaConfig().getProperties());
        this.kafkaConsumer.subscribe(Collections.singletonList(appConfig.getKafkaConfig().getTopicName()));
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
            featureFlags.put(update.getFlagName(), update.getValue());
            System.out.println("Updated feature flag: " + update.getFlagName() + " = " + update.getValue());
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
