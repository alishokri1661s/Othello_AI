package Controller;
import Model.Point ;

import java.util.Arrays;
import java.util.Date;

public class Agent {
    Board mainBoard = Board.getInstance();
    private static int[][] weights = {{99, -8, 8, 6, 6, 8, -8, 99},
            {-8, -24, -4, -3, -3, -4, -24, -8},
            {8, -4, 7, 4, 4, 7, -4, 8},
            {6, -3, 4, 0, 0, 4, 3, 6},
            {6, -3, 4, 0, 0, 4, 3, 6},
            {8, -4, 7, 4, 4, 7, -4, 8},
            {-8, -24, -4, -3, -3, -4, -24, -8},
            {99, -8, 8, 6, 6, 8, -8, 99}};
    private static int numberOfChildren = 0;
    private static int maxDepth = 7;

//    private int PointSort(){
//
//    }
    //hello

    private int heuristic(Board board) {

        //double[] X = new double[2] ;

        int linearSum = board.getAvailableMoves().size();
        for (int i = 0; i < board.getBoard().length; i++) {
            for (int j = 0; j < board.getBoard()[i].length; j++) {
                int x = board.getColor(i,j) == Board.WHITE ? 1 : -1  ;
                x = board.getColor(i,j) == Board.EMPTY ? 0 : x ;
                linearSum += weights[i][j] * x ;
            }
        }
        return linearSum;
    }

    //Called with the knowledge that there is a move to choose
    public Point chooseMove() {
        Date d1 = new Date();
        Point choice = null;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        int value;
        int maxValue = Integer.MIN_VALUE;
        for (Point p : mainBoard.getAvailableMoves()) {

            numberOfChildren = 0;
            Board b = Board.copyBoard(mainBoard);
            b.move(p);
            value = Minimize(b, alpha, beta, 0);
            System.out.println(p + " - " + numberOfChildren);
            if (maxValue < value) {
                choice = p;
                maxValue = value;
            }
            alpha = Math.max(alpha, value);
        }
        System.out.println();
        Date d2 = new Date();
        System.out.println(( d2.getTime()-(double) d1.getTime())/1000);
        return choice;
    }

    private int Minimize(Board board, int alpha, int beta, int depth) {
        numberOfChildren++;
//        System.out.println(depth + " - " + numberOfChildren);

        board.changeTurn();

        if (board.checkEnd() || depth > maxDepth) {
            return terminal(board);
        }

        int value = Integer.MAX_VALUE;

        for (Point p : board.getAvailableMoves()) {
            Board b = Board.copyBoard(board);
            b.move(p);
            value = Math.min(value, Maximize(b, alpha, beta, depth + 1));
            if (value <= alpha) {
                return value;
            }
            beta = Math.min(beta, value);
        }
        return value;
    }

    int Maximize(Board board, int alpha, int beta, int depth) {
        numberOfChildren++;
//        System.out.println(depth + " - " + numberOfChildren);

        board.changeTurn();

        //terminal Test
        if (board.checkEnd() || depth > maxDepth) {
            return terminal(board);
        }

        //check for each move
        board.findAvailableMoves();

        int value = Integer.MIN_VALUE;

        for (Point p : board.getAvailableMoves()) {
            Board b = Board.copyBoard(board);
            b.move(p);

            value = Math.max(value, Minimize(b, alpha, beta, depth + 1));

            if (value >= beta) {
                return value;
            }
            alpha = Math.max(alpha, value);
        }
        return value;
    }

    private int terminal(Board board) {
        return heuristic(board);
//            if(board.getWinner()== Board.BLACK){
//                return -1*board.getBlackScore() ;
//            }
//            else if(board.getWinner()==Board.EMPTY){
//                return 0 ;
//            }
//            else
//                return board.getWhiteScore() ;
//        }

    }
}
