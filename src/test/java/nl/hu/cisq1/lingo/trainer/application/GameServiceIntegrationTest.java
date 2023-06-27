package nl.hu.cisq1.lingo.trainer.application;

import nl.hu.cisq1.lingo.trainer.data.GameRepository;
import nl.hu.cisq1.lingo.trainer.domain.Enums.GameState;
import nl.hu.cisq1.lingo.trainer.domain.Enums.RoundState;
import nl.hu.cisq1.lingo.trainer.domain.Game;
import nl.hu.cisq1.lingo.trainer.domain.Round;
import nl.hu.cisq1.lingo.trainer.exceptions.*;
import nl.hu.cisq1.lingo.words.application.WordService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class GameServiceIntegrationTest {
    @Autowired
    private GameService gameService;
    @MockBean
    private WordService wordService;
    @Autowired
    private GameRepository gameRepository;

    @BeforeEach
    void setUp() {
        gameRepository.deleteAll();
    }

    @Test
    void startGame() {
        when(wordService.provideRandomWord(5)).thenReturn("apple");
        Game game = gameService.startNewGame("player");

        assertEquals(1, gameRepository.count());
        assertEquals("player", game.getPlayerName());
        assertEquals(0, game.getScore());
        assertEquals(GameState.Playing, game.getGameState());
        assertEquals(0, game.getListOfRounds().size());
    }

    @Test
    void startNewRound() {
        when(wordService.provideRandomWord(5)).thenReturn("apple");
        when(wordService.provideRandomWord(6)).thenReturn("orange");
        when(wordService.provideRandomWord(7)).thenReturn("banana");
        when(wordService.wordExists("apple")).thenReturn(true);
        when(wordService.wordExists("orange")).thenReturn(true);

        Game game = gameService.startNewGame("player");
        long gameId = game.getGameID();
        gameService.startNewRound(gameId);
        game = gameService.getGameFromID(gameId);
        gameService.guessRoundWord(gameId, "apple");
        assertEquals(1, game.getListOfRounds().size());
        Round round = game.getListOfRounds().get(0);
        assertEquals(5, round.getRoundWordSize());
        assertEquals(RoundState.Playing, round.getRoundState());
    }

    @Test
    void startNewRoundGameDoesNotExist() {
        assertThrows(GameNotFindException.class, () -> gameService.startNewRound(1L));
    }

    @Test
    void guessRoundWord() {
        when(wordService.provideRandomWord(5)).thenReturn("apple");

        Game game = gameService.startNewGame("player");
        long gameId = game.getGameID();
        gameService.startNewRound(gameId);
        game = gameService.getGameFromID(gameId);
        String result = gameService.guessRoundWord(gameId, "apple");

        assertEquals("word does not exist The game is still ongoing! And you have 5 tries left!", result);

        when(wordService.wordExists("apple")).thenReturn(true);
        result = gameService.guessRoundWord(gameId, "apple");

        assertEquals("The last hint is : APPLE The game is still ongoing! And YOU WON THE ROUND! CREATE A NEW ONE TO CONTINUE!", result);
    }

    @AfterEach
    void clearTestData() {
        // Remove test fixtures from test database after each test case
        gameRepository.deleteAll();
    }
}