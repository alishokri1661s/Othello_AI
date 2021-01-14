package Controller;

import Model.Point;
import View.GUI;


public class Main {
    public static void main(String[] args) {
        Board board =Board.getInstance();
        GUI gui = GUI.getInstance();
        board.play(new Point(1,1));
    }
}
