package com.gymer.api.workinghours;

import com.gymer.api.employee.entity.Employee;
import com.gymer.api.workinghours.entity.WorkingHour;
import com.gymer.api.workinghours.entity.WorkingHourDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/partners/{partnerId}")
public class WorkingHourController {

	private final WorkingHourService workingHourService;

	@Autowired
	public WorkingHourController(WorkingHourService workingHourService) {
		this.workingHourService = workingHourService;
	}


	@GetMapping("/workingHours")
	public Iterable<WorkingHour> getPartnerWorkingHoursById(@PathVariable Long partnerId) {
		Partner partner = partnerService.getPartnerById(partnerId);
		return partner.getWorkingHoursList();
	}

	@PutMapping("/workingHours/{workingHourId}")
	public void updatePartnerWorkingHours(@RequestBody WorkingHourDTO workingHourDTO,
										  @PathVariable Long partnerId, @PathVariable Long workingHourId) {
		Partner partner = partnerService.getPartnerById(partnerId);
		List<WorkingHour> workingHoursList = partner.getWorkingHoursList();
		WorkingHour workingHour = convertToWorkingHour(workingHourDTO);
		if (!workingHoursList.contains(workingHour)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}

		workingHourService.updateWorkingHour(workingHourId, workingHour);
	}


	@GetMapping("/employees/{employeeId}/workingHours")
	public Iterable<WorkingHour> getEmployeeWorkingHoursById(@PathVariable Long partnerId, @PathVariable Long employeeId) {
		Partner partner = partnerService.getPartnerById(partnerId);
		List<Employee> employeesList = partner.getEmployees();
		Employee employee = employeeService.getEmployeeById(employeeId);
		if (!employeesList.contains(employee)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}

		return employee.getWorkingHoursList();
	}

	@PutMapping("/employees/{employeeId}/workingHours/{workingHourId}")
	public void updateEmployeeWorkingHours(@RequestBody WorkingHourDTO workingHourDTO, @PathVariable Long partnerId,
										   @PathVariable Long employeeId, @PathVariable Long workingHourId) {
		Partner partner = partnerService.getPartnerById(partnerId);
		List<Employee> employeesList = partner.getEmployees();
		Employee employee = employeeService.getEmployeeById(employeeId);
		List<WorkingHour> workingHoursList = partner.getWorkingHoursList();
		WorkingHour workingHour = convertToWorkingHour(workingHourDTO);

		if (!workingHoursList.contains(workingHour) && !employeesList.contains(employee)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}

		workingHourService.updateWorkingHour(workingHourId, workingHour);
	}


	private WorkingHourDTO convertToWorkingHourDTO(WorkingHour workingHour) {
		return new WorkingHourDTO(workingHour.getId(),
				workingHour.getDay(),
				workingHour.getStartHour(),
				workingHour.getEndHour());
	}

	private WorkingHour convertToWorkingHour(WorkingHourDTO workingHourDTO) {
		return new WorkingHour(workingHourDTO.getId(),
				workingHourDTO.getDay(),
				workingHourDTO.getStartHour(),
				workingHourDTO.getEndHour());
	}
}
