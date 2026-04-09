package org.redflag.configuration;

import lombok.Getter;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class KafkaConfig {
    private static KafkaConfig instance;
    @Getter
    private Properties properties = new Properties();
    @Getter
    private String topicName;

    KafkaConfig() {
        Properties internal = loadFromInternalProperties();
        Properties user = loadFromUserProperties();
        this.properties = new Properties();
        this.properties.putAll(internal);
        this.properties.putAll(user);
    }

    private Properties loadFromUserProperties() {
        Properties props = new Properties();
        Properties result = new Properties();

        ClassLoader appClassLoader = Thread.currentThread().getContextClassLoader();

        try (InputStream input = appClassLoader.getResourceAsStream("ff-lib.properties")) {

            if (input == null) {
                throw new RuntimeException("Internal config ff-lib.properties not found");
            }

            props.load(input);

            result.put("sasl.jaas.config",
                    "org.apache.kafka.common.security.scram.ScramLoginModule required " +
                            "username=\"" + props.getProperty("username") + "\" " +
                            "password=\"" + props.getProperty("password") + "\";");

            result.put(ConsumerConfig.GROUP_ID_CONFIG, props.getProperty("groupName"));

            this.topicName = props.getProperty("topicName");

        } catch (IOException e) {
            throw  new RuntimeException("Error occurred in ff-lib.properties", e);
        }

        return result;
    }

    private Properties loadFromInternalProperties() {
        Properties props = new Properties();

        try (InputStream input = KafkaConfig.class.getResourceAsStream("/kafka.properties")) {

            if (input == null) {
                System.err.println("Internal sdk config kafka.properties not found");
                return null;
            }

            props.load(input);

        } catch (IOException e) {
            throw  new RuntimeException("Error occurred in feature-flags.properties", e);
        }

        return props;
    }

    public static KafkaConfig getInstance() {
        if (instance == null) {
            instance = new KafkaConfig();
        }

        return instance;
    }

}
