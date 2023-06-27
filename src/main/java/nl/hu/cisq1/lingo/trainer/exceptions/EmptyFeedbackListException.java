package nl.hu.cisq1.lingo.trainer.exceptions;

public class EmptyFeedbackListException extends RuntimeException {
    public EmptyFeedbackListException() {
        super("The feedback list is empty, add a feedback by giving an input before calling this method!");
    }
}
