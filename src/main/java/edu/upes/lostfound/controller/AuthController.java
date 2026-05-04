package edu.upes.lostfound.controller;

import edu.upes.lostfound.dao.UserDAO;
import edu.upes.lostfound.model.User;
import edu.upes.lostfound.service.AuthService;

import java.sql.SQLException;

public class AuthController {
    private final AuthService authService;

    public AuthController() {
        this.authService = new AuthService(new UserDAO());
    }

    public User login(String email, String password) throws SQLException {
        return authService.login(email, password);
    }

    public User register(String fullName, String universityId, String email, String phone, String password)
            throws SQLException {
        return authService.register(fullName, universityId, email, phone, password);
    }
}
