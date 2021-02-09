package com.gymer.api.partner;

import com.gymer.api.credential.entity.Credential;
import com.gymer.api.partner.entity.Partner;
import com.gymer.api.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class PartnerService {

    private final PartnerRepository partnerRepository;

    @Autowired
    public PartnerService(PartnerRepository partnerRepository) {
        this.partnerRepository = partnerRepository;
    }

    public Iterable<Partner> getAllPartners() {
        return partnerRepository.findAll();
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

    public Optional<Partner> getByCredentials(Credential credential) {
        return partnerRepository.findByCredential(credential);
    }

}
