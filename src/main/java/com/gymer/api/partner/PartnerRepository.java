package com.gymer.api.partner;

import com.gymer.api.credential.entity.Credential;
import com.gymer.api.partner.entity.Partner;
import com.gymer.api.slot.entity.Slot;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PartnerRepository extends PagingAndSortingRepository<Partner, Long> {

    Iterable<Partner> findAllByAddress_CityContainsOrAddress_StreetContainsOrAddress_ZipCodeContains(String addressName, String cityName, String zipCode, Sort sort);

    Optional<Partner> findBySlotsContaining(Slot slot);

    Optional<Partner> findByCredential(Credential credential);

}
