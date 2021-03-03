package com.gymer.accountsession;

import com.gymer.common.resources.credential.entity.Credential;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
class ActiveAccount {

    Credential credential;
    private Long id;

}
