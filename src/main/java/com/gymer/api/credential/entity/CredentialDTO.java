package com.gymer.api.credential.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class CredentialDTO extends RepresentationModel<CredentialDTO> {

    private Long id;
    private String email;

    @JsonIgnore
    private String password;
    private String phoneNumber;
    private Role role;
    private boolean active;
    private Timestamp registrationTime;

    public CredentialDTO(Credential credential) {
        this.id = credential.getId();
        this.email = credential.getEmail();
        this.password = credential.getPassword();
        this.phoneNumber = credential.getPhoneNumber();
        this.role = credential.getRole();
        this.active = credential.isActive();
        this.registrationTime = credential.getRegistrationTime();
    }

}
