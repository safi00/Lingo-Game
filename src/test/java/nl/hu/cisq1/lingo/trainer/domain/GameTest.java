package nl.hu.cisq1.lingo.trainer.domain;

import nl.hu.cisq1.lingo.trainer.domain.Enums.GameState;
import nl.hu.cisq1.lingo.trainer.exceptions.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;

public class GameTest {
    static Game game;
    static String word;
    static String word2;

    @BeforeEach
    void init() {
        game = new Game("name");
        word = "woord";
        word2 = "grond";
    }

    @Test
    @DisplayName("Making a game, this test should show if a Game is made and set to setup, see if the playerName is there, see if the score is set 0 & see if the round list is empty")
    void createNewGameTest() {
        String name = game.getPlayerName();

        assertEquals(GameState.Setup, game.getGameState());
        assertEquals("name", name);
        assertEquals(0, game.getScore());
        assertEquals(0, game.getListOfRounds().size());
    }

    @Test
    @DisplayName("Starting a game, this test should show the Game state")
    void startGameTest() {
        game.startGame();
        assertEquals(GameState.Playing, game.getGameState());
    }

    @Test
    @DisplayName("Stopping a game, this test should show the Game state")
    void stopGameTest() {
        game.createNewRound(word);
        game.stopGame();
        assertEquals(GameState.Stopped, game.getGameState());
    }

    @Test
    @DisplayName("pausing a game, this test should show the Game state")
    void pauseGameTest() {
        game.createNewRound(word);
        game.pauseGame();

        assertEquals(GameState.Paused, game.getGameState());
    }

    @Test
    @DisplayName("Starting a game and creating a new Round, testing the round aspects")
    void createNewRoundTest() {
        Round newRound = game.createNewRound(word);

        assertEquals(0, newRound.getRoundAttemptAmount());
        assertEquals("woord", newRound.getRoundWord());
    }

    @Test
    @DisplayName("Trying to create a new Round in a stopped game")
    void createRoundStoppedGameTest() {
        game.createNewRound(word);
        Round testRound = game.getListOfRounds().get(game.getListOfRounds().size() - 1);
        testRound.input("woord");
        testRound.isRoundWon();
        game.stopGame();

        assertThrows(RoundWasNotMadeException.class, () -> game.createNewRound(word));
    }

    @ParameterizedTest
    @MethodSource("scenarios")
    @DisplayName("This test will test the game states after in a few scenarios")
    void gameStateTests(String roundWord, boolean testBoolean1, boolean testBoolean2, GameState expectedState) {
        game.startGame();
        game.createNewRound(roundWord);
        Round testRound = game.getListOfRounds().get(game.getListOfRounds().size() - 1);

        // switch with 3
        if (testBoolean1) {
            if (!testBoolean2) {
                testRound.input("woord");
            } else {
                testRound.input("worms");
                testRound.input("worms");
                testRound.input("worms");
                testRound.input("worms");
                testRound.input("worms");
            }
            testRound.isRoundWon();
        }
        game.calculateGameScore();
        assertEquals(expectedState, game.getGameState());
    }

    static Stream<Arguments> scenarios() {
        return Stream.of(
                Arguments.of("woord", false, false, GameState.Playing),
                Arguments.of("woord", true, false, GameState.Playing),
                Arguments.of("woord", true, true, GameState.Stopped));
    }

    @Test
    @DisplayName("this test should show if a Game can calculate the game score")
    void calculatingGameScoreTest() {
        Round round1 = game.createNewRound(word);
        round1.input("woord");
        round1.isRoundWon();
        Round round2 = game.createNewRound(word);
        round2.input("worms");
        round2.input("woord");
        round2.isRoundWon();
        Round round3 = game.createNewRound(word);
        round3.input("worms");
        round3.input("worms");
        round3.input("woord");
        round3.isRoundWon();
        Round round4 = game.createNewRound(word);
        round4.input("worms");
        round4.isRoundWon();

        game.calculateGameScore();

        assertEquals(60, game.getScore());
    }

    @Test
    @DisplayName("Trying to calculate a score of a game without rounds")
    void scoreWithNoRoundsTest() {
        assertThrows(EmptyRoundListException.class, () -> game.calculateGameScore());
    }

    @Test
    @DisplayName("this test should show if a new round could be made after the game is over")
    void creatingNewRoundAfterTheRoundIsOverTest() {
        Round round = game.createNewRound(word);

        round.input("worms");
        round.input("worms");
        round.input("worms");
        round.input("worms");
        round.input("worms");

        round.isRoundWon();

        assertThrows(RuntimeException.class, () -> game.createNewRound(word2));
    }

    @Test
    @DisplayName("this test should show if a new round could be made after the game is over")
    void creatingNewRoundAfterTheGameIsOverTest() {
        Round round = game.createNewRound(word);

        round.input("worms");
        round.input("worms");
        round.input("worms");
        round.input("worms");
        round.input("worms");

        assertThrows(RuntimeException.class, () -> game.createNewRound(word2));
    }

    @Test
    @DisplayName("this test should show if a new round could be made after the game is over")
    void creatingNewRoundAfterTheGameIsStoppedTest() {
        Round round = game.createNewRound(word);
        round.input("worms");
        round.isRoundWon();
        game.stopGame();

        assertThrows(RuntimeException.class, () -> game.createNewRound(word2));
    }

    @Test
    @DisplayName("this test should show if a new round could be made after the game is paused")
    void creatingNewRoundAfterTheGameIsPausedTest() {
        Round round = game.createNewRound(word);

        round.input("worms");
        game.pauseGame();

        assertThrows(RuntimeException.class, () -> game.createNewRound(word2));
    }

    @Test
    @DisplayName("creating a new Round with one already still ongoing")
    void createARoundWithOnAlreadyPlayingTest() {
        Round round1 = game.createNewRound(word);
        round1.input("worms");

        assertThrows(GameException.class, () -> game.createNewRound(word));
    }

    @Test
    @DisplayName("getting current round with no rounds")
    void getCurrentRoundWhenThereIsNoRoundTest() {
        game.setGameState(GameState.Playing);
        assertThrows(EmptyRoundListException.class, () -> game.input(word));
    }

    @Test
    @DisplayName("getting current round with no rounds while in setup")
    void getCurrentRoundWhenThereIsNoRoundWhileInSetupTest() {
        game.input(word);
        assertEquals(GameState.Setup, game.getGameState());
    }

    @Test
    @DisplayName("setting a list of rounds")
    void setRoundListTest() {
        List<Round> listRounds = new ArrayList<>();
        listRounds.add(new Round(word));
        listRounds.add(new Round(word));
        game.setListOfRounds(listRounds);
        assertEquals(2, game.getListOfRounds().size());
    }

    @Test
    @DisplayName("setting player names")
    void setPlayerNameTest() {
        game.setPlayerName("kevin");
        assertEquals("kevin", game.getPlayerName());
    }

    @Test
    @DisplayName("getting game id in domain is null, because it hass none yet")
    void getGameID() {
        assertNull(game.getGameID());
    }
}