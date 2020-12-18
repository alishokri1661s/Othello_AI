package Controller;
import Model.Point ;
import sun.plugin.javascript.navig.Link;

import java.util.*;

public class Agent {
    Board mainBoard = Board.getInstance();

    private static int[][] weights = {
            {99, -8, 8, 6, 6, 8, -8, 99},
            {-8, -24, -4, -3, -3, -4, -24, -8},
            {8, -4, 7, 4, 4, 7, -4, 8},
            {6, -3, 4, 0, 0, 4, 3, 6},
            {6, -3, 4, 0, 0, 4, 3, 6},
            {8, -4, 7, 4, 4, 7, -4, 8},
            {-8, -24, -4, -3, -3, -4, -24, -8},
            {99, -8, 8, 6, 6, 8, -8, 99}};

    //for debug
    private static int numberOfChildren = 0;
    private static long numberOfBurning = 0;
    private static int maxDepth = 6;

    private ArrayList<Map.Entry<Point,Integer>> pointSort(HashSet<Point> set) {
        HashMap<Point,Integer> map = new HashMap<>();
        for (Point p : set) {
            map.put(p,weights[p.x][p.y]);
        }
        ArrayList<Map.Entry<Point,Integer>> list = new ArrayList<>(map.entrySet());
//        list.sort(Map.Entry.comparingByValue());
        list.sort((Comparator) (o1, o2) -> ((Map.Entry<Point, Integer>) o2).getValue().compareTo(((Map.Entry<Point, Integer>) (o1)).getValue()));
        return list;
    }


    private int heuristic(Board board) {


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
            numberOfBurning = 0;
            Board b = Board.copyBoard(mainBoard);
            b.move(p);
            value = Minimize(b, alpha, beta, 0);
            System.out.println(p + " - numberOfChildren: " + numberOfChildren
                    + " - numberOfBurning: " + numberOfBurning);
            if (maxValue < value) {
                choice = p;
                maxValue = value;
            }
            alpha = Math.max(alpha, value);
        }
        Date d2 = new Date();
        System.out.println(( d2.getTime()-(double) d1.getTime())/1000);
        System.out.println();
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

        ArrayList<Map.Entry<Point,Integer>> list = pointSort(board.getAvailableMoves());


        for (Map.Entry<Point,Integer> p :list) {
            Board b = Board.copyBoard(board);
            b.move(p.getKey());

            value = Math.max(value, Minimize(b, alpha, beta, depth + 1));

            if (value >= beta) {
                numberOfBurning++;
                return value;
            }
            alpha = Math.max(alpha, value);
        }
        return value;
    }

    private int terminal(Board board) {
        return heuristic(board);

    }
}
