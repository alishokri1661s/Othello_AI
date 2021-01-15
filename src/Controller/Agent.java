package Controller;
import Model.Point ;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Agent {
    private Board mainBoard;

    //for debug
    private static long T  ;
    private static int numberOfChildren = 0;
    private static long numberOfPruning = 0;
    private static int maxDepth = 7;
    private static Date Time_start ;
    private static Date Time_end ;
    private static long timeLimit = 10;
    private static boolean isCompleted ;
    private static int maxBranching = 3;
    public static boolean debuggingMode = false;
    private static boolean usingTimeLimit = true ;

    public static int numberOfMoves=0;
    public static int sumDepth=0;
    private int[] featureWeight;

    private int[][] weights = {
            {99, -8, 8, 6, 6, 8, -8, 99},
            {-8, -24, -4, -3, -3, -4, -24, -8},
            {8, -4, 7, 4, 4, 7, -4, 8},
            {6, -3, 4, 0, 0, 4, 3, 6},
            {6, -3, 4, 0, 0, 4, 3, 6},
            {8, -4, 7, 4, 4, 7, -4, 8},
            {-8, -24, -4, -3, -3, -4, -24, -8},
            {99, -8, 8, 6, 6, 8, -8, 99},
            {1}};

    public Agent(Board board){
        mainBoard = board;
    }

    public Agent(Board board, boolean readFromFile){
        this(board);
        if (readFromFile) {
            int[] w = new int[11];
            File file = new File("src/weight.txt");
            try(Scanner sc = new Scanner(file)) {
                for (int i = 0; i < 11; i++) {
                    w[i] = sc.nextInt();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            setWeights(w);
        }
    }

    public static void config(long timeLimit, int maxBranching, boolean debuggingMode,boolean usingTimeLimit){
        Agent.timeLimit = timeLimit;
        Agent.maxBranching = maxBranching;
        Agent.debuggingMode = debuggingMode;
        Agent.usingTimeLimit = usingTimeLimit ;
    }




    public void setWeights(int [] w){
        featureWeight = w;
        weights[0][0] = w[0];
        weights[0][1] = w[1];
        weights[0][2] = w[2];
        weights[0][3] = w[3];

        weights[1][1] = w[4];
        weights[1][2] = w[5];
        weights[1][3] = w[6];
        weights[2][2] = w[7];
        weights[2][3] = w[8];
        weights[3][3] = w[9];


        weights[8][0] = w[10];


        for (int i = 0; i < 4; i++) {

            for (int j = 0; j < 4; j++) {
                weights[j][i] = weights[i][j];
                weights[7-i][j] = weights[i][j];
                weights[7-i][7-j] = weights[i][j];
                weights[i][7-j] = weights[i][j];
            }
        }
        if (debuggingMode)
            printWeight();

    }

    private void printWeight() {
        for (int i = 0; i < 8; i++) {
            System.out.println(Arrays.toString(weights[i]));
        }
        System.out.println();
    }

    public int[] getFeatureWeight() {
        return featureWeight;
    }

    public Point iterative_deepening(){
        Point move = null;
        Time_start = new Date( );
        T = 0 ;
        if(usingTimeLimit) {
            for (int i = 5; i <50 ; i++) {
                maxDepth = i ;
                Point Temp = chooseMove();
                Time_end = new Date() ;
                T = Time_end.getTime() - Time_start.getTime() ;
                if(!isCompleted){
                    maxDepth-- ;
                    break;
                }
                else if(T > timeLimit){
                    break ;
                }
                move = Temp ;
            }
        }
        if (move==null) {
            maxDepth = 4;
            move = chooseMove();
        }

        if (debuggingMode) {
            System.out.println(T);
            System.out.println(maxDepth) ;
        }
        if (debuggingMode) {
            sumDepth += maxDepth;
            numberOfMoves++;
        }
        return move ;

    }


    //Called with the knowledge that there is a move to choose
    public Point chooseMove() {

        if (mainBoard.getAvailableMoves().size()==1)
            return mainBoard.getAvailableMoves().iterator().next();

        Date d1 = new Date();
        Point choice = null;
        int alpha = Integer.MIN_VALUE ;
        int beta = Integer.MAX_VALUE;
        int value;
        int maxValue = Integer.MIN_VALUE;
        isCompleted = true ;
        for (Point p : mainBoard.getAvailableMoves()) {

            numberOfChildren = 0;
            numberOfPruning = 0;
            Board b = Board.copyBoard(mainBoard);
            b.move(p);
            value = Minimize(b, alpha, beta, 0);
            if (debuggingMode)
                System.out.println(p + " - numberOfChildren: " + numberOfChildren
                        + " - numberOfPruning: " + numberOfPruning);
            if (maxValue < value) {
                choice = p;
                maxValue = value;
            }
            alpha = Math.max(alpha, value);
        }
        Date d2 = new Date();
        if (debuggingMode){
            System.out.println(( d2.getTime()-(double) d1.getTime())/1000);
            System.out.println();
        }

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
            //b.setProbability(heuristic_value);
            boards.add(b) ;
            sum += heuristic_value ;
        }


        if (mainBoard.currentPlayer == board.currentPlayer)
            boards.sort(Comparator.comparingDouble(Board::getHeuristic).reversed());
        else
            boards.sort(Comparator.comparingDouble(Board::getHeuristic));

        ArrayList<Board> result = new ArrayList<>() ;


        if (board.getAvailableMoves().size() <= maxBranching)
            return boards;

        int size = board.getAvailableMoves().size();
        double p;
        double r = 0.05;
        int c=0;
        for (int i = 0; i < size ; i++) {
            p = Math.random();
            if (p<r && size-i-maxBranching > c) {
                r+=0.05;
                continue;
            }
            result.add(boards.get(i));
            c++;

            if (c==maxBranching)
                break;
        }
        return result;
    }

    private int Minimize(Board board, int alpha, int beta, int depth) {
        numberOfChildren++;

        board.changeTurn();

        Time_end = new Date() ;
        T = Time_end.getTime() - Time_start.getTime();
        if(T>= timeLimit){
            isCompleted = false ;
            return 0 ;
        }

        if (board.checkEnd() || depth > maxDepth) {
            return board.getHeuristic();
        }

        int value = Integer.MAX_VALUE;


        ArrayList<Board> boards = assign_potential(board);

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

        board.changeTurn();

        Time_end = new Date() ;
        T = Time_end.getTime() - Time_start.getTime();
        if(T>= timeLimit){
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

        ArrayList<Board> boards = assign_potential(board);


        for (Board b: boards) {
            value = Math.max(value, Minimize(b, alpha, beta, depth + 1));

            if (value >= beta) {
                numberOfPruning++;
                return value;
            }
            alpha = Math.max(alpha, value);
        }
        return value;
    }

    private int heuristic(Board board) {
        int linearSum;
        if (board.currentPlayer == mainBoard.currentPlayer)
            linearSum =weights[8][0]/10 *  board.getAvailableMoves().size();
        else
            linearSum = -1 * weights[8][0]/10 *  board.getAvailableMoves().size();

        for (int i = 0; i < board.getBoard().length; i++) {
            for (int j = 0; j < board.getBoard()[i].length; j++) {
                int x = board.getColor(i,j) == mainBoard.currentPlayer ? 1 : -1  ;
                x = board.getColor(i,j) == Board.EMPTY ? 0 : x ;
                linearSum += weights[i][j] * x ;
            }
        }
        return linearSum;
    }

}
