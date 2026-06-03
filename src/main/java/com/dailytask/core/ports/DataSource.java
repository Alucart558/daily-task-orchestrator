package com.dailytask.core.ports;

import com.dailytask.core.domain.RawData;
import java.time.Instant;
import java.util.List;

public interface DataSource {
    List<RawData> fetch(Instant from);
    String getName();
}