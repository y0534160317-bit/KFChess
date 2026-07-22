package org.kfchess.server.rules;

public class MoveValidationResult {

    private final boolean valid;
    private final MoveValidationReason reason;

    public MoveValidationResult(
            boolean valid,
            MoveValidationReason reason) {

        this.valid = valid;
        this.reason = reason;
    }

    public boolean isValid() {
        return valid;
    }

    public MoveValidationReason getReason() {
        return reason;
    }

    public static MoveValidationResult valid() {
        return new MoveValidationResult(
                true,
                MoveValidationReason.VALID);
    }

    public static MoveValidationResult invalid(
            MoveValidationReason reason) {

        return new MoveValidationResult(
                false,
                reason);
    }
}
