package ca.mcgill.ecse321.boardgamesharingsystem.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.util.Objects;

@Entity
public class Registration {

    @EmbeddedId
    private RegistrationKey key;

    private Date registrationDate;
    private Time registrationTime;

    public Registration() {

    }
    public Registration(RegistrationKey key,Date registrationDate, Time registrationTime) {
        this.key = key;
        this.registrationDate = registrationDate;
        this.registrationTime = registrationTime;

    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public Time getRegistrationTime() {
        return registrationTime;
    }

    @Embeddable
    public static class RegistrationKey implements Serializable  {
        @ManyToOne
        private UserAccount user;
        @ManyToOne
        private Event event;

        public RegistrationKey(){

        }

        public RegistrationKey(UserAccount user, Event event){
            this.user = user;
            this.event = event;
        }

        public UserAccount getUser() {
            return user;
        }

        public Event getEvent() {
            return event;
        }

        @Override
        public boolean equals(Object obj){
            if (!(obj instanceof RegistrationKey)){
                return false;
            }
            RegistrationKey that = (RegistrationKey) obj;
            return this.event.getId() == that.event.getId() && this.user.getId() == that.user.getId();
        }

        @Override
        public int hashCode(){
            return Objects.hash(this.user.getId(), this.event.getId());
        }
    }
}
