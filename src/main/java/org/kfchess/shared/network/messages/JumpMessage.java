package org.kfchess.shared.network.messages;

import org.kfchess.shared.model.Position;
import org.kfchess.shared.network.ClientMessage;

public class JumpMessage extends ClientMessage {

    private Position position;

    public JumpMessage() {
    }

    public JumpMessage(Position position) {
        this.position = position;
    }

    public int getRow() {
        return position.getRow();
    }

//    public void setRow(int row) {
//        this.position.se = row;
//    }

    public int getCol() {
        return position.getCol();
    }
//
//    public void setCol(int col) {
//        this.col = col;
//    }
}