package com.gymer.api.employee.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.hateoas.Link;

import java.util.List;

@Data
@AllArgsConstructor
public class EmployeeDTO {

    private Long id;

    private String firstName;
    private String lastName;
    private String description;
    private String image;
    private List<Link> workingHours;

}
