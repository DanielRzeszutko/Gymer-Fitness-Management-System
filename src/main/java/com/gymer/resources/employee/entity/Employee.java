package com.gymer.resources.employee.entity;

import com.gymer.resources.workinghours.entity.WorkingHour;
import com.sun.istack.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    private String description;
    private String image;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<WorkingHour> workingHours;

    public Employee(String firstName, String lastName, String description, String image, List<WorkingHour> workingHours) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.description = description;
        this.image = image;
        this.workingHours = workingHours;
    }

    public Employee(EmployeeDTO employeeDTO) {
        if (employeeDTO.getId() != null) this.id = employeeDTO.getId();
        this.firstName = employeeDTO.getFirstName();
        this.lastName = employeeDTO.getLastName();
        this.description = employeeDTO.getDescription();
        this.image = employeeDTO.getImage();
    }

}
