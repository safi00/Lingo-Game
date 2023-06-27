package nl.hu.cisq1.lingo.trainer.presentation;

import nl.hu.cisq1.lingo.trainer.application.GameService;
import nl.hu.cisq1.lingo.trainer.domain.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class GameControllerTest {
    private GameController gameController;

    @Mock
    private GameService gameService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        gameController = new GameController(gameService);
    }

    @Test
    public void getGameByIdTest() {
        Long id = 1L;
        Game game = new Game("John");
        when(gameService.getGameFromID(id)).thenReturn(game);

        ResponseEntity<Game> response = gameController.getGameById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(game, response.getBody());
        verify(gameService, times(1)).getGameFromID(id);
    }

    @Test
    public void getGamesTest() {
        List<Game> games = new ArrayList<>();
        games.add(new Game("John"));
        games.add(new Game("Jane"));
        when(gameService.getGames()).thenReturn(games);

        ResponseEntity<Game> response = gameController.getGames();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(games, response.getBody());
        verify(gameService, times(1)).getGames();
    }

    @Test
    public void getGameStateTest() {
        Long id = 1L;
        String expectedResponse = "The gameState is: playing!";
        when(gameService.getGameState(id)).thenReturn(expectedResponse);

        ResponseEntity<String> response = gameController.getGameState(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(gameService, times(1)).getGameState(id);
    }

    @Test
    public void startNewGameTest() {
        String name = "John";
        Game game = new Game(name);
        when(gameService.startNewGame(name)).thenReturn(game);

        ResponseEntity<Game> response = gameController.startNewGame(name);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(game, response.getBody());
        verify(gameService, times(1)).startNewGame(name);
    }

    @Test
    public void startNewRoundTest() {
        Long id = 1L;
        String feedback = "Round started";
        when(gameService.startNewRound(id)).thenReturn(feedback);

        ResponseEntity<String> response = gameController.startNewRound(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(feedback, response.getBody());
        verify(gameService, times(1)).startNewRound(id);
    }

    @Test
    public void guessTest() {
        Long id = 1L;
        String attempt = "word";
        String feedback = "feedback";
        when(gameService.guessRoundWord(id, attempt)).thenReturn(feedback);

        ResponseEntity<String> response = gameController.guessTheRoundWord(id, attempt);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(feedback, response.getBody());
        verify(gameService, times(1)).guessRoundWord(id, attempt);
    }

    @Test
    void stopGameTest() {
        Long id = 1L;
        String expectedGameState = "stopped";
        when(gameService.updateGameState(anyLong(), anyString())).thenReturn(expectedGameState);

        ResponseEntity<String> response = gameController.stopGame(id);

        assertEquals(expectedGameState, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}