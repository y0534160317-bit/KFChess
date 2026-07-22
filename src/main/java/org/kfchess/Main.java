package org.kfchess;

import org.kfchess.command.CommandExecutor;
import org.kfchess.shared.CommandParser;
import org.kfchess.shared.model.GameCommand;
import org.kfchess.server.core.GameEngine;
import org.kfchess.shared.events.EventBus;
import org.kfchess.model.BoardParser;
import org.kfchess.server.model.Board;
import org.kfchess.shared.model.GameState;
import org.kfchess.server.model.ScoreManager;
import org.kfchess.server.realtime.CollisionResolver;
import org.kfchess.server.realtime.RealTimeArbiter;
import org.kfchess.server.rules.RuleEngine;
import org.kfchess.input.InteractionHandler;
import org.kfchess.client.view.GameWindow;    // הוספת ה-View
import org.kfchess.client.view.ImgRenderer;   // הוספת ה-View
import org.kfchess.client.view.panels.FooterPanel;
import org.kfchess.client.view.panels.HeaderPanel;

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
            EventBus eventBus = new EventBus(); // או שימוש במופע ה-EventBus הקיים במחחלקה הראשית
            ScoreManager scoreManager = new ScoreManager(eventBus);

            // 2. אתחול ה-Engine
            GameEngine gameEngine =
                    new GameEngine(
                            board,
                            arbiter,
                            ruleEngine,
                            gameState,
                            scoreManager,
                            eventBus
                    );
            // 3. אתחול רכיבי הקלט והפקודות
            CommandParser parser = new CommandParser();
            InteractionHandler interactionHandler = new InteractionHandler(gameEngine);
            CommandExecutor executor = new CommandExecutor(gameEngine, arbiter, interactionHandler);
            HeaderPanel headerPanel = new HeaderPanel(eventBus);
            FooterPanel footerPanel = new FooterPanel(eventBus);

            // 4. אתחול רכיבי ה-View (הוספה חדשה)
            ImgRenderer renderer = new ImgRenderer();
            GameWindow window = new GameWindow(
                    gameEngine,
                    interactionHandler,
                    renderer,
                    headerPanel,
                    footerPanel,
                    eventBus
            );
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