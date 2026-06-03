package com.dailytask.core.ports;

import com.dailytask.core.domain.TasksSummary;

/**
 * Contract for dispatching the finalized task analysis to the user.
 */
public interface TaskNotifier {
    void notify(TasksSummary tasks);
}