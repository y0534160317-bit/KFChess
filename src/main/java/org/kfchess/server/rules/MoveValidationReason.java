package org.kfchess.server.rules;

public enum MoveValidationReason {

    VALID,

    NO_PIECE,

    WRONG_TURN,

    SAME_POSITION,

    ILLEGAL_PATTERN,

    PATH_BLOCKED,

    DESTINATION_OCCUPIED,

    OUT_OF_BOUNDS,

    PIECE_BUSY,

    GAME_OVER
}
