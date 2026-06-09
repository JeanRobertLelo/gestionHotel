package fr.jlndev.jrHotel.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.jlndev.jrHotel.dto.Response;
import fr.jlndev.jrHotel.entity.Booking;
import fr.jlndev.jrHotel.service.CustomUserDetailsService;
import fr.jlndev.jrHotel.service.interfac.IBookingService;
import fr.jlndev.jrHotel.utils.JWTUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.security.authorization.AuthorizationDeniedException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc(addFilters = false)
@EnableMethodSecurity
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @MockitoBean
    private IBookingService bookingService;

    @MockitoBean
    private JWTUtils jwtUtils;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser(authorities = "ADMIN")
    void givenAdmin_whenSaveBooking_thenReturn200() throws Exception {
        Booking bookingRequest = new Booking();
        bookingRequest.setCheckInDate(LocalDate.of(2026, 6, 17));
        bookingRequest.setCheckOutDate(LocalDate.of(2026, 6, 19));
        bookingRequest.setNumOfAdults(2);
        bookingRequest.setNumOfChildren(1);

        Response response = new Response();
        response.setStatusCode(200);
        response.setMessage("successful");
        response.setBookingConfirmationCode("ABC1234567");

        when(bookingService.saveBooking(eq(1001L), eq(1005L), any(Booking.class)))
                .thenReturn(response);

        mockMvc.perform(post("/bookings/book-room/1001/1005")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("successful"))
                .andExpect(jsonPath("$.bookingConfirmationCode").value("ABC1234567"));
    }

    @Test
    @WithMockUser(authorities = "USER")
    void givenUser_whenSaveBooking_thenReturn200() throws Exception {
        Booking bookingRequest = new Booking();
        bookingRequest.setCheckInDate(LocalDate.of(2026, 6, 17));
        bookingRequest.setCheckOutDate(LocalDate.of(2026, 6, 19));
        bookingRequest.setNumOfAdults(1);
        bookingRequest.setNumOfChildren(0);

        Response response = new Response();
        response.setStatusCode(200);
        response.setMessage("successful");

        when(bookingService.saveBooking(eq(1001L), eq(1005L), any(Booking.class)))
                .thenReturn(response);

        mockMvc.perform(post("/bookings/book-room/1001/1005")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    @Test
    @WithMockUser(authorities = "CLIENT")
    void givenUnauthorizedRole_whenSaveBooking_thenThrowAuthorizationDeniedException() throws Exception {
        Booking bookingRequest = new Booking();
        bookingRequest.setCheckInDate(LocalDate.of(2026, 6, 17));
        bookingRequest.setCheckOutDate(LocalDate.of(2026, 6, 19));

        assertThatThrownBy(() ->
                mockMvc.perform(post("/bookings/book-room/1001/1005")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequest)))
        )
                .hasCauseInstanceOf(AuthorizationDeniedException.class);
    }
}