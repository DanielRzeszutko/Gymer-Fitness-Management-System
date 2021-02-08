package com.gymer.api.address.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddressDTO {

    private Long id;
    private String city;
    private String street;
    private String number;
    private String zipCode;

}
