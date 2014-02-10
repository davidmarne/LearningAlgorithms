/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package learning.algorithms;

import java.util.ArrayList;

/**
 *
 * @author davemarne
 */
public class q2 {
    ArrayList<State> states;
    Action[] actions;
    ArrayList<Double> qvalue;
    ArrayList<Integer> freq;
    
    public q2(){
        states = new ArrayList();
        actions = new Action[9];
        initializeAcc();
        qvalue = new ArrayList();
        freq = new ArrayList();
    }    
    
    private void initializeAcc(){
        //initialize acceleration table
        int spot = 0;
        for (int m = -1; m < 2; m++) {
            for (int n = -1; n < 2; n++) {
                actions[spot] = new Action(m, n);
                spot++;
            }
        }
    }
    
    public void addState(State s){
        for(int i = -5; i < 6; i++){
            for(int j = -5; j < 6; j++){
                State newS = new State(i, j, s.getY(), s.getX());
                states.add(newS);
                for(int m = -1; m < 2; m++){
                    for(int n = -1; n < 2; n++){
                        qvalue.add(0.0);
                        freq.add(0);
                    }
                }
            }
        }
    }
    
    public int getStateIndex(State s){
        for(int i = 0; i < states.size(); i+=121){
            if(states.get(i).spotEquals(s)){
                return i;
            }
        }
        return -1;
    }
    public int getVelocityIndex(int index, State s){
        if(index == -1){
            return -1;
        }
        for(int i = index; i < (index+121); i++){
            if(states.get(i).velocityEquals(s)){
                return i;
            }
        }
        return -1;
    }
    public int getAccIndex(Action a){
        for(int h = 0; h < 9; h++){
            if(actions[h].accelerationsEqual(a)){
                return h;
            }
        }
        return -1;
    }
    
    private int getRow(State s, Action a){
        int index = getStateIndex(s);
        //if the state we are looking for isnt in our list of states
        if(index == -1){
            return -1;
        }
       
        index = getVelocityIndex(index, s);
        
        if(index == -1){
            return -1;
        }
        int indexA = getAccIndex(a);
        return index*9+indexA;
    }
    
    public double getQValueAtIndex(int index){
        try{
            return qvalue.get(index);
        }catch (Exception e){
            System.out.print("EXCEPTION, -1 RETURNED");
            return -1.0;
        }
    }
    
    
    //returns the frequency of a State Action Pair  
    public int getFreqOfSAP(State s, Action a){
        int row = getRow(s,a);
        if(row == -1){
            return 0;
        }else{
            return freq.get(row);
        }
    }
    //returns the qvalue of a state action pair
    public double getQValueOfSAP(State s, Action a){
        int index = getRow(s,a);
        if(index == -1){
            return 0;
        }else{
            return qvalue.get(index);
        }
    }
    
    public void incrementFreqOfSAP(State s, Action a){
        int row = getRow(s,a);
        freq.set(row, freq.get(row)+1);
    }
    
    public void setQValue(State s, Action a, double newVal){
        int row = getRow(s,a);
        qvalue.set(row, newVal);
    }
    
    
    public void setActByIndex(int index, Action a){
        actions[index] = a;
    }
    
    public void setStateByIndex(State st, int spot){
        states.set(spot, st);
    }
}
