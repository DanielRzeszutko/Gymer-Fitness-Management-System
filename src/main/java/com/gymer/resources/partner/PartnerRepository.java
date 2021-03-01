package com.gymer.resources.partner;

import com.gymer.resources.credential.entity.Credential;
import com.gymer.resources.employee.entity.Employee;
import com.gymer.resources.partner.entity.Partner;
import com.gymer.resources.slot.entity.Slot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PartnerRepository extends PagingAndSortingRepository<Partner, Long> {

    boolean existsByIdAndCredentialActivatedIsTrue(Long id);

    Page<Partner> findAllByCredentialActivatedIsTrue(Pageable pageable);

    Optional<Partner> findByIdAndCredentialActivatedIsTrue(Long id);

    Page<Partner> findAllByNameContainsOrDescriptionContainsAndCredentialActivatedIsTrue(String name, String description, Pageable pageable);

    Optional<Partner> findBySlotsContainingAndCredentialActivatedIsTrue(Slot slot);

    Optional<Partner> findByCredential(Credential credential);

    Optional<Partner> findByEmployeesContainingAndCredentialActivatedIsTrue(Employee employee);

}