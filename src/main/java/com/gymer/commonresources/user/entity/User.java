package com.gymer.commonresources.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gymer.commonresources.credential.entity.Credential;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity(name = "user_account")
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    private String providerId;

    private String firstName;
    private String lastName;

    @OneToOne(cascade = CascadeType.ALL)
    private Credential credential;

    public User(String firstName, String lastName, Credential credential) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.credential = credential;
    }

    public User(UserDTO userDTO) {
        this.id = userDTO.getId();
        this.firstName = userDTO.getFirstName();
        this.lastName = userDTO.getLastName();
    }

}
