package ca.mcgill.ecse321.boardgamesharingsystem.model;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
/**
 * A class that defines the User account.
 */
@Entity
public class UserAccount {
    private String email;
    private String name;
    private String password;
    @Id
    @GeneratedValue
    private int id;

    public UserAccount (String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public int getId() { 
        return id; 
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
