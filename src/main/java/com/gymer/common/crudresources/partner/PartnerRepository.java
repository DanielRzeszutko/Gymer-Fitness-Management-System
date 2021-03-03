package com.gymer.common.crudresources.partner;

import com.gymer.common.crudresources.credential.entity.Credential;
import com.gymer.common.crudresources.employee.entity.Employee;
import com.gymer.common.crudresources.partner.entity.Partner;
import com.gymer.common.crudresources.slot.entity.Slot;
import com.gymer.common.crudresources.user.entity.User;
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
