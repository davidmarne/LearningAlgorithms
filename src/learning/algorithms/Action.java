/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package learning.algorithms;

/**
 *
 * @author davemarne
 */
public class Action {
    int xAcc;
    int yAcc;

    public Action(int y, int x){
        yAcc = y;
        xAcc = x;
    }
    
    public int getXAcc(){
        return xAcc;
    }
    
    public int getYAcc(){
        return yAcc;
    
    } 
    public boolean accelerationsEqual(Action a){
        if(a.xAcc == xAcc && a.yAcc == yAcc){
            return true;
        }else{
            return false;
        }
    }
    
}
