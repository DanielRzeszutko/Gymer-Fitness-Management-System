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
		Partner partner = partnerService.getPartnerById(partnerId);
		return CollectionModel.of(partner.getWorkingHours().stream().map(this::convertToWorkingHourDTO).collect(Collectors.toList()));
	}

	@PostMapping("/workinghours")
	public void addNewWorkingHourToPartner(@RequestBody WorkingHourDTO workingHourDTO, @PathVariable Long partnerId) {
		Partner partner = partnerService.getPartnerById(partnerId);
		WorkingHour newWorkingHour = convertToWorkingHour(workingHourDTO);
		partner.getWorkingHours().add(newWorkingHour);
		partnerService.updatePartner(partner);
	}

	@GetMapping("/workinghours/{workingHourId}")
	public WorkingHourDTO getWorkingHourById(@PathVariable Long partnerId, @PathVariable Long workingHourId) {
		Partner partner = partnerService.getPartnerById(partnerId);
		WorkingHour workingHour = workingHourService.getWorkingHourById(workingHourId);
		if (!partner.getWorkingHours().contains(workingHour)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		return convertToWorkingHourDTO(workingHour);
	}

	@DeleteMapping("/workinghours/{workingHourId}")
	public void deleteWorkingHourFromPartner(@PathVariable Long partnerId, @PathVariable Long workingHourId) {
		Partner partner = partnerService.getPartnerById(partnerId);
		WorkingHour workingHour = workingHourService.getWorkingHourById(workingHourId);
		if (!partner.getWorkingHours().contains(workingHour)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		partner.getWorkingHours().remove(workingHour);
		partnerService.updatePartner(partner);
	}

	@PutMapping("/workinghours/{workingHourId}")
	public void updatePartnerWorkingHours(@RequestBody WorkingHourDTO workingHourDTO,
										  @PathVariable Long partnerId, @PathVariable Long workingHourId) {
		Partner partner = partnerService.getPartnerById(partnerId);
		List<WorkingHour> workingHoursList = partner.getWorkingHours();
		WorkingHour workingHour = convertToWorkingHour(workingHourDTO);
		if (!workingHoursList.contains(workingHour)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}

		workingHourService.updateWorkingHour(workingHourId, workingHour);
	}


	@GetMapping("/employees/{employeeId}/workinghours/")
	public Iterable<WorkingHourDTO> getEmployeeWorkingHoursById(@PathVariable Long partnerId, @PathVariable Long employeeId) {
		Partner partner = partnerService.getPartnerById(partnerId);
		List<Employee> employeesList = partner.getEmployees();
		Employee employee = employeeService.getEmployeeById(employeeId);
		if (!employeesList.contains(employee)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		return employee.getWorkingHours().stream().map(this::convertToWorkingHourDTO).collect(Collectors.toList());
	}

	@PostMapping("/employees/{employeeId}/workinghours")
	public void addNewWorkingHourToEmployee(@RequestBody WorkingHourDTO workingHourDTO,
											@PathVariable Long partnerId, @PathVariable Long employeeId) {
		Partner partner = partnerService.getPartnerById(partnerId);
		Employee employee = employeeService.getEmployeeById(employeeId);
		if (!partner.getEmployees().contains(employee)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		WorkingHour newWorkingHour = convertToWorkingHour(workingHourDTO);
		employee.getWorkingHours().add(newWorkingHour);
		employeeService.updateEmployee(employee);
	}

	@DeleteMapping("/employees/{employeeId}/workinghours/{workingHourId}")
	public void deleteWorkingHourFromEmployee(@PathVariable Long partnerId, @PathVariable Long employeeId,
											@PathVariable Long workingHourId) {
		Partner partner = partnerService.getPartnerById(partnerId);
		Employee employee = employeeService.getEmployeeById(employeeId);
		if (!partner.getEmployees().contains(employee)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		WorkingHour workingHour = workingHourService.getWorkingHourById(workingHourId);
		if (!employee.getWorkingHours().contains(workingHour)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		employee.getWorkingHours().remove(workingHour);
		employeeService.updateEmployee(employee);
	}

	@GetMapping("/employees/{employeeId}/workinghours/{workingHourId}")
	public WorkingHourDTO getEmployeeWorkingHourById(@PathVariable Long partnerId,
										   @PathVariable Long employeeId,
										   @PathVariable Long workingHourId) {
		Partner partner = partnerService.getPartnerById(partnerId);
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
		Partner partner = partnerService.getPartnerById(partnerId);
		List<Employee> employeesList = partner.getEmployees();
		Employee employee = employeeService.getEmployeeById(employeeId);
		List<WorkingHour> workingHoursList = partner.getWorkingHours();
		WorkingHour workingHour = convertToWorkingHour(workingHourDTO);

		if (!workingHoursList.contains(workingHour) || !employeesList.contains(employee)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}

		workingHourService.updateWorkingHour(workingHourId, workingHour);
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
