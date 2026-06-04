package com.dailytask.core.config;

import com.dailytask.adapters.analyzers.ClaudeTasksSummarizer;
import com.dailytask.adapters.datasources.GmailDataSource;
import com.dailytask.adapters.notifiers.EmailTaskNotifier;
import com.dailytask.core.ports.DataSource;
import com.dailytask.core.ports.TaskSummarizer;
import com.dailytask.core.ports.TaskNotifier;

import java.util.List;

public class AppConfig {

    public static List<DataSource> createDataSources() {
        return List.of(new GmailDataSource());
    }

    public static TaskSummarizer createAnalyzer() {
        return new ClaudeTasksSummarizer();
    }

    public static TaskNotifier createNotifier() {
        return new EmailTaskNotifier();
    }
}