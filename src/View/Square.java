package View;

import Controller.Board;
import Model.Point;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Square extends JPanel implements MouseListener {

    private static final int LENGTH=70;
    private static final int PIECE_BUFFER = 5;
    private static final Board board = Board.getInstance();

    private int x;
    private int y;

    public Square(int x, int y) {
        this.x = x;
        this.y = y;
        setPreferredSize(new Dimension(LENGTH, LENGTH));
        setBackground(new Color(0, 200, 200));
        setBorder(BorderFactory.createLineBorder(new Color(5, 30, 10)));
        addMouseListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int color = board.getColor(x,y);


        if(color == Board.WHITE) {
            g.setColor(Color.white);
            g.fillOval(PIECE_BUFFER, PIECE_BUFFER,
                    LENGTH - 2*PIECE_BUFFER,
                    LENGTH - 2*PIECE_BUFFER);
        }
        else if (color == Board.BLACK){
            g.setColor(Color.black);
            g.fillOval(PIECE_BUFFER, PIECE_BUFFER,
                    LENGTH - 2*PIECE_BUFFER,
                    LENGTH - 2*PIECE_BUFFER);
        }
        else if(board.getAvailableMoves().contains(new Point(x,y))) {
            int current = board.getCurrentPlayer();
            if(current == Board.WHITE) {
                g.setColor(new Color(255,255,255,80));
                g.fillOval(PIECE_BUFFER, PIECE_BUFFER,
                        LENGTH - 2*PIECE_BUFFER,
                        LENGTH - 2*PIECE_BUFFER);
            }
            else if (current == Board.BLACK) {
                g.setColor(new Color(0, 0, 0, 60));
                g.fillOval(PIECE_BUFFER, PIECE_BUFFER,
                        LENGTH - 2 * PIECE_BUFFER,
                        LENGTH - 2 * PIECE_BUFFER);
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        board.play(new Point(x,y));
        GUI.getInstance().paint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        setBackground(new Color(0, 180, 180));
    }

    @Override
    public void mouseExited(MouseEvent e) {
        setBackground(new Color(0, 200, 200));
    }
}
