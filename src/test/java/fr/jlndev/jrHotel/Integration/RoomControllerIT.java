package fr.jlndev.jrHotel.Integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.jlndev.jrHotel.controller.RoomController;
import fr.jlndev.jrHotel.dto.Response;
import fr.jlndev.jrHotel.dto.RoomDTO;
import fr.jlndev.jrHotel.service.CustomUserDetailsService;
import fr.jlndev.jrHotel.service.interfac.IBookingService;
import fr.jlndev.jrHotel.service.interfac.IRoomService;
import fr.jlndev.jrHotel.utils.JWTUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.context.annotation.Import;

import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;

@WebMvcTest(RoomController.class)
@AutoConfigureMockMvc(addFilters = true)
@EnableMethodSecurity
@Import(TestSecurityExceptionHandler.class)
class RoomControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IRoomService roomService;

    @MockitoBean
    private IBookingService bookingService;

    @MockitoBean
    private JWTUtils jwtUtils;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /*@Test
    @WithMockUser(authorities = "ADMIN")
    void givenAdmin_whenAddRoom_thenReturn200() throws Exception {
        MockMultipartFile photo = new MockMultipartFile(
                "photo",
                "room.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "fake-image".getBytes()
        );

        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setId(1001L);
        roomDTO.setRoomType("SIMPLE");

        Response response = new Response();
        response.setStatusCode(200);
        response.setMessage("successful");
        response.setRoom(roomDTO);

        when(roomService.addNewRoom(any(), eq("SIMPLE"), eq(new BigDecimal("89.00")), eq("Chambre simple")))
                .thenReturn(response);

        mockMvc.perform(multipart("/rooms/add")
                        .file(photo)
                        .param("roomType", "SIMPLE")
                        .param("roomPrice", "89.00")
                        .param("roomDescription", "Chambre simple"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.room.roomType").value("SIMPLE"));
    }*/

    @Test
    @WithMockUser(authorities = "ADMIN")
    void givenAdmin_whenAddRoom_thenReturn200() throws Exception {
        MockMultipartFile photo = new MockMultipartFile(
                "photo",
                "room.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "fake-image".getBytes()
        );

        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setId(1001L);
        roomDTO.setRoomType("SIMPLE");

        Response mockedResponse = new Response();
        mockedResponse.setStatusCode(200);
        mockedResponse.setMessage("successful");
        mockedResponse.setRoom(roomDTO);

        when(roomService.addNewRoom(any(), eq("SIMPLE"), eq(new BigDecimal("89.00")), eq("Chambre simple")))
                .thenReturn(mockedResponse);

        MvcResult result = mockMvc.perform(multipart("/rooms/add")
                        .file(photo)
                        .param("roomType", "SIMPLE")
                        .param("roomPrice", "89.00")
                        .param("roomDescription", "Chambre simple"))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        Response response = objectMapper.readValue(json, Response.class);

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getMessage()).isEqualTo("successful");
        assertThat(response.getRoom().getRoomType()).isEqualTo("SIMPLE");
    }

    @Test
    @WithMockUser(authorities = "USER")
    void givenUser_whenAddRoom_thenReturn403() throws Exception {
        MockMultipartFile photo = new MockMultipartFile(
                "photo",
                "room.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "fake-image".getBytes()
        );

        mockMvc.perform(multipart("/rooms/add")
                        .file(photo)
                        .param("roomType", "SIMPLE")
                        .param("roomPrice", "89.00")
                        .param("roomDescription", "Chambre simple"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.statusCode").value(403))
                .andExpect(jsonPath("$.message").value("Access Denied"));
    }

    @Test
    void whenGetAllRooms_thenReturn200() throws Exception {
        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setId(1001L);
        roomDTO.setRoomType("SIMPLE");

        Response response = new Response();
        response.setStatusCode(200);
        response.setMessage("successful");
        response.setRoomList(List.of(roomDTO));

        when(roomService.getAllRooms()).thenReturn(response);

        mockMvc.perform(get("/rooms/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomList[0].roomType").value("SIMPLE"));
    }

    /*@Test
    void whenGetRoomTypes_thenReturn200() throws Exception {
        when(roomService.getAllRoomTypes()).thenReturn(List.of("SIMPLE", "DOUBLE"));

        mockMvc.perform(get("/rooms/types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("SIMPLE"))
                .andExpect(jsonPath("$[1]").value("DOUBLE"));
    }*/

    @Test
    void whenGetRoomTypes_thenReturn200() throws Exception {
        when(roomService.getAllRoomTypes()).thenReturn(List.of("SIMPLE", "DOUBLE"));

        MvcResult result = mockMvc.perform(get("/rooms/types"))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        List<String> roomTypes = objectMapper.readValue(
                json,
                new TypeReference<List<String>>() {}
        );

        assertThat(roomTypes).containsExactly("SIMPLE", "DOUBLE");
    }

    @Test
    void givenRoomId_whenGetRoomById_thenReturn200() throws Exception {
        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setId(1001L);
        roomDTO.setRoomType("DOUBLE");

        Response response = new Response();
        response.setStatusCode(200);
        response.setMessage("successful");
        response.setRoom(roomDTO);

        when(roomService.getRoomById(1001L)).thenReturn(response);

        mockMvc.perform(get("/rooms/room-by-id/1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.room.roomType").value("DOUBLE"));
    }

    @Test
    void whenGetAllAvailableRooms_thenReturn200() throws Exception {
        Response response = new Response();
        response.setStatusCode(200);
        response.setMessage("successful");

        when(roomService.getAllAvailableRooms()).thenReturn(response);

        mockMvc.perform(get("/rooms/all-available-rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    @Test
    void givenDateAndType_whenGetAvailableRoomsByDateAndType_thenReturn200() throws Exception {
        Response response = new Response();
        response.setStatusCode(200);
        response.setMessage("successful");

        when(roomService.getAvailableRoomsByDataAndType(
                eq(LocalDate.of(2026, 6, 17)),
                eq(LocalDate.of(2026, 6, 19)),
                eq("SIMPLE")
        )).thenReturn(response);

        mockMvc.perform(get("/rooms/available-rooms-by-date-and-type")
                        .param("checkInDate", "2026-06-17")
                        .param("checkOutDate", "2026-06-19")
                        .param("roomType", "SIMPLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("successful"));
    }

    @Test
    void givenMissingParams_whenGetAvailableRoomsByDateAndType_thenReturn400() throws Exception {
        mockMvc.perform(get("/rooms/available-rooms-by-date-and-type")
                        .param("checkInDate", "2026-06-17"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void givenAdmin_whenUpdateRoom_thenReturn200() throws Exception {
        Response response = new Response();
        response.setStatusCode(200);
        response.setMessage("successful");

        when(roomService.updateRoom(eq(1001L), eq("Nouvelle description"), eq("DOUBLE"), eq(new BigDecimal("129.00")), any()))
                .thenReturn(response);

        MockMultipartHttpServletRequestBuilder requestBuilder = multipart("/rooms/update/1001");
        requestBuilder.with(request -> {
            request.setMethod("PUT");
            return request;
        });

        mockMvc.perform(requestBuilder
                        .param("roomType", "DOUBLE")
                        .param("roomPrice", "129.00")
                        .param("roomDescription", "Nouvelle description"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("successful"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void givenAdmin_whenDeleteRoom_thenReturn200() throws Exception {
        Response response = new Response();
        response.setStatusCode(200);
        response.setMessage("successful");

        when(roomService.deleteRoom(1001L)).thenReturn(response);

        mockMvc.perform(delete("/rooms/delete/1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("successful"));
    }
}
