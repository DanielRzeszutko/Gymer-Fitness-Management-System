package com.gymer.commonresources.authorize;

import com.gymer.commonresources.user.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface OAuth2AuthorizeRepository extends CrudRepository<OAuth2AuthorizeEntity, Long> {

    Optional<OAuth2AuthorizeEntity> findByUser(User user);

}
