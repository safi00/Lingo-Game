package nl.hu.cisq1.lingo.trainer.exceptions;

public class GameOverException extends RuntimeException {
    public GameOverException(String reason) {
        super("You've Lost! GAME OVER: " + reason);
    }
}