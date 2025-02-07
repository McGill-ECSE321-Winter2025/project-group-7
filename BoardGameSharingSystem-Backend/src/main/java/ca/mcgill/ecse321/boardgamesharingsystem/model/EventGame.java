package ca.mcgill.ecse321.boardgamesharingsystem.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

public class EventGame{
    private Game gamePlayed;
    private Event event;

    protected EventGame() {
    }
}