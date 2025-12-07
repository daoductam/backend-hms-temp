package com.hms.user.UserMS.entity;

import com.hms.user.UserMS.dto.Roles;
import com.hms.user.UserMS.dto.UserDTO;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;

    @Column(unique = true)
    String email;
    String password;

//    @Enumerated(value = EnumType.STRING)
    Roles role;
    Long profileId;
    @Column(updatable = false)
    LocalDateTime createdAt;
//    @Column(nullable = false)
    LocalDateTime updatedAt;

    public UserDTO toDTO() {
        return UserDTO.builder()
                .id(this.id)
                .name(this.name)
                .email(this.email)
                .password(this.password)
                .role(this.role)
                .profileId(this.profileId)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    @PrePersist
    protected void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
