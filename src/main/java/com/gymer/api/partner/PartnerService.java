package com.gymer.api.partner;

import com.gymer.api.common.service.AbstractRestApiService;
import com.gymer.api.credential.entity.Credential;
import com.gymer.api.employee.entity.Employee;
import com.gymer.api.partner.entity.Partner;
import com.gymer.api.slot.entity.Slot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PartnerService extends AbstractRestApiService<Partner, Long> {

    @Autowired
    public PartnerService(PartnerRepository repository) {
        super(repository);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Partner> getAllElements(Pageable pageable) {
        return ((PartnerRepository) repository).findAllByCredentialActivatedIsTrue(pageable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Partner getElementById(Long elementId) {
        return ((PartnerRepository) repository).findByIdAndCredentialActivatedIsTrue(elementId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isElementExistById(Long elementId) {
        return ((PartnerRepository) repository).existsByIdAndCredentialActivatedIsTrue(elementId);
    }

    /**
     * Service method responsible for changing status of partner to deactivated
     */
    public void deletePartner(Partner partner) {
        partner.getCredential().setActivated(false);
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
        return ((PartnerRepository) repository).findBySlotsContainingAndCredentialActivatedIsTrue(slot).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    /**
     * Service method responsible for obtaining Partner by employee
     */
    public Partner findPartnerContainingEmployee(Employee employee) {
        return ((PartnerRepository) repository).findByEmployeesContainingAndCredentialActivatedIsTrue(employee).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Partner> findAllContaining(Pageable pageable, String searchBy) {
        return ((PartnerRepository) repository).findAllByNameContainsOrDescriptionContainsAndCredentialActivatedIsTrue(searchBy, searchBy, pageable);
    }

}
