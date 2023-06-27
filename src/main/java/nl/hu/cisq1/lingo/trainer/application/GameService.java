package nl.hu.cisq1.lingo.trainer.application;

import nl.hu.cisq1.lingo.trainer.data.GameRepository;
import nl.hu.cisq1.lingo.trainer.domain.Enums.GameState;
import nl.hu.cisq1.lingo.trainer.domain.Enums.RoundState;
import nl.hu.cisq1.lingo.trainer.domain.Feedback;
import nl.hu.cisq1.lingo.trainer.domain.Game;
import nl.hu.cisq1.lingo.trainer.domain.Round;
import nl.hu.cisq1.lingo.trainer.exceptions.*;
import nl.hu.cisq1.lingo.words.application.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameService {
    private final WordService wordService;
    private final GameRepository gameRepo;

    @Autowired
    public GameService(WordService wordService, GameRepository gameRepo) {
        this.wordService = wordService;
        this.gameRepo = gameRepo;
    }

    public Game startNewGame(String name) {
        Game game = new Game(name);
        game.startGame();
        gameRepo.save(game);
        return game;
    }

    public String startNewRound(Long gameID) {
        return newRound(getGameFromID(gameID));
    }

    private String newRound(Game game) {
        int nextWordLength = giveNextWordLength(game);
        game.createNewRound(wordService.provideRandomWord(nextWordLength));
        gameRepo.save(game);
        return getCurrentRound(game).getStartingHint();
    }

    public List<Game> getGames() {
        return gameRepo.findAll();
    }

    public Game getGameFromID(Long gameID) {
        Optional<Game> game = gameRepo.findById(gameID);
        if (game.isPresent()) {
            return game.get();
        } else {
            throw new GameNotFindException(gameID);
        }
    }

    public int giveNextWordLength(Game game) {
        if (game.getListOfRounds().size() < 1) {
            return 5;
        } else {
            return giveWordLength(game);
        }
    }

    private int giveWordLength(Game game) {
        int gameWordLength = getCurrentRound(game).getRoundWordSize();
        if (gameWordLength == 7 || gameWordLength == 6 || gameWordLength == 5) {
            if (gameWordLength == 7) {
                return 5;
            } else {
                if (gameWordLength == 6) {
                    return 7;
                } else {
                    return 6;
                }
            }
        } else {
            return 5;
        }
    }

    public String guessRoundWord(Long gameID, String attempt) {
        Game game = getGameFromID(gameID);
        String returnString;
        if (game.getGameState() == GameState.Playing) {
            if (!wordExists(attempt)) {
                returnString = "word does not exist ";
            } else {
                game.input(attempt);
                returnString = "The last hint is : " + getLastFeedback(getCurrentRound(game)).getHint() + " ";
                this.gameRepo.save(game);
            }
        } else {
            throw new GameException("The game is still in: " + game.getGameState() + ". this means in this game state you can not guess a word.");
        }
        switch (game.getGameState()) {
            case Playing -> {
                returnString = returnString + "The game is still ongoing! ";
                if (getCurrentRound(game).getRoundState() == RoundState.Won) {
                    returnString = returnString + "And YOU WON THE ROUND! CREATE A NEW ONE TO CONTINUE!";
                }
                if (getCurrentRound(game).getRoundState() == RoundState.Playing) {
                    returnString = returnString + "And you have " + (5 - getCurrentRound(game).getRoundAttemptAmount() + " tries left!");
                }
            }
            case Stopped -> returnString = returnString + "and Also you've already lost the game!";
        }
        return returnString;
    }

    public String updateGameState(Long gameID, String gameState) {
        Game game = getGameFromID(gameID);
        switch (gameState.toLowerCase()) {
            default -> {
                return getGameState(gameID);
            }
            case "playing" -> {
                updateGame(game, game.getPlayerName(), game.getScore(), GameState.Playing, game.getListOfRounds());
                return getGameState(gameID);
            }
            case "setup" -> {
                updateGame(game, game.getPlayerName(), game.getScore(), GameState.Setup, game.getListOfRounds());
                return getGameState(gameID);
            }
            case "paused" -> {
                updateGame(game, game.getPlayerName(), game.getScore(), GameState.Paused, game.getListOfRounds());
                return getGameState(gameID);
            }
            case "stopped" -> {
                updateGame(game, game.getPlayerName(), game.getScore(), GameState.Stopped, game.getListOfRounds());
                return getGameState(gameID);
            }
        }
    }

    public void updateGame(Game oldGame, String playerName, int score, GameState gameState, List<Round> rounds) {
        oldGame.setPlayerName(playerName);
        oldGame.setScore(score);
        oldGame.setGameState(gameState);
        oldGame.setListOfRounds(rounds);
        this.gameRepo.save(oldGame);
    }

    public String getGameState(Long gameID) {
        String returnString = "The gameState is: error!";
        Game game = getGameFromID(gameID);
        switch (game.getGameState()) {
            case Playing -> {
                returnString = "The gameState is: playing!";
            }
            case Setup -> {
                returnString = "The gameState is: Setup!";
            }
            case Paused -> {
                returnString = "The gameState is: Paused!";
            }
            case Stopped -> {
                returnString = "The gameState is: Stopped!";
            }
        }
        return returnString;
    }

    public boolean wordExists(String attempt) {
        return wordService.wordExists(attempt);
    }

    private Round getCurrentRound(Game game) {
        return game.getListOfRounds().get(game.getListOfRounds().size() - 1);
    }

    private Feedback getLastFeedback(Round round) {
        return round.getFeedbackList().get(round.getFeedbackList().size() - 1);
    }

    public void deleteGameById(Long id) {
        this.gameRepo.deleteById(id);
    }
}
