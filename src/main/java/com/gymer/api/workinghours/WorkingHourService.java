package com.gymer.api.workinghours;

import com.gymer.api.common.service.AbstractRestApiService;
import com.gymer.api.workinghours.entity.WorkingHour;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Time;

@Service
public class WorkingHourService extends AbstractRestApiService<WorkingHour, Long> {

    public WorkingHourService(WorkingHourRepository repository) {
        super(repository);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<WorkingHour> findAllContaining(Pageable pageable, String searchBy) {
        Time time = Time.valueOf(searchBy);
        return ((WorkingHourRepository) repository).findAllByStartHourContainsOrEndHourContains(time, time, pageable);
    }

}
