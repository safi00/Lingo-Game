package nl.hu.cisq1.lingo.trainer.domain;

import jakarta.persistence.*;
import nl.hu.cisq1.lingo.trainer.domain.Enums.RoundState;
import nl.hu.cisq1.lingo.trainer.exceptions.*;

import java.util.*;

@Entity
public class Round {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roundID;
    private String roundWord;
    private String startingHint;
    private int roundAttemptAmount;
    private int roundWordSize;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Feedback> feedbackList;
    @Enumerated(EnumType.STRING)
    private RoundState roundState;


    public Round() {
    }

    public Round(String roundWord) {
        this.roundWord = roundWord;
        this.roundAttemptAmount = 0;
        this.feedbackList = new ArrayList<>();
        this.roundWordSize = roundWord.length();
        this.startingHint = startingHintMaker(this.roundWord);
        setRoundState(RoundState.Playing);
    }

    private String startingHintMaker(String roundWord) {
        return roundWord.toUpperCase().charAt(0) +
                ".".repeat(Math.max(0, (roundWordSize - 1)));
    }

    private Feedback getLastFeedBack() {
        if (roundAttemptAmount < 1) {
            throw new EmptyFeedbackListException();
        } else {
            return getFeedbackList().get(roundAttemptAmount - 1);
        }
    }

    private String getLastHint() {
        if (roundAttemptAmount < 1) {
            return startingHint;
        } else {
            return getLastFeedBack().getHint();
        }
    }

    public String input(String attemptedWord) {
        switch (roundState) {
            case Lost -> throw new RoundException("You've already lost the round, Create a new game to continue.");
            case Won -> throw new RoundException("You've already won the round, Move on to the next round.");
            default -> {
                return inputProcess(attemptedWord);
            }
        }
    }

    private String inputProcess(String attemptedWord) {
        String feedbackString;
        Feedback feedback = new Feedback(roundWord, attemptedWord, getLastHint());
        feedbackList.add(feedback);
        roundAttemptAmount += 1;
        isRoundWon();
        feedbackString = feedback.getHint();
        return feedbackString;
    }

    public void isRoundWon() {
        if (getFeedbackList().size() > 0) {
            if (getLastFeedBack().isWordGuessed()) {
                setRoundState(RoundState.Won);
            } else {
                if (getRoundAttemptAmount() > 4) {
                    setRoundState(RoundState.Lost);
                }
            }
        } else {
            throw new RoundException("There is no feedback, meaning you have not put in a guess yet!");
        }
    }

    //region Setters & Getters
    public void setRoundState(RoundState roundState) {
        this.roundState = roundState;
    }

    public void setRoundAttemptAmount(int roundAttemptAmount) {
        this.roundAttemptAmount = roundAttemptAmount;
    }

    public String getRoundWord() {
        return roundWord;
    }

    public String getStartingHint() {
        return startingHint;
    }

    public int getRoundAttemptAmount() {
        return roundAttemptAmount;
    }

    public int getRoundWordSize() {
        return roundWordSize;
    }

    public List<Feedback> getFeedbackList() {
        return feedbackList;
    }

    public RoundState getRoundState() {
        return roundState;
    }

    //endregion
}