package com.gymer.api.credential.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
@AllArgsConstructor
public class CredentialDTO extends RepresentationModel<CredentialDTO> {

    private Long id;
    private String email;
    private String password;
    private String phoneNumber;
    private boolean active;

}
