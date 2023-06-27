package nl.hu.cisq1.lingo.trainer.exceptions;

public class RoundWasNotMadeException extends RuntimeException {
    public RoundWasNotMadeException(String reason) {
        super("Round was not created because: " + reason);
    }
}
