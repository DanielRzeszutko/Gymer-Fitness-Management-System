package com.gymer.common.crudresources.user;

import com.gymer.common.crudresources.credential.entity.Credential;
import com.gymer.common.crudresources.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface UserRepository extends PagingAndSortingRepository<User, Long> {

    boolean existsByIdAndCredentialActivatedIsTrue(Long id);

    Page<User> findAllByCredentialActivatedIsTrue(Pageable pageable);

    Optional<User> findByIdAndCredentialActivatedIsTrue(Long id);

    Optional<User> findByCredential(Credential credential);

    Page<User> findAllByFirstNameContainsOrLastNameContainsAndCredentialActivatedIsTrue(String firstName, String lastName, Pageable pageable);

    Optional<User> findByCredentialEmailAndCredentialActivatedIsTrue(String email);

}