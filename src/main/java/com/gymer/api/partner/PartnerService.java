package com.gymer.api.partner;

import com.gymer.api.common.service.AbstractRestApiService;
import com.gymer.api.credential.entity.Credential;
import com.gymer.api.partner.entity.Partner;
import com.gymer.api.slot.entity.Slot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class PartnerService extends AbstractRestApiService<Partner, Long> {

    @Autowired
    public PartnerService(PartnerRepository repository) {
        super(repository);
    }

    public void deletePartner(Partner partner) {
        partner.getCredential().setActive(false);
        repository.save(partner);
    }

    public Iterable<Partner> findAllContaining(String name) {
        return ((PartnerRepository) repository).findAllByAddress_CityContainsOrAddress_StreetContainsOrAddress_ZipCodeContains(name, name, name);
    }

    public Partner findPartnerContainingSlot(Slot slot) {
        return ((PartnerRepository) repository).findBySlotsContaining(slot).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public Optional<Partner> getByCredentials(Credential credential) {
        return ((PartnerRepository) repository).findByCredential(credential);
    }

}
