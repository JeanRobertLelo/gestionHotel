package fr.jlndev.jrHotel.Integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.jlndev.jrHotel.controller.UserController;
import fr.jlndev.jrHotel.dto.Response;
import fr.jlndev.jrHotel.dto.UserDTO;
import fr.jlndev.jrHotel.service.CustomUserDetailsService;
import fr.jlndev.jrHotel.service.interfac.IUserService;
import fr.jlndev.jrHotel.utils.JWTUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.context.annotation.Import;

import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = true)
@Import(TestSecurityExceptionHandler.class)
@EnableMethodSecurity
class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IUserService userService;

    @MockitoBean
    private JWTUtils jwtUtils;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /*@Test
    @WithMockUser(authorities = "ADMIN")
    void givenAdmin_whenGetAllUsers_thenReturn200() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1001L);
        userDTO.setEmail("admin@jr-hotel.fr");
        userDTO.setRole("ADMIN");

        Response response = new Response();
        response.setStatusCode(200);
        response.setMessage("successful");
        response.setUserList(List.of(userDTO));

        when(userService.getAllUsers()).thenReturn(response);

        mockMvc.perform(get("/users/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userList[0].email").value("admin@jr-hotel.fr"));
    }*/

  // Pourquoi ça arrive
  //
  // @WithMockUser(authorities = "USER") crée bien un utilisateur connecté avec l’autorité USER.
  // Mais ton endpoint exige :
  //
  // @PreAuthorize("hasAuthority('ADMIN')")
  //
  // Donc Spring refuse l’appel et lance :
  //
  // AuthorizationDeniedException
  //
  // Dans une application démarrée complètement, cette exception est souvent convertie par la chaîne
  // de sécurité en 403. Dans un @WebMvcTest, tu testes une tranche MVC réduite. Donc il faut
  // importer explicitement un handler pour dire :
  //
  // AuthorizationDeniedException -> HTTP 403
  //
  // Après ça, tes tests 403 seront stables.
 /* @Test
  @WithMockUser(authorities = "USER")
  void givenUser_whenGetAllUsers_thenReturn403() throws Exception {
        mockMvc.perform(get("/users/all"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.statusCode").value(403))
                .andExpect(jsonPath("$.message").value("Access Denied"));
    }*/

    @Test
    @WithMockUser(authorities = "USER")
    void givenUser_whenGetAllUsers_thenReturn403() throws Exception {
        MvcResult result = mockMvc.perform(get("/users/all"))
                .andExpect(status().isForbidden())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        Response response = objectMapper.readValue(json, Response.class);

        assertThat(response.getStatusCode()).isEqualTo(403);
        assertThat(response.getMessage()).isEqualTo("Access Denied");
    }

    @Test
    void givenUserId_whenGetUserById_thenReturn200() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1001L);
        userDTO.setEmail("admin@jr-hotel.fr");

        Response response = new Response();
        response.setStatusCode(200);
        response.setMessage("successful");
        response.setUser(userDTO);

        when(userService.getUserById("1001")).thenReturn(response);

        mockMvc.perform(get("/users/get-by-id/1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.email").value("admin@jr-hotel.fr"));
    }

   @Test
    @WithMockUser(authorities = "ADMIN")
    void givenAdmin_whenDeleteUser_thenReturn200() throws Exception {
        Response response = new Response();
        response.setStatusCode(200);
        response.setMessage("successful");

        when(userService.deleteUser("1001")).thenReturn(response);

        mockMvc.perform(delete("/users/delete/1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("successful"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void givenAdmin_whenGetAllUsers_thenReturn200() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1001L);
        userDTO.setEmail("admin@jr-hotel.fr");
        userDTO.setRole("ADMIN");

        Response mockedResponse = new Response();
        mockedResponse.setStatusCode(200);
        mockedResponse.setMessage("successful");
        mockedResponse.setUserList(List.of(userDTO));

        when(userService.getAllUsers()).thenReturn(mockedResponse);

        MvcResult result = mockMvc.perform(get("/users/all"))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        Response response = objectMapper.readValue(json, Response.class);

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getMessage()).isEqualTo("successful");
        assertThat(response.getUserList()).hasSize(1);
        assertThat(response.getUserList().getFirst().getEmail())
                .isEqualTo("admin@jr-hotel.fr");
    }

    @Test
    @WithMockUser(username = "admin@jr-hotel.fr")
    void givenAuthenticatedUser_whenGetProfile_thenReturn200() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("admin@jr-hotel.fr");

        Response response = new Response();
        response.setStatusCode(200);
        response.setMessage("successful");
        response.setUser(userDTO);

        when(userService.getMyInfo("admin@jr-hotel.fr")).thenReturn(response);

        mockMvc.perform(get("/users/get-logged-in-profile-info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.email").value("admin@jr-hotel.fr"));
    }

    @Test
    void givenUserId_whenGetUserBookings_thenReturn200() throws Exception {
        Response response = new Response();
        response.setStatusCode(200);
        response.setMessage("successful");

        when(userService.getUserBookingHistory("1001")).thenReturn(response);

        mockMvc.perform(get("/users/get-user-bookings/1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200));
    }
}
