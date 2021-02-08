package com.gymer.api.partner.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.hateoas.Link;

import java.util.List;

@Data
@AllArgsConstructor
public class PartnerDTO {

    private Long id;
    private String name;
    private String logo;
    private String description;
    private String website;
    private Link credential;
    private Link address;
    private List<Link> employees;
    private List<Link> slots;
    private List<Link> workingHours;

}
