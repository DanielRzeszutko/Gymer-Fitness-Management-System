package com.gymer.api.employee.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.hateoas.Links;

@Data
@AllArgsConstructor
public class EmployeeDTO {

    private Long id;

    private String firstName;
    private String lastName;
    private String description;
    private String image;
    private Links workingHours;

}
