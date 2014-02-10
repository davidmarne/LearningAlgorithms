/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package learning.algorithms;

import java.io.File;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author davemarne
 */
public class Track {
    char track[][];
    int dimx;
    int dimy;
    public Track(File f){
        InitializeTrack(f);
    }
    
    private void InitializeTrack(File f){
        
        try{
            //initialize scanner
            Scanner br = new Scanner(f);
            //get dimensions
            dimy = br.nextInt();
            br.next();
            dimx = br.nextInt();
            br.nextLine();

            track = new char[dimy][dimx];

            for (int i = 0; i < dimy; i++) {
                String line = br.nextLine();
                for (int j = 0; j < dimx; j++) {
                    track[i][j] = line.charAt(j);
                }
            }
        }catch(Exception e){
            
        }
    }
    
    public int getNumStates(){
        int result = 0;
        for(int i = 0; i < dimy; i++){
            for(int j = 0; j < dimx; j++){
                if(track[i][j] == '.' || track[i][j] == 'S'){// || track[i][j] == 'F'){
                    result++;
                }
            }
        }
        
        return result;
    }
    
    public char getSpot(int y, int x){
        try{
        return track[y][x];
        }catch(Exception e){
            return '#';
        }
    }
    
    public State findStart(){
        ArrayList<State> al = new ArrayList();
        for(int i = 0; i < dimy; i++){
            for(int j = 0; j < dimx; j++){
                if(track[i][j] == 'S'){
                    al.add(new State(0,0,i,j));
                }
            }
        }
        //if no start is found return all -1s
        if(al.isEmpty()){
            return new State(-1,-1,-1,-1);
        }else{
            //get a random integer, mod it by the size of my list and chose
            //the state at that spot in the list
            Random r = new Random();
            int rand = r.nextInt();
            int size = al.size()-1;
            int spot = Math.abs(rand % size);
            return al.get(spot);
        }
    }
    public int getDimx(){
        return dimx;
    }
    public int getDimy(){
        return dimy;
    }
    
