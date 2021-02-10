package com.gymer.api.workinghours;

import com.gymer.api.common.service.AbstractRestApiService;
import com.gymer.api.workinghours.entity.WorkingHour;
import org.springframework.stereotype.Service;

@Service
public class WorkingHourService extends AbstractRestApiService<WorkingHour, Long> {

    public WorkingHourService(WorkingHourRepository repository) {
        super(repository);
    }

}
