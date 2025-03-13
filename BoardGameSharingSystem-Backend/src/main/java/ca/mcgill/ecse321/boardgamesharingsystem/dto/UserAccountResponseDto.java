package ca.mcgill.ecse321.boardgamesharingsystem.dto;

import ca.mcgill.ecse321.boardgamesharingsystem.model.UserAccount;

public class UserAccountResponseDto {
    private String email;
    private String name;
    private String password;

    @SuppressWarnings("unused")
    private UserAccountResponseDto() {

    }

    public UserAccountResponseDto(UserAccount user) {
        this.email = user.getEmail();
        this.name = user.getName();
        this.password = user.getPassword();
    }

    public String getEmail(){
        return this.email;
    }

    public String getName(){
        return this.name;
    }

    public String getPassword(){
        return this.password;
    }
}
