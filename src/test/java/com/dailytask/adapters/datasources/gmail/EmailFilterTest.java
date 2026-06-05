package com.dailytask.adapters.datasources.gmail;

import com.dailytask.core.domain.GmailMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmailFilterTest {
    private EmailFilter filter;

    @BeforeEach
    void setUp() {
        filter = new EmailFilter(List.of("assignment", "deadline"));
    }

    @Test
    void isTaskEmail_KeywordInSubject_ReturnsTrue() {
        GmailMessage msg = new GmailMessage();
        msg.setSubject("Upcoming ASSIGNMENT");
        assertTrue(filter.isTaskEmail(msg));
    }

    @Test
    void isTaskEmail_NoKeywords_ReturnsFalse() {
        GmailMessage msg = new GmailMessage();
        msg.setSubject("Hello there");
        msg.setBody("How are you?");
        assertFalse(filter.isTaskEmail(msg));
    }

    @Test
    void getTaskQuery_WithInstant_GeneratesCorrectly() {
        Instant now = Instant.now();
        String query = filter.getTaskQuery(now);
        assertEquals("after:" + now.getEpochSecond(), query);
    }
}