package com.gymer.api.partner;

import com.gymer.api.partner.entity.Partner;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartnerRepository extends CrudRepository<Partner, Long> {
}
