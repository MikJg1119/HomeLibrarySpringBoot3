package home.library.model;

import java.io.Serializable;

public record JwtRequest (
        String username,
        String password
) implements Serializable {
    private static final long serialVersionUID = 5926468583005150707L;



}