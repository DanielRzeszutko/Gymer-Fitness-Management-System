package com.gymer.api.address;

import com.gymer.api.address.entity.Address;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
public class AddressController {

    @GetMapping("/api/addresses")
    public Iterable<Address> getAllAddresses() {
        return Collections.emptyList();
    }

    @GetMapping("/api/addresses/{addressId}")
    public Address getAddressById(@PathVariable Long addressId) {
        return null;
    }

    @GetMapping("/api/partners/{partnerId}/addresses/{addressId}")
    public Address getPartnerAddressById(@PathVariable Long partnerId, @PathVariable Long addressId) {
        return null;
    }

}
