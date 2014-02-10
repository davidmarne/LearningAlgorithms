/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package learning.algorithms;

import java.io.File;
import java.util.Scanner;

/**
 *
 * @author davemarne
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        File f = new File("LTrack.txt");
        Track t = new Track(f);
        int i = 0;
        if (i == 0)
           i=5;
        
        QLearningAgent ql = new QLearningAgent(t, false);
        
        boolean quit = false;
        while(!quit){
            System.out.println("Enter 1 to run Q-Learning Agent");
            System.out.println("Enter 2 watch your Q-Learning agent run a trail");
            System.out.println("Enter 3 to start a new ValueIteration Agent and race around the track");
            System.out.println("Enter 9 to quit");
            
            Scanner in = new Scanner(System.in);
            int choice = in.nextInt();
            switch(choice){
                case 1:
                    
                    State s = new State(0,0,0,0);
                    Action a;
                    
                    System.out.println("How many times would you like the agent to run the track?");
                    int numTimes = in.nextInt();
                    
                    for ( i = 0; i < numTimes; i++) {

                        s.setStateEqual(ql.getStart());
                        //t.printTrack(s);

                        a = ql.QLearningAlgorithm(s);
                        ql.resultOf(s, a);

                        boolean flag = true;
                        while (flag) {
                            //t.printTrack(s);
                            a = ql.QLearningAlgorithm(s);
                            if (a.getXAcc() == Integer.MAX_VALUE) {
                                flag = false;
                            } else {
                                ql.resultOf(s, a);
                            }
                        }
                    }
                    break;
                case 2:
                        int count = 0;
                        State st = new State(0,0,0,0);
                        st.setStateEqual(ql.getStart());
                        //t.printTrack(s);

                        a = ql.QLearningAlgorithm(st);
                        ql.resultOf(st, a);

                        boolean flag = true;
                        while (flag) {
                            count++;
                            t.printTrack(st);
                            a = ql.QLearningAlgorithm(st);
                            if (a.getXAcc() == Integer.MAX_VALUE) {
                                flag = false;
                                System.out.printf("%d", count);
                            } else {
                                ql.resultOf(st, a);
                            }
                        }
                    break;
                case 3:
                    ValueIterationAgent vi = new ValueIterationAgent(t);
                    vi.ValueIteration();
                    vi.raceTrack();
                    break;
              
                case 9:
                    quit = true;
                    break;
            }
        }

        
        
    }
}
