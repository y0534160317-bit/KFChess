package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<String> boardLines = new ArrayList<>();
        boolean readingBoard = false;
        boolean hasPrintCommand = false;

        // Read all inputs line by line
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }

            if (line.equalsIgnoreCase("Board:")) {
                readingBoard = true;
                continue;
            }

            if (line.equalsIgnoreCase("Commands:")) {
                readingBoard = false;
                continue;
            }

            // Collect board configuration lines
            if (readingBoard) {
                boardLines.add(line);
            }
            // Check for execution commands
            else if (line.equalsIgnoreCase("print board")) {
                hasPrintCommand = true;
            }
        }
        scanner.close();

        // Process and validate the board inside a try-catch block
        try {
            Board board = BoardParser.parse(boardLines);

            // Execute the print command if requested
            if (hasPrintCommand) {
                board.print();
            }
        } catch (IllegalArgumentException e) {
            // Prints the exact VPL validation error message
            System.out.println(e.getMessage());
        }
    }
}