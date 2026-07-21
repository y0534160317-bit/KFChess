//package org.kfchess.network;
//
//import org.kfchess.core.GameEngine;
//import org.kfchess.input.GameClient;
//import org.kfchess.model.Piece;
//import org.kfchess.model.Position;
//import org.kfchess.view.GameSnapshot;
//
//public class LocalGameClient implements GameClient {
//
//    private final GameEngine engine;
//
//    public LocalGameClient(GameEngine engine) {
//        this.engine = engine;
//    }
//
//    @Override
//    public boolean isGameOver() {
//        return engine.isGameOver();
//    }
//
//    @Override
//    public boolean requestMove(Position source, Position destination) {
//        return engine.requestMove(source, destination);
//    }
//
//    @Override
//    public boolean requestJump(Position source, Position destination) {
//        return engine.requestJump(source, destination);
//    }
//
//    @Override
//    public void advanceTime(long currentTimeMillis) {
//        engine.advanceTime(currentTimeMillis);
//    }
//
//    @Override
//    public GameSnapshot snapshot(Piece selectedPiece) {
//        return engine.snapshot(selectedPiece);
//    }
//}