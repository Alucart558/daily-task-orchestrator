package com.dailytask.adapters.datasources.gmail;

import com.dailytask.core.domain.GmailMessage;
import com.dailytask.core.domain.RawData;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmailToRawDataConverterTest {

    @Test
    void convert_PopulatesAllFields() {
        EmailToRawDataConverter converter = new EmailToRawDataConverter();
        GmailMessage msg = new GmailMessage();
        msg.setMessageId("msg-001");
        msg.setSubject("Test Subject");
        msg.setBody("Test Body");
        msg.setFrom("sender@test.com");
        msg.setReceivedDate(LocalDateTime.of(2023, 1, 1, 10, 0));
        msg.setLabels(List.of("INBOX", "UNREAD"));

        RawData data = converter.convert(msg);

        assertEquals("Gmail", data.getSource());
        assertEquals("Test Subject", data.getTitle());
        assertEquals("Test Body", data.getRawContent());
        assertEquals("sender@test.com", data.getSender());
        assertEquals("msg-001", data.getOriginalSource());
        assertTrue(data.getMetadata().containsKey("labels"));
    }
}