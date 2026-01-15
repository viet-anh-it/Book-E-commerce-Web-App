package com.bookommerce.auth_server.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bookommerce.auth_server.entity.AccountActivationToken;

@Repository
public interface AccountActivationTokenRepository extends JpaRepository<AccountActivationToken, Long> {

    Optional<AccountActivationToken> findByTokenValue(String token);
}
