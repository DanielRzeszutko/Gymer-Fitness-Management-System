package com.gymer.api.employee;

import com.gymer.api.common.controller.AbstractRestApiController;
import com.gymer.api.employee.entity.Employee;
import com.gymer.api.employee.entity.EmployeeDTO;
import com.gymer.api.partner.PartnerService;
import com.gymer.api.partner.entity.Partner;
import com.gymer.api.partner.entity.PartnerDTO;
import lombok.Builder;
import org.hibernate.mapping.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class EmployeeController extends AbstractRestApiController<EmployeeDTO, Employee, Long> {

    private final PartnerService partnerService;

    @Autowired
    public EmployeeController(EmployeeService employeeService, PartnerService partnerService) {
        super(employeeService);
        this.partnerService = partnerService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @GetMapping("/api/employees")
    public CollectionModel<EmployeeDTO> getAllElementsSortable(Sort sort, @RequestParam(required = false, name = "contains") String searchBy) {
        return super.getAllElementsSortable(sort, searchBy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @GetMapping("/api/employees/{addressId}")
    public EmployeeDTO getElementById(@PathVariable Long addressId) {
        return super.getElementById(addressId);
    }

    /**
     * Endpoint responsible for obtaining list of employees from partner
     */
    @GetMapping("/api/partners/{partnerId}/employees")
    public CollectionModel<EmployeeDTO> getAllEmployeesByPartnerId(@PathVariable Long partnerId) {
        Partner partner = partnerService.getElementById(partnerId);
        return CollectionModel.of(partner.getEmployees().stream().map(this::convertToDTO).collect(Collectors.toList()));
    }

    /**
     * Endpoint responsible for obtaining specific employee from partner
     */
    @GetMapping("/api/partners/{partnerId}/employees/{employeeId}")
    public EmployeeDTO getEmployeeById(@PathVariable Long partnerId, @PathVariable Long employeeId) {
        Partner partner = partnerService.getElementById(partnerId);
        for (Employee employee : partner.getEmployees()) {
            if (employee.getId().equals(employeeId)) {
                return convertToDTO(employee);
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    /**
     * Endpoint responsible for add new employee to partner
     */
    @PostMapping("/api/partners/{partnerId}/employees")
    public void addEmployeeToPartner(@RequestBody EmployeeDTO employeeDTO, @PathVariable Long partnerId) {
        Partner partner = partnerService.getElementById(partnerId);
        partner.getEmployees().add(convertToEntity(employeeDTO));
        partnerService.updateElement(partner);
    }

    /**
     * Endpoint responsible for updating employee details from partner
     */
    @PutMapping("/api/partners/{partnerId}/employees/{employeeId}")
    public void updateEmployee(@RequestBody EmployeeDTO employeeDTO, @PathVariable Long partnerId, @PathVariable Long employeeId) {
        if (!employeeDTO.getId().equals(employeeId)) throw new ResponseStatusException(HttpStatus.CONFLICT);
        Partner partner = partnerService.getElementById(partnerId);
        for (Employee employee : partner.getEmployees()) {
            if (employee.getId().equals(employeeId)) {
                service.updateElement(convertToEntity(employeeDTO));
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    /**
     * Endpoint responsible for deleting employee completely from database
     */
    @DeleteMapping("/api/partners/{partnerId}/employees/{employeeId}")
    public void deleteEmployee(@PathVariable Long partnerId, @PathVariable Long employeeId) {
        Partner partner = partnerService.getElementById(partnerId);
        List<Employee> employees = partner.getEmployees();
        for (Employee employee : employees) {
            if (employee.getId().equals(employeeId)){
                ((EmployeeService) service).deleteEmployee(employee);
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Employee convertToEntity(EmployeeDTO employeeDTO) {
        Employee newEmployee = new Employee(employeeDTO);
        if (service.isElementExistById(employeeDTO.getId())) {
            Employee oldEmployee = service.getElementById(employeeDTO.getId());
            newEmployee.setWorkingHours(oldEmployee.getWorkingHours());
            return newEmployee;
        }
        newEmployee.setWorkingHours(Collections.emptyList());
        return newEmployee;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EmployeeDTO convertToDTO(Employee employee) {
        EmployeeDTO employeeDTO = new EmployeeDTO(employee);

        Link selfLink = Link.of("/partners/" + employee.getId() + "/employees/" + employee.getId()).withSelfRel();
        Links workingHoursLinks = Links.of(employee.getWorkingHours().stream().map(
                workingHour -> Link.of("/partners/" + employee.getId() + "/employees/" + employee.getId() + "/workinghours/" + workingHour.getId()).withRel("workinghours")
        ).collect(Collectors.toList()));

        employeeDTO.add(selfLink);
        employeeDTO.add(workingHoursLinks);
        return employeeDTO;
    }

}
