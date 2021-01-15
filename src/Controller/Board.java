package Controller;

import Model.Point;
import View.GUI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import static Model.Utils.*;

public class Board{

    private static Board instance;
    public static Board getInstance(){
        if (instance==null)
            instance = new Board();
        return instance;
    }


    public final int SIZE = 8 ;
    public final static int WHITE = 2;
    public final static int BLACK = 1 ;
    public final static int EMPTY = 0 ;
    private double probability ;
    private int heuristic;

    public Agent agentWhite;
    public Agent agentBlack;
    public int currentPlayer;
    public boolean isWhiteBot = true;
    public boolean isBlackBot = false;

    private HashSet<Point> availableMoves = new HashSet<>(); ;
    private int[][] board = new int[SIZE][SIZE] ;

    private int blackScore=2;
    private int whiteScore=2;

    public static Board copyBoard (Board b){
        Board board = new Board(true);

        board.setBoard(copyMatrix(b.getBoard()));
        board.whiteScore = b.getWhiteScore();
        board.blackScore = b.getBlackScore();
        board.currentPlayer = b.currentPlayer;
        board.availableMoves = (HashSet<Point>) b.availableMoves.clone();
        return board;
    }

    public Board(){
        init();
        Agent.config(2000,4,true,true);
        if (isWhiteBot)
            agentWhite = new Agent(this,true);
        if (isBlackBot)
            agentBlack = new Agent(this);
    }
    public Board(boolean usingAgent){
        init();
    }

    public Board(boolean white, boolean black){
        isWhiteBot=white;
        isBlackBot=black;
        if (isWhiteBot)
            agentWhite = new Agent(this);
        if (isBlackBot)
            agentBlack = new Agent(this);
        init();
    }

    public void init(){
        for (int[] states : board) {
            Arrays.fill(states, EMPTY);
        }
        currentPlayer = BLACK ;

        board[3][3] = WHITE ;
        board[3][4] = BLACK ;
        board[4][3] = BLACK ;
        board[4][4] = WHITE ;
        blackScore = 2;
        whiteScore = 2;

        findAvailableMoves();
    }


    public void print(){
        for (int[] states : board) {
            for (int state : states) {
                System.out.print(state + " ");
            }
            System.out.println();
        }
    }


    public int getOppositeTurn(int turn){
        return turn == BLACK ? WHITE : BLACK ;
    }


    public void changeColor(int i, int j){
        this.board[i][j] = getOppositeTurn(this.board[i][j]) ;
    }

    public void updateScores(){
        int countWhite = 0 ;
        int countBlack = 0 ;
        for (int[] states : board) {
            for (int state : states) {
                if (WHITE == state)
                    countWhite++;
                else if (BLACK == state)
                    countBlack++;
            }
        }
        whiteScore = countWhite ;
        blackScore = countBlack;

    }


    boolean isValidCell(int x,int y){
        return x >= 0 && x< SIZE && y< SIZE && y>= 0 ;
    }

    boolean isValidEmpty(int x  , int y){
        return this.isValidCell(x,y) && getColor(x,y) == EMPTY ;
    }

    public int getColor(int x , int y ){
        return this.board[x][y] ;
    }


    public ArrayList<Point> getValidDirections(int x, int y){
        ArrayList<Point> directions = new ArrayList<>() ;
        for (int i = -1; i <2 ; i++) {
            for (int j = -1; j <2 ; j++) {
                if(isValidCell(x+i,y+j)){
                    if (this.board[x+i][y+j]==getOppositeTurn(this.currentPlayer)){
                        directions.add(new Point(i,j)) ;
                    }
                }
            }
        }
        return directions ;
    }

    boolean checkDirectionMove(Point point , Point direction, int turn){
        int dx = direction.x;
        int dy = direction.y;
        int row = point.x + dx ;
        int col = point.y + dy ;

        while (true){
            if(!isValidCell(row,col))
                break ;
            if(getColor(row,col)==EMPTY)
                break ;
            if(getColor(row,col)==turn){
                return true ;
            }
            row += dx ;
            col += dy ;
        }
        return false ;
    }


