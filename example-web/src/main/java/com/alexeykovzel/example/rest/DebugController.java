package com.alexeykovzel.example.rest;

import com.alexeykovzel.example.features.debug.DebugConfig;
import com.alexeykovzel.example.features.debug.Log;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/debug")
@RequiredArgsConstructor
@Slf4j
public class DebugController {

    @GetMapping("/logs")
    public List<Log> logs(@RequestParam(value = "from", defaultValue = "0") int from,
                          @RequestParam(value = "to", defaultValue = "100") int to,
                          @RequestParam(value = "exclude", defaultValue = "") List<String> exclude) {
        if (to < 1 || from >= to) return List.of();
        List<Log> logs = getLogs(exclude);
        Collections.reverse(logs);
        to = Math.min(to, logs.size());
        return logs.subList(from, to);
    }

    @DeleteMapping("/logs/clear")
    public void clearLogs() {
        try {
            new FileWriter(DebugConfig.TEXT_PATH, false).close();
            new FileWriter(DebugConfig.JSON_PATH, false).close();
        } catch (IOException e) {
            log.error("Failed to clear logs: error='{}'", e.getMessage());
        }
    }

    private List<Log> getLogs(List<String> exclude) {
        List<Log> logs = new ArrayList<>();
        try (FileReader reader = new FileReader(DebugConfig.JSON_PATH);
             BufferedReader in = new BufferedReader(reader)) {
            ObjectMapper mapper = new ObjectMapper();
            String line;
            while ((line = in.readLine()) != null) {
                Log log = mapper.readValue(line, Log.class);
                if (!anyCaseContains(log.getType(), exclude))
                    logs.add(log);
            }
            return logs;
        } catch (IOException e) {
            log.error("Failed to get logs: error='{}'", e.getMessage());
            return logs;
        }
    }

    private boolean anyCaseContains(String value, List<String> values) {
        if (value == null) return false;
        value = value.toUpperCase();
        return values.stream()
                .filter(Objects::nonNull)
                .map(String::toUpperCase)
                .anyMatch(value::equals);
    }
}
