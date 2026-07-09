package org.example.input;

import org.example.core.GameEngine;

public enum GameCommand {
    CLICK {
        @Override
        public void execute(GameEngine engine, String[] args) {
            int x = Integer.parseInt(args[1]);
            int y = Integer.parseInt(args[2]);
            engine.handleClick(x, y);
        }
    },
    JUMP {
        @Override
        public void execute(GameEngine engine, String[] args) {
            int x = Integer.parseInt(args[1]);
            int y = Integer.parseInt(args[2]);
            engine.handleJump(x, y);
        }
    },
    WAIT {
        @Override
        public void execute(GameEngine engine, String[] args) {
            long ms = Long.parseLong(args[1]);
            engine.advanceTime(ms);
        }
    },
    PRINT_BOARD {
        @Override
        public void execute(GameEngine engine, String[] args) {
            engine.printBoard();
        }
    },
    UNKNOWN {
        @Override
        public void execute(GameEngine engine, String[] args) {
            // פקודה לא מוכרת - לא עושים כלום
        }
    };

    // מתודה מופשטת שכל פקודה ב-Enum חייבת לממש
    public abstract void execute(GameEngine engine, String[] args);

    // מפעל (Factory) פנימי שמקבל שורה, מפענח אותה ומריץ מייד
    public static void parseAndExecute(String line, GameEngine engine) {
        String lower = line.toLowerCase();
        String[] tokens = line.split("\\s+");

        GameCommand command = UNKNOWN;

        if (lower.startsWith("click") && tokens.length == 3) command = CLICK;
        else if (lower.startsWith("jump") && tokens.length == 3) command = JUMP;
        else if (lower.startsWith("wait") && tokens.length == 2) command = WAIT;
        else if (lower.equals("print board")) command = PRINT_BOARD;

        command.execute(engine, tokens);
    }
}