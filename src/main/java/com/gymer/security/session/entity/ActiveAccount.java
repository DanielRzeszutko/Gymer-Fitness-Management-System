package com.gymer.security.session.entity;

import com.gymer.resources.credential.entity.Credential;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ActiveAccount {

    Credential credential;
    private Long id;

}
