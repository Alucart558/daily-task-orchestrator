package com.dailytask.adapters.datasources;

import com.dailytask.adapters.datasources.gmail.EmailFilter;
import com.dailytask.adapters.datasources.gmail.EmailToRawDataConverter;
import com.dailytask.adapters.datasources.gmail.GmailApiClient;
import com.dailytask.adapters.datasources.gmail.GmailMessageParser;
import com.dailytask.core.domain.GmailMessage;
import com.dailytask.core.domain.RawData;
import com.google.api.services.gmail.model.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.anyLong;

class GmailDataSourceTest {

    private GmailApiClient apiClient;
    private EmailFilter filter;
    private GmailMessageParser parser;
    private EmailToRawDataConverter converter;
    private GmailDataSource dataSource;

    @BeforeEach
    void setUp() {
        apiClient = mock(GmailApiClient.class);
        filter = mock(EmailFilter.class);
        parser = mock(GmailMessageParser.class);
        converter = mock(EmailToRawDataConverter.class);
        dataSource = new GmailDataSource(apiClient, filter, parser, converter, 20);
    }

    @Test
    void fetch_ProcessesEmailsAndSkipsFailures() throws Exception {
        Message validMsg = new Message().setId("1");
        Message invalidMsg = new Message().setId("2");

        // Używamy any() dla bezpiecznego dopasowania czasu
        when(filter.getTaskQuery(any(Instant.class))).thenReturn("after:123");

        // Zmiana: Jeśli Twoja metoda oczekuje long, używamy anyLong().
        // Jeśli wyrzuca tutaj błąd kompilacji na czerwono w środowisku, po prostu
        // zmień "anyLong()" na "anyInt()" w linijce poniżej.
        when(apiClient.getEmails(anyString(), anyLong())).thenReturn(List.of(validMsg, invalidMsg));

        GmailMessage parsedMsg = new GmailMessage();
        when(parser.parse(validMsg)).thenReturn(parsedMsg);
        when(parser.parse(invalidMsg)).thenThrow(new RuntimeException("Parse error"));

        when(filter.isTaskEmail(parsedMsg)).thenReturn(true);
        when(converter.convert(parsedMsg)).thenReturn(
                new RawData("Gmail", "Title", "Body", LocalDateTime.now())
        );

        List<RawData> results = dataSource.fetch(Instant.now());

        assertEquals(1, results.size(), "Should contain exactly one valid element.");
        // Zaktualizowana weryfikacja dopasowana do użycia anyLong()
        verify(apiClient, times(1)).getEmails(anyString(), anyLong());
        verify(parser, times(2)).parse(any());
    }
}