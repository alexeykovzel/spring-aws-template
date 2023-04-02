package com.alexeykovzel.example.features.debug;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileWriter;
import java.io.IOException;

@Configuration
@Slf4j
public class DebugConfig {
    public final static String TEXT_PATH = "logs/app-text.log";
    public final static String JSON_PATH = "logs/app-json.log";

    @Bean
    public void clearAllLogs() {
        try {
            new FileWriter(TEXT_PATH, false).close();
            new FileWriter(JSON_PATH, false).close();
        } catch (IOException e) {
            log.error("Failed to clear all logs: error='{}'", e.getMessage());
        }
    }
}

