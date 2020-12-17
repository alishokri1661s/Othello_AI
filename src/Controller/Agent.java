package Controller;
import Model.Point ;
public class Agent {
    Board mainBoard = Board.getInstance() ;

    private static int numberOfChildren = 0 ;
    private static int maxDepth = 7;


    //Called with the knowledge that there is a move to choose
    public Point chooseMove(){

        Point choice = null;
        int alpha = Integer.MIN_VALUE ;
        int beta = Integer.MAX_VALUE ;
        int value;
        int maxValue=Integer.MIN_VALUE;
        for (Point p : mainBoard.getAvailableMoves()) {

            numberOfChildren=0;
            Board b = Board.copyBoard(mainBoard);
            b.move(p);
            value = Minimize(b,alpha,beta,0);
            System.out.println(p +" - "+ numberOfChildren);
            if(maxValue < value){
                choice = p;
                maxValue=value;
            }
            alpha = Math.max(alpha,value) ;
        }
        System.out.println();
        return choice;
    }

    private int Minimize(Board board,int alpha , int beta,int depth){
        numberOfChildren++ ;
//        System.out.println(depth + " - " + numberOfChildren);

        board.changeTurn();

        if(board.checkEnd() || depth>maxDepth){
            return terminal(board);
        }

        int value = Integer.MAX_VALUE ;

        for (Point p: board.getAvailableMoves()) {
            Board b = Board.copyBoard(board);
            b.move(p);
            value = Math.min(value,Maximize(b,alpha,beta,depth+1));
            if(value<= alpha) {
                return value;
            }
            beta = Math.min(beta,value) ;
        }
        return value ;
    }
    int Maximize(Board board,int alpha , int beta,int depth){
        numberOfChildren++;
//        System.out.println(depth + " - " + numberOfChildren);

        board.changeTurn();

        //terminal Test
        if(board.checkEnd() || depth>maxDepth){
            return terminal(board);
        }

        //check for each move
        board.findAvailableMoves();

        int value = Integer.MIN_VALUE ;

        for (Point p : board.getAvailableMoves()) {
            Board b = Board.copyBoard(board);
            b.move(p);

            value = Math.max(value,Minimize(b,alpha,beta,depth+1));

            if(value>= beta) {
                return value;
            }
            alpha = Math.max(alpha,value) ;
        }
        return value ;
    }

    private int terminal(Board board){

            if(board.getWinner()== Board.BLACK){
                return -1*board.getBlackScore() ;
            }
            else if(board.getWinner()==Board.EMPTY){
                return 0 ;
            }
            else
                return board.getWhiteScore() ;
        }

}
