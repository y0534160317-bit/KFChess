package org.kfchess.view.panels;

import org.kfchess.events.MoveEvent;
import org.kfchess.events.MoveObserver;
import org.kfchess.model.Piece;
import org.kfchess.view.MoveNotationFormatter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MovesPanel extends JPanel implements MoveObserver {

    private final Piece.Color panelColor;
    private final DefaultTableModel tableModel;
    private final JTable table;

    public MovesPanel(Piece.Color panelColor) {

        this.panelColor = panelColor;

        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(
                new Object[]{"Time", "Move"},
                0
        ) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(table);

        add(scrollPane, BorderLayout.CENTER);

        setPreferredSize(new Dimension(180, 0));
    }

    @Override
    public void onMoveCompleted(MoveEvent event) {

        if (event.getPiece().getColor() != panelColor) {
            return;
        }

        tableModel.addRow(new Object[]{
                "",
                MoveNotationFormatter.format(event)
        });

    }
}