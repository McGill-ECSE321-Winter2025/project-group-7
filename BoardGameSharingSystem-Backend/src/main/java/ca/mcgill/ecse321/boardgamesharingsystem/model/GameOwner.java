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

    @OneToOne(optional = true) private UserAccount user;


    public GameOwner(){
    }

    public int getId(){
        return this.id;
    }

    public void setUser(UserAccount user){
        this.user = user;

    }
}
