package ca.mcgill.ecse321.boardgamesharingsystem.dto;

import ca.mcgill.ecse321.boardgamesharingsystem.model.GameOwner;
import ca.mcgill.ecse321.boardgamesharingsystem.model.UserAccount;

public class GameOwnerResponseDto {
    private int id;
    private int userId;
    private String userName;
    private String userEmail;
    private boolean isGameOwner; 

    @SuppressWarnings("unused")
    private GameOwnerResponseDto(){

    }

    public GameOwnerResponseDto(GameOwner gameOwner){
        this.id = gameOwner.getId();
        UserAccount user = gameOwner.getUser();
        if (user == null){
            this.userId = -1;
            this.userName="";
            this.userEmail="";
            this.isGameOwner=false;
        }else{
            this.userId = user.getId();
            this.userName = user.getName();
            this.userEmail = user.getEmail();
            this.isGameOwner = true;
        }
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public boolean getIsGameOwner(){
        return isGameOwner;
    }
}
