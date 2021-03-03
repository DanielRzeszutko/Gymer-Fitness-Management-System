package com.gymer.common.resources.address.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
@AllArgsConstructor
public class AddressDTO extends RepresentationModel<AddressDTO> {

    private Long id;
    private String city;
    private String street;
    private String number;
    private String zipCode;

    public AddressDTO(Address address) {
        this.id = address.getId();
        this.city = address.getCity();
        this.street = address.getStreet();
        this.number = address.getNumber();
        this.zipCode = address.getZipCode();
    }

}
