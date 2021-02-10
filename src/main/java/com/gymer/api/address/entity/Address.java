package com.gymer.api.address.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String city;
    private String street;
    private String number;
    private String zipCode;

    public Address(AddressDTO addressDTO) {
        this.id = addressDTO.getId();
        this.city = addressDTO.getCity();
        this.street = addressDTO.getStreet();
        this.number = addressDTO.getNumber();
        this.zipCode = addressDTO.getZipCode();
    }

}
