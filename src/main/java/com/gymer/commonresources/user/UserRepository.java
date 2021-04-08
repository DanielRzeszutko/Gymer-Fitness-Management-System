package com.gymer.commonresources.user;

import com.gymer.commonresources.credential.entity.Credential;
import com.gymer.commonresources.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Optional;

@Repository
interface UserRepository extends PagingAndSortingRepository<User, Long> {

    boolean existsByIdAndCredentialActivatedIsTrue(Long id);

    Page<User> findAllByCredentialActivatedIsTrue(Pageable pageable);

    Optional<User> findByIdAndCredentialActivatedIsTrue(Long id);

    Optional<User> findByCredential(Credential credential);

    Page<User> findAllByFirstNameContainsOrLastNameContainsAndCredentialActivatedIsTrue(String firstName, String lastName, Pageable pageable);

    Optional<User> findByCredentialEmailAndCredentialActivatedIsTrue(String email);

    Optional<User> findByProviderId(String providerId);

    Iterable<User> findAllByCredential_RegistrationTimeIsBetweenAndCredential_ActivatedIsFalse(Timestamp start, Timestamp end);

}
