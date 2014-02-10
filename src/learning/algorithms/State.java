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
public class State {
    int x;
    int y;
    int xVel;
    int yVel;
    
    public State(int yv, int xv, int ys, int xs){
        yVel = yv;
        xVel = xv;
        x = xs;
        y = ys;
    }
    
    public int getX(){
        return x;
    }
    
    public int getY(){
        return y;
    }
    
    public void setX(int newX){
        x = newX;
    }
    
    public void setY(int newY){
        y = newY;
    }
    
    public int getXVel(){
        return xVel;
    }
    
    public int getYVel(){
        return yVel;
    }
    
    public void setXVel(int newX){
        xVel = newX;
    }
    
    public void setYVel(int newY){
        yVel = newY;
    }
    
    public boolean spotEquals(State s){
        if(s.getX() == x && s.getY() == y){
            return true;
        }else{
            return false;
        }
    }
    
    public boolean velocityEquals(State s){
        if(s.getXVel() == xVel && s.getYVel() == yVel){
            return true;
        }else{
            return false;
        }
    }
    
    public ArrayList<Action> getActions(){
        ArrayList<Action> a = new ArrayList();
        if(xVel + 1 < 6 && yVel + 1 < 6){
            a.add(new Action(1, 1));
        }
        if(xVel + 1 < 6){
            a.add(new Action(0, 1));
        }
        if(yVel + 1 < 6){
            a.add(new Action(1, 0));
        }
        if(xVel - 1 > -6 && yVel - 1 > -6){
            a.add(new Action(-1, -1));
        }
        if(xVel - 1 > -6){
            a.add(new Action(0, -1));
        }
        if(yVel - 1 > -6){
            a.add(new Action(-1, 0));
        }
        a.add(new Action(0,0));
        
        return a;
    }
    
    public void setStateEqual(State st){
        x = st.getX();
        y = st.getY();
        xVel = st.getXVel();
        yVel = st.getYVel();
    }
}
