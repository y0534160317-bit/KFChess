package org.kfchess.server.realtime;

import org.kfchess.server.model.Board;

import java.util.ArrayList;
import java.util.List;

public class MotionConflictResolver {

    private final CollisionResolver collisionResolver =
            new CollisionResolver();

    public List<JumpCapture> resolve(
            List<ActiveMotion> activeMotions,
            Board board,
            long currentTimeMillis) {
        List<JumpCapture> captures = new ArrayList<>();

        resolveAirCollisions(activeMotions, board);
        resolveAirCapturesByJumps(activeMotions, board);
        resolveJumpCapturesMovingPieces(
                activeMotions,
                board,
                currentTimeMillis,
                captures);
        return captures;

    }

    private void resolveAirCollisions(
            List<ActiveMotion> activeMotions,
            Board board) {
        for (int i = 0; i < activeMotions.size(); i++) {
            ActiveMotion first = activeMotions.get(i);
            if (first.isCancelled() || first.isJump()) continue;

            for (int j = i + 1; j < activeMotions.size(); j++) {
                ActiveMotion second = activeMotions.get(j);
                if (second.isCancelled() || second.isJump()) continue;

                if (first.getPiece().getColor() == second.getPiece().getColor()) continue;


                boolean headOnCollision = first.getSource().equals(second.getDestination())
                        && first.getDestination().equals(second.getSource());

                if (  headOnCollision) {
                    ActiveMotion winner = collisionResolver.determineWinner(first, second);
                    ActiveMotion loser = (winner == first) ? second : first;

                    loser.cancel();

                    // הסרת הכלי המפסיד מהלוח בצורה אנקפסולרית
                    board.removePiece(loser.getSource());
                }
            }


        }
    }

    private void resolveAirCapturesByJumps(
            List<ActiveMotion> activeMotions,
            Board board) {
        ActiveMotion activeJump = null;
        for (ActiveMotion motion : activeMotions) {
            if (motion.isJump() && !motion.isCancelled()) {
                activeJump = motion;
                break;
            }
        }

        if (activeJump == null) return;

        for (ActiveMotion motion : activeMotions) {
            if (!motion.isJump() && !motion.isCancelled()
                    && motion.getDestination().equals(activeJump.getDestination())
                    && motion.getPiece().getColor() != activeJump.getPiece().getColor()) {

                if (motion.getStartTimeMillis() == activeJump.getStartTimeMillis()) {
                    motion.cancel();
                    // תיקון המשתנה: שימוש ב-motion.getSource() במקום loser שלא היה קיים בפונקציה זו
                    board.removePiece(motion.getSource());
                }
            }
        }
    }

    private void resolveJumpCapturesMovingPieces(
            List<ActiveMotion> activeMotions,
            Board board,
            long currentTimeMillis,
            List<JumpCapture> captures) {

        for (ActiveMotion jumper : activeMotions) {

            if (jumper.isCancelled() || !jumper.isJump()) {
                continue;
            }

            for (ActiveMotion mover : activeMotions) {

                if (mover == jumper) {
                    continue;
                }

                if (mover.isCancelled()) {
                    continue;
                }

                if (jumper.isComplete(currentTimeMillis)) {
                    continue;
                }

                if (mover.isJump()) {
                    continue;
                }

                if (jumper.getPiece().getColor()
                        == mover.getPiece().getColor()) {
                    continue;
                }

                // הכלי הנע חייב להגיע למשבצת
                if (!mover.isComplete(currentTimeMillis)) {
                    continue;
                }

                // הוא חייב להגיע ליעד של הקופץ
                if (!mover.getDestination().equals(jumper.getDestination())) {
                    continue;
                }

                captures.add(
                        new JumpCapture(
                                jumper.getPiece(),
                                mover.getPiece(),
                                mover.getSource(),
                                mover.getDestination()
                        )
                );

                mover.cancel();
                board.removePiece(mover.getSource());
            }
        }
    }


}