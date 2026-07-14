package org.example.command;

import org.example.core.GameEngine;
import org.example.input.InteractionHandler;
import org.example.io.BoardPrinter;
import org.example.model.Position;
import org.example.realtime.RealTimeArbiter;

public class CommandExecutor {
    private final GameEngine gameEngine;
    private final RealTimeArbiter arbiter;
    private final InteractionHandler interactionHandler;

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
                    // פקודת טקסט מקבלת שורה ועמודה ישירות
                    int row = Integer.parseInt(args[0]);
                    int col = Integer.parseInt(args[1]);
                    interactionHandler.handleClick(new Position(row, col));
                }
                break;
            case JUMP:
                if (args.length >= 2) {
                    int row = Integer.parseInt(args[0]);
                    int col = Integer.parseInt(args[1]);
                    interactionHandler.handleJump(new Position(row, col));
                }
                break;
            case TICK:
            case WAIT:
                if (args.length >= 1) {
                    arbiter.advanceTime(Long.parseLong(args[0]));
                }
                break;
            case PRINT:
                new BoardPrinter().print(gameEngine.getBoard());
                break;
            case EXIT:
                System.exit(0);
                break;
            default:
                System.out.println("פקודה זו אינה נתמכת.");
        }
        new BoardPrinter().print(gameEngine.getBoard());
    }
}