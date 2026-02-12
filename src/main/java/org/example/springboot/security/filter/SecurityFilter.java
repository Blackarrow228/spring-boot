package org.example.springboot.security.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.entity.User;
import org.example.springboot.service.SocialAppService;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityFilter {

    private final SocialAppService socialAppService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
            .csrf(csrf -> csrf
                      .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                      .ignoringRequestMatchers("/logout")
                 )
            .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                                       .requestMatchers("/login", "/error", "/webjars/**")
                                       .permitAll() // Разрешаем доступ к этим маршрутам всем
                                       .requestMatchers("/h2-console/*")
                                       .permitAll()
                                       .requestMatchers("/admin/**")
                                       .hasRole("ADMIN")
                                       .anyRequest()
                                       .authenticated()
                                  )
            .exceptionHandling(ex -> ex
                                   .authenticationEntryPoint((request, response, authException) -> {
                                       String acceptHeader = request.getHeader("Accept");
                                       if (acceptHeader != null && acceptHeader.contains("application/json")) {
                                           response.setStatus(HttpStatus.UNAUTHORIZED.value());
                                           response.setContentType("application/json");
                                           response.getWriter()
                                                   .write("{\"error\":\"Authentication required\"}");
                                       } else {
                                           response.sendRedirect("/oauth2/authorization/github");
                                       }
                                   })
                                   .accessDeniedHandler((request, response, accessDeniedException) -> {
                                       log.warn("Доступ запрещен: {}", accessDeniedException.getMessage());
                                       response.sendRedirect("/error/403");
                                   })
                              )
            .oauth2Login(oauth2 -> oauth2
                             .loginPage("/")
                             .userInfoEndpoint(userInfo -> userInfo
                                 .userService(socialAppService))
                             .defaultSuccessUrl("/user", true)
                             .failureUrl("/error/401")
                        )
            .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            if (authentication != null && authentication.getPrincipal() instanceof User user) {
                                log.info("User вышел: {}", user.getLogin());
                            }
                            response.sendRedirect("/user");
                        })
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID", "XSRF-TOKEN")
                   );
        return http.build();
    }
}
