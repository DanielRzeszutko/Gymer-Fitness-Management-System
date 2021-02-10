package com.gymer.api.workinghours;

import com.gymer.api.employee.EmployeeService;
import com.gymer.api.employee.entity.Employee;
import com.gymer.api.partner.PartnerService;
import com.gymer.api.partner.entity.Partner;
import com.gymer.api.workinghours.entity.WorkingHour;
import com.gymer.api.workinghours.entity.WorkingHourDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/partners/{partnerId}")
public class WorkingHourController {

	private final WorkingHourService workingHourService;
	private final PartnerService partnerService;
	private final EmployeeService employeeService;

	@Autowired
	public WorkingHourController(WorkingHourService workingHourService, PartnerService partnerService, EmployeeService employeeService) {
		this.workingHourService = workingHourService;
		this.partnerService = partnerService;
		this.employeeService = employeeService;
	}

	@GetMapping("/workinghours")
	public CollectionModel<WorkingHourDTO> getPartnerWorkingHoursById(@PathVariable Long partnerId) {
		Partner partner = partnerService.getElementById(partnerId);
		return CollectionModel.of(partner.getWorkingHours().stream().map(this::convertToWorkingHourDTO).collect(Collectors.toList()));
	}

	@PostMapping("/workinghours")
	public void addNewWorkingHourToPartner(@RequestBody WorkingHourDTO workingHourDTO, @PathVariable Long partnerId) {
		Partner partner = partnerService.getElementById(partnerId);
		WorkingHour newWorkingHour = convertToWorkingHour(workingHourDTO);
		partner.getWorkingHours().add(newWorkingHour);
		partnerService.updateElement(partner);
	}

	@GetMapping("/workinghours/{workingHourId}")
	public WorkingHourDTO getWorkingHourById(@PathVariable Long partnerId, @PathVariable Long workingHourId) {
		Partner partner = partnerService.getElementById(partnerId);
		WorkingHour workingHour = workingHourService.getElementById(workingHourId);
		if (!partner.getWorkingHours().contains(workingHour)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		return convertToWorkingHourDTO(workingHour);
	}

	@DeleteMapping("/workinghours/{workingHourId}")
	public void deleteWorkingHourFromPartner(@PathVariable Long partnerId, @PathVariable Long workingHourId) {
		Partner partner = partnerService.getElementById(partnerId);
		WorkingHour workingHour = workingHourService.getElementById(workingHourId);
		if (!partner.getWorkingHours().contains(workingHour)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		partner.getWorkingHours().remove(workingHour);
		partnerService.updateElement(partner);
	}

	@PutMapping("/workinghours/{workingHourId}")
	public void updatePartnerWorkingHours(@RequestBody WorkingHourDTO workingHourDTO,
										  @PathVariable Long partnerId, @PathVariable Long workingHourId) {
		Partner partner = partnerService.getElementById(partnerId);
		List<WorkingHour> workingHoursList = partner.getWorkingHours();
		WorkingHour workingHour = convertToWorkingHour(workingHourDTO);
		if (!workingHoursList.contains(workingHour)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		workingHour.setId(workingHourId);
		workingHourService.updateElement(workingHour);
	}


	@GetMapping("/employees/{employeeId}/workinghours/")
	public Iterable<WorkingHourDTO> getEmployeeWorkingHoursById(@PathVariable Long partnerId, @PathVariable Long employeeId) {
		Partner partner = partnerService.getElementById(partnerId);
		List<Employee> employeesList = partner.getEmployees();
		Employee employee = employeeService.getElementById(employeeId);
		if (!employeesList.contains(employee)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		return employee.getWorkingHours().stream().map(this::convertToWorkingHourDTO).collect(Collectors.toList());
	}

	@PostMapping("/employees/{employeeId}/workinghours")
	public void addNewWorkingHourToEmployee(@RequestBody WorkingHourDTO workingHourDTO,
											@PathVariable Long partnerId, @PathVariable Long employeeId) {
		Partner partner = partnerService.getElementById(partnerId);
		Employee employee = employeeService.getElementById(employeeId);
		if (!partner.getEmployees().contains(employee)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		WorkingHour newWorkingHour = convertToWorkingHour(workingHourDTO);
		employee.getWorkingHours().add(newWorkingHour);
		employeeService.updateElement(employee);
	}

	@DeleteMapping("/employees/{employeeId}/workinghours/{workingHourId}")
	public void deleteWorkingHourFromEmployee(@PathVariable Long partnerId, @PathVariable Long employeeId,
											@PathVariable Long workingHourId) {
		Partner partner = partnerService.getElementById(partnerId);
		Employee employee = employeeService.getElementById(employeeId);
		if (!partner.getEmployees().contains(employee)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		WorkingHour workingHour = workingHourService.getElementById(workingHourId);
		if (!employee.getWorkingHours().contains(workingHour)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		employee.getWorkingHours().remove(workingHour);
		employeeService.updateElement(employee);
	}

	@GetMapping("/employees/{employeeId}/workinghours/{workingHourId}")
	public WorkingHourDTO getEmployeeWorkingHourById(@PathVariable Long partnerId,
										   @PathVariable Long employeeId,
										   @PathVariable Long workingHourId) {
		Partner partner = partnerService.getElementById(partnerId);
		for (Employee employee : partner.getEmployees()) {
			if (employee.getId().equals(employeeId)) {
				for (WorkingHour workingHour : employee.getWorkingHours()) {
					if (workingHour.getId().equals(workingHourId)) {
						return convertToWorkingHourDTO(workingHour);
					}
				}
			}
		}
		throw new ResponseStatusException(HttpStatus.NOT_FOUND);
	}

	@PutMapping("/employees/{employeeId}/workinghours/{workingHourId}")
	public void updateEmployeeWorkingHours(@RequestBody WorkingHourDTO workingHourDTO, @PathVariable Long partnerId,
										   @PathVariable Long employeeId, @PathVariable Long workingHourId) {
		Partner partner = partnerService.getElementById(partnerId);
		List<Employee> employeesList = partner.getEmployees();
		Employee employee = employeeService.getElementById(employeeId);
		List<WorkingHour> workingHoursList = partner.getWorkingHours();
		WorkingHour workingHour = convertToWorkingHour(workingHourDTO);

		if (!workingHoursList.contains(workingHour) || !employeesList.contains(employee)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		workingHour.setId(workingHourId);
		workingHourService.updateElement(workingHour);
	}


	private WorkingHourDTO convertToWorkingHourDTO(WorkingHour workingHour) {
		return new WorkingHourDTO(
				workingHour.getId(),
				workingHour.getDay(),
				workingHour.getStartHour(),
				workingHour.getEndHour()
		);
	}

	private WorkingHour convertToWorkingHour(WorkingHourDTO workingHourDTO) {
		return new WorkingHour(
				workingHourDTO.getId(),
				workingHourDTO.getDay(),
				workingHourDTO.getStartHour(),
				workingHourDTO.getEndHour()
		);
	}
}
