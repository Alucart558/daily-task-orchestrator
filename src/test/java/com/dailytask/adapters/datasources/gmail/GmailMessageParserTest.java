package com.dailytask.adapters.datasources.gmail;

import com.dailytask.core.domain.GmailMessage;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartBody;
import com.google.api.services.gmail.model.MessagePartHeader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GmailMessageParserTest {
    private GmailMessageParser parser;

    @BeforeEach
    void setUp() {
        parser = new GmailMessageParser();
    }

    @Test
    void parse_Success() {
        Message apiMessage = new Message().setId("12345");
        MessagePart payload = new MessagePart().setMimeType("text/plain");

        String rawBody = "Please submit the assignment.";
        String encodedBody = Base64.getUrlEncoder().encodeToString(rawBody.getBytes());
        payload.setBody(new MessagePartBody().setData(encodedBody));

        payload.setHeaders(List.of(
                new MessagePartHeader().setName("Subject").setValue("Homework Due"),
                new MessagePartHeader().setName("From").setValue("prof@university.edu")
        ));
        apiMessage.setPayload(payload);

        GmailMessage result = parser.parse(apiMessage);

        assertEquals("12345", result.getMessageId());
        assertEquals("Homework Due", result.getSubject());
        assertEquals("prof@university.edu", result.getFrom());
        assertEquals(rawBody, result.getBody());
    }

    @Test
    void parse_MissingHeaders_HandlesGracefully() {
        Message apiMessage = new Message().setId("123").setPayload(new MessagePart());
        GmailMessage result = parser.parse(apiMessage);

        assertNull(result.getSubject());
        assertNull(result.getFrom());
        assertEquals("No plain text body found.", result.getBody());
    }
}