package com.dailytask.adapters.datasources.gmail;

import com.dailytask.core.domain.GmailMessage;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.List;

@Component
public class GmailMessageParser {
    private static final Logger log = LoggerFactory.getLogger(GmailMessageParser.class);

    public GmailMessage parse(Message apiMessage) {
        if (apiMessage == null || apiMessage.getPayload() == null) {
            throw new IllegalArgumentException("Cannot parse null or empty message");
        }

        GmailMessage parsedMessage = new GmailMessage();
        parsedMessage.setMessageId(apiMessage.getId());
        parsedMessage.setLabels(apiMessage.getLabelIds());

        if (apiMessage.getLabelIds() != null) {
            parsedMessage.setUnread(apiMessage.getLabelIds().contains("UNREAD"));
        }

        if (apiMessage.getInternalDate() != null) {
            parsedMessage.setReceivedDate(LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(apiMessage.getInternalDate()), ZoneId.systemDefault()));
        }

        extractHeaders(apiMessage.getPayload().getHeaders(), parsedMessage);
        parsedMessage.setBody(extractBody(apiMessage.getPayload()));

        log.debug("Successfully parsed message ID: {}", parsedMessage.getMessageId());
        return parsedMessage;
    }

    private void extractHeaders(List<MessagePartHeader> headers, GmailMessage message) {
        if (headers == null) return;
        for (MessagePartHeader header : headers) {
            if ("Subject".equalsIgnoreCase(header.getName())) {
                message.setSubject(header.getValue());
            } else if ("From".equalsIgnoreCase(header.getName())) {
                message.setFrom(header.getValue());
            }
        }
    }

    private String extractBody(MessagePart payload) {
        if (payload.getMimeType() != null && payload.getMimeType().startsWith("multipart/")) {
            checkAttachments(payload);
            if (payload.getParts() != null) {
                for (MessagePart part : payload.getParts()) {
                    if ("text/plain".equalsIgnoreCase(part.getMimeType())) {
                        return decodeBase64(part.getBody().getData());
                    }
                }
            }
        } else if ("text/plain".equalsIgnoreCase(payload.getMimeType())) {
            return decodeBase64(payload.getBody().getData());
        }
        return "No plain text body found.";
    }

    private void checkAttachments(MessagePart payload) {
        if (payload.getParts() != null) {
            boolean hasAttachment = payload.getParts().stream()
                    .anyMatch(part -> part.getFilename() != null && !part.getFilename().isEmpty());
            if (hasAttachment) {
                log.info("Message contains attachments (Not parsed).");
            }
        }
    }

    private String decodeBase64(String encoded) {
        if (encoded == null || encoded.isEmpty()) return "";
        try {
            byte[] decodedBytes = Base64.getUrlDecoder().decode(encoded);
            return new String(decodedBytes);
        } catch (IllegalArgumentException e) {
            log.warn("Failed to decode base64 body content");
            return "";
        }
    }
}