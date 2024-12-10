/*

Creators:  Alex Hartel and Justin Nelson
Created For: CS351 group project at the University of New Mexico

Parses the configuration file and extracts simulation parameters.
Provides methods to access these parameters throughout the simulation.

 */


package src.config;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class Configuration {
    private int[] dimensions = {200, 200};
    private int[] grid = {0,0};
    private int exposureDistance = 20;
    private int incubationPeriod = 5;
    private int sicknessTime = 10;
    private double recoverProbability = 0.95;
    private double nImmune = 0;
    private int initialAgents = 100;
    private int initialSickAgents = 1;
    private boolean randomCheck = true;
    private boolean gridCheck = false;

    /**

    Create the configuration based on the file given to it

     */

    public Configuration(File filePath) {
        readInDocument(filePath);
    }

    /**
     *
     * @param dimensions
     * @param grid
     * @param exposureDistance
     * @param incubationPeriod
     * @param sicknessTime
     * @param initialAgents
     * @param initialSickAgents
     * @param recoverProbability
     * @param nImmune
     * @param randomCheck
     * @param gridCheck
     */

    public Configuration(int[] dimensions, int[] grid, int exposureDistance,
                         int incubationPeriod,
                         int sicknessTime, int initialAgents,
                         int initialSickAgents, double recoverProbability,
                         double nImmune,
                         boolean randomCheck, boolean gridCheck) {
        setDimensions(dimensions[0],dimensions[1]);
        if(grid != null) { setGrid(grid[0],grid[1]);}
        setExposureDistance(exposureDistance);
        setIncubationPeriod(incubationPeriod);
        setSicknessTime(sicknessTime);
        setRecoverProbability(recoverProbability);
        setInitialSickAgents(initialSickAgents);
        setInitialAgents(initialAgents);
        setNImmune(nImmune);
        this.randomCheck = randomCheck;
        this.gridCheck = gridCheck;
    }

    /**
     *
     * @return
     */

    @Override
    public String toString() {
        String temp = "dimension: " +dimensions[0]+","+dimensions[1]+"\n"
                + "grid: "+grid[0]+","+grid[1]+"\n"
                + "exposure distance: "+exposureDistance+"\n"
                + "incubation period: "+incubationPeriod+"\n"
                + "sickness time: "+sicknessTime+"\n"
                + "recovery probability: "+recoverProbability+"\n"
                + "immunity percentage: "+nImmune+"\n"
                + "initial agents: "+initialAgents+"\n"
                + "initial sick agents: "+initialSickAgents+"\n"
                + "random check: "+randomCheck+"\n"
                + "grid check: "+gridCheck+"\n";

        String temp2 = String.format("dimension: %14d,%d\ngrid: %19d,%d" +
                        "\nexposure distance: %5d\n" +
                "incubation period: %4d\nsickness time: %9d\n" +
                        "recovery probability: %.01f\n" +
                "immunity percentage: %.01f\ninitial agents: %9d" +
                        "\ninitial sick agents: %2d\nrandom check: %b" +
                        "\ngrid check: %b\n",
                dimensions[0],dimensions[1],grid[0],grid[1],
                exposureDistance,incubationPeriod,
                sicknessTime,recoverProbability*100,nImmune*100,
                initialAgents,initialSickAgents,
                randomCheck,gridCheck,randomCheck,gridCheck);
        return temp2;
    }

    /**

    Take in two int's and replace dimensions with the new in array

     */

    private void setDimensions(int row, int col){
        dimensions = new int[]{row,col};
    }

    /**

    Used to get the Dimensions for the scenerio

     */

    public int[] getDimensions(){
        return dimensions;
    }


    /**

    set grid with two integers as an input

     */

    private void setGrid(int row, int col){
        grid = new int[]{row,col};
        this.gridCheck = true;
    }

    /**

    get the grid as an int array

     */

    public int[] getGrid(){
        return grid;
    }

    /**

   Take in an integer and set the exposure distance

     */

    private void setExposureDistance(int input){
        exposureDistance = input;
    }

    /**

    get the exposure distance

     */


    public int getExposureDistance(){
        return exposureDistance;
    }

    /**

    get the incubation period

     */

    public int getIncubationPeriod(){
        return incubationPeriod;
    }

    /**

    set the incubation period based on an int provided

     */

    public void setIncubationPeriod(int input){
        incubationPeriod = input;
    }

    /**

    take in an int and set sickness time to the input

     */

    private void setSicknessTime(int input){
        sicknessTime = input;
    }

    /**

    returns an int for sickness time

     */

    public int getSicknessTime(){
        return sicknessTime;
    }

    /**

    set recover probability using input(double)

     */

    private void setRecoverProbability(double input){
        recoverProbability = input;
    }


    private void setNImmune(double nImmune) {this.nImmune = nImmune; }


    public double getNImmune() {return nImmune;}

    /**

    get the double value for recover probability

     */

    public double getRecoverProbability(){
        return recoverProbability;
    }

    /**

    Take in an integer and set the initial amount of agents

     */

    private void setInitialAgents(int amount){
        initialAgents = amount;
    }

    /**

    get the number of initial agents

     */

    public int getInitialAgents(){
        return initialAgents;
    }

    /**

    take in an integer and place it as the initial amount of sick agents

     */

    public void setInitialSickAgents(int amount){
        initialSickAgents = amount;
    }

    /**

    get the number of initial sick agents as an integer

     */

    public int getInitialSickAgents(){
        return initialSickAgents;
    }

    /**

    If randomGrid is found in text, it will set grid and agent

     */

    private void foundRandomGridOption(int row, int col, int agent){
        setGrid(row,col);
        setInitialAgents(agent);
    }


    public boolean getGridCheck() {return this.gridCheck;}


    public boolean getRandCheck() {return this.randomCheck;}

    /**

    What to do with the line that is being processed when reading in the file

     */

    private void processLineOfDocument(String line){
        String[] lineArray = line.split(" ");
        if(lineArray[0].equals("grid")){
            int row = Integer.parseInt(lineArray[1]);
            int col = Integer.parseInt(lineArray[2]);
            setGrid(row,col);
            setInitialAgents(row*col);
            this.randomCheck = false;
            this.gridCheck = true;
        }
        else if(lineArray[0].equals("initialsick")){
            setInitialSickAgents(Integer.parseInt(lineArray[1]));
        }
        else if(lineArray[0].equals("exposuredistance")){
            setExposureDistance(Integer.parseInt(lineArray[1]));
        }
        else if(lineArray[0].equals("incubation")){
            setIncubationPeriod(Integer.parseInt(lineArray[1]));
        }
        else if(lineArray[0].equals("sickness")){
            setSicknessTime(Integer.parseInt(lineArray[1]));
        }
        else if(lineArray[0].equals("recover")){
            setRecoverProbability(Double.parseDouble(lineArray[1]));
        }
        else if(lineArray[0].equals("dimensions")){
            setDimensions(Integer.parseInt(lineArray[1]),
                    Integer.parseInt(lineArray[2]));
        }
        else if(lineArray[0].equals("random")){
            setInitialAgents(Integer.parseInt(lineArray[1]));
        }
        else if(lineArray[0].equals("randomgrid")){
            foundRandomGridOption(Integer.parseInt(lineArray[1]),
                    Integer.parseInt(lineArray[2]),
                    Integer.parseInt(lineArray[3]));
        }
    }

    /**

    Read in a text file and set values

     */

    public void readInDocument(File pathToFile){
        try {
            FileReader fileReader = new FileReader(pathToFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                processLineOfDocument(line);
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
