package com.gymer.api.credential;

import com.gymer.api.credential.entity.Credential;
import com.gymer.api.credential.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface CredentialRepository extends PagingAndSortingRepository<Credential, Long> {

    Page<Credential> findAllByEmailContainsOrPhoneNumberContains(String email, String phoneNumber, Pageable pageable);

    Optional<Credential> findByEmailAndPhoneNumberAndRole(String email, String phoneNumber, Role role);

    boolean existsCredentialByEmail(String email);

    Optional<Credential> getCredentialByEmail(String email);

    Optional<Credential> findCredentialByVerificationCode(String code);
}
