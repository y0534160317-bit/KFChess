package org.kfchess.shared;

import org.kfchess.shared.model.GameCommand;

import java.util.Arrays;

public class CommandParser {

    public GameCommand parse(String input) throws IllegalArgumentException {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("הפקודה אינה יכולה להיות ריקה.");
        }

        String[] tokens = input.trim().split("\\s+");
        String commandWord = tokens[0].toUpperCase();
        String[] args = Arrays.copyOfRange(tokens, 1, tokens.length);

        try {
            GameCommand.Type type = GameCommand.Type.valueOf(commandWord);
            return new GameCommand(type, args);
        } catch (IllegalArgumentException e) {
            // במקום לבלוע את השגיאה, אנחנו מייצרים חיווי ברור על פקודה לא מוכרת
            throw new IllegalArgumentException("פקודה לא מוכרת: " + commandWord);
        }
    }
}