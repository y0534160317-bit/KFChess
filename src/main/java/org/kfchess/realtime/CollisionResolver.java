package org.kfchess.realtime;

public class CollisionResolver {
    /**
     * קובע איזה מהלך מנצח במקרה של התנגשות ביעד.
     * המהלך שהתחיל מוקדם יותר מנצח. אם התחילו יחד, ה-sequence הנמוך מנצח.
     */
    public ActiveMotion determineWinner(ActiveMotion first, ActiveMotion second) {
        if (first.getStartTimeMillis() < second.getStartTimeMillis()) {
            return first;
        }
        if (second.getStartTimeMillis() < first.getStartTimeMillis()) {
            return second;
        }
        return first.getSequence() < second.getSequence() ? first : second;
    }
}