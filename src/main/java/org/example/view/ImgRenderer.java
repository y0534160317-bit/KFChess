package org.example.view;

//import org.example.GameSnapshot;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.*;
import org.example.realtime.ActiveMotion;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImgRenderer {
    private static final int CELL_SIZE = 100;

    public ImgRenderer() {
        System.out.println("ImgRenderer Constructor started!");
        loadPieceAnimations();
        System.out.println("loadPieceAnimations finished!");
    }

    private static class AnimationState {
        AnimationConfig config;
        List<BufferedImage> frames = new ArrayList<>();
    }



    private final Map<String, Map<String, AnimationState>> pieceAnimations = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    //private final Map<String, Img> pieceImages = new HashMap<>();



    private void loadPieceAnimations() {
        String[] colors = {"w", "b"};
        String[] types = {"K", "Q", "R", "N", "B", "P"};
        String[] states = {"idle", "jump", "long_rest", "move", "short_rest"};

        for (String c : colors) {
            for (String t : types) {
                String pieceKey = c + t;
                pieceAnimations.put(pieceKey, new HashMap<>());

                for (String state : states) {
                    try {
                        String basePath = "/pieces/" + pieceKey + "/states/" + state;
                        // טעינת הקונפיג
                        InputStream configStream = getClass().getResourceAsStream(basePath + "/config.json");
                        if (configStream != null) {
                            AnimationState animState = new AnimationState();
                            animState.config = objectMapper.readValue(configStream, AnimationConfig.class);

                            // טעינת הפריימים מהתיקיה
                            for (int i = 1; i <= 5; i++) { // הנחה שיש עד 10 פריימים לדוגמה
                                InputStream imgStream = getClass().getResourceAsStream(basePath + "/sprites/" + i + ".png");
                                if (imgStream != null) {
                                    animState.frames.add(ImageIO.read(imgStream));
                                } else break;
                            }
                            pieceAnimations.get(pieceKey).put(state, animState);
                        }
                    } catch (Exception e) {
                        System.err.println("Failed to load state " + state + " for " + pieceKey);
                    }
                }
            }
        }
        String pieceKey = "wP"; // נבדוק רק כלי אחד כדי לא להציף את הקונסול
        String testState = "idle";
        String path = "pieces/" + pieceKey + "/states/" + testState + "/config.json";

        java.net.URL resource = getClass().getResource(path);
        System.out.println("Checking path: " + path);
        System.out.println("Resource found: " + (resource != null));
    }

    public Img render(GameSnapshot snapshot) {
        Board board = snapshot.getBoard();

        Img canvas = new Img();
        canvas.createEmpty(board.getWidth() * CELL_SIZE, board.getHeight() * CELL_SIZE);

        drawBoard(canvas, board);

        drawPieces(canvas, board, snapshot.getActiveMotions(),snapshot.getCurrentTimeMillis());
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

    private void drawPieces(Img canvas, Board board, List<ActiveMotion> motions, long currentTime) {
        for (int r = 0; r < board.getHeight(); r++) {
            for (int c = 0; c < board.getWidth(); c++) {
                Piece p = board.getPiece(new Position(r, c));
                // דלג אם הכלי נמצא בתנועה כדי לא לצייר אותו פעמיים
                if (p != null && !isPieceMoving(p, motions)) {
                    drawPiece(canvas, p, c * CELL_SIZE, r * CELL_SIZE, "idle", currentTime);
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
            drawPiece(canvas, m.getPiece(), currentX, currentY, m.getCurrentState(currentTime), currentTime);

        }
    }

    private void drawPiece(Img canvas, Piece p, int x, int y, String state, long currentTime) {
        String key = "" + p.getColor().getSymbol() + p.getType().getSymbol();
        Map<String, AnimationState> states = pieceAnimations.get(key);

        if (states != null && states.containsKey(state)) {
            AnimationState anim = states.get(state);
            if (!anim.frames.isEmpty()) {
                // חישוב אינדקס הפריים לפי FPS
                int frameIndex = (int) (((currentTime - 0) / 1000.0) * anim.config.graphics.frames_per_sec) % anim.frames.size();
                BufferedImage frame = anim.frames.get(frameIndex);

                Graphics2D g = canvas.get().createGraphics();
                g.drawImage(frame, x, y, CELL_SIZE, CELL_SIZE, null);
                g.dispose();
            }
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