package nl.hu.cisq1.lingo.trainer.exceptions;

public class GameNotFindException extends RuntimeException {
    public GameNotFindException(Long ID) {
        super("The game with the id: " + ID + ", does not exist!");
    }
}