package home.library.service;

import home.library.model.User;
import home.library.model.dto.UserRegistrationDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User save(UserRegistrationDto registrationDto);

    User getUserByName(String name);

    User getUserByEmail(String email);

    User getUserById(int id);
}
