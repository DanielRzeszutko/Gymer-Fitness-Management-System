package com.gymer.common.resources.partner;

import com.gymer.common.resources.common.service.AbstractRestApiService;
import com.gymer.common.resources.credential.entity.Credential;
import com.gymer.common.resources.credential.entity.Role;
import com.gymer.common.resources.employee.entity.Employee;
import com.gymer.common.resources.partner.entity.Partner;
import com.gymer.common.resources.slot.entity.Slot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    /**
     * Service method that returns true if email is existing in database and Role.PARTNER is set up with this account
     * In another case when Role.GUEST is only in database new record is created
     */
    public boolean isUserExistsByEmail(String email) {
        Optional<Partner> partner = ((PartnerRepository) repository).findByCredentialEmail(email);
        return partner.isPresent() && partner.get().getCredential().getRole().equals(Role.PARTNER);
    }

}
