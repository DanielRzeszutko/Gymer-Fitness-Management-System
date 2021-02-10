package com.gymer.api.credential.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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
    @Column(columnDefinition = "boolean default true")
    private boolean active;

    public Credential(String email, String password, String phoneNumber, Role role, boolean active) {
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.active = active;
    }

    public Credential(CredentialDTO credentialDTO) {
        this.id = credentialDTO.getId();
        this.email = credentialDTO.getEmail();
        this.password = credentialDTO.getPassword();
        this.phoneNumber = credentialDTO.getPhoneNumber();
        this.role = credentialDTO.getRole();
        this.active = credentialDTO.isActive();
    }

}
