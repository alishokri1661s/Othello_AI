package View;

import Controller.Board;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class GUI extends JFrame implements MouseListener {

    private static GUI instance;
    private static Panel panel;
    private static Board board = Board.getInstance();
    private static JPanel main;
    private static EndingPanel endingPanel;

    public static GUI getInstance(){
        if (instance ==null)
            instance = new GUI();
        return instance;
    }

    private final Square[][] squares = new Square[8][8];

    private GUI(){
        setVisible(true);
        setTitle("Othello");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addMouseListener(this);

        main = new JPanel();
        main.setLayout(new BorderLayout());
        JPanel board = new JPanel();
        board.setLayout(new GridLayout(8, 8));
        for(int x = 0; x < 8; x++) {
            for(int y = 0; y < 8; y++) {
                board.add(squares[x][y] = new Square(x, y));
            }
        }
        board.setMaximumSize(new java.awt.Dimension(560, 560));
        board.setMinimumSize(new java.awt.Dimension(560, 560));
        board.setPreferredSize(new java.awt.Dimension(560, 560));

        main.add(board, BorderLayout.NORTH);

        setLayout(new FlowLayout());
        add(main, BorderLayout.NORTH);
        panel = new Panel();
        add(panel);

        endingPanel = new EndingPanel();
        setGlassPane(endingPanel);
        getGlassPane().setVisible(false);

        pack();
        setSize(600, 750);
        setResizable(false);

        paint();
        //main.setVisible(false);

    }

    public void endGame(){
        endingPanel.setVisible(true);
        endingPanel.endGame();

    }

    public void paint() {

        for(int x = 0; x < 8; x++) {
            for(int y = 0; y < 8; y++) {
                squares[x][y].repaint();
            }
        }
        super.repaint();
        panel.setScores(board.getWhiteScore(),board.getBlackScore());
        panel.setTurnLabel(board.currentPlayer);
        panel.repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON3)
            endGame();
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
