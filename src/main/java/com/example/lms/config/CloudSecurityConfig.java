package com.example.lms.config;

import com.example.lms.enums.Role;
import com.sap.cloud.security.xsuaa.XsuaaServiceConfiguration;
import com.sap.cloud.security.xsuaa.token.TokenAuthenticationConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile("cloud")
public class CloudSecurityConfig {

  public static final String MANAGER_ROLE = Role.MANAGER.name();

  @Autowired
  XsuaaServiceConfiguration xsuaaServiceConfiguration;

  @Bean
  @Order(1)
  public SecurityFilterChain actuatorSecurityFilterChain(HttpSecurity http) throws Exception {
    http
        .securityMatcher("/actuator/**")
        .authorizeHttpRequests(auth -> auth
            .anyRequest().hasRole(MANAGER_ROLE)
        )
        .httpBasic(Customizer.withDefaults())
        .csrf(AbstractHttpConfigurer::disable);

    return http.build();
  }

  @Bean
  public UserDetailsService userDetailsService(PasswordEncoder encoder) {
    UserDetails manager = User.builder()
        .username("manager")
        .password(encoder.encode("password"))
        .roles(MANAGER_ROLE)
        .build();

    return new InMemoryUserDetailsManager(manager);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  @Order(2)
  public SecurityFilterChain xsuaaSecurityFilterChain(HttpSecurity http) throws Exception {
    http
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .anyRequest().authenticated()
        )
        .oauth2ResourceServer(oauth2 -> oauth2
            .jwt(jwt -> jwt.jwtAuthenticationConverter(getJwtAuthoritiesConverter()))
        )
        .csrf(AbstractHttpConfigurer::disable);

    return http.build();
  }

  Converter<Jwt, AbstractAuthenticationToken> getJwtAuthoritiesConverter() {
    TokenAuthenticationConverter converter = new TokenAuthenticationConverter(xsuaaServiceConfiguration);
    converter.setLocalScopeAsAuthorities(true);
    return converter;
  }
}
