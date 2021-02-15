package com.gymer.api.credential;

import com.gymer.api.credential.entity.Credential;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
interface CredentialRepository extends PagingAndSortingRepository<Credential, Long> {

    Page<Credential> findAllByEmailContainsOrPhoneNumberContains(String email, String phoneNumber, Pageable pageable);

}
