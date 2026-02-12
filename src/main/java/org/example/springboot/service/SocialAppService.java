package org.example.springboot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.entity.User;
import org.example.springboot.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SocialAppService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        User user = extractUser(attributes);
        user = saveOrUpdateUser(user);
        user.setAttributes(attributes);
        log.info("User зашел: {}, роль: {}",
                 user.getLogin(), user.getRole()
                );
        return user;
    }

    private User extractUser(Map<String, Object> attributes) {
        String providerId = String.valueOf(attributes.get("id"));
        String login = (String) attributes.get("login");
        String name = (String) attributes.get("name");

        if (name == null || name.isEmpty()) {
            name = login;
        }

        User user = new User();
        user.setProviderId(providerId);
        user.setName(name);
        user.setLogin(login);

        return user;
    }

    private User saveOrUpdateUser(User user) {
        Optional<User> existingUser = userRepository.findByProviderId(user.getProviderId());

        if (existingUser.isPresent()) {
            User savedUser = existingUser.get();
            savedUser.setName(user.getName());
            savedUser.setLogin(user.getLogin());
            checkAndAssignAdminRole(savedUser);
            log.debug("Обновление существующего user: {}", savedUser.getLogin());
            return userRepository.save(savedUser);
        } else {
            checkAndAssignAdminRole(user);
            log.info("Создание нового user: {}", user.getLogin());
            return userRepository.save(user);
        }
    }

    private void checkAndAssignAdminRole(User user) {
        if (isAdmin(user.getName())) {
            if (!user.getRole().equals("ROLE_ADMIN")) {
                user.setRole("ROLE_ADMIN");
                user.setAdmin(true);
                log.info("роль ADMIN назначена пользователю: {}", user.getLogin());
            }
        }
    }

    private boolean isAdmin(String name) {
        return name != null && (
            name.equals("Blackarrow228"));
    }
}
