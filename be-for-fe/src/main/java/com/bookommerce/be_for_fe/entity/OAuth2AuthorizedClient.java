package com.bookommerce.be_for_fe.entity;

import java.time.Instant;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "oauth2_authorized_client")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OAuth2AuthorizedClient {

    @EmbeddedId
    OAuth2AuthorizedClientId id;

    @Column(name = "access_token_type", length = 100, nullable = false)
    String accessTokenType;

    @Lob
    @Column(name = "access_token_value", columnDefinition = "MEDIUMBLOB", nullable = false)
    byte[] accessTokenValue;

    @Column(name = "access_token_issued_at", nullable = false)
    Instant accessTokenIssuedAt;

    @Column(name = "access_token_expires_at", nullable = false)
    Instant accessTokenExpiresAt;

    @Column(name = "access_token_scopes", length = 1000)
    String accessTokenScopes;

    @Lob
    @Column(name = "refresh_token_value", columnDefinition = "MEDIUMBLOB")
    byte[] refreshTokenValue;

    @Column(name = "refresh_token_issued_at")
    Instant refreshTokenIssuedAt;

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    Instant createdAt;
}
