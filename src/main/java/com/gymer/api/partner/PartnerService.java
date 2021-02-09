package com.gymer.api.partner;

import com.gymer.api.partner.entity.Partner;
import com.gymer.api.slot.entity.Slot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PartnerService {

    private final PartnerRepository partnerRepository;

    @Autowired
    public PartnerService(PartnerRepository partnerRepository) {
        this.partnerRepository = partnerRepository;
    }

    public Iterable<Partner> getAllPartnersAndSort(Sort sort) {
        return partnerRepository.findAll(sort);
    }

    public Partner getPartnerById(Long partnerId) {
        return partnerRepository.findById(partnerId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public void updatePartner(Partner partner) {
        partnerRepository.save(partner);
    }

    public void deletePartner(Partner partner) {
        partner.getCredential().setActive(false);
        partnerRepository.save(partner);
    }

    public Iterable<Partner> findAllContaining(String name, Sort sort) {
        return partnerRepository.findAllByAddress_CityContainsOrAddress_StreetContainsOrAddress_ZipCodeContains(name, name, name, sort);
    }

    public Partner findPartnerContainingSlot(Slot slot) {
        return partnerRepository.findBySlotsContaining(slot).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

}
