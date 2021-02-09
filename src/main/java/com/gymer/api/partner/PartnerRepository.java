package com.gymer.api.partner;

import com.gymer.api.partner.entity.Partner;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartnerRepository extends PagingAndSortingRepository<Partner, Long> {
}
