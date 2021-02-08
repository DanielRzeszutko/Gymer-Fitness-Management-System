package com.gymer.api.address;

import com.gymer.api.address.entity.Address;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
class AddressService {

    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public Iterable<Address> getAllAddresses() {
        return addressRepository.findAll();
    }

    public Address getAddressById(Long addressId) {
        return addressRepository.findById(addressId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public Address getPartnerAddressById(Long addressId) {
        return addressRepository.findById(addressId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public void updateAddress(Address address, Long addressId) {
        address.setId(addressId);
        addressRepository.save(address);
    }

}
