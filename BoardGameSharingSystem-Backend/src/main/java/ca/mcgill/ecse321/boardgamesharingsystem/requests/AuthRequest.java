package ca.mcgill.ecse321.boardgamesharingsystem.requests;
import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String email;
    private String password;
}
