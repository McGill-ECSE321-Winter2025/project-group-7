package ca.mcgill.ecse321.boardgamesharingsystem.model;

import jakarta.persistence.*;

@Entity
public class GameCopy {

    @Id
    @GeneratedValue
    private Integer id;
    @ManyToOne
    private Game game;
    @ManyToOne
    private GameOwner owner;

    public GameCopy() {

    }

    public GameCopy(Game game, GameOwner gameOwner) {
        this.game = game;
        this.owner = gameOwner;
    }

    public Integer getId() {
        return id;
    }
    public Game getGame() {
        return game;
    }
    public void setGame(Game game) {
        this.game = game;
    }
    public GameOwner getGameOwner() {
        return owner;
    }

    public void setGameOwner(GameOwner gameOwner) {
        this.owner = gameOwner;
    }
}
