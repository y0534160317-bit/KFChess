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

    private static class LoadedAnimation{
        AnimationConfig config;
        List<BufferedImage> frames = new ArrayList<>();
    }



    private final Map<String, Map<String, LoadedAnimation>> pieceAnimations = new HashMap<>();    private final ObjectMapper objectMapper = new ObjectMapper();

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
                            LoadedAnimation animState = new LoadedAnimation();
                            animState.config = objectMapper.readValue(configStream, AnimationConfig.class);

                            // טעינת הפריימים מהתיקיה
                            for (int i = 1; i <= 5; i++) { // הנחה שיש עד 10 פריימים לדוגמה
                                InputStream imgStream = getClass().getResourceAsStream(basePath + "/sprites/" + i + ".png");
                                if (imgStream != null) {
                                    animState.frames.add(ImageIO.read(imgStream));
                                    System.out.println("Loaded: " + basePath);
                                } else {


                                        System.out.println("NOT FOUND: " + basePath);


                                    break;}
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

        drawPieces(canvas, board, snapshot.getActiveMotions(),snapshot.getCurrentTimeMillis(),snapshot);
        drawAnimations(canvas, snapshot.getActiveMotions(), snapshot.getCurrentTimeMillis(),snapshot);

        if (snapshot.getSelectedPosition() != null) {
            drawSelection(canvas, snapshot.getSelectedPosition());
        }
        return canvas;
    }

    private void drawBoard(Img canvas, Board board) {


// System.out.println("Drawing board...");
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

    private void drawPieces(Img canvas, Board board, List<ActiveMotion> motions, long currentTime, GameSnapshot snapshot) {
        for (int r = 0; r < board.getHeight(); r++) {
            for (int c = 0; c < board.getWidth(); c++) {
                Piece p = board.getPiece(new Position(r, c));
                // דלג אם הכלי נמצא בתנועה כדי לא לצייר אותו פעמיים
                if (p != null && !isPieceMoving(p, motions)) {
                    drawPiece(
                            canvas,
                            p,
                            c * CELL_SIZE,
                            r * CELL_SIZE,
                            null,               // אין ActiveMotion
                            currentTime,
                            snapshot
                    );
                }
            }
        }
    }

    private void drawAnimations(Img canvas, List<ActiveMotion> motions, long currentTime,GameSnapshot snapshot) {
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
            drawPiece(
                    canvas,
                    m.getPiece(),
                    currentX,
                    currentY,
                    m,
                    currentTime,
                    snapshot
            );

        }
    }

    private void drawPiece(Img canvas, Piece p, int x, int y, ActiveMotion motion, long currentTime, GameSnapshot snapshot) {
        PieceVisualState visual =
                snapshot.getVisualStates().get(p);

        if (visual == null) {
            return;
        }
        AnimationState state;

        long animationTime;

        state = visual.getState();

        Graphics2D g = canvas.get().createGraphics();
        g.setColor(Color.RED);
        g.drawString(state.name(), x + 5, y + 15);
        g.dispose();
        if (motion != null) {


            animationTime =
                    currentTime - motion.getStartTimeMillis();

        } else {

            state = visual.getState();
            animationTime =
                    currentTime - visual.getStateStartTime();
        }

        String stateName;

        switch (state) {

            case IDLE:
                stateName = "idle";
                break;

            case MOVE:
                stateName = "move";
                break;

            case JUMP:
                stateName = "jump";
                break;

            case SHORT_REST:
                stateName = "short_rest";
                break;

            case LONG_REST:
                stateName = "long_rest";
                break;

            default:
                stateName = "idle";
        }


        String key = "" + p.getColor().getSymbol() + p.getType().getSymbol();

        Map<String, LoadedAnimation> states = pieceAnimations.get(key);

        if (states == null)
            return;

        LoadedAnimation anim = states.get(stateName);
        System.out.println(
                "state=" + stateName +
                        " anim=" + (anim != null) +
                        " frames=" + (anim == null ? 0 : anim.frames.size())
        );
        System.out.println(
                "state=" + stateName +
                        " loaded=" + (anim != null)
        );

        if (anim == null || anim.frames.isEmpty())
            return;



        int frameIndex =
                (int) ((animationTime / 1000.0)
                        * anim.config.graphics.frames_per_sec);


        if (anim.config.graphics.is_loop) {
            frameIndex %= anim.frames.size();
        } else {
            frameIndex = Math.min(frameIndex, anim.frames.size() - 1);
        }
        BufferedImage frame = anim.frames.get(frameIndex);


        Graphics2D dd = canvas.get().createGraphics();
        dd.drawImage(frame, x, y, CELL_SIZE, CELL_SIZE, null);
        dd.dispose();
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