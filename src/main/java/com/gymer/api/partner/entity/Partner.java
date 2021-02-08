package com.gymer.api.partner.entity;

import com.gymer.api.address.entity.Address;
import com.gymer.api.credential.entity.Credential;
import com.gymer.api.employee.entity.Employee;
import com.gymer.api.slot.entity.Slot;
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
public class Partner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String logo;
    private String description;
    private String website;

    @OneToOne(cascade = CascadeType.ALL)
    private Credential credential;

    @OneToOne(cascade = CascadeType.ALL)
    private Address address;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Employee> employees;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Slot> slots;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<WorkingHour> workingHours;

}
