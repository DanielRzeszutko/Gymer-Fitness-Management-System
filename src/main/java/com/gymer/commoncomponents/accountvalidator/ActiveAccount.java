package com.gymer.commoncomponents.accountvalidator;

import com.gymer.commonresources.credential.entity.Credential;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
class ActiveAccount {

    Credential credential;
    private Long id;

}
