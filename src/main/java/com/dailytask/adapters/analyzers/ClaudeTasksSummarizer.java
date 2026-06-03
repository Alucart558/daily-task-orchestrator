package com.dailytask.adapters.analyzers;

import com.dailytask.core.domain.TasksSummary;
import com.dailytask.core.domain.Task;
import com.dailytask.core.ports.TaskSummarizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ClaudeTasksSummarizer implements TaskSummarizer {
    private static final Logger logger = LoggerFactory.getLogger(ClaudeTasksSummarizer.class);

    @Override
    public TasksSummary summarize(List<Task> tasks) {
        logger.info("Analyzing {} tasks using Claude AI...", tasks.size());
        // TODO: Implement actual Claude AI API call
        return new TasksSummary(
                tasks,
                "Dummy Summary: You have " + tasks.size() + " tasks pending.",
                "Dummy Schedule: Do everything ASAP.",
                new ArrayList<>()
        );

    }
}