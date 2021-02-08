package com.gymer.api.employee.entity;

import com.gymer.api.workinghours.entity.WorkingHour;
import com.sun.istack.NotNull;
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

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    private String description;
    private String image;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<WorkingHour> workingHours;

}
