package org.example;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameController {
    private final Board board;
    private Position selectedPosition;
    private long gameTimeMillis;
    private boolean isGameOver;

    // List to manage all pieces currently moving in real-time
    private final List<ActiveMove> activeMoves;

    // Constant for Iteration 6: every move takes exactly 1000ms
    private static final long MOVE_DURATION_PER_SQUARE = 1000;

    public GameController(Board board) {
        this.board = board;
        this.selectedPosition = null; // No piece selected at start
        this.gameTimeMillis = 0;      // Game clock starts at 0
        this.activeMoves = new ArrayList<>();
    }

    // Process a click event at pixel coordinates (x, y)
    public void handleClick(int x, int y) {

        if (isGameOver) {
            return;
        }
        // Convert pixel coordinates to board indices
        int row = y / Board.CELL_SIZE;
        int col = x / Board.CELL_SIZE;

        Position clickedPos = new Position(row, col);

        // Ignore clicks that are outside the board boundaries
        if (!board.isWithinBounds(clickedPos)) {
            return;
        }

        Piece clickedPiece = board.getPiece(clickedPos);

        // Case 1: No piece is currently selected
        if (selectedPosition == null) {
            if (clickedPiece != null) {
                // NO REDIRECT: Only select the piece if it is not already mid-move
                if (!isPieceMovingFrom(clickedPos)) {
                    selectedPosition = clickedPos; // Select the piece
                }
            }
            return;
        }

        // Case 2: A piece is already selected
        Piece selectedPiece = board.getPiece(selectedPosition);

        if (clickedPiece != null && clickedPiece.getColor() == selectedPiece.getColor()) {
            // Clicked another friendly piece -> change selection (if it's not moving)
            if (!isPieceMovingFrom(clickedPos)) {
                selectedPosition = clickedPos;
            }
        } else {
            // Determine the opponent color
            Piece.Color opponentColor = (selectedPiece.getColor() == Piece.Color.WHITE) ? Piece.Color.BLACK : Piece.Color.WHITE;

            // NO CONCURRENT OPPOSITE MOVEMENT: Check if an opponent piece is already moving
            if (!isColorMoving(opponentColor) && !isPieceMovingTo(clickedPos) && isValidMove(selectedPosition, clickedPos, selectedPiece)) {
                // 1. Calculate how many squares the piece is traveling
                int distance = calculateDistance(selectedPosition, clickedPos);

                // 2. Calculate dynamic travel time (1000ms per square)
                long totalTravelTime = distance * MOVE_DURATION_PER_SQUARE;
                long arrivalTime = this.gameTimeMillis + totalTravelTime;

                // Register the active move instead of moving instantly
                activeMoves.add(new ActiveMove(selectedPosition, clickedPos, selectedPiece, arrivalTime));
            }
            selectedPosition = null; // Clear selection after a move attempt
        }
    }

    // Helper to calculate the move distance in squares (Chebyshev distance)
    private int calculateDistance(Position from, Position to) {
        int deltaRow = Math.abs(to.getRow() - from.getRow());
        int deltaCol = Math.abs(to.getCol() - from.getCol());
        return Math.max(deltaRow, deltaCol);
    }

    // Helper to check if a piece on a specific position is already in transit
    private boolean isPieceMovingFrom(Position pos) {
        for (ActiveMove move : activeMoves) {
            if (move.getFrom().equals(pos)) {
                return true;
            }
        }
        return false;
    }

    // Helper to check if a destination square is already reserved by another moving piece
    private boolean isPieceMovingTo(Position pos) {
        for (ActiveMove move : activeMoves) {
            if (move.getTo().equals(pos)) {
                return true;
            }
        }
        return false;
    }


    // Dynamic validation helper: checks if a specific square is the future landing spot of a friendly moving piece
    private boolean isSquareOccupiedByActiveMove(Position pos, Piece.Color movingColor) {
        for (ActiveMove move : activeMoves) {
            if (move.getTo().equals(pos) && move.getPiece().getColor() == movingColor) {
                return true;
            }
        }
        return false;
    }

    // Dynamic path validation: evaluates both static obstacles and active movements of friendly pieces
    private boolean isPathClearWithActiveMoves(Position from, Position to, Piece.Color pieceColor) {
        int startRow = from.getRow();
        int startCol = from.getCol();
        int endRow = to.getRow();
        int endCol = to.getCol();

        int deltaRow = endRow - startRow;
        int deltaCol = endCol - startCol;

        int stepRow = Integer.compare(deltaRow, 0);
        int stepCol = Integer.compare(deltaCol, 0);

        int currentRow = startRow + stepRow;
        int currentCol = startCol + stepCol;

        // Traverse through the path squares until reaching the destination
        while (currentRow != endRow || currentCol != endCol) {
            Position currentPos = new Position(currentRow, currentCol);

            // Check 1: Static collision on board
            if (board.getPiece(currentPos) != null) {
                return false;
            }

            // Check 2: Dynamic collision (friendly piece landing on this path square ahead)
            if (isSquareOccupiedByActiveMove(currentPos, pieceColor)) {
                return false;
            }

            currentRow += stepRow;
            currentCol += stepCol;
        }

        return true;
    }

    // Comprehensive helper to validate if a move complies with all chess mechanics for Iteration 3
    private boolean isValidMove(Position from, Position to, Piece piece) {

        // Target cannot be the same as origin
        if (from.equals(to)) {
            return false;
        }

        // Route pawn movement validation to the specific class
        if (piece.getType() == Piece.Type.PAWN) {
            return PawnMoveValidator.isValidPawnMove(from, to, piece, board);
        }

        // Existing logic for all other pieces
        int deltaRow = to.getRow() - from.getRow();
        int deltaCol = to.getCol() - from.getCol();

        // 1. Check if the piece's pattern allows this jump geometry
        if (!piece.getType().isValidMoveShape(deltaRow, deltaCol)) {
            return false;
        }

        // 2. Validate destination square: cannot capture a friendly piece
        Piece targetPiece = board.getPiece(to);
        if (targetPiece != null && targetPiece.getColor() == piece.getColor()) {
            return false;
        }

        // 3. Validate destination square dynamically: cannot land on a reserved friendly square
        if (isSquareOccupiedByActiveMove(to, piece.getColor())) {
            return false;
        }

        // 3. Validate path clearance for sliding pieces (Knight jumps over blockers)
        if (piece.getType() != Piece.Type.KNIGHT) {
            if (!isPathClearWithActiveMoves(from, to, piece.getColor())) {
                return false;
            }
        }

        return true;
    }

    // Advance the game clock
    public void advanceTime(long millis) {
        if (millis > 0) {
            this.gameTimeMillis += millis;
        }

        // Use Iterator to safely remove items from the list while iterating
        Iterator<ActiveMove> iterator = activeMoves.iterator();
        while (iterator.hasNext()) {
            // תיקון: הגדרת הטיפוס ActiveMove בצורה מפורשת
            ActiveMove move = iterator.next();

            // Check if the game clock has reached or passed the arrival time
            if (move.isComplete(this.gameTimeMillis)) {
                // Check if the landing square contains an enemy King BEFORE completing the physical move
                Piece targetPiece = board.getPiece(move.getTo());
                if (targetPiece != null && targetPiece.getType() == Piece.Type.KING) {
                    isGameOver = true;
                }
                // NO COOLDOWN: Execute physical move immediately
                board.movePiece(move.getFrom(), move.getTo());
                iterator.remove(); // Move is finished, remove from active list
            }
        }
    }

    // Helper to check if any piece of a specific color is currently moving
    private boolean isColorMoving(Piece.Color color) {
        for (ActiveMove move : activeMoves) {
            if (move.getPiece().getColor() == color) {
                return true;
            }
        }
        return false;
    }

    // Standard print delegate
    public void printBoard() {
        board.print();
    }
}
