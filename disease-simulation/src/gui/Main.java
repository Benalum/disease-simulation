/*

Creators:  Alex Hartel and Justin Nelson
Created For: CS351 group project at the University of New Mexico

Responsible for initializing the simulation, reading the configuration file, and starting the simulation loop.
Manages the GUI and user interactions.

*/


package src.gui;


import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import src.config.Configuration;
import src.log.EventLog;
import src.log.Plot;
import java.time.Duration;
import java.util.concurrent.LinkedBlockingQueue;


public class Main extends Application {

    private BorderPane mainPane;
    private HBox left,bottom;
    private VBox top;
    private Plot plot;
    private Thread listenerPlot,eventArea;
    private GraphicsBuilder builder;
    private EventLog eventLog;
    private Simulation sim;
    private LinkedBlockingQueue<int[]> alreadyPresentData =
            new LinkedBlockingQueue<>();
    private LinkedBlockingQueue<Message> eventLogger =
            new LinkedBlockingQueue<>();
    private FlowPane flowPane;
    private final int HEIGHT = 900;
    private final int WIDTH = 1100;
    private boolean killApp;
    private long updatedTime = 0;
    private boolean isRunning;

    /**
     *
     * @param args
     *
     */

    public static void main(String[] args) { launch(args);}

    /**
     *
     * @param primaryStage the primary stage for this application, onto which
     * the application scene can be set.
     * Applications may create other stages, if needed, but they will not be
     * primary stages.
     */

