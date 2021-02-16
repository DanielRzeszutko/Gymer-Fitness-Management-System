package com.gymer.api.credential;

import com.gymer.api.common.service.AbstractRestApiService;
import com.gymer.api.credential.entity.Credential;
import com.gymer.api.credential.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class CredentialService extends AbstractRestApiService<Credential, Long> {

    @Autowired
    public CredentialService(CredentialRepository repository) {
        super(repository);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Credential> findAllContaining(Pageable pageable, String searchBy) {
        return ((CredentialRepository) repository).findAllByEmailContainsOrPhoneNumberContains(searchBy, searchBy, pageable);
    }

    /**
     * Service method returning Credential from database if exists, when one is no found new credential is created and returned
     */
    public Credential getCredentialFromEmailPhoneAndRoleOrCreateNewOne(String email, String phoneNumber, Role role) {
        Timestamp timestamp = new Timestamp(new java.util.Date().getTime());
        return ((CredentialRepository) repository).findByEmailAndPhoneNumberAndRole(email, phoneNumber, role).orElse(
                new Credential(email, null, phoneNumber, Role.GUEST, false, timestamp)
        );
    }

}
