package com.gymer.api.credential;

import com.gymer.api.credential.entity.Credential;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface CredentialRepository extends CrudRepository<Credential, Long> {
}
