package com.gymer.api.user;

import com.gymer.api.credential.entity.Credential;
import com.gymer.api.user.entity.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Long> {

    Optional<User> findByCredential(Credential credential);

    Iterable<User> findAllByFirstNameContainsOrLastNameContains(String firstName, String lastName, Sort sort);

}
