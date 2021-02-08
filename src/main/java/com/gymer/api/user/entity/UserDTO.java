package com.gymer.api.user.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.hateoas.Link;

@Data
@AllArgsConstructor
public class UserDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private Link credential;

}
