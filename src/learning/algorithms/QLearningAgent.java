/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package learning.algorithms;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author davemarne
 */
public class QLearningAgent {
    q2 q;//table for state action pairs
    State s;//previous state
    State start;//holds start spot in case of crash
    Action a;//previous action
    double r;//previous reward
    boolean startOver;//true if crash starts agent over, false if starts where the agent went off the track
    Track t;
    int numStates;//number of states the agent can be in
    final static double alpha = 1;
    final static double lambda = .95;
    boolean finished;
    int cost;
    
    public QLearningAgent(Track tr, boolean so){
        a = null;
        s = new State(0,0,0,0);
        startOver = so;
        t = tr;
        q = new q2();
        finished = false;
        cost = 0;
        //creates a state that holds the start x and y values with velocity of 0,0
        start = t.findStart();
    }
    
    public Action QLearningAlgorithm(State currentState){
        //if the agent has never been in this space, add it to states and
        //add 1089 spots to qvalue
        if(q.getStateIndex(currentState) == -1){
           q.addState(currentState);
        }
        
        if(finished){
            q.setQValue(s, new Action(0,0), 1000-cost);
            cost = 0;
            finished = false;
            s.setX(0);
            s.setY(0);
            s.setYVel(0);
            s.setXVel(0);
            a = null;
            return new Action(Integer.MAX_VALUE, Integer.MAX_VALUE);
        }
        
        if(Terminal(s, currentState)){
            finished = true;
        }
        
        
        if(a != null){
            cost++;
            q.incrementFreqOfSAP(s, a);
            
            double currentQV = q.getQValueOfSAP(s,a);
            double m = maxActionPrime(currentState);
            double v = currentQV + alpha * (r + lambda * m - currentQV);
            q.setQValue(s,a, v);
        }
        
        s.setStateEqual(currentState);
        a = explorationFunct(currentState, finished);
        r = rewardOf(currentState);
        
        return a;
    } 
    
    private Action explorationFunct(State st, boolean fin){
        Action result = new Action(0,0);
        ArrayList<Action> arrlst = st.getActions();
        Random ran = new Random();
        double probability = ran.nextDouble();
        
        if(fin){
            
        }
        
        //found is set to true when result has been set at least once
        //if result is still false at the end of the function we know the result
        //was never set therefore we need to randomly choose the action
        boolean found = false;
        
        //75% of the time choose the best action
        if(probability <= .75){
            
            double max = 0;
            //loop through every action and choose the one with the highest qvalue
            for(int i = 0; i < arrlst.size(); i++)
            {
                double value = q.getQValueOfSAP(st, arrlst.get(i));
                if(value > max){
                    max = value;
                    result = arrlst.get(i);
                    found = true;
                }
            }
        }else{//choose a sub optimal action
            
            //bInit is set to true if result is set
            //if bInit is fals after the first loop we know every action has been
            //attempted at least 100 times so it is safe to take the optimal path
            boolean bInit = false;
            //maxN holds freq of the action with the least amount of times tried
            int maxN = Integer.MAX_VALUE;
            Action act;
            
            for(int i = 0; i < arrlst.size(); i++ ){
                //get the frequency of the state acton pair
                act = arrlst.get(i);
                int freq = q.getFreqOfSAP(st, act);
                
                //if the action has been chosen less than 100 times and
                //has been chosen less than any other action, let it be taken
                if(freq < 10000 && freq < maxN){
                    maxN = freq;
                    result = act;
                    bInit = true;
                    found = true;
                }
            }
            //choose optimal action
            if (bInit == false) {
                
                double max = 0;
                for (int i = 0; i < arrlst.size(); i++) {
                    double value = q.getQValueOfSAP(st, arrlst.get(i));
                    if (value > max) {
                        max = value;
                        result = arrlst.get(i);
                        found = true;
                    }
                }
            }
        }
        //result was never set, choose random action
        if(!found){
                int randomI = ran.nextInt();
                int randomAct = randomI % arrlst.size();
                int ranActIndex = Math.abs(randomAct);
                result = arrlst.get(ranActIndex);
        }
        
        return result;
    }
    
    public State getStart(){
        return start;
    }
    
    //returns the qvalue of the action from s' with the highest qvalue
    private double maxActionPrime(State sPrime){
        
        
        //max holds the max qvalue of all the actions we will be considering
        double max = 0;
        
        //get the state index of the state s'
        int index = q.getStateIndex(sPrime); 
        if(index == -1){
            max = 9;
        }
       
        
        //loops through all possible accelerations and updates max if it finds a higher qvalue
        for (int h = -1; h < 2; h++) {
            for (int g = -1; g < 2; g++) {

                double qv = q.getQValueOfSAP(sPrime, new Action(h, g));
                if (qv > max) {
                    max = qv;
                }
            }
        }
        return max;
    }

    
    //returns the reward of a state st
    //the only state that returns any value is the finish line
    private int rewardOf(State st){
        char c = t.getSpot(st.getY(), st.getX());
        if(c == 'F'){
            return 1000;
        }else{
            return 0;
        }
    }
    
    //checks if the state is a F
    public boolean Terminal(State st, State result){
        if(st != null){
            return t.checkFinish(st, result);
        }else{
            return false;
        }
    }
    
    //returns the result of an action
    public void resultOf(State st, Action act){
        State temp = new State(0,0,0,0);
        temp.setStateEqual(st);
        
        Random rnd = new Random();
        double ran = rnd.nextDouble();
        
        //90 percent of the time acceleration works
        if(ran <= .9){
            st.setX(st.getX()+st.getXVel()+act.getXAcc());
            st.setY(st.getY()+st.getYVel()+act.getYAcc());
            st.setXVel(st.getXVel()+act.getXAcc());
            st.setYVel(st.getYVel()+act.getYAcc());
            
            //if velocitites exceed 5 in either direction set them to 5
            if(st.getXVel() > 5){
                st.setXVel(5);
            }else if(st.getXVel() < -5){
                st.setXVel(-5);
            }
            
            if(st.getYVel() > 5){
                st.setYVel(5);
            }else if(st.getYVel() < -5){
                st.setYVel(-5);
            }
   
        }else{//acceleration fails
            st.setX(st.getX()+st.getXVel());
            st.setY(st.getY()+st.getYVel());
        }
        
        //handles a crash
        if(t.getSpot(st.getY(), st.getX()) == '#'){
            if(startOver){
                st.setStateEqual(start);
            }else{
                temp.setXVel(0);
                temp.setYVel(0);
                st.setStateEqual(temp);
            }
        }
    }
    
}
