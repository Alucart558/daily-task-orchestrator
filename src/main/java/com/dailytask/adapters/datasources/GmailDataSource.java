package com.dailytask.adapters.datasources;

import com.dailytask.core.domain.RawData;
import com.dailytask.core.ports.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class GmailDataSource implements DataSource {
    private static final Logger logger = LoggerFactory.getLogger(GmailDataSource.class);

    @Override
    public List<RawData> fetch(Instant from) {
        logger.info("Fetching raw tasks from Gmail since {}...", from);
        // TODO: Implement actual Gmail API fetching logic
        return new ArrayList<>();
    }

    @Override
    public String getName() {
        return "Gmail API Source";
    }
}