package org.example.springboot;

import org.example.springboot.controller.UserController;
import org.example.springboot.dto.OrderDto;
import org.example.springboot.enums.OrderStatus;
import org.example.springboot.exception.NotFoundException;
import org.example.springboot.request.UserRequest;
import org.example.springboot.response.UserResponse;
import org.example.springboot.service.UserService;
import org.example.springboot.view.Views;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTests {
    private static final String ERROR_MESSAGE = "user с id: %s не найден";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
    }

    @Test
    @DisplayName("POST - успешное создание user")
    void createUser_201_test() throws Exception {
        UserRequest request = new UserRequest();
        request.setName("abc");
        request.setEmail("bac@fjal");

        when(userService.createUser(any(UserRequest.class))).thenReturn(userId);

        mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value(userId.toString()))
                .andDo(print());

        verify(userService, times(1)).createUser(any(UserRequest.class));
    }

    @Test
    @DisplayName("PATCH - успешное обновление user")
    void patchUser_200_test() throws Exception {
        UserRequest request = new UserRequest();
        request.setName("abc");
        request.setEmail("bac@fjal");

        when(userService.patchUser(any(UserRequest.class), eq(userId))).thenReturn(userId);

        mockMvc.perform(patch("/api/v1/user/" + userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("user с id: " + userId.toString() + " обновлен"))
                .andDo(print());

        verify(userService, times(1)).patchUser(any(UserRequest.class), eq(userId));
    }

    @Test
    @DisplayName("DELETE - успешное удаление user")
    void deleteUser_200_test() throws Exception {

        when(userService.deleteUser(eq(userId))).thenReturn(userId);

        mockMvc.perform(delete("/api/v1/user/delete/" + userId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("user с id: " + userId.toString() + " удален"))
                .andDo(print());

        verify(userService, times(1)).deleteUser(eq(userId));
    }

    @Test
    @DisplayName("GET - успешное получение всех user")
    void getAllUsers_200_test() throws Exception {
        List<UserResponse> responses = new ArrayList<>();
        responses.add(UserResponse.builder().name("abc").email("f@fli").build());
        responses.add(UserResponse.builder().name("acb").email("a@gli").build());
        responses.add(UserResponse.builder().name("bca").email("b@hli").build());

        when(userService.getAllUser()).thenReturn(responses);

        mockMvc.perform(get("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writerWithView(Views.UserSummary.class).writeValueAsString(responses)))
                .andDo(print());

        verify(userService, times(1)).getAllUser();
    }

    @Test
    @DisplayName("GET - успешное получение user со списком order")
    void getUser_200_test() throws Exception {
        List<OrderDto> orders =
                List.of(OrderDto.builder().item("песок").amount(BigDecimal.valueOf(2)).status(OrderStatus.PROCESS).build(),
                OrderDto.builder().item("щебень").amount(BigDecimal.valueOf(3)).status(OrderStatus.DELIVERY).build(),
                OrderDto.builder().item("арматура").amount(BigDecimal.valueOf(5)).status(OrderStatus.COMPLETE).build());
        UserResponse response = UserResponse.builder().name("abc").email("f@fli").orders(orders).build();

        when(userService.getUser(eq(userId))).thenReturn(response);

        mockMvc.perform(get("/api/v1/user/" + userId.toString() + "/details")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writerWithView(Views.UserDetails.class).writeValueAsString(response)))
                .andDo(print());

        verify(userService, times(1)).getUser(eq(userId));
    }

    @Test
    @DisplayName("POST - BadRequest name is null")
    void createUser_400_nameIsNull_test() throws Exception {
        UserRequest request = new UserRequest();
        request.setEmail("bac@fjal");

        when(userService.createUser(any(UserRequest.class))).thenReturn(userId);

        mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Значение null в name укажите имя"))
                .andDo(print());
    }

    @Test
    @DisplayName("POST - BadRequest email is null")
    void createUser_400_emailIsNull_test() throws Exception {
        UserRequest request = new UserRequest();
        request.setName("abc");

        when(userService.createUser(any(UserRequest.class))).thenReturn(userId);

        mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Значение null в email укажите email"))
                .andDo(print());
    }

    @Test
    @DisplayName("POST - BadRequest not in email format")
    void createUser_400_notInEmailFormat_test() throws Exception {
        UserRequest request = new UserRequest();
        request.setName("abc");
        request.setEmail("bac");

        when(userService.createUser(any(UserRequest.class))).thenReturn(userId);

        mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Значение bac в email неверный формат email"))
                .andDo(print());
    }

    @Test
    @DisplayName("GET - NotFound userList is empty")
    void getAllUsers_404_userListIsEmpty_test() throws Exception {
        when(userService.getAllUser()).thenThrow(new NotFoundException("список пользователей пуст"));

        mockMvc.perform(get("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("список пользователей пуст"))
                .andDo(print());

        verify(userService, times(1)).getAllUser();
    }


    @Test
    @DisplayName("GET - user не найден")
    void getUser_404_userNotFound_test() throws Exception {
        when(userService.getUser(eq(userId))).thenThrow(new NotFoundException(ERROR_MESSAGE.formatted(userId)));

        mockMvc.perform(get("/api/v1/user/" + userId.toString() + "/details")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(ERROR_MESSAGE.formatted(userId)))
                .andDo(print());

        verify(userService, times(1)).getUser(eq(userId));
    }

    @Test
    @DisplayName("PATCH - user не найден")
    void patchUser_404_userNotFound_test() throws Exception {
        UserRequest request = new UserRequest();

        when(userService.patchUser(any(UserRequest.class), eq(userId))).thenThrow(new NotFoundException(ERROR_MESSAGE.formatted(userId)));

        mockMvc.perform(patch("/api/v1/user/" + userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(ERROR_MESSAGE.formatted(userId)))
                .andDo(print());

        verify(userService, times(1)).patchUser(any(UserRequest.class), eq(userId));
    }
}

