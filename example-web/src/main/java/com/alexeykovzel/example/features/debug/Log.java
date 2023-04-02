package com.alexeykovzel.example.features.debug;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class Log {
    Date date;
    String message;
    String stackTrace;
    String path;
    String thread;
    String type;
    int typeValue;
    int version;

    @JsonProperty(value = "date")
    public Date getDate() {
        return date;
    }

    @JsonProperty(value = "@timestamp")
    public void setDate(Date date) {
        this.date = date;
    }

    @JsonProperty("version")
    public int getVersion() {
        return version;
    }

    @JsonProperty("@version")
    public void setVersion(int version) {
        this.version = version;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("stack_trace")
    public String getStackTrace() {
        return stackTrace;
    }

    @JsonProperty("stack_trace")
    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    @JsonProperty(value = "path")
    public String getPath() {
        return path;
    }

    @JsonProperty(value = "logger_name")
    public void setPath(String path) {
        this.path = path;
    }

    @JsonProperty("thread")
    public String getThread() {
        return thread;
    }

    @JsonProperty("thread_name")
    public void setThread(String thread) {
        this.thread = thread;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("level")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("type_value")
    public int getTypeValue() {
        return typeValue;
    }

    @JsonProperty("level_value")
    public void setTypeValue(int typeValue) {
        this.typeValue = typeValue;
    }
}