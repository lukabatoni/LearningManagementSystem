package com.example.lms.config;

import com.example.lms.enums.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class UserConfig {

  public static final String MANAGER_ROLE = Role.MANAGER.name();
  public static final String USER_ROLE = Role.USER.name();

  @Bean
  public UserDetailsService userDetailsService(PasswordEncoder encoder) {
    UserDetails user = User.builder()
        .username("user")
        .password(encoder.encode("password"))
        .roles(USER_ROLE)
        .build();

    UserDetails manager = User.builder()
        .username("manager")
        .password(encoder.encode("password"))
        .roles(MANAGER_ROLE)
        .build();

    return new InMemoryUserDetailsManager(user, manager);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }


}
