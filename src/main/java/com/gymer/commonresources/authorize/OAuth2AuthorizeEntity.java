package com.gymer.commonresources.authorize;

import com.gymer.commonresources.user.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity(name = "oauth2_authorization_object")
public class OAuth2AuthorizeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private User user;

    @Column(columnDefinition = "text")
    private String key;

    public OAuth2AuthorizeEntity(User user, String key) {
        this.user = user;
        this.key = key;
    }

}
