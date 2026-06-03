package com.dailytask.core.ports;

import com.dailytask.adapters.TestDataBuilder;
import com.dailytask.core.domain.TasksSummary;
import com.dailytask.core.domain.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskSummarizerTest {

    @Mock

    private TaskSummarizer taskSummarizer;

    @Test
    void testAnalyzerContract() {
        TasksSummary mockAnalyzed = TestDataBuilder.buildSummarizedTasks();
        when(taskSummarizer.summarize(anyList())).thenReturn(mockAnalyzed);

        List<Task> tasksToAnalyze = List.of(TestDataBuilder.buildData());
        TasksSummary result = taskSummarizer.summarize(tasksToAnalyze);

        assertNotNull(result);
        assertEquals("Test Summary", result.getSummary());
        assertEquals(1, result.getAllTasks().size());
    }
}