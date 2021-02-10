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

    /**
     * Service method responsible for changing status of partner to deactivated
     */
    public void deletePartner(Partner partner) {
        partner.getCredential().setActive(false);
        repository.save(partner);
    }

    /**
     * Service method responsible for obtaining Partner by credential
     */
    public Partner getByCredentials(Credential credential) {
        return ((PartnerRepository) repository).findByCredential(credential).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    /**
     * Service method responsible for obtaining Partner by slot
     */
    public Partner findPartnerContainingSlot(Slot slot) {
        return ((PartnerRepository) repository).findBySlotsContaining(slot).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<Partner> findAllContaining(Sort sort, String searchBy) {
        return ((PartnerRepository) repository).findAllByNameContainsOrDescriptionContains(searchBy, searchBy, sort);
    }

}
