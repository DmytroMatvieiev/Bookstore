package org.dmdev.bookstore.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("users")
@EnableR2dbcAuditing
public class User implements UserDetails {

    @Id
    private UUID id;
    private String username;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String email;
    private UserRole role;
    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column("updated_at")
    private LocalDateTime updatedAt;
    @Column("verification_token")
    private String verificationToken;
    @Column("is_verified")
    private boolean isVerified;
    @Column("reset_token")
    private String resetToken;
    private boolean enabled;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @ToString.Include(name = "password")
    private String maskPassword() {
        return "********";
    }
}
