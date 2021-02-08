package com.gymer.api.address;

import com.gymer.api.address.entity.Address;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface AddressRepository extends CrudRepository<Address, Long> {
}
