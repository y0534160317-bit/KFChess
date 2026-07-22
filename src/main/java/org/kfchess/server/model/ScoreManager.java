package org.kfchess.server.model;

import org.kfchess.shared.events.EventBus;
import org.kfchess.shared.events.EventListener;
import org.kfchess.shared.events.MoveEvent;
import org.kfchess.shared.events.ScoreChangedEvent;
import org.kfchess.shared.model.Piece;

public class ScoreManager implements EventListener<MoveEvent> {

    private int whiteScore;
    private int blackScore;
    private final EventBus eventBus;

    @Override
    public void onEvent(MoveEvent event) {
        // המידע על הכלי שנתפס מגיע כחלק מהמהלך שהושלם (אם נרצה נוכל לוודא או להעביר את ה-CompletedMove / CapturedPiece ישירות)
        // לחלופין, אם ה-MoveEvent מכיל את המידע או שנזין את הטיפול דרך CompletedMove:
    }

    public ScoreManager(EventBus eventBus) {
        this.eventBus = eventBus;
        // הרשמה אוטומטית ל-EventBus עבור MoveEvent
        this.eventBus.subscribe(MoveEvent.class, this);
    }

    public void handleCapturedPiece(Piece capturedPiece) {

        if (capturedPiece == null) {
            return;
        }

        int value = getValue(capturedPiece);

        if (capturedPiece.getColor() == Piece.Color.WHITE) {
            blackScore += value;
        } else {
            whiteScore += value;
        }
        eventBus.publish(new ScoreChangedEvent(whiteScore, blackScore));
    }

    public int getWhiteScore() {
        return whiteScore;
    }

    public int getBlackScore() {
        return blackScore;
    }

    private int getValue(Piece piece) {

        switch (piece.getType()) {

            case PAWN:
                return 1;

            case KNIGHT:
            case BISHOP:
                return 3;

            case ROOK:
                return 5;

            case QUEEN:
                return 9;

            case KING:
                return 0;
        }

        return 0;
    }
}
