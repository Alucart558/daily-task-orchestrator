package com.dailytask.adapters.datasources.gmail;

import com.dailytask.core.domain.GmailMessage;
import com.dailytask.core.domain.RawData;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class EmailToRawDataConverter {

    public RawData convert(GmailMessage message) {
        if (message == null) {
            throw new IllegalArgumentException("GmailMessage cannot be null");
        }

        Map<String, String> metadata = new HashMap<>();
        if (message.getLabels() != null) {
            metadata.put("labels", String.join(",", message.getLabels()));
        }
        metadata.put("isUnread", String.valueOf(message.isUnread()));

        LocalDateTime fetchedAt = message.getReceivedDate() != null ? message.getReceivedDate() : LocalDateTime.now();
        String title = message.getSubject() != null ? message.getSubject() : "No Subject";
        String body = message.getBody() != null ? message.getBody() : "";

        return new RawData(
                "Gmail",
                title,
                body,
                fetchedAt,
                message.getFrom(),
                message.getMessageId(),
                null, // Priority unassigned at this stage
                metadata
        );
    }
}