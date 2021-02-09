package com.gymer.api.partner.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.RepresentationModel;

@Data
@AllArgsConstructor
public class PartnerDTO extends RepresentationModel<PartnerDTO> {

    private Long id;
    private String name;
    private String logo;
    private String description;
    private String website;

}
