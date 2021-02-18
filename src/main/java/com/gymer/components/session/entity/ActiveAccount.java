package com.gymer.components.session.entity;

import com.gymer.api.credential.entity.Credential;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ActiveAccount {

    Credential credential;
    private Long id;

}
