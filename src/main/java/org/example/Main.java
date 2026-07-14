package org.example;

import org.example.command.CommandExecutor;
import org.example.command.CommandParser;
import org.example.command.GameCommand;
import org.example.core.GameEngine;
import org.example.model.BoardParser;
import org.example.model.Board;
import org.example.model.GameState;
import org.example.realtime.CollisionResolver;
import org.example.realtime.RealTimeArbiter;
import org.example.rules.RuleEngine;
import org.example.input.InteractionHandler;
import org.example.view.GameWindow;    // הוספת ה-View
import org.example.view.ImgRenderer;   // הוספת ה-View

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

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

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

            if (readingBoard) boardLines.add(line);
            else if (readingCommands) commandLines.add(line);
        }
        scanner.close();


        // ניסוי: אתחל את ה-View פה מיד!
        System.out.println("Starting Window...");

        try {
            // 1. אתחול ליבת המודל
            Board board = BoardParser.parse(boardLines);
            CollisionResolver resolver = new CollisionResolver();
            RealTimeArbiter arbiter = new RealTimeArbiter(board, resolver);
            RuleEngine ruleEngine = new RuleEngine();
            GameState gameState = new GameState();

            // 2. אתחול ה-Engine
            GameEngine gameEngine = new GameEngine(board, arbiter, ruleEngine, gameState);

            // 3. אתחול רכיבי הקלט והפקודות
            CommandParser parser = new CommandParser();
            InteractionHandler interactionHandler = new InteractionHandler(gameEngine);
            CommandExecutor executor = new CommandExecutor(gameEngine, arbiter, interactionHandler);

            // 4. אתחול רכיבי ה-View (הוספה חדשה)
            ImgRenderer renderer = new ImgRenderer();
            GameWindow window = new GameWindow(gameEngine, interactionHandler, renderer);

            // הפעלת החלון הגרפי
            window.start();

            // 5. הרצת פקודות (טקסטואליות)
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