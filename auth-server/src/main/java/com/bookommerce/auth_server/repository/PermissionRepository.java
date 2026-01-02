package com.bookommerce.auth_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bookommerce.auth_server.entity.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

}
