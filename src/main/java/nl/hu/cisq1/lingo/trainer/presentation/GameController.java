package nl.hu.cisq1.lingo.trainer.presentation;

import nl.hu.cisq1.lingo.trainer.application.GameService;
import nl.hu.cisq1.lingo.trainer.domain.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trainer/game")
public class GameController {
    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Game> getGameById(@PathVariable("id") Long id) {
        return new ResponseEntity(this.gameService.getGameFromID(id), HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<Game> getGames() {
        return new ResponseEntity(this.gameService.getGames(), HttpStatus.OK);
    }

    @GetMapping("/{id}/gamestate")
    public ResponseEntity<String> getGameState(@PathVariable("id") Long id) {
        return new ResponseEntity(this.gameService.getGameState(id), HttpStatus.OK);
    }

    @PostMapping("/{name}")
    public ResponseEntity<Game> startNewGame(@PathVariable String name) {
        return new ResponseEntity(this.gameService.startNewGame(name), HttpStatus.OK);
    }

    @PostMapping("/newRound")
    public ResponseEntity<String> startNewRound(@RequestParam(name = "id") Long id) {
        String breakp = gameService.startNewRound(id);
        return new ResponseEntity(breakp, HttpStatus.OK);
    }

    @PutMapping("/guess")
    public ResponseEntity<String> guessTheRoundWord(@RequestParam(name = "id") Long id, @RequestParam String attempt) {
        return new ResponseEntity(gameService.guessRoundWord(id, attempt), HttpStatus.OK);
    }

    @PutMapping("/stopGame")
    public ResponseEntity<String> stopGame(@RequestParam(name = "id") Long id) {
        return new ResponseEntity(gameService.updateGameState(id, "stopped"), HttpStatus.OK);
    }
}
