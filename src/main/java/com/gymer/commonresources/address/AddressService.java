package com.gymer.commonresources.address;

import com.gymer.commonresources.address.entity.Address;
import com.gymer.commonresources.common.service.AbstractRestApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AddressService extends AbstractRestApiService<Address, Long> {

    @Autowired
    public AddressService(AddressRepository repository) {
        super(repository);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Address> findAllContaining(Pageable pageable, String searchBy) {
        return ((AddressRepository) repository).findAllByCityContainsOrStreetContainsOrZipCodeContains(searchBy, searchBy, searchBy, pageable);
    }

}
