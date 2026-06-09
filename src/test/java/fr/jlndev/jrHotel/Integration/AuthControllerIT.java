package fr.jlndev.jrHotel.Integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.jlndev.jrHotel.controller.AuthController;
import fr.jlndev.jrHotel.dto.LoginRequest;
import fr.jlndev.jrHotel.dto.Response;
import fr.jlndev.jrHotel.entity.User;
import fr.jlndev.jrHotel.service.CustomUserDetailsService;
import fr.jlndev.jrHotel.service.interfac.IUserService;
import fr.jlndev.jrHotel.utils.JWTUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = true)
@EnableMethodSecurity
class AuthControllerIT {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private IUserService userService;

    @MockitoBean
    private JWTUtils jwtUtils;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void givenValidLogin_whenLogin_thenReturnToken() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("admin@jr-hotel.fr");
        loginRequest.setPassword("password");

        Response response = new Response();
        response.setStatusCode(200);
        response.setMessage("successful");
        response.setToken("jwt-token");
        response.setRole("ADMIN");

        when(userService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void givenValidUser_whenRegister_thenReturn200() throws Exception {
        String userJson = """
            {
              "email": "newuser@jr-hotel.fr",
              "name": "New User",
              "phoneNumber": "0600000000",
              "password": "password"
            }
            """;

        Response response = new Response();
        response.setStatusCode(200);
        response.setMessage("successful");

        when(userService.register(any(User.class))).thenReturn(response);

        mockMvc.perform(post("/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("successful"));
    }
}
