package com.dailytask.core.domain;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

public class RawData {
    private final String source;
    private final String title;
    private final String rawContent;
    private final LocalDateTime fetchedAt;
    private final String sender;
    private final String originalSource;
    private final String priority;
    private final Map<String, String> metadata;

    // Original constructor for backward compatibility
    public RawData(String source, String title, String rawContent, LocalDateTime fetchedAt) {
        this(source, title, rawContent, fetchedAt, null, null, null, Collections.emptyMap());
    }

    // Enhanced constructor
    public RawData(String source, String title, String rawContent, LocalDateTime fetchedAt,
                   String sender, String originalSource, String priority, Map<String, String> metadata) {
        this.source = source;
        this.title = title;
        this.rawContent = rawContent;
        this.fetchedAt = fetchedAt;
        this.sender = sender;
        this.originalSource = originalSource;
        this.priority = priority;
        this.metadata = metadata != null ? Map.copyOf(metadata) : Collections.emptyMap();
    }

    public String getSource() { return source; }
    public String getTitle() { return title; }
    public String getRawContent() { return rawContent; }
    public LocalDateTime getFetchedAt() { return fetchedAt; }
    public String getSender() { return sender; }
    public String getOriginalSource() { return originalSource; }
    public String getPriority() { return priority; }
    public Map<String, String> getMetadata() { return metadata; }
}