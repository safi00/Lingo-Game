package nl.hu.cisq1.lingo.trainer.exceptions;

public class GameException extends RuntimeException {
    public GameException(String reason) {
        super("Error! reason: " + reason);
    }
}