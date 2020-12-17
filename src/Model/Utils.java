package Model;

public class Utils {

    public static int[][] copyMatrix(int[][] board){
        int[][] state = new int[board.length][board[0].length];
        for (int i = 0; i < board.length; i++) {
            System.arraycopy(board[i],0,state[i],0,board[i].length);
        }
        return state ;
    }

}
