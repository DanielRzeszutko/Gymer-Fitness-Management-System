package com.gymer.resources.workinghours;

import com.gymer.resources.workinghours.entity.WorkingHour;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.sql.Time;

@Repository
public interface WorkingHourRepository extends PagingAndSortingRepository<WorkingHour, Long> {

    Page<WorkingHour> findAllByStartHourContainsOrEndHourContains(Time startHour, Time endHour, Pageable pageable);

}
