package nl.hu.cisq1.lingo.trainer.domain;

import nl.hu.cisq1.lingo.trainer.domain.Enums.Mark;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.*;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;

public class FeedbackTest {
    private String word;

    @BeforeEach
    void setup() {
        word = "woord";
    }

    @Test
    @DisplayName("Check if round word is guessed correctly (Fail)")
    void checkRoundWordIsGuessed() {
        String attemptedWord = "worms";
        String prevHint = "W....";

        Feedback feedback = new Feedback(word, attemptedWord, prevHint);
        boolean isWordGuessed = feedback.isWordGuessed();
        assertFalse(isWordGuessed);
    }

    @Test
    @DisplayName("Check if round word is guessed correctly (Succeed)")
    void checkRoundWordIsGuessed2() {
        String attemptedWord = "woord";
        String prevHint = "W....";

        Feedback feedback = new Feedback(word, attemptedWord, prevHint);
        boolean isWordGuessed = feedback.isWordGuessed();
        assertTrue(isWordGuessed);
    }

    @Test
    @DisplayName("Check if round word in feedback is the same word")
    void checkRoundWordFromFeedback() {
        String attemptedWord = "worms";
        String prevHint = "W....";

        Feedback feedback = new Feedback(word, attemptedWord, prevHint);
        assertEquals(word, feedback.getRoundWord());
    }

    @Test
    @DisplayName("Tests the Attempted word given is the same")
    void checkAttemptedWordFromFeedback() {
        String attemptedWord = "worms";
        String prevHint = "W....";

        Feedback feedback = new Feedback(word, attemptedWord, prevHint);
        assertEquals(attemptedWord, feedback.getWordGuessed());
    }

    @ParameterizedTest
    @MethodSource("GuessedParTest")
    @DisplayName("Check if words given is right or not")
    void checkRoundWordIsGuessedParTest(String roundWord, String guessWord, String prevHint, Boolean expected) {
        word = roundWord;
        Feedback feedback = new Feedback(word, guessWord, prevHint);
        boolean isWordGuessed = feedback.isWordGuessed();
        assertEquals(expected, isWordGuessed);
    }

    static Stream<Arguments> GuessedParTest() {
        return Stream.of(
                Arguments.of("woord", "worms", "w....", false),
                Arguments.of("bnaaa", "kanana", "b....", false),
                Arguments.of("lopen", "lopen", "l....", true));
    }

    @Test
    @DisplayName("Mark list created with the constructor in feedback")
    void charListGivenBasedOnAttemptedWord() {
        String attemptedWord = "worms";
        String prevHint = "W....";

        Feedback feedback = new Feedback(word, attemptedWord, prevHint);
        List<Mark> testMarkList = Arrays.asList(Mark.CORRECT, Mark.CORRECT, Mark.PRESENT, Mark.ABSENT, Mark.ABSENT);

        assertEquals(testMarkList, feedback.getMarkList());
    }

    @ParameterizedTest
    @MethodSource("markListTest")
    @DisplayName("This tests the mark-lists Method")
    void updateFeedbackMarkList(String roundWord, String guessWord, String prevHint, List<Mark> testMarkList) {
        word = roundWord;
        Feedback feedback = new Feedback(word, guessWord, prevHint);

        assertEquals(testMarkList, feedback.getMarkList());
    }

    static Stream<Arguments> markListTest() {
        List<Mark> woordTestMarkList = Arrays.asList(Mark.CORRECT, Mark.CORRECT, Mark.PRESENT, Mark.ABSENT, Mark.ABSENT);
        List<Mark> bnaaaTestMarkList = Arrays.asList(Mark.ABSENT, Mark.PRESENT, Mark.PRESENT, Mark.CORRECT, Mark.ABSENT);
        List<Mark> lopenTestMarkList = Arrays.asList(Mark.PRESENT, Mark.PRESENT, Mark.PRESENT, Mark.PRESENT, Mark.PRESENT);
        List<Mark> bananTestMarkList = Arrays.asList(Mark.CORRECT, Mark.CORRECT, Mark.CORRECT, Mark.CORRECT, Mark.CORRECT);
        List<Mark> lopen2TestMarkList = Arrays.asList(Mark.ABSENT, Mark.ABSENT, Mark.ABSENT, Mark.ABSENT, Mark.ABSENT);
        List<Mark> banan2TestMarkList = Arrays.asList(Mark.INVALID, Mark.INVALID, Mark.INVALID, Mark.INVALID, Mark.INVALID);

        return Stream.of(
                Arguments.of("woord", "worms", "w....", woordTestMarkList),
                Arguments.of("bnaaa", "kanan", "b....", bnaaaTestMarkList),
                Arguments.of("lopen", "openl", "l....", lopenTestMarkList),
                Arguments.of("banan", "banan", "b....", bananTestMarkList),
                Arguments.of("lopen", "duurt", "l....", lopen2TestMarkList),
                Arguments.of("banan", "banaan", "b....", banan2TestMarkList));
    }

