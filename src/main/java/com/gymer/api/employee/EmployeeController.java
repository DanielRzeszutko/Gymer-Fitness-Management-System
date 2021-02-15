package com.gymer.api.employee;

import com.gymer.api.common.controller.AbstractRestApiController;
import com.gymer.api.employee.entity.Employee;
import com.gymer.api.employee.entity.EmployeeDTO;
import com.gymer.api.partner.PartnerService;
import com.gymer.api.partner.entity.Partner;
import com.gymer.api.workinghours.WorkingHourController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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
    public PagedModel<EntityModel<EmployeeDTO>> getAllElementsSortable(Pageable pageable,
                                                          @RequestParam(required = false, name = "contains") String searchBy,
                                                          PagedResourcesAssembler<EmployeeDTO> assembler) {
        PagedModel<EntityModel<EmployeeDTO>> model = super.getAllElementsSortable(pageable, searchBy, assembler);
        model.add(linkTo(methodOn(EmployeeController.class).getAllElementsSortable(pageable, searchBy, assembler)).withSelfRel().expand());
        return model;
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
    public PagedModel<EntityModel<EmployeeDTO>> getAllEmployeesByPartnerId(@PathVariable Long partnerId,
                                                                           Pageable pageable,
                                                                           PagedResourcesAssembler<EmployeeDTO> assembler) {
        Partner partner = partnerService.getElementById(partnerId);
        PagedModel<EntityModel<EmployeeDTO>> model = super.getCollectionModel(((EmployeeService) service).findAllEmployeesForPartner(pageable, partner), assembler);
        model.add(linkTo(methodOn(EmployeeController.class).getAllEmployeesByPartnerId(partnerId, pageable, assembler)).withSelfRel());
        return model;
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
        Partner partner = partnerService.findPartnerContainingEmployee(employee);
        EmployeeDTO employeeDTO = new EmployeeDTO(employee);

        Link selfLink = linkTo(
                methodOn(EmployeeController.class).getEmployeeById(partner.getId(), employee.getId())).withSelfRel();
        Link workingHoursLink = linkTo(
                methodOn(WorkingHourController.class).getEmployeeWorkingHoursById(partner.getId(), employee.getId())).withRel("workinghours");

        employeeDTO.add(selfLink, workingHoursLink);
        return employeeDTO;
    }

}
