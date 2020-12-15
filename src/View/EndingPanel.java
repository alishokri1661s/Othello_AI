package View;

import Controller.Board;
import com.sun.deploy.panel.JavaPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.Guard;

public class EndingPanel extends JPanel implements ActionListener {

    JLabel jLabel;
    JLabel scoreLabel;

    public EndingPanel(){

        /*JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.PAGE_AXIS));
*/
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        //setLayout(new BorderLayout());
        //setLayout(new FlowLayout());
        setBackground(new Color(100,100,100));
        setSize(600,750);
        setMaximumSize(new Dimension(600,750));
        setMinimumSize(new Dimension(600,750));
        setPreferredSize(new Dimension(600,750));

        jLabel = new JLabel("Whose winner?",SwingConstants.CENTER);
        jLabel.setForeground(Color.GREEN);

        jLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jLabel.setFont(new Font("",Font.BOLD,50));

        scoreLabel = new JLabel();
        //scoreLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        scoreLabel.setFont(new Font("",Font.BOLD,30));


        JButton jButton = new JButton("Retry");
        jButton.addActionListener(this);
        jButton.setSize(300,100);
        jButton.setMaximumSize(new Dimension(150,40));
        jButton.setFont(new Font("Arial",Font.PLAIN,25));

        jLabel.setAlignmentX(CENTER_ALIGNMENT);
        jButton.setAlignmentX(CENTER_ALIGNMENT);
        scoreLabel.setAlignmentX(CENTER_ALIGNMENT);


        add(Box.createRigidArea(new Dimension(0,220)));
        add(jLabel);
        //add(Box.createRigidArea(new Dimension(0,10)));
        add(scoreLabel);
        add(Box.createRigidArea(new Dimension(0,50)));
        add(jButton);
    }

    public void endGame(){
        int black = Board.getInstance().getBlackScore();
        int white = Board.getInstance().getWhiteScore();
        if (black>white)
            jLabel.setText("BLACK wins");
        else if (white>black)
            jLabel.setText("WHITE wins");
        else
            jLabel.setText("draw");

        scoreLabel.setText("White: " + white + " - " + black + " :Black");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("Retry"))
        {
            Board.getInstance().init();
            setVisible(false);
            GUI.getInstance().paint();
        }
    }
}