    @Test
    @DisplayName("Hint is based on given word")
    void hintGivenBasedOnWord() {
        String attemptedWord = "worms";
        String prevHint = "W....";

        Feedback feedback = new Feedback(word, attemptedWord, prevHint);
        String fbHint = feedback.getHint();
        assertEquals("WO...", fbHint);
    }

    @ParameterizedTest
    @MethodSource("hintTest")
    @DisplayName("Hints is based on given words")
    void hintGivenBasedOnWordParTest(String roundWord, String guessWord, String prevHint, String currentHint) {
        word = roundWord;
        Feedback feedback = new Feedback(word, guessWord, prevHint);

        String fbHint = feedback.getHint();
        assertEquals(currentHint, fbHint);
    }

    static Stream<Arguments> hintTest() {
        return Stream.of(
                Arguments.of("bnaaa", "kanan", "BN..A", "BN.AA"),
                Arguments.of("lopen", "openl", "L....", "L...."),
                Arguments.of("banan", "banan", "BA..N", "BANAN"),
                Arguments.of("lopen", "duurt", "L.PEN", "L.PEN"));
    }

    @ParameterizedTest
    @MethodSource("mandatoryTest")
    @DisplayName("Hints is based on given words")
    void mandatoryParTest(String roundWord, String guessWord, String prevHint, String expectedHint, List<Mark> testMarkList) {
        word = roundWord;
        Feedback feedback = new Feedback(word, guessWord, prevHint);
        String fbHint = feedback.getHint();

        assertEquals(expectedHint, fbHint);
        assertEquals(testMarkList, feedback.getMarkList());
    }

    static Stream<Arguments> mandatoryTest() {
        List<Mark> banaanTestMarkList = Arrays.asList(Mark.CORRECT, Mark.CORRECT, Mark.CORRECT, Mark.CORRECT, Mark.PRESENT, Mark.PRESENT);
        List<Mark> ksuirTestMarkList = Arrays.asList(Mark.CORRECT, Mark.PRESENT, Mark.CORRECT, Mark.CORRECT, Mark.PRESENT);
        List<Mark> kaasjeTestMarkList = Arrays.asList(Mark.CORRECT, Mark.CORRECT, Mark.PRESENT, Mark.ABSENT, Mark.CORRECT, Mark.CORRECT);
        List<Mark> aaabbbTestMarkList = Arrays.asList(Mark.PRESENT, Mark.PRESENT, Mark.PRESENT, Mark.PRESENT, Mark.PRESENT, Mark.PRESENT);
        List<Mark> aaababTestMarkList = Arrays.asList(Mark.PRESENT, Mark.PRESENT, Mark.ABSENT, Mark.PRESENT, Mark.CORRECT, Mark.PRESENT);
        List<Mark> aaaaaaTestMarkList = Arrays.asList(Mark.ABSENT, Mark.ABSENT, Mark.ABSENT, Mark.ABSENT, Mark.ABSENT, Mark.ABSENT);
        List<Mark> gehoorTestMarkList = Arrays.asList(Mark.PRESENT, Mark.ABSENT, Mark.ABSENT, Mark.PRESENT, Mark.ABSENT, Mark.ABSENT);
        List<Mark> aabbccTestMarkList = Arrays.asList(Mark.CORRECT, Mark.PRESENT, Mark.PRESENT, Mark.PRESENT, Mark.PRESENT, Mark.CORRECT);
        List<Mark> aliannaTestMarkList = Arrays.asList(Mark.PRESENT, Mark.PRESENT, Mark.PRESENT, Mark.ABSENT, Mark.PRESENT, Mark.PRESENT, Mark.ABSENT);
        List<Mark> herenTestMarkList = Arrays.asList(Mark.CORRECT, Mark.ABSENT, Mark.CORRECT, Mark.CORRECT, Mark.CORRECT);
        List<Mark> eeaaaeTestMarkList = Arrays.asList(Mark.PRESENT, Mark.PRESENT, Mark.PRESENT, Mark.PRESENT, Mark.CORRECT, Mark.CORRECT);
        return Stream.of(
                Arguments.of("banaan", "banana", "B.....", "BANA..", banaanTestMarkList),
                Arguments.of("ksuir", "kruis", "K....", "K.UI.", ksuirTestMarkList),
                Arguments.of("kaasje", "kastje", "K.....", "KA..JE", kaasjeTestMarkList),
                Arguments.of("aaabbb", "bbbaaa", "A.....", "A.....", aaabbbTestMarkList),
                Arguments.of("aaabab", "bbbaaa", "A.....", "A...A.", aaababTestMarkList),
                Arguments.of("aaaaaa", "bbbbbb", "A.....", "A.....", aaaaaaTestMarkList),
                Arguments.of("gehoor", "onmens", "G.....", "G.....", gehoorTestMarkList),
                Arguments.of("aabbcc", "abcabc", "A.....", "A....C", aabbccTestMarkList),
                Arguments.of("alianna", "liniaal", "A......", "A......", aliannaTestMarkList),
                Arguments.of("heren", "haren", "H....", "H.REN", herenTestMarkList),
                Arguments.of("eeaaae", "aaeeae", "E.....", "E...AE", eeaaaeTestMarkList));
    }
}
