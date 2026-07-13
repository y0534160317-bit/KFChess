package org.example;

import org.example.command.CommandExecutor;
import org.example.command.CommandParser;
import org.example.command.GameCommand;
import org.example.core.GameEngine;
import org.example.io.BoardParser;
import org.example.model.Board;
import org.example.realtime.CollisionResolver;
import org.example.realtime.RealTimeArbiter;
import org.example.rules.RuleEngine;
import org.example.input.InteractionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<String> boardLines = new ArrayList<>();
        List<String> commandLines = new ArrayList<>();
        boolean readingBoard = false;
        boolean readingCommands = false;

        // Read all inputs line by line
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }

            if (line.equalsIgnoreCase("Board:")) {
                readingBoard = true;
                readingCommands = false;
                continue;
            }

            if (line.equalsIgnoreCase("Commands:")) {
                readingBoard = false;
                readingCommands = true;
                continue;
            }

            // Collect board static public final String var = ;nfiguration lines
            if (readingBoard) {
                boardLines.add(line);
            }
            // Collect command lines to execute later
            else if (readingCommands) {
                commandLines.add(line);
            }
        }
        scanner.close();

        try {
            // 1. יצירת הלוח
            Board board = BoardParser.parse(boardLines);

            // 2. אתחול מנועי התשתית (סדר החשיבות: Resolver -> Arbiter -> RuleEngine)
            CollisionResolver resolver = new CollisionResolver();
            RealTimeArbiter arbiter = new RealTimeArbiter(board, resolver);
            RuleEngine ruleEngine = new RuleEngine();

            // 3. אתחול ה-Engine עם הרכיבים החדשים
            GameEngine gameEngine = new GameEngine(board, arbiter, ruleEngine);

            // 4. אתחול רכיבי הפקודות (Parser ו-Executor במקום הקריאה הסטטית הישנה)


            CommandParser parser = new CommandParser();

            InteractionHandler interactionHandler = new org.example.input.InteractionHandler(board, gameEngine);
            CommandExecutor executor = new CommandExecutor(gameEngine, arbiter ,interactionHandler );

            // 5. הרצת פקודות
            for (String commandLine : commandLines) {
                try {
                    GameCommand command = parser.parse(commandLine);
                    executor.execute(command);
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }

        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}