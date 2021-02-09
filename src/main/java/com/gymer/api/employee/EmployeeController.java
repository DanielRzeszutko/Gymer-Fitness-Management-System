package com.gymer.api.employee;

import com.gymer.api.employee.entity.Employee;
import com.gymer.api.employee.entity.EmployeeDTO;
import com.gymer.api.partner.PartnerService;
import com.gymer.api.partner.entity.Partner;
import com.gymer.api.slot.entity.Slot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/partners/{partnerId}/employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final PartnerService partnerService;

    @Autowired
    public EmployeeController(EmployeeService employeeService, PartnerService partnerService) {
        this.employeeService = employeeService;
        this.partnerService = partnerService;
    }

    @GetMapping
    public CollectionModel<EmployeeDTO> getAllEmployeesByPartnerId(@PathVariable Long partnerId) {
        Partner partner = partnerService.getPartnerById(partnerId);
        return CollectionModel.of(partner.getEmployees().stream().map(this::convertToEmployeeDTO).collect(Collectors.toList()));
    }

    @GetMapping("/{employeeId}")
    public EmployeeDTO getEmployeeById(@PathVariable Long partnerId, @PathVariable Long employeeId) {
        Partner partner = partnerService.getPartnerById(partnerId);
        for (Employee employee : partner.getEmployees()) {
            if (employee.getId().equals(employeeId)) {
                return convertToEmployeeDTO(employee);
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    @PostMapping
    public void addEmployeeToPartner(@RequestBody EmployeeDTO employeeDTO, @PathVariable Long partnerId) {
        Partner partner = partnerService.getPartnerById(partnerId);
        partner.getEmployees().add(convertToEmployee(employeeDTO));
        partnerService.updatePartner(partner);
    }

    @PutMapping("/employeeId")
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

    @DeleteMapping("/{employeeId}")
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
