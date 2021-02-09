package com.gymer.api.employee;

import com.gymer.api.employee.entity.Employee;
import com.gymer.api.employee.entity.EmployeeDTO;
import com.gymer.api.partner.PartnerService;
import com.gymer.api.partner.entity.Partner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class EmployeeController {

    private final EmployeeService employeeService;
    private final PartnerService partnerService;

    @Autowired
    public EmployeeController(EmployeeService employeeService, PartnerService partnerService) {
        this.employeeService = employeeService;
        this.partnerService = partnerService;
    }

    @GetMapping("/api/employees")
    public CollectionModel<EmployeeDTO> getAllEmployeesAndSort(Sort sort, @RequestParam(required = false, name = "contains") String details) {
        if (details != null) {
            return CollectionModel.of(((List<Employee>) employeeService.getEmployeesContaining(details, sort))
                    .stream().map(this::convertToEmployeeDTO).collect(Collectors.toList()));
        }
        List<Employee> employees = (List<Employee>) employeeService.getEmployeesAndSort(sort);
        return CollectionModel.of(employees.stream().map(this::convertToEmployeeDTO).collect(Collectors.toList()));
    }

    @GetMapping("/api/partners/{partnerId}/employees")
    public CollectionModel<EmployeeDTO> getAllEmployeesByPartnerId(@PathVariable Long partnerId) {
        Partner partner = partnerService.getPartnerById(partnerId);
        return CollectionModel.of(partner.getEmployees().stream().map(this::convertToEmployeeDTO).collect(Collectors.toList()));
    }

    @GetMapping("/api/partners/{partnerId}/employees/{employeeId}")
    public EmployeeDTO getEmployeeById(@PathVariable Long partnerId, @PathVariable Long employeeId) {
        Partner partner = partnerService.getPartnerById(partnerId);
        for (Employee employee : partner.getEmployees()) {
            if (employee.getId().equals(employeeId)) {
                return convertToEmployeeDTO(employee);
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/api/partners/{partnerId}/employees")
    public void addEmployeeToPartner(@RequestBody EmployeeDTO employeeDTO, @PathVariable Long partnerId) {
        Partner partner = partnerService.getPartnerById(partnerId);
        partner.getEmployees().add(convertToEmployee(employeeDTO));
        partnerService.updatePartner(partner);
    }

    @PutMapping("/api/partners/{partnerId}/employees/employeeId")
    public void updateEmployee(@RequestBody EmployeeDTO employeeDTO, @PathVariable Long partnerId, @PathVariable Long employeeId) {
        if (!employeeDTO.getId().equals(employeeId)) throw new ResponseStatusException(HttpStatus.CONFLICT);
        Partner partner = partnerService.getPartnerById(partnerId);
        for (Employee employee : partner.getEmployees()) {
            if (employee.getId().equals(employeeId)) {
                employeeService.updateEmployee(convertToEmployee(employeeDTO));
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/api/partners/{partnerId}/employees/{employeeId}")
    public void deleteEmployee(@PathVariable Long partnerId, @PathVariable Long employeeId) {
        Partner partner = partnerService.getPartnerById(partnerId);
        List<Employee> employees = partner.getEmployees();
        for (Employee employee : employees) {
            if (employee.getId().equals(employeeId)){
                employeeService.deleteEmployee(employee);
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    private Employee convertToEmployee(EmployeeDTO employeeDTO) {
        Employee employee = employeeService.getEmployeeById(employeeDTO.getId());
        employee.setFirstName(employeeDTO.getFirstName());
        employee.setLastName(employeeDTO.getLastName());
        employee.setImage(employeeDTO.getImage());
        employee.setDescription(employeeDTO.getDescription());
        return employee;
    }

    private EmployeeDTO convertToEmployeeDTO(Employee employee) {
        EmployeeDTO employeeDTO = new EmployeeDTO(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getDescription(),
                employee.getDescription()
        );

        Link selfLink = Link.of("/partners/" + employee.getId() + "/employees/" + employee.getId()).withSelfRel();

        Links workingHoursLinks = Links.of(employee.getWorkingHours().stream().map(
                workingHour -> Link.of("/partners/" + employee.getId() + "/employees/" + employee.getId() + "/workinghours/" + workingHour.getId()).withRel("workinghours")
        ).collect(Collectors.toList()));

        employeeDTO.add(selfLink);
        employeeDTO.add(workingHoursLinks);
        return employeeDTO;
    }

}
