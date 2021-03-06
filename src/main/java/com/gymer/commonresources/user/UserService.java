package com.gymer.commonresources.user;

import com.gymer.commonresources.common.service.AbstractRestApiService;
import com.gymer.commonresources.credential.entity.Credential;
import com.gymer.commonresources.credential.entity.Role;
import com.gymer.commonresources.slot.SlotService;
import com.gymer.commonresources.slot.entity.Slot;
import com.gymer.commonresources.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.time.LocalDateTime;
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
    public Page<User> getAllElements(Pageable pageable) {
        return ((UserRepository) repository).findAllByCredentialActivatedIsTrue(pageable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getElementById(Long elementId) {
        return ((UserRepository) repository).findByIdAndCredentialActivatedIsTrue(elementId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isElementExistById(Long elementId) {
        return ((UserRepository) repository).existsByIdAndCredentialActivatedIsTrue(elementId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<User> findAllContaining(Pageable pageable, String searchBy) {
        return ((UserRepository) repository).findAllByFirstNameContainsOrLastNameContainsAndCredentialActivatedIsTrue(searchBy, searchBy, pageable);
    }

    /**
     * Service method responsible for changing status of user to deactivated
     */
    public void deleteUser(User user) {
        user.getCredential().setActivated(false);
        repository.save(user);
    }

    /**
     * Service method responsible for obtaining user by credentials
     */
    public User getByCredentials(Credential credential) {
        return ((UserRepository) repository).findByCredential(credential).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    /**
     * Service method that returns all users connected with specific slot in database
     */
    public Page<User> findAllUsersSubmittedToSlot(Pageable pageable, Long slotId) {
        Slot oldSlot = slotService.getElementById(slotId);
        return new PageImpl<>(oldSlot.getUsers(), pageable, oldSlot.getUsers().size());
    }

    /**
     * Service method that returns true if email is existing in database and Role.USER is set up with this account
     * In another case when Role.GUEST is only in database new record is created
     */
    public boolean isUserExistsByEmail(String email) {
        Optional<User> user = ((UserRepository) repository).findByCredentialEmailAndCredentialActivatedIsTrue(email);
        return user.isPresent() && user.get().getCredential().getRole().equals(Role.USER);
    }

    /**
     * Service method that returns user or null when user is connected with provider account
     */
    public User findByProviderId(String providerId) {
        return ((UserRepository) repository).findByProviderId(providerId).orElse(null);
    }

    /**
     * Service method that returns all users when registration time is between 10 and 15 minutes old
     */

    public Iterable<User> findAllGuestOlderThan10MinutesYoungerThan15Minutes() {
        LocalDateTime now = LocalDateTime.now();
        Timestamp fifteenMinutesAgo = Timestamp.valueOf(now.minusMinutes(15));
        Timestamp tenMinutesAgo = Timestamp.valueOf(now.minusMinutes(10));
        return ((UserRepository) repository).findAllByCredential_RegistrationTimeIsBetweenAndCredential_ActivatedIsFalse(fifteenMinutesAgo, tenMinutesAgo);
    }

}