    //prints the track with an R representing the current state of the agent
    public void printTrack(State s){
        int y = s.getY();
        int x = s.getX();
        for(int i = 0; i < dimy; i++){
            for(int j = 0; j < dimx; j++){
                if(i == y && j == x){
                    System.out.print("R");
                }else{
                    System.out.print(track[i][j]);
                }
            }
            System.out.println();
        }
    }
    
    
    //returns false if collision occurs
    public boolean checkCollision(State sFrom, State sTo){
        int toX = sTo.getX();
        int toY = sTo.getY();
        int fromX = sFrom.getX();
        int fromY = sFrom.getY();
        //move is out of bounds
        if(toX < 0 || toX >= dimx || toY < 0 || toY >= dimy){
            return false;
        }
        
        //the destination is a wall
        if(getSpot(toY, toX) == '#'){
            return false;
        }
        
        //get delta y and delta x
        int xDiff = toX - fromX;
        int yDiff = toY - fromY;
        double slope;
        
        //if x is not 0 (slope is not vertical) then slope = delta y/delta x
        if(xDiff != 0){
            slope = (double) yDiff / xDiff;
        }else{
            slope = Integer.MAX_VALUE;//slope is vertical, set to max integer value
        }
        
        //if the slope is vertical incriment only the y value and check every spot for a #
        if(slope == Integer.MAX_VALUE && yDiff > 0){
            for(int i = 0; i < yDiff; i++){
                if(getSpot(fromY+i, fromX) == '#'){
                    return false;
                }
            }
            return true;
        }else if(slope == Integer.MAX_VALUE && yDiff < 0){
            for(int i = -1; i > yDiff; i--){
                if(getSpot(fromY+i, fromX) == '#'){
                    return false;
                }
            }
            return true;
        }
        else if(slope == 1 && xDiff > 0){
            for(int i = 1; i <  Math.abs(yDiff); i++){
                if(getSpot(fromY+i, fromX+i) == '#'){
                    return false;
                }
            }
        }
        else if(slope == 1 && xDiff < 0){
            for(int i = 1; i <  Math.abs(yDiff); i++){
                if(getSpot(fromY-i, fromX-i) == '#'){
                    return false;
                }
            }
        } 
        else if(slope == -1 && xDiff > 0){
            for(int i = 1; i < Math.abs(yDiff); i++){
                if(getSpot(fromY-i, fromX+i) == '#'){
                    return false;
                }
            }
        }
        else if(slope == -1 && xDiff < 0){
            for(int i = 1; i <  Math.abs(yDiff); i++){
                if(getSpot(fromY+i, fromX-i) == '#'){
                    return false;
                }
            }
        }  
        else if(slope > 1 && xDiff > 0){
            //holds the actual x distance traveled according to the slope
            double xDistance = 0;
            
            //both is true if two x values with the same y need to be checked
            //this is because the slope goes through both x coodinates while staying in the same y
            boolean both = false;
            
            //loop through how ever many times you need to increment y to reach s'
            for(int i = 1; i < Math.abs(yDiff); i++){
                //add the inverse of the slope (distance x moved) to the the distance x has traveld
                xDistance += 1.0/slope;
                
                //cut off the decimal from the distance x has traveled
                int iXDistance = (int) xDistance;
                
                //gets the value of the decimal cut off from xDistance
                double dec = xDistance - iXDistance;
                
                //if that decimal + one more increment of the slope is greater than
                //1 then it will travel through two x values. both is set to true
                if(dec + (1.0/slope) >= 1){
                    both = true;
                }
                
                //checks the neccessary spots for # and returns false if one is found
                if(getSpot(fromY+i, fromX+iXDistance) == '#'){
                    return false;
                }
                if(both){
                        if(getSpot(fromY+i, fromX+iXDistance+1) == '#'){
                            return false;
                        }
                }
            }
        }
        else if(slope > 1 && xDiff < 0){
            //holds the actual x distance traveled according to the slope
            double xDistance = 0;
            
            //both is true if two x values with the same y need to be checked
            //this is because the slope goes through both x coodinates while staying in the same y
            boolean both = false;
            
            //loop through how ever many times you need to increment y to reach s'
            for(int i = 1; i < Math.abs(yDiff); i++){
                //add the inverse of the slope (distance x moved) to the the distance x has traveld
                xDistance -= 1.0/slope;
                
                //cut off the decimal from the distance x has traveled
                int iXDistance = (int) xDistance;
                
                //gets the value of the decimal cut off from xDistance
                double dec = xDistance - iXDistance;
                
                //if that decimal + one more increment of the slope is greater than
                //1 then it will travel through two x values. both is set to true
                if(dec - (1.0/slope) <= -1){
                    both = true;
                }
                
                //checks the neccessary spots for # and returns false if one is found
                if(getSpot(fromY-i, fromX+iXDistance) == '#'){
                    return false;
                }
                if(both){
                        if(getSpot(fromY-i, fromX+iXDistance-1) == '#'){
                            return false;
                        }
                }
            }
        }
        else if(slope < -1 && xDiff > 0){
            //holds the actual x distance traveled according to the slope
            double xDistance = 0;
            
            //both is true if two x values with the same y need to be checked
            //this is because the slope goes through both x coodinates while staying in the same y
            boolean both = false;
            
            //loop through how ever many times you need to increment y to reach s'
            for(int i = 1; i < Math.abs(yDiff); i++){
                //subtract the inverse  slope from the distance x has traveld
                xDistance += Math.abs(1.0 / slope);
                
                //cut off the decimal from the distance x has traveled
                int iXDistance = (int) xDistance;
                
                //gets the value of the decimal cut off from xDistance
                double dec = xDistance - iXDistance;
                
                
                
                //if that decimal + one more increment of the slope is greater than
                //1 then it will travel through two x values. both is set to true
                if(dec + Math.abs(1.0/slope) >= 1){
                    both = true;
                }
                
                //checks the neccessary spots for # and returns false if one is found
                if(getSpot(sFrom.getY()-i, sFrom.getX()+iXDistance) == '#'){
                    return false;
                }
                if(both){
                    if(getSpot(sFrom.getY()-i, sFrom.getX()+iXDistance+1) == '#'){
                         return false;
                    }
                }
                
            }
        }else if(slope < -1 && xDiff < 0){
            //holds the actual x distance traveled according to the slope
            double xDistance = 0;
            
            //both is true if two x values with the same y need to be checked
            //this is because the slope goes through both x coodinates while staying in the same y
            boolean both = false;
            
            //loop through how ever many times you need to increment y to reach s'
            for(int i = 1; i < Math.abs(yDiff); i++){
                //subtract the inverse  slope from the distance x has traveld
                xDistance += (1.0 / slope);
                
                //cut off the decimal from the distance x has traveled
                int iXDistance = (int) xDistance;
                
                //gets the value of the decimal cut off from xDistance
                double dec = xDistance - iXDistance;
                
                
                
                //if that decimal + one more increment of the slope is greater than
                //1 then it will travel through two x values. both is set to true
                if(dec + (1.0/slope) <= -1){
                    both = true;
                }
                
                //checks the neccessary spots for # and returns false if one is found
                if(getSpot(sFrom.getY()+i, sFrom.getX()+iXDistance) == '#'){
                    return false;
                }
                if(both){
                    if(getSpot(sFrom.getY()+i, sFrom.getX()+iXDistance-1) == '#'){
                         return false;
                    }
                }
                
            }
        }
        else if(slope > 0 && slope < 1 && xDiff < 0){
            double yDistance = 0;
            
            //both is true if two x values with the same y need to be checked
            //this is because the slope goes through both x coodinates while staying in the same y
            boolean both = false;
            
            //loop through how ever many times you need to increment y to reach s'
            for(int i = 1; i < Math.abs(xDiff); i++){
                //subtract the inverse  slope from the distance x has traveld
                yDistance -= slope;
                
                //cut off the decimal from the distance x has traveled
                int iYDistance = (int) yDistance;
                
                //gets the value of the decimal cut off from xDistance
                double dec = yDistance - iYDistance;
                
                
                
                //if that decimal + one more increment of the slope is greater than
                //1 then it will travel through two x values. both is set to true
                if(dec - slope <= -1){
                    both = true;
                }
                
                //checks the neccessary spots for # and returns false if one is found
                if(getSpot(fromY+iYDistance, fromX-i) == '#'){
                    return false;
                }
                if(both){
                    if(getSpot(fromY+iYDistance-1, fromX-i) == '#'){
                         return false;
                    }
                }
            }
          } else if (slope > 0 && slope < 1 && xDiff > 0) {
            double yDistance = 0;

            //both is true if two x values with the same y need to be checked
            //this is because the slope goes through both x coodinates while staying in the same y
            boolean both = false;

            //loop through how ever many times you need to increment y to reach s'
            for (int i = 1; i < xDiff; i++) {
                //subtract the inverse  slope from the distance x has traveld
                yDistance += slope;

                //cut off the decimal from the distance x has traveled
                int iYDistance = (int) yDistance;

                //gets the value of the decimal cut off from xDistance
                double dec = yDistance - iYDistance;



                //if that decimal + one more increment of the slope is greater than
                //1 then it will travel through two x values. both is set to true
                if (dec + slope >= 1) {
                    both = true;
                }

                //checks the neccessary spots for # and returns false if one is found
                if (getSpot(fromY + iYDistance, fromX + i) == '#') {
                    return false;
                }
                if (both) {
                    if (getSpot(fromY + iYDistance + 1, fromX + i) == '#') {
                        return false;
                    }
                }
            }
        } else if(slope < 0 && slope > -1 && xDiff > 0){
            double yDistance = 0;

            //both is true if two x values with the same y need to be checked
            //this is because the slope goes through both x coodinates while staying in the same y
            boolean both = false;

            //loop through how ever many times you need to increment y to reach s'
            for (int i = 1; i < xDiff; i++) {
                //subtract the inverse  slope from the distance x has traveld
                yDistance += slope;

                //cut off the decimal from the distance x has traveled
                int iYDistance = (int) yDistance;

                //gets the value of the decimal cut off from xDistance
                double dec = yDistance - iYDistance;



                //if that decimal + one more increment of the slope is greater than
                //1 then it will travel through two x values. both is set to true
                if (dec + slope <= -1) {
                    both = true;
                }

                //checks the neccessary spots for # and returns false if one is found
                if (getSpot(fromY + iYDistance, fromX + i) == '#') {
                    return false;
                }
                if (both) {
                    if (getSpot(fromY + iYDistance - 1, fromX + i) == '#') {
                        return false;
                    }
                }
            }
        } else if (slope < 0 && slope > -1 && xDiff < 0) {
            double yDistance = 0;

            //both is true if two x values with the same y need to be checked
            //this is because the slope goes through both x coodinates while staying in the same y
            boolean both = false;

            //loop through how ever many times you need to increment y to reach s'
            for (int i = 1; i < Math.abs(xDiff); i++) {
                //subtract the inverse  slope from the distance x has traveld
                yDistance += Math.abs(slope);

                //cut off the decimal from the distance x has traveled
                int iYDistance = (int) yDistance;

                //gets the value of the decimal cut off from xDistance
                double dec = yDistance - iYDistance;



                //if that decimal + one more increment of the slope is greater than
                //1 then it will travel through two x values. both is set to true
                if (dec + slope >= 1) {
                    both = true;
                }

                //checks the neccessary spots for # and returns false if one is found
                if (getSpot(fromY + iYDistance, fromX - i) == '#') {
                    return false;
                }
                if (both) {
                    if (getSpot(fromY + iYDistance + 1, fromX - i) == '#') {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    //This function is essentially the same as checkCollision but
    //it returns true if the agent passes over an F rather than returning
    //false when the agent passes over a #
    public boolean checkFinish(State sFrom, State sTo){
        int toX = sTo.getX();
        int toY = sTo.getY();
        int fromX = sFrom.getX();
        int fromY = sFrom.getY();
        //move is out of bounds
        if(toX < 0 || toX >= dimx || toY < 0 || toY >= dimy){
            return false;
        }
        
        //the destination is a wall
        if(getSpot(toY, toX) == '#'){
            return false;
        }
        
        //get delta y and delta x
        int xDiff = toX - fromX;
        int yDiff = toY - fromY;
        double slope;
        
        //if x is not 0 (slope is not vertical) then slope = delta y/delta x
        if(xDiff != 0){
            slope = (double) yDiff / xDiff;
        }else{
            slope = Integer.MAX_VALUE;//slope is vertical, set to max integer value
        }
        
        //if the slope is vertical incriment only the y value and check every spot for a #
        if(slope == Integer.MAX_VALUE && yDiff > 0){
            for(int i = 0; i <  Math.abs(yDiff); i++){
                if(getSpot(fromY+i, fromX) == '#'){
                    return false;
                }else if(getSpot(fromY+i, fromX) == 'F'){
                    return true;
                }
            }
        }else if(slope == Integer.MAX_VALUE && yDiff < 0){
            for(int i = 0; i >  yDiff; i--){
                if(getSpot(fromY+i, fromX) == '#'){
                    return false;
                }else if(getSpot(fromY+i, fromX) == 'F'){
                    return true;
                }
            }
        }
        else if(slope == 1 && xDiff > 0){
            for(int i = 1; i <  Math.abs(yDiff); i++){
                if(getSpot(fromY+i, fromX+i) == '#'){
                    return false;
                }else if(getSpot(fromY+i, fromX+i) == 'F'){
                    return true;
                }
            }
        }
        else if(slope == 1 && xDiff < 0){
            for(int i = 1; i <  Math.abs(yDiff); i++){
                if(getSpot(fromY-i, fromX-i) == '#'){
                    return false;
                }else if(getSpot(fromY-i, fromX-i) == 'F'){
                    return true;
                }
            }
        } 
        else if(slope == -1 && xDiff > 0){
            for(int i = 1; i <  Math.abs(yDiff); i++){
                if(getSpot(fromY-i, fromX+i) == '#'){
                    return false;
                }else if(getSpot(fromY-i, fromX+i) == 'F'){
                    return true;
                }
            }
        }
        else if(slope == -1 && xDiff < 0){
            for(int i = 1; i <  Math.abs(yDiff); i++){
                if(getSpot(fromY+i, fromX-i) == '#'){
                    return false;
                }else if(getSpot(fromY+i, fromX-i) == 'F'){
                    return true;
                }
            }
        }  
        else if(slope > 1 && xDiff > 0){
            //holds the actual x distance traveled according to the slope
            double xDistance = 0;
            
            //both is true if two x values with the same y need to be checked
            //this is because the slope goes through both x coodinates while staying in the same y
            boolean both = false;
            
            //loop through how ever many times you need to increment y to reach s'
            for(int i = 1; i < Math.abs(yDiff); i++){
                //add the inverse of the slope (distance x moved) to the the distance x has traveld
                xDistance += 1.0/slope;
                
                //cut off the decimal from the distance x has traveled
                int iXDistance = (int) xDistance;
                
                //gets the value of the decimal cut off from xDistance
                double dec = xDistance - iXDistance;
                
                //if that decimal + one more increment of the slope is greater than
                //1 then it will travel through two x values. both is set to true
                if(dec + (1.0/slope) >= 1){
                    both = true;
                }
                
                //checks the neccessary spots for # and returns false if one is found
                if(getSpot(fromY+i, fromX+iXDistance) == '#'){
                    return false;
                }else if(getSpot(fromY+i, fromX+iXDistance) == 'F'){
                    return true;
                }
                if(both){
                        if(getSpot(fromY+i, fromX+iXDistance+1) == '#'){
                            return false;
                        }else if(getSpot(fromY+i, fromX+iXDistance+1) == 'F'){
                            return true;
                        }
                }
            }
        }
        else if(slope > 1 && xDiff < 0){
            //holds the actual x distance traveled according to the slope
            double xDistance = 0;
            
            //both is true if two x values with the same y need to be checked
            //this is because the slope goes through both x coodinates while staying in the same y
            boolean both = false;
            
            //loop through how ever many times you need to increment y to reach s'
            for(int i = 1; i < Math.abs(yDiff); i++){
                //add the inverse of the slope (distance x moved) to the the distance x has traveld
                xDistance -= 1.0/slope;
                
                //cut off the decimal from the distance x has traveled
                int iXDistance = (int) xDistance;
                
                //gets the value of the decimal cut off from xDistance
                double dec = xDistance - iXDistance;
                
                //if that decimal + one more increment of the slope is greater than
                //1 then it will travel through two x values. both is set to true
                if(dec - (1.0/slope) <= -1){
                    both = true;
                }
                
                //checks the neccessary spots for # and returns false if one is found
                if(getSpot(fromY-i, fromX+iXDistance) == '#'){
                    return false;
                }else if(getSpot(fromY-i, fromX+iXDistance) == 'F'){
                    return true;
                }
                if(both){
                        if(getSpot(fromY-i, fromX+iXDistance-1) == '#'){
                            return false;
                        }else if(getSpot(fromY-i, fromX+iXDistance-1) == 'F'){
                            return true;
                        }
                }
            }
        }
        else if(slope < -1 && xDiff > 0){
            //holds the actual x distance traveled according to the slope
            double xDistance = 0;
            
            //both is true if two x values with the same y need to be checked
            //this is because the slope goes through both x coodinates while staying in the same y
            boolean both = false;
            
            //loop through how ever many times you need to increment y to reach s'
            for(int i = 1; i < Math.abs(yDiff); i++){
                //subtract the inverse  slope from the distance x has traveld
                xDistance += Math.abs(1.0 / slope);
                
                //cut off the decimal from the distance x has traveled
                int iXDistance = (int) xDistance;
                
                //gets the value of the decimal cut off from xDistance
                double dec = xDistance - iXDistance;
                
                
                
                //if that decimal + one more increment of the slope is greater than
                //1 then it will travel through two x values. both is set to true
                if(dec + Math.abs(1.0/slope) >= 1){
                    both = true;
                }
                
                //checks the neccessary spots for # and returns false if one is found
                if(getSpot(sFrom.getY()-i, sFrom.getX()+iXDistance) == '#'){
                    return false;
                }else if(getSpot(sFrom.getY()-i, sFrom.getX()+iXDistance) == 'F'){
                    return true;
                }
                if(both){
                    if(getSpot(sFrom.getY()-i, sFrom.getX()+iXDistance+1) == '#'){
                         return false;
                    }else if(getSpot(sFrom.getY()-i, sFrom.getX()+iXDistance+1) == 'F'){
                         return true;
                    }
                }
                
            }
        }else if(slope < -1 && xDiff < 0){
            //holds the actual x distance traveled according to the slope
            double xDistance = 0;
            
            //both is true if two x values with the same y need to be checked
            //this is because the slope goes through both x coodinates while staying in the same y
            boolean both = false;
            
            //loop through how ever many times you need to increment y to reach s'
            for(int i = 1; i < Math.abs(yDiff); i++){
                //subtract the inverse  slope from the distance x has traveld
                xDistance += (1.0 / slope);
                
                //cut off the decimal from the distance x has traveled
                int iXDistance = (int) xDistance;
                
                //gets the value of the decimal cut off from xDistance
                double dec = xDistance - iXDistance;
                
                
                
                //if that decimal + one more increment of the slope is greater than
                //1 then it will travel through two x values. both is set to true
                if(dec + (1.0/slope) <= -1){
                    both = true;
                }
                
                //checks the neccessary spots for # and returns false if one is found
                if(getSpot(sFrom.getY()+i, sFrom.getX()+iXDistance) == '#'){
                    return false;
                }else if(getSpot(sFrom.getY()+i, sFrom.getX()+iXDistance) == 'F'){
                    return true;
                }
                if(both){
                    if(getSpot(sFrom.getY()+i, sFrom.getX()+iXDistance-1) == '#'){
                         return false;
                    }else if(getSpot(sFrom.getY()+i, sFrom.getX()+iXDistance-1) == 'F'){
                        return true;
                    }
                }
                
            }
        }
        else if(slope > 0 && slope < 1 && xDiff < 0){
            double yDistance = 0;
            
            //both is true if two x values with the same y need to be checked
            //this is because the slope goes through both x coodinates while staying in the same y
            boolean both = false;
            
            //loop through how ever many times you need to increment y to reach s'
            for(int i = 1; i < Math.abs(xDiff); i++){
                //subtract the inverse  slope from the distance x has traveld
                yDistance -= slope;
                
                //cut off the decimal from the distance x has traveled
                int iYDistance = (int) yDistance;
                
                //gets the value of the decimal cut off from xDistance
                double dec = yDistance - iYDistance;
                
                
                
                //if that decimal + one more increment of the slope is greater than
                //1 then it will travel through two x values. both is set to true
                if(dec - slope <= -1){
                    both = true;
                }
                
                //checks the neccessary spots for # and returns false if one is found
                if(getSpot(fromY+iYDistance, fromX-i) == '#'){
                    return false;
                }else if(getSpot(fromY+iYDistance, fromX-i) == 'F'){
                    return true;
                }
                if(both){
                    if(getSpot(fromY+iYDistance-1, fromX-i) == '#'){
                         return false;
                    }else if(getSpot(fromY+iYDistance-1, fromX-i) == 'F'){
                        return true;
                    }
                }
            }
          } else if (slope > 0 && slope < 1 && xDiff > 0) {
            double yDistance = 0;

            //both is true if two x values with the same y need to be checked
            //this is because the slope goes through both x coodinates while staying in the same y
            boolean both = false;

            //loop through how ever many times you need to increment y to reach s'
            for (int i = 1; i < xDiff; i++) {
                //subtract the inverse  slope from the distance x has traveld
                yDistance += slope;

                //cut off the decimal from the distance x has traveled
                int iYDistance = (int) yDistance;

                //gets the value of the decimal cut off from xDistance
                double dec = yDistance - iYDistance;



                //if that decimal + one more increment of the slope is greater than
                //1 then it will travel through two x values. both is set to true
                if (dec + slope >= 1) {
                    both = true;
                }

                //checks the neccessary spots for # and returns false if one is found
                if (getSpot(fromY + iYDistance, fromX + i) == '#') {
                    return false;
                }else if (getSpot(fromY + iYDistance, fromX + i) == 'F') {
                    return true;
                }
                if (both) {
                    if (getSpot(fromY + iYDistance + 1, fromX + i) == '#') {
                        return false;
                    }else if(getSpot(fromY + iYDistance + 1, fromX + i) == 'F'){
                        return true;
                    }
                }
            }
        } else if(slope < 0 && slope > -1 && xDiff > 0){
            double yDistance = 0;

            //both is true if two x values with the same y need to be checked
            //this is because the slope goes through both x coodinates while staying in the same y
            boolean both = false;

            //loop through how ever many times you need to increment y to reach s'
            for (int i = 1; i < xDiff; i++) {
                //subtract the inverse  slope from the distance x has traveld
                yDistance += slope;

                //cut off the decimal from the distance x has traveled
                int iYDistance = (int) yDistance;

                //gets the value of the decimal cut off from xDistance
                double dec = yDistance - iYDistance;



                //if that decimal + one more increment of the slope is greater than
                //1 then it will travel through two x values. both is set to true
                if (dec + slope <= -1) {
                    both = true;
                }

                //checks the neccessary spots for # and returns false if one is found
                if (getSpot(fromY + iYDistance, fromX + i) == '#') {
                    return false;
                }else if(getSpot(fromY + iYDistance, fromX + i) == 'F') {
                    return true;
                }
                if (both) {
                    if (getSpot(fromY + iYDistance - 1, fromX + i) == '#') {
                        return false;
                    }else if(getSpot(fromY + iYDistance - 1, fromX + i) == 'F') {
                        return true;
                    }
                }
            }
        } else if (slope < 0 && slope > -1 && xDiff < 0) {
            double yDistance = 0;

            //both is true if two x values with the same y need to be checked
            //this is because the slope goes through both x coodinates while staying in the same y
            boolean both = false;

            //loop through how ever many times you need to increment y to reach s'
            for (int i = 1; i < Math.abs(xDiff); i++) {
                //subtract the inverse  slope from the distance x has traveld
                yDistance += Math.abs(slope);

                //cut off the decimal from the distance x has traveled
                int iYDistance = (int) yDistance;

                //gets the value of the decimal cut off from xDistance
                double dec = yDistance - iYDistance;



                //if that decimal + one more increment of the slope is greater than
                //1 then it will travel through two x values. both is set to true
                if (dec + slope >= 1) {
                    both = true;
                }

                //checks the neccessary spots for # and returns false if one is found
                if (getSpot(fromY + iYDistance, fromX - i) == '#') {
                    return false;
                }else if (getSpot(fromY + iYDistance, fromX - i) == 'F') {
                    return true;
                }
                if (both) {
                    if (getSpot(fromY + iYDistance + 1, fromX - i) == '#') {
                        return false;
                    }else if(getSpot(fromY + iYDistance + 1, fromX - i) == 'F') {
                        return true;
                    }
                }
            }
        }
        if(getSpot(toY, toX) == 'F'){
            return true;
        }
        return false;
    }
}
