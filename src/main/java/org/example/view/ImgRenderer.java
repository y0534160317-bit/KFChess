package org.example.view;

//import org.example.GameSnapshot;
import org.example.model.*;
import org.example.realtime.ActiveMotion;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImgRenderer {
    private static final int CELL_SIZE = 100;
    private final Map<String, Img> pieceImages = new HashMap<>();

    public ImgRenderer() {
        loadPieceImages();
    }

    private void loadPieceImages() {
        String[] colors = {"w", "b"};
        String[] types = {"K", "Q", "R", "N", "B", "P"};

        for (String c : colors) {
            for (String t : types) {
                String key = c + t;
                String path = "/pieces/" + key + ".png";

                try (java.io.InputStream is = getClass().getResourceAsStream(path)) {
                    if (is != null) {
                        // כאן צריך מתודה ב-Img שתדע לקרוא מ-InputStream
                        // אם אין לך, את יכולה להשתמש ב-ImageIO.read(is) ולהמיר ל-Img
                        pieceImages.put(key, new Img().readFromStream(is));
                    } else {
                        System.err.println("Could not find: " + path);
                    }
                } catch (Exception e) {
                    System.err.println("Failed to load: " + key + " - " + e.getMessage());
                }
            }
        }
    }

    public Img render(GameSnapshot snapshot) {
        Board board = snapshot.getBoard();

        Img canvas = new Img();
        canvas.createEmpty(board.getWidth() * CELL_SIZE, board.getHeight() * CELL_SIZE);
        drawBoard(canvas, board);
        drawPieces(canvas, board, snapshot.getActiveMotions());
        drawAnimations(canvas, snapshot.getActiveMotions(), snapshot.getCurrentTimeMillis());

        if (snapshot.getSelectedPosition() != null) {
            drawSelection(canvas, snapshot.getSelectedPosition());
        }
        return canvas;
    }

    private void drawBoard(Img canvas, Board board) {
//        System.out.println("Drawing board...");
        for (int r = 0; r < board.getHeight(); r++) {
            for (int c = 0; c < board.getWidth(); c++) {
                Color color = ((r + c) % 2 == 0) ? Color.WHITE : Color.GRAY;
                // ציור מלבן ב-canvas (דורש תמיכה ב-Graphics2D בתוך Img)
                Graphics2D g = canvas.get().createGraphics();
                g.setColor(color);
                g.fillRect(c * CELL_SIZE, r * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                g.dispose();
            }
        }
    }

    private void drawPieces(Img canvas, Board board, List<ActiveMotion> motions) {
        for (int r = 0; r < board.getHeight(); r++) {
            for (int c = 0; c < board.getWidth(); c++) {
                Piece p = board.getPiece(new Position(r, c));
                // דלג אם הכלי נמצא בתנועה כדי לא לצייר אותו פעמיים
                if (p != null && !isPieceMoving(p, motions)) {
                    drawPiece(canvas, p, c * CELL_SIZE, r * CELL_SIZE);
                }
            }
        }
    }

    private void drawAnimations(Img canvas, List<ActiveMotion> motions, long currentTime) {
        for (ActiveMotion m : motions) {
            if (m.isCancelled()) continue;

            double progress = (double)(currentTime - m.getStartTimeMillis()) / (m.getArrivalTimeMillis() - m.getStartTimeMillis());
            progress = Math.min(1.0, Math.max(0.0, progress));

            int startX = m.getSource().getCol() * CELL_SIZE;
            int startY = m.getSource().getRow() * CELL_SIZE;
            int endX = m.getDestination().getCol() * CELL_SIZE;
            int endY = m.getDestination().getRow() * CELL_SIZE;

            int currentX = startX + (int)((endX - startX) * progress);
            int currentY = startY + (int)((endY - startY) * progress);

            drawPiece(canvas, m.getPiece(), currentX, currentY);
        }
    }

    private void drawPiece(Img canvas, Piece p, int x, int y) {
        String key = "" + p.getColor().getSymbol() + p.getType().getSymbol();
        if (pieceImages.containsKey(key)) {
            pieceImages.get(key).drawOn(canvas, x, y);
        }
    }

    private boolean isPieceMoving(Piece p, List<ActiveMotion> motions) {
        return motions.stream().anyMatch(m -> m.getPiece().equals(p) && !m.isCancelled());
    }

    private void drawSelection(Img canvas, Position pos) {
        Graphics2D g = canvas.get().createGraphics();
        g.setColor(Color.YELLOW);
        g.setStroke(new BasicStroke(5));
        g.drawRect(pos.getCol() * CELL_SIZE, pos.getRow() * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        g.dispose();
    }
}