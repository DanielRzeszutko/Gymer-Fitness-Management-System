package com.gymer.api.credential;

import com.gymer.api.credential.entity.Credential;
import com.gymer.api.credential.entity.Role;
import com.gymer.api.partner.entity.Partner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface CredentialRepository extends PagingAndSortingRepository<Credential, Long> {

    boolean existsByIdAndActivatedIsTrue(Long id);

    Page<Credential> findAllByActivatedIsTrue(Pageable pageable);

    Optional<Credential> findByIdAndActivatedIsTrue(Long id);

    Page<Credential> findAllByEmailContainsOrPhoneNumberContainsAndActivatedIsTrue(String email, String phoneNumber, Pageable pageable);

    Optional<Credential> findByEmailAndPhoneNumberAndRoleAndActivatedIsTrue(String email, String phoneNumber, Role role);

    boolean existsCredentialByEmailAndActivatedIsTrue(String email);

    Optional<Credential> getCredentialByEmail(String email);
}
