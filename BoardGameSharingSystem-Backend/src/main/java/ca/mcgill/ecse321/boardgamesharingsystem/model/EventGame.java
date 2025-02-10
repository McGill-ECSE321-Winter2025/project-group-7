package ca.mcgill.ecse321.boardgamesharingsystem.model;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToOne;

@Entity
public class EventGame{

    @EmbeddedId
    private Key key;

    protected EventGame() {
    }

    public EventGame(Key key) {
        this.key = key;
    }

    public Key getKey() {
        return key;
    }

    @Embeddable
    public static class Key implements Serializable {

        @ManyToOne
        private Event event;

        @ManyToOne
        private Game gamePlayed;

        public Key() {
        }

        public Key(Event event, Game gamePlayed) {
            this.event = event;
            this.gamePlayed = gamePlayed;
        }

        public Event getEvent() {
            return event;
        }

        public Game getGame() {
            return gamePlayed;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Key)) {
                return false;
            }
            Key that = (Key) obj;
            return this.event.getId() == that.event.getId() &&
                    this.game.getId() == that.game.getId();
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.event.getId(), this.game.getId());
        }

    }
}