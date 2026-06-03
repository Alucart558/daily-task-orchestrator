package com.dailytask.core.domain;

import java.util.List;

public class TasksSummary {
    private final List<Task> allTasks;
    private final String summary;
    private final List<String> recommendations;

    public TasksSummary(List<Task> allTasks, String summary, String schedule, List<String> recommendations) {
        this.allTasks = allTasks;
        this.summary = summary;
        this.recommendations = recommendations;
    }

    public List<Task> getAllTasks() { return allTasks; }
    public String getSummary() { return summary; }
    public List<String> getRecommendations() { return recommendations; }

    @Override
    public String toString() {
        return "AnalyzedTasks{" +
                "tasksCount=" + (allTasks != null ? allTasks.size() : 0) +
                ", summary='" + summary + '\'' +
                '}';
    }
}