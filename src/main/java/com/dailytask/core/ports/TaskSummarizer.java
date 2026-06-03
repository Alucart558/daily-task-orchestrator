package com.dailytask.core.ports;

import com.dailytask.core.domain.TasksSummary;
import com.dailytask.core.domain.Task;
import java.util.List;

public interface TaskSummarizer {
    TasksSummary summarize(List<Task> tasks);
}
