package fr.jlndev.jrHotel.service.interfac;

import fr.jlndev.jrHotel.dto.Response;
import fr.jlndev.jrHotel.entity.Booking;

public interface IBookingService {

    Response saveBooking(Long roomId, Long userId, Booking bookingRequest);

    Response findBookingByConfirmationCode(String confirmationCode);

    Response getAllBookings();

    Response cancelBooking(Long bookingId);

}
