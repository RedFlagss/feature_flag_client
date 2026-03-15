package org.redflag.server;

import org.redflag.model.FeatureFlag;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class DataStorage {
    private Map<String, Boolean> ffStorage;
    private final Properties properties = new Properties();

    public DataStorage() {
        ffStorage = collectFlags();
    }

    public boolean getValue(String flagKey) {
        return ffStorage.get(flagKey);
    }

    private HashMap<String, Boolean> collectFlags() {
        HashMap<String, Boolean> ffMap = new HashMap<>();

        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("feature-flags.properties")) {

            if (input == null) {
                System.err.println("feature-flags.properties not found");
                return null;
            }

            Properties props = new Properties();
            props.load(input);

            for (String key : props.stringPropertyNames()) {
                boolean value = Boolean.parseBoolean(props.getProperty(key).trim());
                ffMap.put(key, value);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ffMap;
    }
}
