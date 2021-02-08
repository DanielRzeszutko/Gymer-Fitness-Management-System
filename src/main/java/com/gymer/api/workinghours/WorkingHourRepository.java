package com.gymer.api.workinghours;

import com.gymer.api.workinghours.entity.WorkingHour;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkingHourRepository extends CrudRepository<WorkingHour, Long> {
}
