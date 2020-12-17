package Controller;

import View.GUI;


public class Main {
    public static void main(String[] args) {
        Board board =Board.getInstance();
        board.init();
        GUI gui = GUI.getInstance();
    }
}
