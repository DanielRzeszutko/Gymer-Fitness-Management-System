package com.gymer.api.address;

import com.gymer.api.address.entity.Address;
import com.gymer.api.common.service.AbstractRestApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddressService extends AbstractRestApiService<Address, Long> {

    @Autowired
    public AddressService(AddressRepository repository) {
        super(repository);
    }

}
