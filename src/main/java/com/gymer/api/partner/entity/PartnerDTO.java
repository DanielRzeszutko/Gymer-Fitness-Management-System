package com.gymer.api.partner.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class PartnerDTO extends RepresentationModel<PartnerDTO> {

    private Long id;
    private String name;
    private String logo;
    private String description;
    private String website;

    public PartnerDTO(Partner partner) {
        this.id = partner.getId();
        this.name = partner.getName();
        this.logo = partner.getLogo();
        this.description = partner.getDescription();
        this.website = partner.getWebsite();
    }

}
