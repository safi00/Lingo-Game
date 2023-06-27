package nl.hu.cisq1.lingo.trainer.domain;

import jakarta.persistence.*;
import nl.hu.cisq1.lingo.trainer.domain.Enums.Mark;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long feedbackID;
    @Enumerated(EnumType.STRING)
    private List<Mark> markList;
    private String hint;
    private String wordGuessed;
    private String roundWord;

    public Feedback() {
    }

    public Feedback(String roundWord, String attemptedWord, String prevHint) {
        this.roundWord = roundWord;
        this.wordGuessed = attemptedWord;
        this.markList = generateNewMarkList();
        this.hint = generateNewHint(prevHint);
    }

    private List<Mark> generateNewMarkList() {
        char[] roundWordList = stringToChar(roundWord);
        char[] wordGuessedList = stringToChar(wordGuessed);
        List<String> dummyCharacterList = new ArrayList<>();

        int roundWordLength = roundWord.length();

        List<Mark> returnList = new ArrayList<>();
        generateABlankMarkList(returnList, roundWordLength);

        // If their not the same size end immediately,
        // if they are then make the dummy list to compare them
        if (roundWordLength != wordGuessed.length()) {
            for (int index = 0; index < roundWordLength; index++) {
                returnList.set(index, Mark.INVALID);
            }
            return returnList;
        } else {
            for (char letter : roundWordList) {
                dummyCharacterList.add(String.valueOf(letter));
            }
        }
        for (int index = 0; index < roundWordLength; index++) {
            if (wordGuessedList[index] == roundWordList[index]) {
                returnList.set(index, Mark.CORRECT);
                dummyCharacterList.set(index, null);
            }
        }
        for (int index = 0; index < roundWordLength; index++) {
            if (dummyCharacterList.get(index) != null) {
                int charIndex = dummyCharacterList.indexOf(String.valueOf(wordGuessedList[index]));
                if (charIndex > -1) {
                    returnList.set(index, Mark.PRESENT);
                    dummyCharacterList.set(charIndex, "gone");
                }
            }
        }
        return returnList;
    }

    private String generateNewHint(String prevHint) {
        StringBuilder returnHint = new StringBuilder();
        char[] roundWordList = stringToChar(roundWord);
        char[] charHintList = stringToChar(prevHint);
        List<String> dummyCharacterList = new ArrayList<>();

        for (char letter : charHintList) {
            dummyCharacterList.add(String.valueOf(letter).toUpperCase());
        }

        for (int index = 0; index < roundWord.length(); index++) {
            if (Objects.equals(dummyCharacterList.get(index), ".")) {
                if (markList.get(index) == Mark.CORRECT) {
                    dummyCharacterList.set(index, String.valueOf(roundWordList[index]).toUpperCase());
                }
            }
        }
        for (String letter : dummyCharacterList) {
            returnHint.append(letter);
        }
        return returnHint.toString();
    }

    public boolean isWordGuessed() {
        for (Mark mark : markList) {
            if (mark == Mark.ABSENT) {
                return false;
            }
            if (mark == Mark.PRESENT) {
                return false;
            }
            if (mark == Mark.INVALID) {
                return false;
            }
        }
        return true;
    }

    public char[] stringToChar(String string) {
        char[] returnList = new char[string.toLowerCase().length()];
        // Copy character by character into array
        for (int i = 0; i < string.length(); i++) {
            returnList[i] = string.toLowerCase().charAt(i);
        }
        return returnList;
    }

    private void generateABlankMarkList(List<Mark> list, int length) {
        for (int i = 0; i < length; i++) {
            list.add(Mark.ABSENT);
        }
    }

    //region Setters & Getters
    public List<Mark> getMarkList() {
        return markList;
    }

    public String getHint() {
        return hint;
    }

    public String getWordGuessed() {
        return wordGuessed;
    }

    public String getRoundWord() {
        return roundWord;
    }
    //endregion
}
