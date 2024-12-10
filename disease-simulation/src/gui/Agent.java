/*

Creators:  Alex Hartel and Justin Nelson
Created For: CS351 group project at the University of New Mexico


Represents individual agents in the simulation.
Each agent runs on its own thread.
Maintains the state of the agent (vulnerable, sick, immune, dead).
Handles interactions with neighboring agents.
Contains methods for state transitions (exposure, recovery, death).

 */


package src.gui;


import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import static src.gui.Status.*;


public class Agent implements Runnable {

    private final int id, sickDelay, recoveryDelay;
    private Coordinate coord;
    private Status status;
    private Circle node;
    private boolean immune, recovery, sick, finished;
    private ArrayList<Agent> neighbors = new ArrayList<>();
    public LinkedBlockingQueue<Message> messageQueue;
    private LinkedBlockingQueue<Message> eventLog;

    /**
     *
     * @param id
     * @param sickDelay
     * @param recoveryDelay
     * @param eventLog
     */

    public Agent(int id, int sickDelay, int recoveryDelay,
                 LinkedBlockingQueue<Message> eventLog ) {
        this.id = id;
        this.status = VULNERABLE;
        this.immune = false;
        this.recovery = false;
        this.finished = false;
        this.sickDelay = sickDelay;
        this.recoveryDelay = recoveryDelay;
        this.eventLog = eventLog;
        messageQueue = new LinkedBlockingQueue<>();
        this.node = generateCircle();
    }


    public LinkedBlockingQueue<Message> getEventLog(){ return this.eventLog; }
    public void clearEventLog(){ this.eventLog.clear(); }
    public Status getStatus() { return this.status; }
    public int getX() { return this.coord.getRow(); }
    public int getY() { return this.coord.getCol(); }
    public int getId() { return this.id; }
    public Circle getCircle() {setCirclePos(); return this.node;}
    public boolean queueCleared() { if(messageQueue.size() == 0)
        return true; else {return false;}}
    public void setImmunity() { this.immune = true; }
    public void setRecovery() { this.recovery = true; }


    private void sendMessage() {
        for(Agent neighbor : neighbors) {
            neighbor.queueMessage(new Message(id, getStatus()));
        }
    }


    private void setSick() {
        try {
            Thread.sleep(sickDelay*1000);
            if(immune) { setStatus(IMMUNE); }
            else if(!sick) { setStatus(SICK);
                this.sick = true;
            }
            else { setFinalStatus(); }
        } catch(InterruptedException e) { 
            //System.out.println("SICK ID: "+id+" "+e); //Used for troubleshooting
        }
    }


    private void setFinalStatus() {
        try {
            Thread.sleep(recoveryDelay* 1000L);
            if(recovery) { setStatus(RECOVERED); }
            else if(immune) { setStatus(IMMUNE); }
            else { setStatus(DEAD); }
            this.finished = true;
        } catch(InterruptedException e) { 
            //System.out.println("FINAL ID: "+id+" "+e); //Used for troubleshooting
        }
    }


    private Circle generateCircle() {
        Circle circle = new Circle(6);
        circle.setFill(Color.AQUA);
        circle.setStrokeWidth(2);
        circle.setStroke(Color.BLACK);
        return circle;
    }


    private void setCirclePos() {
        this.node.setCenterY(getY());
        this.node.setCenterX(getX());
    }

    /**
     *
     * Run the message thread
     *
     */

    @Override
    public void run() {
        while(true) {
            try {
                processMessage(messageQueue.take());
            } catch(InterruptedException e) {
            }
        }
    }

    /**
     *
     * @param agent
     * Add an agent to the current agents neighbors
     */

    public void addNeighbor(Agent agent) { neighbors.add(agent); }

    /**
     *
     * @param coord
     * set the coordinates
     */

    public void setCoord(Coordinate coord) { this.coord = coord; }

    /**
     *
     * @param message
     */

    public synchronized void queueMessage(Message message) {
        try { messageQueue.put(message); }
        catch(InterruptedException e) {
            System.out.println("Queue Message ID: " + this.id + " " + e);
        }
    }


    private void processMessage(Message message) {
        Status mStatus = message.status();
        switch(mStatus) {
            case SICK -> { setSick(); }
            case DEAD -> { setFinalStatus(); }
            case IMMUNE -> setSick();
            case RECOVERED -> { setFinalStatus(); }
            default -> System.out.println(messageQueue.size());
        }
        if(finished) { messageQueue.clear();}
    }


    private void setStatus(Status status) throws InterruptedException {
        this.status = status;
        eventLog.put(new Message(this.id, getStatus()));

        if(!finished) {sendMessage();}
        switch(status) {
            case VULNERABLE -> node.setFill(Color.AQUA);
            case SICK -> node.setFill(Color.YELLOW);
            case DEAD -> node.setFill(Color.RED);
            case RECOVERED -> node.setFill(Color.GREEN);
            case IMMUNE -> node.setFill(Color.IVORY);
        }
    }
}
