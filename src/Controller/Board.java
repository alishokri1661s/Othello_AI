package Controller;

import Model.Point;
import View.GUI;
import jdk.nashorn.internal.ir.Block;
import jdk.nashorn.internal.ir.WhileNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Board{

    private static Board instance;

    public static Board getInstance(){
        if (instance==null)
            instance = new Board();
        return instance;
    }

    public final static int SIZE = 8 ;
    public final static int WHITE = 2;
    public final static int BLACK = 1 ;
    public final static int EMPTY = 0 ;

    public int currentPlayer;

    private ArrayList<Point> availableMoves = new ArrayList<>(); ;
    private int[][] board = new int[SIZE][SIZE] ;

    private int blackScore=2;
    private int whiteScore=2;



    private Board(){
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

        canMove();
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
        return availableMoves.size()>0 ;
    }


    public void changeTurn(){
        this.currentPlayer = getOppositeTurn(this.currentPlayer);
        if (!canMove()){
            this.currentPlayer = getOppositeTurn(this.currentPlayer);
            if(!canMove()){
                endGame();
            }
        }

    }


    public void move(Point move){

        if (!availableMoves.contains(move)){
            return;
        }
        int x = move.x;
        int y = move.y;
        ArrayList<Point> directions = getValidDirections(x,y) ;
        board[x][y] = currentPlayer;
        for (Point direction : directions) {
            flipCells(move, direction);
        }
        updateScores();
        changeTurn();
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
        updateScores();
        int winner = getWinner();
        GUI.getInstance().endGame();
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


    public int[][] getBoard() {
        return board;
    }

    public ArrayList<Point> getAvailableMoves() {
        return availableMoves;
    }

    public int getBlackScore() {
        return blackScore;
    }

    public int getWhiteScore() {
        return whiteScore;
    }
}