package nl.hu.cisq1.lingo.trainer.application;

import nl.hu.cisq1.lingo.trainer.data.GameRepository;
import nl.hu.cisq1.lingo.trainer.domain.Enums.GameState;
import nl.hu.cisq1.lingo.trainer.domain.Game;
import nl.hu.cisq1.lingo.trainer.exceptions.*;
import nl.hu.cisq1.lingo.words.application.WordService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import java.util.*;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GameServiceTest {
    private GameService gameService;
    private GameRepository gameRepository;
    private WordService wordService;
    private Game testGame1;

    @BeforeEach
    void setup() {
        gameRepository = Mockito.mock(GameRepository.class);
        wordService = Mockito.mock(WordService.class);
        gameService = new GameService(wordService, gameRepository);

        testGame1 = gameService.startNewGame("Test");
        when(gameRepository.saveAndFlush(any(Game.class))).thenReturn(testGame1);
    }

    @Test
    void startNewGameTest() {
        assertEquals(GameState.Playing, testGame1.getGameState());
    }

    @Test
    void getGamesTest() {
        List<Game> gameList = new ArrayList<>();
        gameList.add(testGame1);
        when(gameRepository.findAll()).thenReturn(gameList);
        assertEquals(gameList, gameService.getGames());
    }

    @Test
    void deleteNewGameTest() {
        gameService.deleteGameById(testGame1.getGameID());
        assertThrows(GameNotFindException.class, () -> gameService.startNewRound(testGame1.getGameID()));
    }

    @Test
    void startNewRoundTest() {
        Game game = new Game("Test");
        game.startGame();
        when(gameRepository.findById(game.getGameID())).thenReturn(Optional.of(game));
        when(wordService.provideRandomWord(Mockito.anyInt())).thenReturn("banaan");
        assertNotNull(game.getListOfRounds());
        assertEquals(GameState.Playing, game.getGameState());
    }

    @Test
    void startNewRoundWithGameThatDoesntExistTest() {
        Game game = new Game("Test");
        game.startGame();
        when(wordService.provideRandomWord(Mockito.anyInt())).thenReturn("banaan");
        assertThrows(GameNotFindException.class, () -> gameService.startNewRound(game.getGameID()));
    }

    @Test
    void startNewRoundWithNonExistentGameTest() {
        when(gameRepository.findById(any())).thenReturn(java.util.Optional.empty());
        assertThrows(GameNotFindException.class, () -> gameService.startNewRound(1L));
    }

    @Test
    void guessRoundWordTest() {
        Game game = new Game("Test");
        game.startGame();
        game.createNewRound("imago");
        when(gameRepository.findById(game.getGameID())).thenReturn(Optional.of(game));
        when(wordService.wordExists("woord")).thenReturn(true);
        String hint = gameService.guessRoundWord(game.getGameID(), "woord");
        assertNotNull(hint);
        assertEquals("The last hint is : I.... The game is still ongoing! And you have 4 tries left!", hint);
    }

    @Test
    void guessRoundWordRightTest() {
        Game game = new Game("Test");
        game.startGame();
        game.createNewRound("woord");
        when(gameRepository.findById(game.getGameID())).thenReturn(Optional.of(game));
        when(wordService.wordExists("woord")).thenReturn(true);
        String hint = gameService.guessRoundWord(game.getGameID(), "woord");
        assertNotNull(hint);
        assertEquals("The last hint is : WOORD The game is still ongoing! And YOU WON THE ROUND! CREATE A NEW ONE TO CONTINUE!", hint);
    }

    @Test
    void guessRoundWordWrongTest() {
        Game game = new Game("Test");
        game.startGame();
        game.createNewRound("woord");
        when(gameRepository.findById(game.getGameID())).thenReturn(Optional.of(game));
        when(wordService.wordExists("woord")).thenReturn(true);
        when(wordService.wordExists("imago")).thenReturn(true);
        gameService.guessRoundWord(game.getGameID(), "imago");
        gameService.guessRoundWord(game.getGameID(), "imago");
        gameService.guessRoundWord(game.getGameID(), "imago");
        gameService.guessRoundWord(game.getGameID(), "imago");
        String hint = gameService.guessRoundWord(game.getGameID(), "imago");

        assertNotNull(hint);
        assertEquals("The last hint is : W.... and Also you've already lost the game!", hint);
    }

    @Test
    void guessRoundWordThatDoesntExistTest() {
        Game game = new Game("Test");
        game.startGame();
        game.createNewRound("imago");
        when(gameRepository.findById(game.getGameID())).thenReturn(Optional.of(game));
        when(wordService.wordExists("woord")).thenReturn(false);
        String hint = gameService.guessRoundWord(game.getGameID(), "woord");
        assertNotNull(hint);
        assertEquals("word does not exist The game is still ongoing! And you have 5 tries left!", hint);
    }

    @Test
    void giveWordLengthWithNoRounds() {
        Game game = new Game("Test");
        game.startGame();
        when(gameRepository.findById(game.getGameID())).thenReturn(Optional.of(game));
        assertEquals(5, gameService.giveNextWordLength(game));
    }

    @ParameterizedTest
    @MethodSource("scenarios")
    @DisplayName("This test will test the game states after in a few scenarios")
    void giveWordLengthWithRounds(String roundWord, int expectedLength) {
        Game game = new Game("Test");
        game.startGame();
        when(gameRepository.findById(game.getGameID())).thenReturn(Optional.of(game));
        when(wordService.provideRandomWord(Mockito.anyInt())).thenReturn(roundWord);
        gameService.startNewRound(game.getGameID());
        assertEquals(expectedLength, gameService.giveNextWordLength(game));
    }

    static Stream<Arguments> scenarios() {
        return Stream.of(
                Arguments.of("woord", 6),
                Arguments.of("woordd", 7),
                Arguments.of("woorddd", 5),
                Arguments.of("woor", 5));
    }

    @Test
    void guessRoundWordWithNonExistentGameTest() {
        when(gameRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(GameNotFindException.class, () -> gameService.guessRoundWord(1L, "banaan"));
    }

    @Test
    void guessRoundWordWithInvalidStateTest() {
        Game game = new Game("Test");
        game.setGameState(GameState.Paused);
        when(gameRepository.findById(game.getGameID())).thenReturn(Optional.of(game));
        assertThrows(GameException.class, () -> gameService.guessRoundWord(game.getGameID(), "banaan"));
    }

    @Test
    void updateGameStateTest() {
        Game game = new Game("Test");
        game.setGameState(GameState.Paused);
        when(gameRepository.findById(game.getGameID())).thenReturn(Optional.of(game));
        assertEquals("The gameState is: playing!", gameService.updateGameState(game.getGameID(), "playing"));
        assertEquals("The gameState is: Setup!", gameService.updateGameState(game.getGameID(), "setup"));
        assertEquals("The gameState is: Stopped!", gameService.updateGameState(game.getGameID(), "stopped"));
        assertEquals("The gameState is: Paused!", gameService.updateGameState(game.getGameID(), "paused"));
        assertEquals("The gameState is: Paused!", gameService.updateGameState(game.getGameID(), "round"));
    }
}