package fr.jlndev.jrHotel.Integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.jlndev.jrHotel.controller.BookingController;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc(addFilters = true)
@EnableMethodSecurity
class BookingControllerIT {

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


    /*@Test
    @WithMockUser(authorities = "ADMIN")
    void givenAdmin_whenSaveBooking_thenReturn200() throws Exception {
        Booking booking = new Booking();
        booking.setCheckInDate(LocalDate.of(2026, 6, 17));
        booking.setCheckOutDate(LocalDate.of(2026, 6, 19));
        booking.setNumOfAdults(2);
        booking.setNumOfChildren(1);

        Response response = new Response();
        response.setStatusCode(200);
        response.setMessage("successful");
        response.setBookingConfirmationCode("ABC1234567");

        when(bookingService.saveBooking(eq(1001L), eq(1005L), any(Booking.class)))
                .thenReturn(response);

        mockMvc.perform(post("/bookings/book-room/1001/1005")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("successful"))
                .andExpect(jsonPath("$.bookingConfirmationCode").value("ABC1234567"));
    }*/

    @Test
    @WithMockUser(authorities = "ADMIN")
    void givenAdmin_whenSaveBooking_thenReturn200() throws Exception {
        Booking booking = new Booking();
        booking.setCheckInDate(LocalDate.of(2026, 6, 17));
        booking.setCheckOutDate(LocalDate.of(2026, 6, 19));
        booking.setNumOfAdults(2);
        booking.setNumOfChildren(1);

        Response mockedResponse = new Response();
        mockedResponse.setStatusCode(200);
        mockedResponse.setMessage("successful");
        mockedResponse.setBookingConfirmationCode("ABC1234567");

        when(bookingService.saveBooking(eq(1001L), eq(1005L), any(Booking.class)))
                .thenReturn(mockedResponse);

        MvcResult result = mockMvc.perform(post("/bookings/book-room/1001/1005")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        Response response = objectMapper.readValue(json, Response.class);

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getMessage()).isEqualTo("successful");
        assertThat(response.getBookingConfirmationCode()).isEqualTo("ABC1234567");
    }

    @Test
    @WithMockUser(authorities = "USER")
    void givenUser_whenCancelBooking_thenReturn200() throws Exception {
        Response response = new Response();
        response.setStatusCode(200);
        response.setMessage("successful");

        when(bookingService.cancelBooking(1001L)).thenReturn(response);

        mockMvc.perform(delete("/bookings/cancel/1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("successful"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void givenAdmin_whenGetAllBookings_thenReturn200() throws Exception {
        Response response = new Response();
        response.setStatusCode(200);
        response.setMessage("successful");

        when(bookingService.getAllBookings()).thenReturn(response);

        mockMvc.perform(get("/bookings/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    @Test
    void givenConfirmationCode_whenGetBooking_thenReturn200() throws Exception {
        Response response = new Response();
        response.setStatusCode(200);
        response.setMessage("successful");

        when(bookingService.findBookingByConfirmationCode("ABC1234567"))
                .thenReturn(response);

        mockMvc.perform(get("/bookings/get-by-confirmation-code/ABC1234567"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("successful"));
    }

    @Test
    @WithMockUser(authorities = "CLIENT")
    void givenClient_whenSaveBooking_thenReturn403() throws Exception {
        Booking booking = new Booking();
        booking.setCheckInDate(LocalDate.of(2026, 6, 17));
        booking.setCheckOutDate(LocalDate.of(2026, 6, 19));

        mockMvc.perform(post("/bookings/book-room/1001/1005")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isForbidden());
    }
}
