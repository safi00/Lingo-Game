package nl.hu.cisq1.lingo.trainer.domain;

import jakarta.persistence.*;
import nl.hu.cisq1.lingo.trainer.domain.Enums.GameState;
import nl.hu.cisq1.lingo.trainer.domain.Enums.RoundState;
import nl.hu.cisq1.lingo.trainer.exceptions.*;
import java.util.ArrayList;
import java.util.List;

//.\mvnw test
@Entity
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gameID;
    private String playerName;
    private int score;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Round> listOfRounds;
    @Enumerated(EnumType.STRING)
    private GameState gameState;

    public Game() {
    }

    public Game(String playerName) {
        setGameState(GameState.Setup);
        this.playerName = playerName;
        this.score = 0;
        this.listOfRounds = new ArrayList<>();
    }

    public void startGame() {
        setGameState(GameState.Playing);
    }

    public void pauseGame() {
        setGameState(GameState.Paused);
    }

    public void stopGame() {
        setGameState(GameState.Stopped);
    }

    public void input(String guessAttempt) {
        if (getGameState() == GameState.Playing) {
            getCurrentRound().input(guessAttempt);
        }
        isGameOver();
    }

    public void calculateGameScore() {
        int totalScore = 0;

        if (getAmountOfRounds() > 0) {
            for (int index = 0; index < getAmountOfRounds(); index++) {
                Round round = getListOfRounds().get(index);
                if (round.getRoundState() != RoundState.Playing) {
                    totalScore = totalScore + (5 * (5 - round.getRoundAttemptAmount()) + 5);
                }
            }
        } else {
            throw new EmptyRoundListException();
        }
        isGameOver();
        setScore(totalScore);
    }

    private int getAmountOfRounds() {
        return getListOfRounds().size();
    }

    private void isGameOver() {
        if (getAmountOfRounds() > 0) {
            RoundState currentRoundState = getCurrentRound().getRoundState();
            if (currentRoundState == RoundState.Lost) {
                setGameState(GameState.Stopped);
            }
        }
    }

    private boolean isAbleToStartANewRound() {
        boolean returnBoolean = gameState == GameState.Setup;
        if (gameState == GameState.Playing) {
            if (!(listOfRounds.isEmpty())) {
                RoundState currentState = getCurrentRound().getRoundState();
                if (currentState == RoundState.Playing || currentState == RoundState.Won) {
                    returnBoolean = true;
                }
            } else {
                returnBoolean = true;
            }
        }

        return returnBoolean;
    }

    public Round createNewRound(String roundWord) {
        if (!(isAbleToStartANewRound())) {
            throw new RoundWasNotMadeException("The game is over or pause!");
        }
        if (getListOfRounds().isEmpty()) {
            return createRound(roundWord);
        }
        getCurrentRound().isRoundWon();
        RoundState lastRoundState = getCurrentRound().getRoundState();
        if (lastRoundState == RoundState.Playing) {
            throw new GameException("The current round has not finished yet, " +
                    "finish the the round and then try to make a new round.");
        }
        if (lastRoundState == RoundState.Lost) {
            isGameOver();
            throw new GameOverException("Max attempts was already reached last round.");
        }
        return createRound(roundWord);
    }

    public Round createRound(String roundWord) {
        Round round = new Round(roundWord);
        listOfRounds.add(round);
        return round;
    }

    private Round getCurrentRound() {
        if (!(listOfRounds.isEmpty())) {
            return listOfRounds.get(listOfRounds.size() - 1);
        } else {
            throw new EmptyRoundListException();
        }
    }

    //region Setters & Getters
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public void setListOfRounds(List<Round> listOfRounds) {
        this.listOfRounds.clear();
        this.listOfRounds.addAll(listOfRounds);
    }

    public Long getGameID() {
        return gameID;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getScore() {
        return score;
    }

    public List<Round> getListOfRounds() {
        return listOfRounds;
    }

    public GameState getGameState() {
        return gameState;
    }
    //endregion
}