package org.kfchess.view.panels;

import org.kfchess.events.MoveEvent;
import org.kfchess.events.MoveObserver;
import org.kfchess.model.Piece;
import org.kfchess.view.MoveNotationFormatter;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MovesPanel extends JPanel implements MoveObserver {

    private final Piece.Color panelColor;
    private final DefaultTableModel tableModel;
    private final JTable table;

    private static final Color DARK = new Color(181, 136, 99);
    private static final Color LIGHT = new Color(240, 217, 181);
    private static final Color BACKGROUND = new Color(248, 242, 233);
    private static final Color TEXT = new Color(74, 52, 38);
    private static final Color BORDER = new Color(160, 120, 88);

    public MovesPanel(Piece.Color panelColor) {

        this.panelColor = panelColor;

        setLayout(new BorderLayout());
        setBackground(LIGHT);

        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 2),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));

        JLabel title = new JLabel(
                panelColor == Piece.Color.BLACK
                        ? "♚ Black Moves"
                        : "♔ White Moves"
        );

        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        title.setOpaque(true);
        title.setBackground(DARK);
        title.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        add(title, BorderLayout.NORTH);

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

        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.setRowHeight(28);

        table.setBackground(BACKGROUND);
        table.setForeground(TEXT);

        table.setSelectionBackground(new Color(220, 200, 170));
        table.setSelectionForeground(TEXT);

        table.setGridColor(BACKGROUND);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);

        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        table.getTableHeader().setBackground(DARK);
        table.getTableHeader().setForeground(Color.WHITE);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);

        table.getColumnModel().getColumn(0).setCellRenderer(center);
        table.getColumnModel().getColumn(1).setCellRenderer(center);

        table.getColumnModel().getColumn(0).setPreferredWidth(70);
        table.getColumnModel().getColumn(0).setMaxWidth(70);

        JScrollPane scrollPane = new JScrollPane(table);

        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(BACKGROUND);

        add(scrollPane, BorderLayout.CENTER);

        setPreferredSize(new Dimension(220, 0));
    }

    @Override
    public void onMoveCompleted(MoveEvent event) {

        if (event.getPiece().getColor() != panelColor) {
            return;
        }

        tableModel.addRow(new Object[]{
                formatTime(event.getGameTimeMillis()),
                MoveNotationFormatter.format(event)
        });

        table.scrollRectToVisible(
                table.getCellRect(table.getRowCount() - 1, 0, true)
        );
    }

    private String formatTime(long millis) {

        long totalSeconds = millis / 1000;

        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;

        return String.format("%02d:%02d", minutes, seconds);
    }
}