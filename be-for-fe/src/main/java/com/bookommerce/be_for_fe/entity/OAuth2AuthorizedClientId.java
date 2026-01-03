package com.bookommerce.be_for_fe.entity;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OAuth2AuthorizedClientId implements Serializable {

    @Column(name = "client_registration_id", length = 100, nullable = false)
    String clientRegistrationId;

    @Column(name = "principal_name", length = 200, nullable = false)
    String principalName;
}
