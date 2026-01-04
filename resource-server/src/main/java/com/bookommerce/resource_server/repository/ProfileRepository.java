package com.bookommerce.resource_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bookommerce.resource_server.entity.Profile;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

}
