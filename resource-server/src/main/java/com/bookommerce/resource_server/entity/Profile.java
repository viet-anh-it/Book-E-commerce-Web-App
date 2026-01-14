package com.bookommerce.resource_server.entity;

import java.time.LocalDate;

import com.bookommerce.resource_server.constant.Genders;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Table(name = "profiles")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    String lastName;
    String firstName;

    @Column(unique = true, nullable = false)
    String email;

    String phone;

    @Enumerated(EnumType.STRING)
    Genders gender;

    LocalDate dob;

    String avatarUrlPath;
}
