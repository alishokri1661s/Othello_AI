package View;

import Controller.Board;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Panel extends JPanel implements ActionListener {

    private JLabel scoreLabel;
    private JLabel turnLabel;

    public Panel (){

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        JButton restartBtn = new JButton("Restart");
        restartBtn.addActionListener(this);

        turnLabel = new JLabel();
        turnLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        turnLabel.setFont(new Font("",Font.BOLD,20));

        scoreLabel = new JLabel();
        scoreLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        scoreLabel.setFont(new Font("",Font.BOLD,25));

        restartBtn.setMaximumSize(new Dimension(150,30));
        restartBtn.setPreferredSize(new Dimension(50,30));
        restartBtn.setFont(new Font("",Font.PLAIN,20));


        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        turnLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        restartBtn.setAlignmentX(Component.CENTER_ALIGNMENT);


        add(scoreLabel);
        add(turnLabel);
        add(Box.createRigidArea(new Dimension(0,20)));
        add(restartBtn);

    }

    public void setScores(int white,int black){
        scoreLabel.setText("White: " + white + " - " + black + " :Black");
    }

    public void setTurnLabel(int player){
        turnLabel.setText((player==Board.WHITE) ? "White's turn" : "Black's turn");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Restart"))
        {
            Board.getInstance().init();
            GUI.getInstance().paint();
        }
    }
}
