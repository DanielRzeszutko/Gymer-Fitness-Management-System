package com.gymer.api.credential;

import com.gymer.api.common.service.AbstractRestApiService;
import com.gymer.api.credential.entity.Credential;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CredentialService extends AbstractRestApiService<Credential, Long> {

    @Autowired
    public CredentialService(CredentialRepository repository) {
        super(repository);
    }

}
