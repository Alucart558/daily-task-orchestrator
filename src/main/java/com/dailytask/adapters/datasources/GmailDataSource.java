package com.dailytask.adapters.datasources;

import com.dailytask.adapters.datasources.gmail.EmailFilter;
import com.dailytask.adapters.datasources.gmail.EmailToRawDataConverter;
import com.dailytask.adapters.datasources.gmail.GmailApiClient;
import com.dailytask.adapters.datasources.gmail.GmailMessageParser;
import com.dailytask.core.domain.GmailMessage;
import com.dailytask.core.domain.RawData;
import com.dailytask.core.ports.DataSource;
import com.google.api.services.gmail.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class GmailDataSource implements DataSource {
    private static final Logger log = LoggerFactory.getLogger(GmailDataSource.class);

    private final GmailApiClient apiClient;
    private final EmailFilter emailFilter;
    private final GmailMessageParser messageParser;
    private final EmailToRawDataConverter rawDataConverter;
    private final int queryLimit;

    public GmailDataSource(GmailApiClient apiClient,
                           EmailFilter emailFilter,
                           GmailMessageParser messageParser,
                           EmailToRawDataConverter rawDataConverter,
                           @Value("${gmail.email-fetch.query-limit:20}") int queryLimit) {
        this.apiClient = apiClient;
        this.emailFilter = emailFilter;
        this.messageParser = messageParser;
        this.rawDataConverter = rawDataConverter;
        this.queryLimit = queryLimit;
    }

    @Override
    public List<RawData> fetch(Instant from) {
        String query = emailFilter.getTaskQuery(from);
        log.info("Fetching Gmail messages with query: {}", query);

        List<RawData> results = new ArrayList<>();
        List<Message> apiMessages;

        try {
            apiMessages = apiClient.getEmails(query, queryLimit);
        } catch (Exception e) {
            log.error("Failed to fetch emails from Gmail API", e);
            return results;
        }

        for (Message apiMessage : apiMessages) {
            try {
                GmailMessage parsedMessage = messageParser.parse(apiMessage);
                if (emailFilter.isTaskEmail(parsedMessage)) {
                    RawData rawData = rawDataConverter.convert(parsedMessage);
                    results.add(rawData);
                }
            } catch (Exception e) {
                log.error("Skipping malformed email ID {}: {}", apiMessage.getId(), e.getMessage());
            }
        }

        log.info("Successfully fetched and converted {} task emails.", results.size());
        return results;
    }

    @Override
    public String getName() {
        return "Gmail";
    }
}