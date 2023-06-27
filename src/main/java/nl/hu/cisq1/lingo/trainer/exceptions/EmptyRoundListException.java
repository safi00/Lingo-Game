package nl.hu.cisq1.lingo.trainer.exceptions;

public class EmptyRoundListException extends RuntimeException {
    public EmptyRoundListException() {
        super("The list of rounds is empty, Create a round to add one");
    }
}
