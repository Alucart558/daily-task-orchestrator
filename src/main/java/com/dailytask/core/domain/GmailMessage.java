package com.dailytask.core.domain;

import java.time.LocalDateTime;
import java.util.List;

public class GmailMessage {
    private String messageId;
    private String from;
    private String subject;
    private String body;
    private LocalDateTime receivedDate;
    private List<String> labels;
    private boolean isUnread;

    public GmailMessage() {}

    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }

    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public LocalDateTime getReceivedDate() { return receivedDate; }
    public void setReceivedDate(LocalDateTime receivedDate) { this.receivedDate = receivedDate; }

    public List<String> getLabels() { return labels; }
    public void setLabels(List<String> labels) { this.labels = labels; }

    public boolean isUnread() { return isUnread; }
    public void setUnread(boolean unread) { isUnread = unread; }

    @Override
    public String toString() {
        return "GmailMessage{id='" + messageId + "', subject='" + subject + "', from='" + from + "'}";
    }
}