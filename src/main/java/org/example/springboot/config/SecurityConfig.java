package org.example.springboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http.authorizeHttpRequests(authorizeRequests -> authorizeRequests
                .requestMatchers("/home")
                .permitAll()
                .anyRequest()
                .authenticated()
            )
            .formLogin(form -> form
                .defaultSuccessUrl("/voice", true)
            );
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        var userDetailsManager = new InMemoryUserDetailsManager();
        var user = User.builder()
                       .username("lol")
                       .password("{noop}123")
                       .build();
        userDetailsManager.createUser(user);
        return userDetailsManager;
    }
}
