package com.dailytask.adapters.analyzers;

import com.dailytask.core.domain.RawData;
import com.dailytask.core.domain.Task;
import com.dailytask.core.ports.TaskExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


public class ClaudeRawDataAnalyzer implements TaskExtractor {
    private static final Logger logger = LoggerFactory.getLogger(ClaudeRawDataAnalyzer.class);

    @Override
    public List<Task> extract(List<RawData> rawDataList) {
        logger.info("Extracting tasks from {} raw items using Claude AI...", rawDataList.size());
        // TODO: Implement actual Claude AI extraction logic
        return new ArrayList<>();
    }
}
