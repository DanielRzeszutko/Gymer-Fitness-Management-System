package com.gymer.api.workinghours;

import com.gymer.api.workinghours.entity.WorkingHour;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

	public WorkingHour getWorkingHourById(Long workingHourId) {
		return workingHourRepository.findById(workingHourId).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}

}
