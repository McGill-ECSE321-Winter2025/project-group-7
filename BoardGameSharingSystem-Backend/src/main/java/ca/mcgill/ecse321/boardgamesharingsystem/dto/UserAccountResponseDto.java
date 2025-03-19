package ca.mcgill.ecse321.boardgamesharingsystem.dto;

import ca.mcgill.ecse321.boardgamesharingsystem.model.UserAccount;

public class UserAccountResponseDto {
    private int id;
    private String email;
    private String name;

    @SuppressWarnings("unused")
    private UserAccountResponseDto() {

    }

    public UserAccountResponseDto(UserAccount user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
    }
    
    public int getId() {
        return id;
    }
    
    public String getEmail(){
        return this.email;
    }

    public String getName(){
        return this.name;
    }
}
