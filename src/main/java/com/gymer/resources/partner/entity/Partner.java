package com.gymer.resources.partner.entity;

import com.gymer.resources.address.entity.Address;
import com.gymer.resources.credential.entity.Credential;
import com.gymer.resources.employee.entity.Employee;
import com.gymer.resources.slot.entity.Slot;
import com.gymer.resources.workinghours.entity.WorkingHour;
import com.sun.istack.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class Partner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    private String logo;
    private String image;
    private String description;
    private String website;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    private Credential credential;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    private Address address;

    @NotNull
    @OneToMany(cascade = CascadeType.ALL)
    private List<Employee> employees;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Slot> slots;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<WorkingHour> workingHours;

    public Partner(String name, String logo, String image, String description, String website,
                   Credential credential, Address address, List<Employee> employees,
                   List<Slot> slots, List<WorkingHour> workingHours) {
        this.name = name;
        this.logo = logo;
        this.image = image;
        this.description = description;
        this.website = website;
        this.credential = credential;
        this.address = address;
        this.employees = employees;
        this.slots = slots;
        this.workingHours = workingHours;
    }

    public Partner(PartnerDTO partnerDTO) {
        this.id = partnerDTO.getId();
        this.name = partnerDTO.getName();
        this.logo = partnerDTO.getLogo();
        this.image = partnerDTO.getImage();
        this.description = partnerDTO.getDescription();
        this.website = partnerDTO.getWebsite();
    }

}
