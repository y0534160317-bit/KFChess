package org.kfchess.command;

import org.kfchess.server.core.GameEngine;
import org.kfchess.client.input.InteractionHandler;
import org.kfchess.io.BoardPrinter;
import org.kfchess.shared.model.GameCommand;
import org.kfchess.shared.model.Position;
import org.kfchess.server.realtime.RealTimeArbiter;

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