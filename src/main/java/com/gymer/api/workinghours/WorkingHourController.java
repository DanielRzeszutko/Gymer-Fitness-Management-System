package com.gymer.api.workinghours;

import com.gymer.api.common.controller.AbstractRestApiController;
import com.gymer.api.employee.EmployeeService;
import com.gymer.api.employee.entity.Employee;
import com.gymer.api.partner.PartnerService;
import com.gymer.api.partner.entity.Partner;
import com.gymer.api.user.UserController;
import com.gymer.api.workinghours.entity.WorkingHour;
import com.gymer.api.workinghours.entity.WorkingHourDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class WorkingHourController extends AbstractRestApiController<WorkingHourDTO, WorkingHour, Long> {

    private final PartnerService partnerService;
    private final EmployeeService employeeService;

    @Autowired
    public WorkingHourController(WorkingHourService service, PartnerService partnerService, EmployeeService employeeService) {
        super(service);
        this.partnerService = partnerService;
        this.employeeService = employeeService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @GetMapping("/api/workinghours")
    public CollectionModel<WorkingHourDTO> getAllElementsSortable(Sort sort, @RequestParam(required = false, name = "contains") String searchBy) {
        CollectionModel<WorkingHourDTO> model = super.getAllElementsSortable(sort, searchBy);
        model.add(linkTo(methodOn(WorkingHourController.class).getAllElementsSortable(sort, searchBy)).withSelfRel().expand());
        return model;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @GetMapping("/api/workinghours/{id}")
    public WorkingHourDTO getElementById(@PathVariable Long id) {
        return super.getElementById(id);
    }

    /**
     * Endpoint that add new workingHour to partner by partnerID
     */
    @PostMapping("/api/partners/{partnerId}/workinghours")
    public void addNewWorkingHourToPartner(@RequestBody WorkingHourDTO workingHourDTO, @PathVariable Long partnerId) {
        Partner partner = partnerService.getElementById(partnerId);
        WorkingHour newWorkingHour = convertToEntity(workingHourDTO);
        partner.getWorkingHours().add(newWorkingHour);
        partnerService.updateElement(partner);
    }

    /**
     * Endpoint that sends back all WorkingHours with partnerID
     */
    @GetMapping("/api/partners/{partnerId}/workinghours")
    public CollectionModel<WorkingHourDTO> getPartnerWorkingHoursById(@PathVariable Long partnerId) {
        Partner partner = partnerService.getElementById(partnerId);
        CollectionModel<WorkingHourDTO> model = CollectionModel.of(partner.getWorkingHours().stream().map(this::convertToDTO).collect(Collectors.toList()));
        model.add(linkTo(methodOn(WorkingHourController.class).getPartnerWorkingHoursById(partnerId)).withSelfRel());
        return model;
    }

    /**
     * Endpoint that sends back WorkingHour with specified ID and partnerID
     */
    @GetMapping("/api/partners/{partnerId}/workinghours/{workingHourId}")
    public WorkingHourDTO getWorkingHourById(@PathVariable Long partnerId, @PathVariable Long workingHourId) {
        Partner partner = partnerService.getElementById(partnerId);
        WorkingHour workingHour = service.getElementById(workingHourId);
        if (!partner.getWorkingHours().contains(workingHour)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        return convertToDTO(workingHour);
    }

    /**
     * Endpoint that receives WorkingHourDTO body and change all details inside database
     */
    @PutMapping("/api/partners/{partnerId}/workinghours/{workingHourId}")
    public void updatePartnerWorkingHours(@RequestBody WorkingHourDTO workingHourDTO,
                                          @PathVariable Long partnerId,
                                          @PathVariable Long workingHourId) {
        Partner partner = partnerService.getElementById(partnerId);
        List<WorkingHour> workingHoursList = partner.getWorkingHours();
        WorkingHour workingHour = convertToEntity(workingHourDTO);
        if (!workingHoursList.contains(workingHour)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        workingHour.setId(workingHourId);
        service.updateElement(workingHour);
    }

    /**
     * Endpoint that removes workingHour from partner
     */
    @DeleteMapping("/api/partners/{partnerId}/workinghours/{workingHourId}")
    public void deleteWorkingHourFromPartner(@PathVariable Long partnerId, @PathVariable Long workingHourId) {
        Partner partner = partnerService.getElementById(partnerId);
        WorkingHour workingHour = service.getElementById(workingHourId);
        if (!partner.getWorkingHours().contains(workingHour)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        partner.getWorkingHours().remove(workingHour);
        partnerService.updateElement(partner);
    }

    /**
     * Endpoint that add new workingHour to employee by partnerID and employeeID
     */
    @PostMapping("/api/partners/{partnerId}/employees/{employeeId}/workinghours")
    public void addNewWorkingHourToEmployee(@RequestBody WorkingHourDTO workingHourDTO,
                                            @PathVariable Long partnerId,
                                            @PathVariable Long employeeId) {
        Partner partner = partnerService.getElementById(partnerId);
        Employee employee = employeeService.getElementById(employeeId);
        if (!partner.getEmployees().contains(employee)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        WorkingHour newWorkingHour = convertToEntity(workingHourDTO);
        employee.getWorkingHours().add(newWorkingHour);
        employeeService.updateElement(employee);
    }

    /**
     * Endpoint that sends back all WorkingHours with partnerID and employeeID
     */
    @GetMapping("/api/partners/{partnerId}/employees/{employeeId}/workinghours")
    public CollectionModel<WorkingHourDTO> getEmployeeWorkingHoursById(@PathVariable Long partnerId, @PathVariable Long employeeId) {
        Partner partner = partnerService.getElementById(partnerId);
        List<Employee> employeesList = partner.getEmployees();
        Employee employee = employeeService.getElementById(employeeId);
        if (!employeesList.contains(employee)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        CollectionModel<WorkingHourDTO> model = CollectionModel.of(employee.getWorkingHours().stream().map(this::convertToDTO).collect(Collectors.toList()));
        model.add(linkTo(methodOn(WorkingHourController.class).getEmployeeWorkingHoursById(partnerId, employeeId)).withSelfRel());
        return model;
    }

    /**
     * Endpoint that sends back WorkingHour with specified ID, partnerID and employeeID
     */
    @GetMapping("/api/partners/{partnerId}/employees/{employeeId}/workinghours/{workingHourId}")
    public WorkingHourDTO getEmployeeWorkingHourById(@PathVariable Long partnerId,
                                                     @PathVariable Long employeeId,
                                                     @PathVariable Long workingHourId) {
        Partner partner = partnerService.getElementById(partnerId);
        Employee employee = employeeService.getElementById(employeeId);
        WorkingHour workingHour = service.getElementById(workingHourId);
        if (partner.getEmployees().contains(employee) && employee.getWorkingHours().contains(workingHour)) {
			return convertToDTO(workingHour);
		}
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    /**
     * Endpoint that receives WorkingHourDTO body and change all details inside database
     */
    @PutMapping("/api/partners/{partnerId}/employees/{employeeId}/workinghours/{workingHourId}")
    public void updateEmployeeWorkingHours(@RequestBody WorkingHourDTO workingHourDTO,
                                           @PathVariable Long partnerId,
                                           @PathVariable Long employeeId,
                                           @PathVariable Long workingHourId) {
        Partner partner = partnerService.getElementById(partnerId);
        List<Employee> employeesList = partner.getEmployees();
        Employee employee = employeeService.getElementById(employeeId);
        List<WorkingHour> workingHoursList = partner.getWorkingHours();
        WorkingHour workingHour = convertToEntity(workingHourDTO);

        if (!workingHoursList.contains(workingHour) || !employeesList.contains(employee)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        workingHour.setId(workingHourId);
        service.updateElement(workingHour);
    }

    /**
     * Endpoint that removes workingHour from employee
     */
    @DeleteMapping("/api/partners/{partnerId}/employees/{employeeId}/workinghours/{workingHourId}")
    public void deleteWorkingHourFromEmployee(@PathVariable Long partnerId,
                                              @PathVariable Long employeeId,
                                              @PathVariable Long workingHourId) {
        Partner partner = partnerService.getElementById(partnerId);
        Employee employee = employeeService.getElementById(employeeId);
        if (!partner.getEmployees().contains(employee)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        WorkingHour workingHour = service.getElementById(workingHourId);
        if (!employee.getWorkingHours().contains(workingHour))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        employee.getWorkingHours().remove(workingHour);
        employeeService.updateElement(employee);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkingHour convertToEntity(WorkingHourDTO workingHourDTO) {
        return new WorkingHour(workingHourDTO);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkingHourDTO convertToDTO(WorkingHour workingHour) {
        WorkingHourDTO workingHourDTO = new WorkingHourDTO(workingHour);
        Link selfLink = linkTo(
                methodOn(WorkingHourController.class).getElementById(workingHour.getId())).withSelfRel();
        workingHourDTO.add(selfLink);
        return workingHourDTO;
    }

}
