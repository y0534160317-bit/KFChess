package org.example.command;

import org.example.core.GameEngine;
import org.example.input.InteractionHandler;
import org.example.io.BoardPrinter;
import org.example.realtime.RealTimeArbiter;

public class CommandExecutor {
    private final GameEngine gameEngine;
    private final RealTimeArbiter arbiter;
    private final InteractionHandler interactionHandler; // הוספת הקונטרולר לטובת פקודות ה-Click

    public CommandExecutor(GameEngine gameEngine, RealTimeArbiter arbiter, InteractionHandler interactionHandler) {
        this.gameEngine = gameEngine;
        this.arbiter = arbiter;
        this.interactionHandler = interactionHandler;
    }

    public void execute(GameCommand command) {
        String[] args = command.getArgs();
        switch (command.getType()) {
            case CLICK:
                if (args.length >= 2) {
                    int x = Integer.parseInt(args[0]);
                    int y = Integer.parseInt(args[1]);
                    interactionHandler.handleClick(x, y);
                }
                break;
            case WAIT:
                if (args.length >= 1) {
                    long ms = Long.parseLong(args[0]);
                    arbiter.advanceTime(ms); // פקודת WAIT בטקסט מקבילה לקידום הזמן בארביטר
                }
                break;
            case PRINT:
                // הדפסת המצב הנוכחי של הלוח
                new BoardPrinter().print(gameEngine.getBoard());
                break;
            case TICK:
                if (args.length >= 1) {
                    long ms = Long.parseLong(args[0]);
                    arbiter.advanceTime(ms);
                }
                break;
            case EXIT:
                System.exit(0);
                break;
            case JUMP:
                if (args.length >= 2) {
                    int x = Integer.parseInt(args[0]);
                    int y = Integer.parseInt(args[1]);
                    // קריאה למתודת הטיפול בקפיצה/התחמקות בזמן אמת
                    interactionHandler.handleJump(x, y);
                }
                break;

            default:
                System.out.println("פקודה זו אינה נתמכת.");

        }
    }
}