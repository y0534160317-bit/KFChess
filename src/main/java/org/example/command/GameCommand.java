package org.example.command;

public class GameCommand {
    public enum Type {
        MOVE, JUMP, TICK, HELP, EXIT, PRINT, CLICK, WAIT, UNKNOWN
    }

    private final Type type;
    private final String[] args;

    public GameCommand(Type type, String[] args) {
        this.type = type;
        this.args = args != null ? args : new String[0];
    }

    public Type getType() {
        return type;
    }

    public String[] getArgs() {
        return args;
    }
}