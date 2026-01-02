package com.bookommerce.auth_server.entity;

import java.util.Set;

import com.bookommerce.auth_server.constant.Operations;
import com.bookommerce.auth_server.constant.Resources;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "permissions", uniqueConstraints = @UniqueConstraint(columnNames = { "operation", "resource" }))
public class Permission {

    static final String DELIMITER = ":";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    Operations operation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    Resources resource;

    @ManyToMany(mappedBy = Role_.PERMISSIONS, fetch = FetchType.LAZY)
    @JsonIgnore
    Set<Role> roles;

    public String getName() {
        return operation.name() + DELIMITER + resource.name();
    }
}