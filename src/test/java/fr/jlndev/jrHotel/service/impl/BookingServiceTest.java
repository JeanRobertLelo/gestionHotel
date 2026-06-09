package fr.jlndev.jrHotel.service.impl;

import fr.jlndev.jrHotel.dto.Response;
import fr.jlndev.jrHotel.entity.Booking;
import fr.jlndev.jrHotel.entity.Room;
import fr.jlndev.jrHotel.entity.User;
import fr.jlndev.jrHotel.repo.BookingRepository;
import fr.jlndev.jrHotel.repo.RoomRepository;
import fr.jlndev.jrHotel.repo.UserRepository;
import fr.jlndev.jrHotel.service.interfac.IRoomService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private IRoomService roomService;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingService bookingService;

    @Test
    void givenAvailableRoom_whenSaveBooking_thenBookingIsSaved() {
        // GIVEN
        Long roomId = 1001L;
        Long userId = 1005L;

        Room room = new Room();
        room.setId(roomId);

        User user = new User();
        user.setId(userId);

        Booking bookingRequest = new Booking();
        bookingRequest.setCheckInDate(LocalDate.of(2026, 6, 17));
        bookingRequest.setCheckOutDate(LocalDate.of(2026, 6, 19));
        bookingRequest.setNumOfAdults(2);
        bookingRequest.setNumOfChildren(1);

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findByRoomId(roomId)).thenReturn(List.of());
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // WHEN
        Response response = bookingService.saveBooking(roomId, userId, bookingRequest);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getMessage()).isEqualTo("successful");
        assertThat(response.getBookingConfirmationCode()).isNotBlank();

        ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(bookingCaptor.capture());

        Booking savedBooking = bookingCaptor.getValue();
        assertThat(savedBooking.getRoom()).isEqualTo(room);
        assertThat(savedBooking.getUser()).isEqualTo(user);
        assertThat(savedBooking.getTotalNumOfGuest()).isEqualTo(3);
    }

    @Test
    void givenRoomNotFound_whenSaveBooking_thenReturn404() {
        // GIVEN
        Long roomId = 999L;
        Long userId = 1005L;

        Booking bookingRequest = new Booking();
        bookingRequest.setCheckInDate(LocalDate.of(2026, 6, 17));
        bookingRequest.setCheckOutDate(LocalDate.of(2026, 6, 19));

        when(roomRepository.findById(roomId)).thenReturn(Optional.empty());

        // WHEN
        Response response = bookingService.saveBooking(roomId, userId, bookingRequest);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(404);
        assertThat(response.getMessage()).isEqualTo("Room Not Found");
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void givenUnavailableRoom_whenSaveBooking_thenReturn404() {
        // GIVEN
        Long roomId = 1001L;
        Long userId = 1005L;

        Room room = new Room();
        room.setId(roomId);

        User user = new User();
        user.setId(userId);

        Booking existingBooking = new Booking();
        existingBooking.setCheckInDate(LocalDate.of(2026, 6, 16));
        existingBooking.setCheckOutDate(LocalDate.of(2026, 6, 20));

        Booking bookingRequest = new Booking();
        bookingRequest.setCheckInDate(LocalDate.of(2026, 6, 17));
        bookingRequest.setCheckOutDate(LocalDate.of(2026, 6, 19));

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findByRoomId(roomId)).thenReturn(List.of(existingBooking));

        // WHEN
        Response response = bookingService.saveBooking(roomId, userId, bookingRequest);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(404);
        assertThat(response.getMessage()).isEqualTo("Room not Available for selected date range");
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void givenInvalidDates_whenSaveBooking_thenReturn500() {
        // GIVEN
        Booking bookingRequest = new Booking();
        bookingRequest.setCheckInDate(LocalDate.of(2026, 6, 19));
        bookingRequest.setCheckOutDate(LocalDate.of(2026, 6, 17));

        // WHEN
        Response response = bookingService.saveBooking(1001L, 1005L, bookingRequest);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(500);
        assertThat(response.getMessage()).contains("Check out date must be after check in date");
        verify(bookingRepository, never()).save(any());
    }
}