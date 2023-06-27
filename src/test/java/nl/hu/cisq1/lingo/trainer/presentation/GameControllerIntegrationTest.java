package nl.hu.cisq1.lingo.trainer.presentation;

import nl.hu.cisq1.lingo.trainer.application.GameService;
import nl.hu.cisq1.lingo.trainer.data.GameRepository;
import nl.hu.cisq1.lingo.trainer.domain.Game;
import nl.hu.cisq1.lingo.words.application.WordService;
import nl.hu.cisq1.lingo.words.data.WordRepository;
import nl.hu.cisq1.lingo.words.domain.Word;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class GameControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private GameRepository gameRepository;
    @MockBean
    private WordRepository wordRepository;
    private Long gameId;

    @BeforeEach
    public void setup() {
        wordRepository.save(new Word("woord"));
        wordRepository.save(new Word("banaan"));
        wordRepository.save(new Word("student"));

        when(gameRepository.saveAndFlush(any(Game.class))).thenReturn(any());
        gameId = 1L;
    }

    @Test
    void startNewGame() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/trainer/game/TestPlayer")
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void startNewRound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/trainer/game/TestPlayer")
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void guessTheRoundWord() throws Exception {
        String guess = "guess";

        mockMvc.perform(MockMvcRequestBuilders.post("/trainer/game/TestPlayer")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void stopGame() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/trainer/game/TestPlayer")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }
}