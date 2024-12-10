/*

Creators:  Alex Hartel and Justin Nelson
Created For: CS351 group project at the University of New Mexico

Creates the basic GUI layout with the buttions and such

 */


package src.gui;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import src.config.Configuration;
import java.io.File;


public class GraphicsBuilder {

    private File filePath;
    private Font small, medium, large;
    private Configuration config;
    private boolean randomCheck, gridCheck;
    private int[] dimensions,locationGrid;
    private int exposureDistance,incubationPeriod,
            sicknessTime,agentAmount,initialSick;
    private double recoveryProbability, nImmune;
    private HBox ctrlButtons;


    public GraphicsBuilder() {
       small = Font.font("Tahoma", FontWeight.MEDIUM, 14);
       medium = Font.font("Tahoma", FontWeight.MEDIUM, 23);
       large = Font.font("Tahoma", FontWeight.MEDIUM, 35);
       this.dimensions = new int[]{200, 200};
       this.locationGrid = new int[]{200,200};
       this.exposureDistance = 20;
       this.incubationPeriod = 5;
       this.sicknessTime = 10;
       this.recoveryProbability = 0.95;
       this.agentAmount = 100;
       this.initialSick = 1;
       this.randomCheck = false;
       this.gridCheck = false;
    }


    public void buildTop(VBox top, BorderPane mainPane) {

        top.setSpacing(10);

        VBox topStack = new VBox();
        topStack.setAlignment(Pos.CENTER);
        topStack.setBorder(Border.stroke(Color.BLACK));
        topStack.setSpacing(5);

        GridPane midGrid = new GridPane();
        midGrid.setAlignment(Pos.CENTER);
        midGrid.setBorder(Border.stroke(Color.BLACK));

        HBox buttons = new HBox();
        buttons.setAlignment(Pos.CENTER);
        buttons.setBorder(Border.stroke(Color.BLACK));

        Label file = labelMaker(
                0,0,"Simulation Settings","big");
        TextField fileField = new TextField();
        fileField.setFont(small);
        fileField.setMaxWidth(500);
        Button button = buildButton("Select File...","small");
        FileChooser fileChooser = new FileChooser();

        button.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        File file = fileChooser.showOpenDialog(new Stage());
                        if(file != null) {
                            fileField.setText(file.toString());
                            setFilePath(file);
                        }
                    }
                }
        );

        topStack.getChildren().addAll(file,fileField,button);

        Label dimensions = labelMaker(
                0,0,"Dimensions (x,y)","small");
        Slider dimX = buildSlider(
                100,500,200,10,100);
        Slider dimY = buildSlider(
                100,500,200,10,100);

        Label gridMark = labelMaker(0,0,"Grid","small");
        Label ranGrid = labelMaker(0,0,"Random Grid", "small");

        Label gridDim = labelMaker(
                0,0, "Grid Dimensions (x,y)", "small");
        Slider gridX = buildSlider(5,26,20,5,5);
        Slider gridY = buildSlider(5,26,20,5,5);

        CheckBox gridCheckBox = new CheckBox();
        CheckBox randGridCheck = new CheckBox();

        randGridCheck.setOnMouseClicked(event ->
                gridCheckBox.setSelected(false));
        gridCheckBox.setOnMouseClicked(event ->
                randGridCheck.setSelected(false));

        HBox gridGroup = new HBox(gridMark,gridCheckBox,ranGrid,randGridCheck);
        gridGroup.setSpacing(10);

        Label exposure = labelMaker(
                0,0,"Exposure Distance","small");
        Slider expDistance = buildSlider(
                1,50,20,1,10);

        Label incubation = labelMaker(
                0,0,"Incubation Time (s)", "small");
        Slider incubationTime = buildSlider(
                0,10,5,1,1);

        Label sickness = labelMaker(
                0,0,"Sickness Time (s)","small");
        Slider sickTime = buildSlider(
                0,10,10,1,1);

        Label totalAgents = labelMaker(
                0,0,"Total Agents","small");
        Slider nAgents = buildSlider(
                1,1000,100,5,100);

        Label initSick = labelMaker(
                0,0,"Initial Sick","small");
        Slider nSick = buildSlider(
                1,10,1,1,1);

        Label recoveryProb = labelMaker(
                0,0,"Recovery Probability","small");
        Slider rProb = buildSlider(
                0,1,0.98,0.05,0.5);

        Label percentImmune = labelMaker(
                0,0,"Immune %","small");
        Slider iPercent = buildSlider(
                0,1,0.50,0.05,0.5);

        midGrid.addRow(1,dimensions,dimX,dimY,incubation,
                incubationTime,initSick,nSick);
        midGrid.addRow(2,gridGroup,exposure,expDistance,sickness,
                sickTime,recoveryProb,rProb);
        midGrid.addRow(3,gridDim,gridX,gridY,totalAgents,nAgents,
                percentImmune,iPercent);

        Button setValues = buildButton("SET VALUES","medium");

        setValues.setOnMouseClicked(event -> {
            if(!fileField.getText().isEmpty()) {
                this.config = new Configuration(this.filePath);
                System.out.println(config);
            }
            else {
               this.dimensions[0] = (int)dimX.getValue();
               this.dimensions[1] = (int)dimY.getValue();

               this.locationGrid[0] = (int)gridX.getValue();
               this.locationGrid[1] = (int)gridY.getValue();

               this.exposureDistance = (int)expDistance.getValue();
               this.incubationPeriod = (int)incubationTime.getValue();
               this.sicknessTime = (int)sickTime.getValue();
               this.agentAmount = (int)nAgents.getValue();
               this.initialSick = (int)nSick.getValue();
               this.recoveryProbability = rProb.getValue();
               this.nImmune = iPercent.getValue();

               if(gridCheckBox.isSelected()) {this.gridCheck =
                       true; this.randomCheck = false;}
               else if(randGridCheck.isSelected()) {this.gridCheck =
                       true; this.randomCheck = true;}
               else {this.gridCheck = false; this.randomCheck = true;}

                this.config = new Configuration(this.dimensions,
                        this.locationGrid,this.exposureDistance,
                        this.incubationPeriod,this.sicknessTime,
                        this.agentAmount,this.initialSick,
                        this.recoveryProbability, this.nImmune,
                        this.randomCheck, this.gridCheck);
            }
        });

        ctrlButtons = new HBox(10,setValues);
        ctrlButtons.setBorder(Border.stroke(Color.BLACK));
        ctrlButtons.setMaxHeight(10);
        ctrlButtons.setAlignment(Pos.CENTER);

        midGrid.setHgap(10);
        midGrid.setVgap(5);

        Label or = labelMaker(0,0,"- OR -","medium");

        top.getChildren().addAll(topStack,or,midGrid,ctrlButtons);
        top.setAlignment(Pos.CENTER);
        top.setSpacing(10);
    }


    private Slider buildSlider(double min, double max, double value,
                               double increments, double disp) {
        Slider slider = new Slider(min,max,value);
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setMajorTickUnit(disp);
        slider.setBlockIncrement(increments);
        return slider;
    }


    public Label labelMaker(int x, int y, String label, String size) {
        Label text = new Label(label);

        if(size.equals("big")) {text.setFont(large);}
        else if(size.equals("medium")) {text.setFont(medium);}
        else {text.setFont(small);}

        return text;
    }


    public Button buildButton(String label, String size) {
        Button button = new Button(label);
        if(size.equals("small")) {
            button.setPrefSize(150,10);
            button.setFont(small);
        }
        else if(size.equals("medium")) {
            button.setPrefSize(200,10);
            button.setFont(medium);
        }
        else {
            button.setPrefSize(30,40);
            button.setFont(large);
        }

        return button;
    }


    public Configuration getConfig() {return this.config;}


    private void setFilePath(File file) {this.filePath = file;}


    public HBox getCtrlButtons() {return ctrlButtons;}
}
