package com.gymer.security.session.entity;

import com.gymer.api.credential.entity.Credential;
import com.gymer.api.credential.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ActiveAccount {

    Long id;
    Role role;
    Credential credential;

}
