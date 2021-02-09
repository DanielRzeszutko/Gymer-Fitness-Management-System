package com.gymer.api.address;

import com.gymer.api.address.entity.Address;
import com.gymer.api.address.entity.AddressDTO;
import com.gymer.api.partner.PartnerService;
import com.gymer.api.partner.entity.Partner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class AddressController {

    private final AddressService addressService;
    private final PartnerService partnerService;

    @Autowired
    public AddressController(AddressService addressService, PartnerService partnerService) {
        this.addressService = addressService;
        this.partnerService = partnerService;
    }

    @GetMapping("/api/addresses")
    public CollectionModel<AddressDTO> getAllAddresses(Sort sort) {
        List<Address> addresses = (List<Address>) addressService.getAllAddresses(sort);
        return CollectionModel.of(addresses.stream()
                .map(this::convertToAddressDTO)
                .collect(Collectors.toList()));
    }

    @GetMapping("/api/addresses/{addressId}")
    public AddressDTO getAddressById(@PathVariable Long addressId) {
        return convertToAddressDTO(addressService.getAddressById(addressId));
    }

    @GetMapping("/api/partners/{partnerId}/addresses/{addressId}")
    public AddressDTO getPartnerAddressById(@PathVariable Long partnerId, @PathVariable Long addressId) {
        Partner partner = partnerService.getPartnerById(partnerId);
        if (!partner.getAddress().getId().equals(addressId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return convertToAddressDTO(addressService.getAddressById(addressId));
    }

    @PutMapping("/api/partners/{partnerId}/addresses")
    public void addNewAddress(@RequestBody AddressDTO addressDTO, @PathVariable Long partnerId, @PathVariable Long addressId) {
        Partner partner = partnerService.getPartnerById(partnerId);
        if (!partner.getAddress().getId().equals(addressId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        Address address = convertToAddress(addressDTO);
        partner.setAddress(address);
        partnerService.updatePartner(partner);
    }

    @PutMapping("/api/partners/{partnerId}/addresses/{addressId}")
    public void updateAddress(@RequestBody AddressDTO addressDTO, @PathVariable Long partnerId, @PathVariable Long addressId) {
        Partner partner = partnerService.getPartnerById(partnerId);
        if (!partner.getAddress().getId().equals(addressId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        Address address = convertToAddress(addressDTO);
        addressService.updateAddress(address);
    }

    private Address convertToAddress(AddressDTO addressDTO) {
        return new Address(
                addressDTO.getId(),
                addressDTO.getCity(),
                addressDTO.getStreet(),
                addressDTO.getNumber(),
                addressDTO.getZipCode()
        );
    }

    private AddressDTO convertToAddressDTO(Address address) {
        return new AddressDTO(
                address.getId(),
                address.getCity(),
                address.getStreet(),
                address.getNumber(),
                address.getZipCode()
        );
    }

}
