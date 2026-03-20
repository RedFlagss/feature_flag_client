package org.redflag.server;

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
        if (ffStorage.putIfAbsent(flagKey, false) == null) {
            System.out.println("Flag not found: " + flagKey + " Creating flag.");
        };
        //TODO: call to main service
        return ffStorage.get(flagKey);
    }

    private HashMap<String, Boolean> collectFlags() {
        //TODO: replace with call to main service / or add call to main service

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
