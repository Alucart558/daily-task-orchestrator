package com.dailytask.core.ports;

import com.dailytask.core.domain.RawData;
import com.dailytask.core.domain.Task;
import java.util.List;

public interface TaskExtractor {
    List<Task> extract(List<RawData> rawDataList);
}
