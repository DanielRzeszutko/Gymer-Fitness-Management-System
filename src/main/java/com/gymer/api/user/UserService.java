package com.gymer.api.user;

import com.gymer.api.common.service.AbstractRestApiService;
import com.gymer.api.credential.entity.Credential;
import com.gymer.api.slot.SlotService;
import com.gymer.api.slot.entity.Slot;
import com.gymer.api.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService extends AbstractRestApiService<User, Long> {

    private final SlotService slotService;

    @Autowired
    public UserService(UserRepository repository, SlotService slotService) {
        super(repository);
        this.slotService = slotService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<User> findAllContaining(Sort sort, String searchBy) {
        return ((UserRepository) repository).findAllByFirstNameContainsOrLastNameContains(searchBy, searchBy, sort);
    }

    /**
     * Service method responsible for changing status of user to deactivated
     */
    public void deleteUser(User user) {
        user.getCredential().setActive(false);
        repository.save(user);
    }

    /**
     * Service method responsible for obtaining user by credentials
     */
    public Optional<User> getByCredentials(Credential credential) {
        return ((UserRepository) repository).findByCredential(credential);
    }

    public Iterable<User> findAllUsersSubmittedToSlot(Long slotId) {
        Slot oldSlot = slotService.getElementById(slotId);
        return oldSlot.getUsers();
    }

}
