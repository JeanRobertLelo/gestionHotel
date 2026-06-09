package fr.jlndev.jrHotel.service.interfac;

import fr.jlndev.jrHotel.dto.LoginRequest;
import fr.jlndev.jrHotel.dto.Response;
import fr.jlndev.jrHotel.entity.User;

public interface IUserService {
    Response register(User user);

    Response login(LoginRequest loginRequest);

    Response getAllUsers();

    Response getUserBookingHistory(String userId);

    Response deleteUser(String userId);

    Response getUserById(String userId);

    Response getMyInfo(String email);

}
