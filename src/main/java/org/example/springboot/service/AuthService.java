package org.example.springboot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.dto.request.LoginRequest;
import org.example.springboot.dto.request.RegistrationRequest;
import org.example.springboot.dto.response.LoginResponse;
import org.example.springboot.dto.response.RegistrationResponse;
import org.example.springboot.entity.User;
import org.example.springboot.entity.component.RoleEnum;
import org.example.springboot.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

import static org.example.springboot.mapper.UserMapper.mapToUserDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final JWTUtils jwtUtils;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager manager;
    private final LoginUtils loginUtils;

    @Transactional
    public RegistrationResponse registration(RegistrationRequest request) {
        RegistrationResponse response = new RegistrationResponse();
        try {
            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRole(RoleEnum.valueOf(request.getRole()));
            User savedUser = userRepository.save(user);
            response.setUserDto(mapToUserDto(savedUser));
            response.setStatusCode(200);
            response.setMessage("Успешная регистрация");
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setError(e.getMessage());
        }
        return response;
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {

        LoginResponse response = new LoginResponse();

        try {
            User user = userRepository.findUserByUsername(request.getUsername())
                                      .orElseThrow(() -> {
                                          log.warn("User not found: {}", request.getUsername());
                                          return new UsernameNotFoundException("Пользователь не найден");
                                      });
            if (!user.isAccountNonLocked()) {
                createErrorMsg(response, 403, "Аккаунт заблокирован", "Обратитесь к администратору для разблокировки");
                return response;
            }
            manager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword(),
                                                                         user.getAuthorities()
            ));

            loginUtils.resetFailedAttempts(user);

            var jwt = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRefreshToken(refreshToken);
            response.setExpirationTime("24 часа");
            response.setMessage("Успешный вход");
            response.setUsername(user.getUsername());
        } catch (BadCredentialsException e) {
            loginUtils.handleFailedLogin(request.getUsername());
            createErrorMsg(response, 401, "Неверный логин или пароль", "Повторите попытку");
        } catch (LockedException e) {
            createErrorMsg(response, 403, "Аккаунт заблокирован", "Обратитесь к администратору для разблокировки");
        } catch (UsernameNotFoundException e) {
            createErrorMsg(response, 404, "Пользователь не найден", "Проверьте логин или зарегистрируйтесь");
        } catch (Exception e) {
            log.error("Login error", e);
            createErrorMsg(response, 500, "Внутренняя ошибка сервера", "Попробуйте позже");
        }
        return response;
    }

    private void createErrorMsg(LoginResponse response, int statusCode, String error,
                                String message) {
        response.setStatusCode(statusCode);
        response.setError(error);
        response.setMessage(message);
    }
}
