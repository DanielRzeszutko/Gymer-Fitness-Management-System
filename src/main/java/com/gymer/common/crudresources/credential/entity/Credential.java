package com.gymer.common.crudresources.credential.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@NoArgsConstructor
public class Credential {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String email;

    @NotNull
    @JsonIgnore
    private String password;

    @NotNull
    private String phoneNumber;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private Role role;

    @NotNull
    @Column(columnDefinition = "boolean default false")
    private boolean activated;

    private String verificationCode;

    private Timestamp registrationTime;

    public Credential(String email, String password, String phoneNumber, Role role,
                      boolean activated, Timestamp registrationTime) {
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.activated = activated;
        this.registrationTime = registrationTime;
    }

    public Credential(CredentialDTO credentialDTO) {
        this.id = credentialDTO.getId();
        this.email = credentialDTO.getEmail();
        this.password = credentialDTO.getPassword();
        this.phoneNumber = credentialDTO.getPhoneNumber();
        this.role = credentialDTO.getRole();
        this.activated = credentialDTO.isActivated();
        this.registrationTime = credentialDTO.getRegistrationTime();
    }

}
