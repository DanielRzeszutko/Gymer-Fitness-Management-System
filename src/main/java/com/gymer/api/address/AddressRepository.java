package com.gymer.api.address;

import com.gymer.api.address.entity.Address;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
interface AddressRepository extends PagingAndSortingRepository<Address, Long> {

    Page<Address> findAllByCityContainsOrStreetContainsOrZipCodeContains(String city, String street, String zipCode, Pageable pageable);

}
