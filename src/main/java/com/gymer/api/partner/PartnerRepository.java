package com.gymer.api.partner;

import com.gymer.api.credential.entity.Credential;
import com.gymer.api.partner.entity.Partner;
import com.gymer.api.user.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PartnerRepository extends CrudRepository<Partner, Long> {

    Optional<Partner> findByCredential(Credential credential);

}
