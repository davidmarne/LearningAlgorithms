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
public class ValueIterationAgent {
    State[] S;
    double[] U;
    double[] UPrime;
    Track t;
    final static double lambda = .90;
    State start;
    int numStates;
    
    public ValueIterationAgent(Track tr){
        t = tr;
        numStates = t.getNumStates();
        //each array is set to a length of numStates * 121 because each state
        //can have 121 different velocity combinations
        S = new State[numStates*121];
        U = new double[numStates*121];
        UPrime = new double[numStates*121];
        //populates S with states, and U and UPrime with 0s
        populateS();
        start = t.findStart();
    }
    
    //This is the value iteration algorithm from Russel and Norvig's 
    //Introduction to Artificial Intelligence
    public double[] ValueIteration(){
        
        boolean flag = true;
        while(flag){
            //set theta equal to 0 and U equal to U'
            double theta = 0;
            System.arraycopy(UPrime, 0, U, 0, U.length);
            
            for (int i = 0; i < S.length; i++) {
                
                UPrime[i] = Reward(S[i]) + lambda * maxAction(S[i]);
                
                if (Math.abs(UPrime[i] - U[i]) > theta) {
                    theta = Math.abs(UPrime[i] - U[i]);
                }
            }
            if(theta < (1-lambda)/lambda){
                flag = false;
            }
        }
        return UPrime;
    }
    
    public void raceTrack(){
        
        boolean flag = true;
        State currentPos = new State(0,0,0,0);
        State tempPrev = new State(0,0,0,0);
        int numActions = 0;
        currentPos.setStateEqual(start);
        
        while(flag){
            int i = getActionFromU(currentPos);
            numActions++;
            if (i == -1) {//passed finish
                flag = false;
                
                System.out.printf("\nRacer Finished in %d moves!\n", numActions);
            }
            if (flag) {
                tempPrev.setStateEqual(currentPos);
                currentPos = S[i];
                
                
             
                
                if (!t.checkCollision(tempPrev, currentPos)) {
                    
                    currentPos.setStateEqual(start);
                }
                t.printTrack(currentPos);
                
            }
        }
    }
    
    /*****************************
     Class Functions 
     ****************************/
    
   
    
    public State getStart(){
        return start;
    }
    
    public double maxAction(State st){
        //holds the max reward of all actions tested
        double max = 0;
        //initialize list to hold actions
        ArrayList<Action> a = st.getActions();
        for(int i = 0; i < a.size(); i++){
            double sumSPrime = 0;
            int newXVel = st.getXVel() + a.get(i).getXAcc();
            int newYVel = st.getYVel() + a.get(i).getYAcc();
            
            //90 percent of the time the accelerations work and 10 percent of the time they do not
            //so we do 90 percent of the U value from where it acceleates and 10 percent of the Uvalue where it doesnt accelerate
            State r1 = new State(newYVel, newXVel, st.getY()+newYVel, st.getX()+newXVel);
            State r2 = new State(st.getYVel(), st.getXVel(), st.getY()+st.getYVel(), st.getX()+st.getXVel());
            
            if(!t.checkCollision(st, r1)){
                //do nothing
            }else{
                sumSPrime += .9 * getValueFromU(st, r1);
            }
            
            if(!t.checkCollision(st, r2)){
                //do nothing
            }else{
                sumSPrime += .1 * getValueFromU(st, r2);
            }
           
            if(sumSPrime > max){
                max = sumSPrime;
            }
        }
        return max;
    }             
    
    private void populateS(){
        //get the dimensions of the track
        int dimy = t.getDimy();
        int dimx = t.getDimx();
        int spot = 0;
        
        //loop through every spot on the track
        for(int i = 0; i < dimy; i++){
            for(int j = 0; j < dimx; j++){
                
                //if the spot is an S, a F, or a . then the racer can go to these spaces
                //so we need to make a state for each posible velocity in each spot
                char charAtSpot = t.getSpot(i,j);
                if(charAtSpot == 'S' || charAtSpot == '.'){// || charAtSpot == 'F'){
                    
                    //start with the fastest velocity in the negative directions 
                    //then we will work our way back up
                    for(int h = -5; h < 6; h++){
                        for(int g = -5; g < 6; g++){
                            S[spot] = new State(h, g, i, j);
                            U[spot] = 0;
                            UPrime[spot] = 0;
                            spot++;
                        }
                    }
                }
            }
        }
    }
    
    private int getActionFromU(State st){
        Random ran = new Random();
        double probability = ran.nextDouble();
        ArrayList<Action> al = st.getActions();
        double max = 0;
        State temp = new State(0,0,0,0);
        int maxSpot = 0;
        
        if (probability < .8) {
            for (int i = 0; i < al.size(); i++) {
                Action a = al.get(i);
                temp.setStateEqual(st);
                temp.setX(st.getX() + st.getXVel() + a.getXAcc());
                temp.setY(st.getY() + st.getYVel() + a.getYAcc());
                temp.setXVel(temp.getXVel() + a.getXAcc());
                temp.setYVel(temp.getYVel() + a.getYAcc());
                if (t.checkFinish(st, temp)) {
                    return -1;
                }
                int spot = searchState(temp);
                if (spot != -1) {
                    if (U[spot] > max) {
                        max = U[spot];
                        maxSpot = spot;
                    }
                }
            }
            return maxSpot;

        } else {
            Action noAct = new Action(0, 0);
            st.setX((st.getX() + st.getXVel()));
            st.setY((st.getY() + st.getYVel()));
            int spot = searchState(st);
            if(spot == -1){
                return 0;
            }else{
                return spot;
            }
        }


    }

    private double getValueFromU(State sFrom, State sTo) {
        if (t.checkFinish(sFrom, sTo)) {
            return 1000 + sFrom.getXVel();
        }
        //search for the spot in U that coorisponds to s' (sTo)
        int spot = searchState(sTo);
        //if negative one is returned then the state is out of bounds
        if (spot == -1) {
            return 0;//no reward
        } else {
            //check if a collision occurs between the states
            //if it does not return the Utility of the spot
            if (t.checkCollision(sFrom, sTo)) {
                return U[spot];
            } else {//else there is a collision so 0 is returned
                int beginingSpot = searchState(start);
                return U[beginingSpot];
            }
        }
    }

    //The reward of every state is 0 aside from the finish line wich is 100
    private int Reward(State s) {
        char c = t.getSpot(s.getY(), s.getX());
        if (c == 'F') {
            return 100;
        } else {
            return 0;
        }
    }

    private int searchState(State s) {
        for (int i = 0; i < numStates * 121; i += 121) {
            if (S[i].spotEquals(s)) {
                for (int j = 0; j < 121; j++) {
                    if (S[j + i].velocityEquals(s)) {
                        return i + j;
                    }
                }
            }
        }
        return -1;
    }
    
}
