/*

Creators:  Alex Hartel and Justin Nelson
Created For: CS351 group project at the University of New Mexico

Class to show user the events of the simulation

*/


package src.log;


import javafx.application.Platform;
import javafx.scene.control.TextArea;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class EventLog {

    private BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();
    private TextArea textArea;

    /**

    Create the Event Log that will be the area that holds the
     events that happened to the agents

     */

    public EventLog() {
        textArea = new TextArea();
        textArea.setEditable(false);
        startMessageListener();
    }

    /**

     Create a thread to wait for messages in the message queue to take them
     and append it to the text area

     */

    private void startMessageListener() {
        new Thread(() -> {
            while (true) {
                try {
                    String message = messageQueue.take();
                    Platform.runLater(() -> textArea.appendText(message));
                } catch (InterruptedException e) {
                }
            }
        }).start();
    }

    /**

     Get the textArea that will hold the Log information presented graphically

     */

    public TextArea getLogArea() {
        return textArea;
    }

    /**

     Clear the message que and text area and create a new event log.

     */

    public void clearLog() {
        messageQueue = new LinkedBlockingQueue<>();
        textArea.clear();
        new EventLog();
    }
}