    @Override
    public void start(Stage primaryStage) {
        this.mainPane = new BorderPane();
        flowPane = new FlowPane();
        bottom = new HBox();
        top = new VBox();
        left = new HBox();
        //leftTextField = new TextField();
        isRunning = true;
        simInitialize();
        primaryStage.setTitle("Disease Simulation");
        Scene scene = new Scene(mainPane,WIDTH,HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    private void eventInitialize(){
        eventLog = new EventLog();
        TextArea logArea = eventLog.getLogArea();
        startMessageListenerTextArea(logArea);
        flowPane.getChildren().add(logArea);
    }


    private void plotInitialize(){
        plot = new Plot();
        StackedBarChart<String, Number> chart = plot.getChart();
        chart.setCategoryGap(0);
        startMessageListenerPlotArea(plot);
        flowPane.getChildren().add(chart);
    }


    private String getTimeOnDeck(){
        return " Time on Deck: " + updatedTime + ".";
    }


    private void simInitialize() {
        if(!top.getChildren().isEmpty()) {
            bottom.getChildren().clear();
            top.getChildren().clear();
            flowPane.getChildren().clear();
            left.getChildren().clear();
            mainPane.getChildren().clear();
        }
        mainPane.setTop(top);
        mainPane.setBottom(bottom);
        mainPane.setLeft(flowPane);
        mainPane.setBackground(Background.fill(Color.BEIGE));
        builder = new GraphicsBuilder();
        builder.buildTop(top, mainPane);
        this.killApp = false;
        Button exit = builder.buildButton("EXIT","small");
        exit.setOnMousePressed(event -> {Platform.exit();
            System.exit(2);});
        Button runSim = builder.buildButton("RUN","medium");
        runSim.setOnMousePressed(event ->{
            isRunning = true;
            updatedTime = 0;
            if(builder.getConfig() != null) {
                try {
                    simulationMain(builder);
                }
                catch(InterruptedException e)
                {System.out.println("BEGINING OF SIM : "+e);}
            }
            else { System.out.println("Null Sim Settings"); }
        });
        builder.getCtrlButtons().getChildren().addAll(runSim);
        bottom.getChildren().addAll(exit);
        bottom.setAlignment(Pos.CENTER);
    }


    private void clearEventBoxAndPlotBox(){
        while(eventArea.isAlive()){
            eventArea.interrupt();
        }
        while(listenerPlot.isAlive()){
            listenerPlot.interrupt();
        }
        alreadyPresentData.clear();
        plot.clearData();
        eventLog.clearLog();
        eventLogger.clear();
        flowPane.getChildren().removeAll();
        updatedTime = 0;
    }


    private void pauseEventAndPlotBox(){
        while(eventArea.isAlive()){
            eventArea.interrupt();
        }
        while(listenerPlot.isAlive()){
            listenerPlot.interrupt();
        }
    }


    private void plotAndEventInitialization() {
        eventInitialize();
        plotInitialize();
    }


    private void startMessageListenerPlotArea(Plot plotArea){
        this.listenerPlot = new Thread(() -> {
            while (!Thread.interrupted()) {
                try {
                    int[] intArray = alreadyPresentData.take();
                    Platform.runLater(() ->
                            plotArea.addToStackBarChart(intArray));
                } catch (InterruptedException e) {
                }
            }
        });
    }


    private void startMessageListenerTextArea(TextArea logArea) {
        this.eventArea = new Thread(() -> {
            while (!Thread.interrupted()) {
                try {
                    String message = eventLogger.take().toString();
                    Platform.runLater(() ->
                            logArea.appendText(messageToTextBox(message)
                                    + getTimeOnDeck() + "\n"));
                } catch (InterruptedException e) {
                }
            }
        });
    }


    private String messageToTextBox(String message) {
        String[] msg = message.replaceAll("Message","").
                replaceAll("id=","")
                .replaceAll("status=","").
                replace("[","")
                .replace("]","").
                replace(",","").split(" ");
        return "Agent " + msg[0] + " is now " + msg[1].toLowerCase() + "!";
    }


    private void simulationMain(GraphicsBuilder builder)
            throws InterruptedException {
        Configuration config = builder.getConfig();
        Button reset = builder.buildButton("RESET","medium");
        reset.setOnMousePressed(event -> {
            sim.terminateSimulation();
            clearEventBoxAndPlotBox();
            isRunning = false;
            this.killApp = true;
            simInitialize();
        });
        Button stopSim = builder.buildButton("STOP","medium");
        stopSim.setOnMouseClicked(events -> {
            Label stoping = builder.labelMaker(
                    0,0,"Simulation Stopped","medium");
            sim.terminateSimulation();
            pauseEventAndPlotBox();
            sim.terminateSimulation();
            stopPopUp(reset, stoping);
        });
        builder.getCtrlButtons().getChildren().addAll(stopSim,reset);
        sim = new Simulation(config);
        Pane simPane = new Pane();
        simPane.setBackground(Background.fill(Color.WHITE));
        simPane.setPrefSize(config.getDimensions()[0],
                config.getDimensions()[1]);
        if(!config.getGridCheck()) {
            simPane.setMaxSize(config.getDimensions()[0],
                    config.getDimensions()[1]);
        }
        else {
            int sizeX = (config.getExposureDistance())*config.getGrid()[0];
            int sizeY = (config.getExposureDistance())*config.getGrid()[1];
            simPane.setMaxSize(sizeX,sizeY);
        }
        simPane.setBorder(Border.stroke(Color.BLACK));
        AnimationTimer a = new AnimationTimer() {
            private long nextTime = 0;
            @Override
            public void handle(long now) {
                if(isRunning) {
                    if (now > nextTime) {
                        simPane.getChildren().clear();
                        sim.updatePane(simPane);
                        try {
                            eventLogger = sim.updateEventLog(eventLogger);
                            alreadyPresentData =
                                    sim.updateGraphData(alreadyPresentData);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        updatedTime++;
                        nextTime = now + Duration.ofMillis(10).toNanos();
                    }
                }
            }
        };
        a.start();
        sim.runSimulation();
        plotAndEventInitialization();
        listenerPlot.start();
        eventArea.start();
        mainPane.setCenter(simPane);
    }


    private void stopPopUp(Button reset, Label stopMessage) {
        Stage newStage = new Stage();
        VBox contents = new VBox();
        contents.setSpacing(10);
        contents.setAlignment(Pos.CENTER);
        HBox buttons = new HBox();
        buttons.setAlignment(Pos.CENTER);
        buttons.setSpacing(5);
        Button exit = new Button("EXIT");
        exit.setPrefSize(150,10);
        exit.setOnMouseClicked(event -> {
            Platform.exit();
            System.exit(3);
        });
        reset.setPrefSize(150,10);
        reset.setOnMouseReleased(event -> newStage.close());
        exit.setPrefSize(100,10);
        exit.setFont(Font.font("Tahoma", FontWeight.MEDIUM, 23));
        buttons.getChildren().addAll(reset,exit);
        contents.getChildren().addAll(stopMessage,buttons);
        StackPane stack = new StackPane(contents);
        Scene popUp = new Scene(stack,300,100);
        newStage.setTitle("Simulation Stopped");
        newStage.setScene(popUp);
        newStage.show();
    }
}

