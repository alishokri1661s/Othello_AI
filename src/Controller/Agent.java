package Controller;
import Model.Point ;

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
    private static double T  ;
    private static int numberOfChildren = 0;
    private static long numberOfBurning = 0;
    private static int maxDepth = 7;
    private static Date Time_start ;
    private static Date Time_end ;
    private static double Time_limit = 3000;
    private static int Alpha  ;
    private static int Beta ;
    static boolean isCompleted ;


    private ArrayList<Map.Entry<Point,Integer>> pointSort(HashSet<Point> set, boolean isAsc) {
        HashMap<Point,Integer> map = new HashMap<>();
        for (Point p : set) {
            map.put(p,weights[p.x][p.y]);
        }
        ArrayList<Map.Entry<Point,Integer>> list = new ArrayList<>(map.entrySet());
        if (isAsc)
            list.sort(Map.Entry.comparingByValue());
        else
            list.sort((Comparator) (o1, o2) -> ((Map.Entry<Point, Integer>) o2).getValue().compareTo(((Map.Entry<Point, Integer>) (o1)).getValue()));
        return list;
    }

    public Point iterative_deepening(){
        Point move = null;
        Alpha = Integer.MIN_VALUE ;
        Beta = Integer.MAX_VALUE ;
        Time_start = new Date( );
        T = 0 ;
        for (int i = 7; i <20 ; i++) {
            maxDepth = i ;
            Point Temp = chooseMove();
            Time_end = new Date() ;
            T = Time_end.getTime() - Time_start.getTime() ;
            if(!isCompleted){
                maxDepth-- ;
                break;
            }
            else if(T > Time_limit){
                break ;
            }
            move = Temp ;
        }
        System.out.println(T);
        System.out.println(maxDepth) ;
        return move ;
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
        int alpha = Alpha ;
        int beta = Beta;
        int value;
        int maxValue = Integer.MIN_VALUE;
        isCompleted = true ;
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
        Alpha = alpha ;
        Beta = beta ;
        return choice;
    }

    public ArrayList<Board> assign_potential(Board board){
        ArrayList<Board> boards = new ArrayList<>() ;
        int sum = 0 ;

        for(Point p : board.getAvailableMoves()){
            Board b = Board.copyBoard(board) ;
            b.move(p);
            int heuristic_value = heuristic(b) ;
            b.setHeuristic(heuristic_value);
            b.setProbability(heuristic_value);
            boards.add(b) ;
            sum += heuristic_value ;
        }



        for(Board b : boards ){
            b.setProbability(1- (b.getProbability()/sum));
        }
        boards.sort(Comparator.comparingInt(o -> (int) o.getProbability()));

        int firstSize = boards.size();
        int size = firstSize;


        if (board.getAvailableMoves().size()<4)
            return boards;
        if (board.getAvailableMoves().size()<6)
            firstSize-=2;
        if (board.getAvailableMoves().size()>9)
            firstSize+=2;
        if (board.getAvailableMoves().size()>13)
            firstSize+=2;
        if (board.getAvailableMoves().size()>17)
            firstSize+=2;




        double p = Math.random() ;
        for (int i = 0; i < firstSize/2 ; i++) {
            for (int j = 0 ; j < size ; j++) {
                if (p <= boards.get(j).getProbability()){
                    boards.remove(j);
                    size--;
                    break;
                }
            }

        }
        return boards;
    }

    private int Minimize(Board board, int alpha, int beta, int depth) {
        numberOfChildren++;
//        System.out.println(depth + " - " + numberOfChildren);

        board.changeTurn();

        Time_end = new Date() ;
        T = Time_end.getTime() - Time_start.getTime();
        if(T>=Time_limit){
            isCompleted = false ;
            return 0 ;
        }

        if (board.checkEnd() || depth > maxDepth) {
//            return terminal(board);
            return board.getHeuristic();
        }

        int value = Integer.MAX_VALUE;

        //ArrayList<Map.Entry<Point,Integer>> list = pointSort(board.getAvailableMoves(),false);

        ArrayList<Board> boards = assign_potential(board);

        //for (Point p : board.getAvailableMoves()) {
//        for (Map.Entry<Point,Integer> p :list) {
//            Board b = Board.copyBoard(board);
//            b.move(p);
            //b.move(p.getKey());

        for (Board b: boards) {
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

        Time_end = new Date() ;
        T = Time_end.getTime() - Time_start.getTime();
        if(T>=Time_limit){
            isCompleted = false ;
            return 0 ;
        }

        //terminal Test
        if (board.checkEnd() || depth > maxDepth) {
            return board.getHeuristic();
        }

        //check for each move
        board.findAvailableMoves();

        int value = Integer.MIN_VALUE;


        //ArrayList<Map.Entry<Point,Integer>> list = pointSort(board.getAvailableMoves(),false);
        ArrayList<Board> boards = assign_potential(board);

//        for (Map.Entry<Point,Integer> p :list) {
//            Board b = Board.copyBoard(board);
//            b.move(p.getKey());
        for (Board b: boards) {
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
