package com.gymer.api.user;

import com.gymer.api.credential.entity.Credential;
import com.gymer.api.credential.entity.Role;
import com.gymer.api.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Long> {

    Optional<User> findByCredential(Credential credential);

    Page<User> findAllByFirstNameContainsOrLastNameContains(String firstName, String lastName, Pageable pageable);

    Page<User> findByCredentialEmailAndCredentialRole(String email, Role role);

    Optional<User> findByCredentialEmail(String email);

}
