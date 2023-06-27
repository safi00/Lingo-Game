package nl.hu.cisq1.lingo.trainer.domain;

import nl.hu.cisq1.lingo.trainer.domain.Enums.Mark;
import nl.hu.cisq1.lingo.trainer.domain.Enums.RoundState;
import nl.hu.cisq1.lingo.trainer.exceptions.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;

public class RoundTest {

    static Round round;

    @BeforeEach
    void init() {
        round = new Round("woord");
    }

    @Test
    @DisplayName("Check if word input is false, you haven't won the game yet")
    void guessFailTest() {
        round.input("worms");
        round.isRoundWon();
        assertEquals(RoundState.Playing, round.getRoundState());
    }

    @Test
    @DisplayName("Check if word input is right you win the round")
    void guessSuccessTest() {
        round.input("woord");
        round.isRoundWon();
        assertEquals(RoundState.Won, round.getRoundState());
    }

    @ParameterizedTest
    @MethodSource("amountOfTurnsPar")
    @DisplayName("Check roundAttemptAmount goes up after every guess including invalids")
    void amountOfTurns(String roundWord, String guessWord, int expected, boolean input2, boolean input3) {
        Round round = new Round(roundWord);
        if (input2) {
            round.input("bird");
        }
        if (input3) {
            round.input("guessWord");
        }
        round.input(guessWord);
        round.isRoundWon();

        assertEquals(expected, round.getRoundAttemptAmount());
    }

    static Stream<Arguments> amountOfTurnsPar() {
        return Stream.of(
                Arguments.of("woord", "worms", 1, false, false),
                Arguments.of("woord", "kanan", 2, true, false),
                Arguments.of("woord", "openl", 3, true, true));
    }

    @Test
    @DisplayName("Test when making the round it makes the starting hint")
    void startingHintMakerTest() {
        assertEquals("W....", round.getStartingHint());
    }

    @Test
    @DisplayName("Test when making the round it makes the round word its round word")
    void roundWordMakerTest() {
        assertEquals("woord", round.getRoundWord());
    }

    @Test
    @DisplayName("Test to see if the wrong word input keeps the round state to playing")
    void roundStatePlayingTest() {
        round.input("worms");
        round.isRoundWon();

        assertEquals(RoundState.Playing, round.getRoundState());
    }

    @Test
    @DisplayName("Test if word input is wins the round and changes that round state to won")
    void roundStateWinTest() {
        round.input("woord");
        round.isRoundWon();

        assertEquals(RoundState.Won, round.getRoundState());
    }

    @Test
    @DisplayName("Test if word input is losses the round and changes that round state to lost")
    void roundStateLoseTest() {
        round.input("banan");
        round.input("cobra");
        round.input("looks");
        round.input("mourn");
        round.input("hippo");
        round.isRoundWon();

        assertEquals(RoundState.Lost, round.getRoundState());
    }

    @Test
    @DisplayName("Checks if you can guess after you going over the max attempt limit")
    void checkMaxAttemptsExceptionTest() {
        round.input("banan");
        round.input("cobra");
        round.input("looks");
        round.input("mourn");
        round.input("hippo");
        assertThrows(RoundException.class, () -> round.input("kokos"));
    }

    @Test
    @DisplayName("Checks if the last feedback for the mark list")
    void checkLastMarkListTest() {
        round.input("worms");
        List<Mark> expectedMarkList = Arrays.asList(Mark.CORRECT, Mark.CORRECT, Mark.PRESENT, Mark.ABSENT, Mark.ABSENT);
        List<Mark> testMarkList = round.getFeedbackList().get(round.getFeedbackList().size() - 1).getMarkList();

        assertEquals(expectedMarkList, testMarkList);
    }

    @Test
    @DisplayName("Checks if you can still guess a word after winning a round")
    void checkAttemptAWordAfterWinningExceptionTest() {
        round.input("woord");
        round.isRoundWon();

        assertThrows(RoundException.class, () -> round.input("kokos"));
    }

    @Test
    @DisplayName("Checks if you can still guess a word after losing a round")
    void checkAttemptAWordAfterLosingExceptionTest() {
        round.input("worms");
        round.input("worms");
        round.input("worms");
        round.input("worms");
        round.input("worms");
        round.isRoundWon();

        assertThrows(RoundException.class, () -> round.input("kokos"));
    }

    @Test
    @DisplayName("Checks how many characters the round word is")
    void roundWordSizeTest() {
        assertEquals(5, round.getRoundWordSize());
    }

    @Test
    @DisplayName("Checks if round is over with no gueses")
    void isRoundOverWithNoFeedbackTest() {
        assertThrows(RoundException.class, () -> round.isRoundWon());
    }

    @Test
    @DisplayName("Checks if round is over with no gueses")
    void getLastFeedbackWithNoFeedbackTest() {
        round.input("imago");
        round.setRoundAttemptAmount(0);
        assertThrows(EmptyFeedbackListException.class, () -> round.isRoundWon());
    }
}