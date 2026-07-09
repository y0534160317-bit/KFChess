package org.example;

import org.example.core.GameEngine;
import org.example.io.BoardParser;
import org.example.model.Board;
import org.example.input.GameCommand;

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

            // Collect board configuration lines
            if (readingBoard) {
                boardLines.add(line);
            }
            // Collect command lines to execute later
            else if (readingCommands) {
                commandLines.add(line);
            }
        }
        scanner.close();

        // Process and validate the board inside a try-catch block
        try {
            // 1. Parse and create the initial board
            Board board = BoardParser.parse(boardLines);

            // 2. Initialize the game controller with the parsed board
            GameEngine gameEngine = new GameEngine(board);

            // 3. Process and execute each command sequentially
            for (String commandLine : commandLines) {
                GameCommand.parseAndExecute(commandLine, gameEngine);
            }

        } catch (IllegalArgumentException e) {
            // Prints the exact VPL validation error message
            System.out.println(e.getMessage());
        }
    }
}