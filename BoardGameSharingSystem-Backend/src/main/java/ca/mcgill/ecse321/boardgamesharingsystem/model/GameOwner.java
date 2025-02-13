package ca.mcgill.ecse321.boardgamesharingsystem.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class GameOwner {
    @Id 
    @GeneratedValue 
    private int id;

    @OneToOne 
    private UserAccount user;


    protected GameOwner(){
    }

    public GameOwner(UserAccount user){
        this.user = user;
    }

    public int getId(){
        return this.id;
    }

    public UserAccount getUser(){
        return this.user;
    }

    public void setUser(UserAccount user){
        this.user = user;

    }
}
