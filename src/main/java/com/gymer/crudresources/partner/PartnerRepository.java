package com.gymer.crudresources.partner;

import com.gymer.crudresources.credential.entity.Credential;
import com.gymer.crudresources.employee.entity.Employee;
import com.gymer.crudresources.partner.entity.Partner;
import com.gymer.crudresources.slot.entity.Slot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface PartnerRepository extends PagingAndSortingRepository<Partner, Long> {

    boolean existsByIdAndCredentialActivatedIsTrue(Long id);

    Page<Partner> findAllByCredentialActivatedIsTrue(Pageable pageable);

    Optional<Partner> findByIdAndCredentialActivatedIsTrue(Long id);

    Page<Partner> findAllByNameContainsOrDescriptionContainsAndCredentialActivatedIsTrue(String name, String description, Pageable pageable);

    Optional<Partner> findBySlotsContainingAndCredentialActivatedIsTrue(Slot slot);

    Optional<Partner> findByCredential(Credential credential);

    Optional<Partner> findByCredentialEmail(String email);

    Optional<Partner> findByEmployeesContainingAndCredentialActivatedIsTrue(Employee employee);

}
