package com.gymer.api.user;

import com.gymer.api.common.service.AbstractRestApiService;
import com.gymer.api.credential.entity.Credential;
import com.gymer.api.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService extends AbstractRestApiService<User, Long> {

    @Autowired
    public UserService(UserRepository repository) {
        super(repository);
    }

    public void deleteUser(User user) {
        user.getCredential().setActive(false);
        repository.save(user);
    }

    public Optional<User> getByCredentials(Credential credential) {
        return ((UserRepository) repository).findByCredential(credential);
    }

}
