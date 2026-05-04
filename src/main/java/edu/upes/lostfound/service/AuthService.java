package edu.upes.lostfound.service;

import edu.upes.lostfound.dao.UserDAO;
import edu.upes.lostfound.model.Role;
import edu.upes.lostfound.model.User;
import edu.upes.lostfound.util.PasswordUtil;
import edu.upes.lostfound.util.ValidationUtil;

import java.sql.SQLException;
import java.util.Optional;

public class AuthService {
    private final UserDAO userDAO;

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User login(String email, String password) throws SQLException {
        ValidationUtil.validateEmail(email);
        ValidationUtil.requireText(password, "Password");
        Optional<User> user = userDAO.findByEmail(email.trim().toLowerCase());
        if (user.isEmpty() || !PasswordUtil.verifyPassword(password, user.get().getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password.");
        }
        if (!user.get().isActive()) {
            throw new IllegalArgumentException("This account is disabled. Contact the Lost and Found office.");
        }
        return user.get();
    }

    public User register(String fullName, String universityId, String email, String phone, String password)
            throws SQLException {
        ValidationUtil.requireText(fullName, "Full name");
        ValidationUtil.requireText(universityId, "University ID");
        ValidationUtil.validateEmail(email);
        ValidationUtil.validatePhone(phone);
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        }
        if (userDAO.findByEmail(email.trim().toLowerCase()).isPresent()) {
            throw new IllegalArgumentException("Email is already registered.");
        }
        User user = new User(
                fullName.trim(),
                universityId.trim(),
                email.trim().toLowerCase(),
                phone.trim(),
                PasswordUtil.hashPassword(password),
                Role.STUDENT
        );
        return userDAO.create(user);
    }
}
