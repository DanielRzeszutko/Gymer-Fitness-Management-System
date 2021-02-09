package com.gymer.api.employee.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.RepresentationModel;

@Data
@AllArgsConstructor
public class EmployeeDTO extends RepresentationModel<EmployeeDTO> {

    private Long id;

    private String firstName;
    private String lastName;
    private String description;
    private String image;

}
