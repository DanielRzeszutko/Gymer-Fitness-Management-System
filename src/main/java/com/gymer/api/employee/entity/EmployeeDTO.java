package com.gymer.api.employee.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class EmployeeDTO extends RepresentationModel<EmployeeDTO> {

    private Long id;

    private String firstName;
    private String lastName;
    private String description;
    private String image;

    public EmployeeDTO(Employee employee) {
        if (employee.getId() != null) this.id = employee.getId();
        this.firstName = employee.getFirstName();
        this.lastName = employee.getLastName();
        this.description = employee.getDescription();
        this.image = employee.getImage();
    }

}
