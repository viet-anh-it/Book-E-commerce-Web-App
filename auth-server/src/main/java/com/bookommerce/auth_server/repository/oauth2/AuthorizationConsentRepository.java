package com.bookommerce.auth_server.repository.oauth2;

import java.util.Optional;

import com.bookommerce.auth_server.entity.oauth2.AuthorizationConsent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// @formatter:off
@Repository
public interface AuthorizationConsentRepository extends JpaRepository<AuthorizationConsent, AuthorizationConsent.AuthorizationConsentId> {
	Optional<AuthorizationConsent> findByRegisteredClientIdAndPrincipalName(String registeredClientId, String principalName);
	void deleteByRegisteredClientIdAndPrincipalName(String registeredClientId, String principalName);
}
