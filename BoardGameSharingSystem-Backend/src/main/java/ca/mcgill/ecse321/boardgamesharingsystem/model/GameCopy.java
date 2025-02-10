package ca.mcgill.ecse321.boardgamesharingsystem.model;

import jakarta.persistence.*;
import java.util.Set;

@Entity
public class GameCopy {

    @Id
    @GeneratedValue
    private Integer id;
    @ManyToOne
    private Game game;
    @ManyToOne
    private GameOwner owner;
    @OneToMany
    private Set<BorrowRequest> requests;

    public GameCopy() {

    }

    public GameCopy(Game game) {
        this.game = game;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Game getGame() {
        return game;
    }
    public void setGame(Game game) {
        this.game = game;
    }
    public GameOwner getGameOwner() {
        return gameOwner;
    }

    public void setGameOwner(GameOwner gameOwner) {
        this.gameOwner = gameOwner;
    }
    public Set<BorrowRequest> getRequests() {
        return requests;
    }
    public void setRequests(Set<BorrowRequest> requests) {
        this.requests = requests;
    }
}
