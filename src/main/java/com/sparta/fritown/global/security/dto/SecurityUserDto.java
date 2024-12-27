package com.sparta.fritown.global.security.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Builder
public class SecurityUserDto implements UserDetails {
    private Long id;
    private String email;
    private String role;
    private String nickname;

    public static class Builder {
        private Long id;
        private String email;
        private String role;
        private String nickname;

        public Builder id(Long id) {
            this.id = id;
            return this; // builder를 반환하는
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder role(String role) {
            this.role = role;
            return this;
        }

        public Builder nickname(String nickname) {
            this.nickname = nickname;
            return this;
        }

        public SecurityUserDto build() {
            return new SecurityUserDto(id, email, role, nickname);
        }
    }

    private SecurityUserDto(Long id, String email, String role, String nickname) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.nickname = nickname;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return null; // password is not needed for this case
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Long getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }
}