package home.library.model.dto;

import lombok.Data;

public record UserRegistrationDto (
        String name,
        String email,
        String password

) {


}
