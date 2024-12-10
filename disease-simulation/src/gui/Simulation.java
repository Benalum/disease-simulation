/*

Creators:  Alex Hartel and Justin Nelson
Created For: CS351 group project at the University of New Mexico

Manages the overall simulation.
Controls the flow of time and updates the state of agents accordingly.
Determines which agents are neighbors and facilitates communication between them.
Handles agent movement if enabled.

*/


package src.gui;


import javafx.scene.layout.Pane;
import src.config.Configuration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;


public class Simulation {
    private ConcurrentHashMap<Integer, Agent> mapThread;
    private ArrayList<Thread> threads;
    private boolean isRunning = false;
    private int exposureDistance, sickDelay, recoveryDelay,
                initialSickAgents;
    private boolean grid, gridRand, random;
    private ArrayList<Agent> agents;
    private Configuration settings;
    private LinkedBlockingQueue<Message> logQ;
    private LinkedBlockingQueue<Message> eventLog;
    private LinkedBlockingQueue<int[]> alreadyPresentData;

    /**

    Initialize values based on the input for the simulation

     */

    public Simulation(Configuration settings) {
        this.settings = settings;
        this.mapThread = new ConcurrentHashMap<>();
        this.logQ = new LinkedBlockingQueue<>(10);
        this.threads = new ArrayList<>();
        this.eventLog = new LinkedBlockingQueue<>();
        this.agents = new ArrayList<>();
        this.alreadyPresentData = new LinkedBlockingQueue<>();
        this.exposureDistance = settings.getExposureDistance();
        this.initialSickAgents = settings.getInitialSickAgents();
        this.sickDelay = settings.getIncubationPeriod();
        this.recoveryDelay = settings.getSicknessTime();
        this.grid = (settings.getGridCheck() && !settings.getRandCheck());
        this.gridRand = (settings.getGridCheck() && settings.getRandCheck());
        this.random = (!settings.getGridCheck() && settings.getRandCheck());
        generateAgents();
    }

    /**

    Used to update neighbors and start threads for computation

     */

    public void runSimulation() {
        positionAgents();
        updateNeighborsRand(settings.getExposureDistance());
        for(Thread thread : threads) {thread.start();}
    }

    /**

    Used to interrupt threads

     */

    public void terminateSimulation() {
        for(Thread thread : threads) {thread.interrupt();}
    }

    /**

    Used to update the location of the agents on the pane

     */

    public void updatePane(Pane pane) {
        int count = 0;
        for(int i=0; i< agents.size(); i++) {
            Agent agent = mapThread.get(i);
            pane.getChildren().add(agent.getCircle());
            if(agent.queueCleared()) {count++;}
            logQ.clear();
        }
        if(count == settings.getInitialAgents()) {this.isRunning = false;}
    }


    private void generateAgents() {
        int total = 0;
        int recoverable, immuneAmount;
        Agent agent;

        if(grid) { total = settings.getGrid()[0]*settings.getGrid()[1]; }
        else if(gridRand || random) {total = settings.getInitialAgents();}
        else {System.out.println("We gots a problem with agent amount");}

        recoverable = (int)(total*settings.getRecoverProbability());
        immuneAmount = (int)(total*settings.getNImmune());

        for(int i=0; i<total; i++) {
            agent = new Agent(i,sickDelay, recoveryDelay, eventLog);
            if(0 < immuneAmount) {agent.setImmunity(); immuneAmount--;}
            else if(0 < initialSickAgents) {agent.queueMessage(new Message(
                    0,Status.SICK)); initialSickAgents--;}
            else if(0 < recoverable) {agent.setRecovery(); recoverable--;}
            agents.add(agent);
            threads.add(new Thread(agent));
            mapThread.put(i,agent);
        }
        Collections.shuffle(this.agents);
    }


    private void positionAgents() {
        Random rand = new Random();
        int rows = settings.getGrid()[0];
        int cols = settings.getGrid()[1];
        int index = 0;
        Agent a;
        Coordinate coord;
        if(grid) {
            for(int i=0; i<rows; i++) {
                for(int j=0; j<cols; j++) {
                    a = agents.get(index);
                    coord = new Coordinate(i*this.exposureDistance+9,
                            j*this.exposureDistance+9);
                    a.setCoord(coord);
                    index++;
                }
            }
        }
        else if(gridRand) {
            int count = 0;
            boolean[][] randomGrid = new boolean[settings.getGrid()
                    [0]][settings.getGrid()[1]];
            initRandGrid(randomGrid);
            while(count < agents.size()) {
                int x = rand.nextInt(settings.getGrid()[0]);
                int y = rand.nextInt(settings.getGrid()[1]);
                if(!randomGrid[x][y]) {
                    coord = new Coordinate(x*exposureDistance+9,
                            y*exposureDistance+9);
                    agents.get(count).setCoord(coord);
                    randomGrid[x][y] = true;
                    count++;
                }
            }
        }
        else if(random) {
            for(Agent agent : agents) {
                int x = rand.nextInt(settings.getDimensions()[0]);
                int y = rand.nextInt(settings.getDimensions()[1]);
                coord = new Coordinate(x,y);
                agent.setCoord(coord);
            }
        }
        else {System.out.println("We gots a problem with grid input");}
    }


    private void initRandGrid(boolean[][] randomGrid) {
        for(int i=0; i< randomGrid.length; i++) {
            for(int j=0; j<randomGrid[0].length; j++) {
                randomGrid[i][j] = false;
            }
        }
    }


    private void updateNeighborsRand(int exposureDistance) {
        for(Agent a1 : agents) {
            for(Agent a2 : agents) {
                if(a1.getId() != a2.getId()) {
                    double distance = Math.sqrt(Math.pow((a2.getX()-a1.getX()),
                            2)+Math.pow((a2.getY()-a1.getY()),2));
                    if(distance <= exposureDistance) { a1.addNeighbor(a2); }
                }
            }
        }
    }

    /**

    Takes in the lined blocking queue int array and returns it to place it in a new queue
    Might be an extra step not needed not sure and not enough time to look further into it

     */

    public LinkedBlockingQueue<int[]> updateGraphData
    (LinkedBlockingQueue<int[]> alreadyPresentData)
            throws InterruptedException {
        int sick = 0;
        int dead = 0;
        int recovered = 0;
        int neverSick = 0;
        int immune = 0;
        for (Agent agent : agents) {
            if (agent.getStatus().equals(Status.SICK)) {
                sick++;
            } else if (agent.getStatus().equals(Status.DEAD)) {
                dead++;
            } else if (agent.getStatus().equals(Status.RECOVERED)) {
                recovered++;
            } else if (agent.getStatus().equals(Status.IMMUNE)) {
                immune++;
            } else {
                neverSick++;
            }
        }
        alreadyPresentData.put(new int[]{neverSick, immune, recovered, sick,
                dead});
        return alreadyPresentData;
    }

    /**

    Used to update event log based on what the agents provide

     */

    public LinkedBlockingQueue<Message> updateEventLog(
            LinkedBlockingQueue<Message> events)
            throws InterruptedException {
        for(Agent agent : agents) {
            if(!agent.getEventLog().isEmpty()) {
                for (Message x : agent.getEventLog()) {
                    events.put(x);
                }
                agent.clearEventLog();
            }
        }
        return events;
    }
}