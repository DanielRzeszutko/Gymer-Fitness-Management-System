package com.gymer.security.session.entity;

import com.gymer.api.credential.entity.Credential;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ActiveAccount {

    Long ownerId;
    Credential credential;

}
