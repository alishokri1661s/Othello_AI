package Controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class MachineLearning {
    private final int SIZE = 11;
    private final int MAX = 100;
    private final double crossOverRate = 0.75;
    private final double mutationRate = 0.4;
    private final int limit = 10;
    private ArrayList<int[]> population = new ArrayList<>();


    private int[] crossOver (int[] w1, int[] w2){
        Random random = new Random();
        int rand = random.nextInt(SIZE-1 )+1;
        int[] result = new int[SIZE];
        for (int i = 0; i < rand; i++)
            result[i] = w1[i];
        for (int i = rand; i < SIZE; i++)
            result[i] = w2[i];
        return result;
    }

    private void mutation (int[] w){
        Random random = new Random();
        int rand = random.nextInt(SIZE);
        w[rand] = random.nextInt(2* MAX +1 ) - MAX;
    }

    private int[] randomWeights(){

        int[] ar = new int[SIZE];
        Random random = new Random();
        for (int i = 0; i < ar.length; i++) {
            ar[i] = random.nextInt(2* MAX +1 ) - MAX;
        }
        return ar;
    }

    private int AIvsAI (int[] a, int[] b) {
        Board board = new Board(true,true);
        board.agentBlack.setWeights(a);
        board.agentWhite.setWeights(b);
        board.playAIvsAI();

        return board.playAIvsAI();
    }

    private void generatePopulation(int l){
        for (int i = 0; i < l; i++) {
            population.add(randomWeights());
        }
    }

    private void generateChildren (){
        int parentSize = population.size();
        double r;
        for (int i = 0; i < parentSize; i++) {
            for (int j = i+1; j < parentSize; j++) {
                r = Math.random();
                int[] child;
                if (r <= crossOverRate){
                    child = crossOver(population.get(i),population.get(j));
                    r = Math.random();
                    if (r <= mutationRate){
                        mutation(child);
                    }
                    population.add(child);
                }
            }
        }
    }

    private Map<Integer,Integer> playAI(){
        HashMap<Integer,Integer> map = new HashMap<>();
        for (int i = 0; i < population.size(); i++) {
            map.put(i,0);
        }
        for (int i = 0; i < population.size(); i++) {
            for (int j = 0; j < population.size(); j++) {
                if (i==j)
                    break;
                System.out.print("start game between " + i + " and " + j +" ...  ");
                int winner = AIvsAI(population.get(i),population.get(j));
                System.out.println("end game");
                if (winner == Board.BLACK) {
                    map.put(i, map.get(i) + 2);
                }
                else if (winner == Board.WHITE)
                    map.put(j,map.get(j)+2);
                else{
                    map.put(i,map.get(i)+1);
                    map.put(j,map.get(j)+1);
                }
            }
        }
        return map;
    }

    private void chooseBest(Map<Integer,Integer> scoreMap){
        ArrayList<int[]> newParent = new ArrayList<>();

        scoreMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(limit)
                .forEachOrdered(x -> {newParent.add(population.get(x.getKey()));
                    System.out.println("First: " + population.get(x.getKey())[0] +" - score: " + x.getValue());} );

        population = newParent;
    }

    private void saveTheBest(int num) {
        File file = new File("src/weight.txt");
        File history = new File("src/history.txt");

        try (
                FileWriter writer = new FileWriter(file);
                FileWriter fw = new FileWriter(history,true);
        ) {
            for (int i = 0; i < SIZE; i++) {
                writer.write(population.get(0)[i]+ " ");
            }
            if(num==1)
                fw.append("\n**************************\n\n");
            if (num==-1)
                fw.append("Final --------------------------------\n");
            else
                fw.append(String.valueOf(num)).append(" --------------------------------\n");

            for (int i = 0; i < population.size(); i++) {
                fw.append(Arrays.toString(population.get(i)));
                fw.append('\n');
            }
            fw.append('\n');
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void simulateEvolution(int depth){
        generatePopulation(20);

        Date startTime = new Date();
        Map<Integer,Integer> m = playAI();
        Date finishTime = new Date();
        long time = finishTime.getTime() - startTime.getTime() ;
        System.out.println("\nGeneration 1: " + time/(1000*60) + " minutes");
        chooseBest(m);
        System.out.println(Arrays.toString(population.get(0))+"\n\n");
        saveTheBest(1);

        for (int i = 0; i < depth; i++) {
            generateChildren();
            System.out.println(population.size());
            startTime = new Date();
            m = playAI();
            finishTime = new Date();
            time = finishTime.getTime() - startTime.getTime() ;
            System.out.println("\nGeneration "+ (i+2) + ": " + time/(1000*60) + " minutes");
            chooseBest(m);
            System.out.println(Arrays.toString(population.get(0))+"\n\n");
            saveTheBest(i+2);
        }
        m=playAI();
        chooseBest(m);
        saveTheBest(-1);
    }



    public static void main(String[] args) {
        MachineLearning machine = new MachineLearning();
        Agent.config(30,3,false);
        machine.simulateEvolution(10);

    }
}
