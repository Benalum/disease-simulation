/*

Creators:  Alex Hartel and Justin Nelson
Created For: CS351 group project at the University of New Mexico

Used as a coordinate system for the agents.
Allows agents to be represented as dots on the GUI based on coordinate
location.

 */


package src.gui;

public class Coordinate {
    private int row;
    private int col;


    public Coordinate(int row, int col) {
        this.row = row;
        this.col = col;
    }


    public int getCol() {return this.col;}
    public int getRow() {return this.row;}


    @Override
    public String toString() {
        return this.row+","+this.col;
    }
}
