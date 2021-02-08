package com.gymer.api.employee.entity;

import com.gymer.api.workinghours.entity.WorkingHour;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String description;
    private String image;

    @ManyToMany
    private List<WorkingHour> workingHours;

}
