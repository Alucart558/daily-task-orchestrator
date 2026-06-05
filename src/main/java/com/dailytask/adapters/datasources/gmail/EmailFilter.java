package com.dailytask.adapters.datasources.gmail;

import com.dailytask.core.domain.GmailMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EmailFilter {

    private final List<String> taskKeywords;

    public EmailFilter(@Value("${gmail.task-keywords}") List<String> taskKeywords) {
        this.taskKeywords = taskKeywords.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }

    public boolean isTaskEmail(GmailMessage message) {
        if (message == null) return false;

        String searchableSubject = message.getSubject() != null ? message.getSubject().toLowerCase() : "";
        String searchableBody = message.getBody() != null ? message.getBody().toLowerCase() : "";

        return taskKeywords.stream().anyMatch(keyword ->
                searchableSubject.contains(keyword) || searchableBody.contains(keyword)
        );
    }

    public String getTaskQuery(Instant since) {
        if (since == null) {
            return "in:inbox";
        }
        return "after:" + since.getEpochSecond();
    }
}