package ca.mcgill.ecse321.boardgamesharingsystem.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Registration {

    @Id
    @GeneratedValue
    private LocalDateTime registrationDateTime;
    @ManyToOne
    private UserAccount user;
    @ManyToOne
    private Event event;

    public Registration() {

    }
    public Registration(LocalDateTime registrationDateTime, UserAccount user, Event event) {
        this.registrationDateTime = registrationDateTime;
        this.user = user;
        this.event = event;
    }
    public LocalDateTime getRegistrationDateTime() {
        return registrationDateTime;
    }

    public void setRegistrationDateTime(LocalDateTime registrationDateTime) {
        this.registrationDateTime = registrationDateTime;
    }

    public UserAccount getUser() {
        return user;
    }

    public void setUser(UserAccount user) {
        this.user = user;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