    public boolean canMove(){
        return !availableMoves.isEmpty();
    }

    public void findAvailableMoves(){
        availableMoves.clear();
        ArrayList<Point> dire;

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if(this.board[i][j]==EMPTY){
                    dire = getValidDirections(i,j) ;

                    for (Point point : dire) {
                        if (checkDirectionMove(new Point(i, j), point, this.currentPlayer)) {
                            availableMoves.add(new Point(i, j));
                        }
                    }
                }
            }
        }
    }


    public void changeTurn(){
        this.currentPlayer = getOppositeTurn(this.currentPlayer);
        findAvailableMoves();
    }

    public boolean checkEnd(){
        if (!canMove()){
            changeTurn();
            return !canMove();
        }
        return false;
    }

    public boolean isValidMove(Point move){
        return availableMoves.contains(move);
    }

    public int playAIvsAI(){
        while(!checkEnd()) {
            if (currentPlayer == BLACK)
                move(agentBlack.iterative_deepening());
            else
                move(agentWhite.iterative_deepening());
            changeTurn();
        }

        return getWinner();
    }

    public void play (Point square){
        if (!isValidMove(square) && !isCurrentBot())
            return;

        if(isCurrentBot()){
//            move(Board.getAgent().chooseMove());
            if (currentPlayer == BLACK)
                move(agentBlack.iterative_deepening());
            else
                move(agentWhite.iterative_deepening());

        }
        else
            move(square);
        changeTurn();
        if(checkEnd()){
            endGame();
            return;
        }

        GUI.getInstance().paint();

        if (isCurrentBot()) {
            new Thread(){
                @Override
                public void run() {
                    play(square);
                    super.run();
                }
            }.start();

        }
    }

    public void move(Point move){

        int x = move.x;
        int y = move.y;
        ArrayList<Point> directions = getValidDirections(x,y) ;
        board[x][y] = currentPlayer;
        for (Point direction : directions) {
            flipCells(move, direction);
        }
        updateScores();
    }

    public int getWinner(){
        if (blackScore==whiteScore){
            return EMPTY ; //draw
        }
        else if (blackScore>whiteScore){
            return BLACK ;
        }
        else
            return WHITE ;
    }


    private void endGame() {
        GUI.getInstance().endGame();
        if (Agent.debuggingMode)
        System.out.println("Average depth: " + (double) Agent.sumDepth / Agent.numberOfMoves);

    }

    void flipCells(Point move, Point direction){
        int x = move.x;
        int y = move.y;
        int dx = direction.x;
        int dy = direction.y;
        int row = x + dx ;
        int col = y + dy ;
        if(checkDirectionMove(new Point(x,y),direction,this.currentPlayer)){
            while (getColor(row,col)==getOppositeTurn(currentPlayer)){
                changeColor(row,col);
                row += dx ;
                col += dy ;
            }
        }
    }


    void printMoves(ArrayList<Point> p){
        for (int i = 0; i <p.size() ; i++) {
            System.out.println(p.get(i).x + " " + p.get(i).y);
        }
    }

    public boolean isCurrentBot (){
        return currentPlayer==WHITE ? isWhiteBot : isBlackBot;
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }

    public int[][] getBoard() {
        return board;
    }

    public HashSet<Point> getAvailableMoves() {
        return availableMoves;
    }

    public int getBlackScore() {
        return blackScore;
    }

    public int getWhiteScore() {
        return whiteScore;
    }
    public int getScore(int color){
        if(color==BLACK)
            return getBlackScore();
        else
            return getWhiteScore() ;
    }
    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public void setTurn (int turn){
        currentPlayer = turn;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    public double getProbability() {
        return probability;
    }

    public void setHeuristic(int heuristic) {
        this.heuristic = heuristic;
    }

    public int getHeuristic() {
        return heuristic;
    }
}