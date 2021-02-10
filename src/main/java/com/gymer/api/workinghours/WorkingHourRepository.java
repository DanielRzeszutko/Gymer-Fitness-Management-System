package com.gymer.api.workinghours;

import com.gymer.api.workinghours.entity.WorkingHour;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.sql.Time;

@Repository
public interface WorkingHourRepository extends PagingAndSortingRepository<WorkingHour, Long> {

    Iterable<WorkingHour> findAllByStartHourContainsOrEndHourContains(Time startHour, Time endHour, Sort sort);

}
