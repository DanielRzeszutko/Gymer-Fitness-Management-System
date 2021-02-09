package com.gymer.api.workinghours;

import com.gymer.api.workinghours.entity.WorkingHour;
import org.springframework.stereotype.Service;

@Service
public class WorkingHourService {

	private final WorkingHourRepository workingHourRepository;

	public WorkingHourService(WorkingHourRepository workingHourRepository) {
		this.workingHourRepository = workingHourRepository;
	}

	public void updateWorkingHour(Long workingHourId, WorkingHour workingHour) {
		workingHour.setId(workingHourId);
		workingHourRepository.save(workingHour);
	}

}
