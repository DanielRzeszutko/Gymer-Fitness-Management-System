package com.gymer.api.partner.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;

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
    private Links employees;
    private Links slots;
    private Links workingHours;

}
