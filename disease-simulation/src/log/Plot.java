/*

Creators:  Alex Hartel and Justin Nelson
Created For: CS351 group project at the University of New Mexico

Class to show a graphical representation of the data aquired from simulation

 */


package src.log;


import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class Plot{

    private static List<Integer> deadAgents = new ArrayList<>();
    private static List<Integer> immuneAgents = new ArrayList<>();
    private static List<Integer> sickAgents = new ArrayList<>();
    private static List<Integer> healthyAgents = new ArrayList<>();
    private static List<Integer> recoveredAgents = new ArrayList<>();
    private static List<Integer> timeInterval = new ArrayList<>();
    private StackedBarChart<String, Number> chart;
    private static int index = 0;
    private BlockingQueue<int[]> presentData = new LinkedBlockingQueue<>();

    /**

    Add information to the Stack Bar Chart to show

     */

    public void addToStackBarChart(int[] a){
        timeInterval.add(index++);
        deadAgents.add(a[4]);
        immuneAgents.add(a[1]);
        sickAgents.add(a[3]);
        healthyAgents.add(a[0]);
        recoveredAgents.add(a[2]);
    }


    private void runDataProcessing() {
        while (true) {
            Platform.runLater(this::updateChartData);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**

     Create the chart and have it process data using a thread

     */

    public Plot() {
        chart = createChart();
        new Thread(this::runDataProcessing).start();
    }

    /**

    return the chart

     */

    public StackedBarChart<String, Number> getChart() {
        return chart;
    }


    private StackedBarChart<String, Number> createChart() {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Time Intervals");
        yAxis.setLabel("Agents");
        xAxis.setAutoRanging(true);
        StackedBarChart<String, Number> newChart = new
                StackedBarChart<>(xAxis, yAxis);
        newChart.setTitle("Data over Time");
        newChart.setAnimated(false);
        return newChart;
    }

    /**

    update chart data for new plotting

     */

    private void updateChartData() {
        styleChartLegend(chart);
        XYChart.Series<String, Number> healthySeries =
                createSeries("Healthy", timeInterval, healthyAgents);
        XYChart.Series<String, Number> immuneSeries =
                createSeries("Immune", timeInterval, immuneAgents);
        XYChart.Series<String, Number> recoveredSeries =
                createSeries("Recovered", timeInterval, recoveredAgents);
        XYChart.Series<String, Number> sickSeries =
                createSeries("Sick", timeInterval, sickAgents);
        XYChart.Series<String, Number> deadSeries =
                createSeries("Dead", timeInterval, deadAgents);
        chart.getData().clear();
        chart.getData().addAll(deadSeries, sickSeries,
                recoveredSeries, immuneSeries, healthySeries);
        applySeriesColor(recoveredSeries, "green");
        applySeriesColor(healthySeries, "aqua");
        applySeriesColor(sickSeries, "yellow");
        applySeriesColor(immuneSeries, "ivory");
        applySeriesColor(deadSeries, "red");
    }

    /**

    keep the legend color the same as the graph

     */

    private void styleChartLegend(StackedBarChart<String, Number> chart) {
        Platform.runLater(() -> {
            for (Node n : chart.lookupAll(".chart-legend-item")) {
                if (n instanceof Label && ((Label) n).getGraphic() != null) {
                    Node symbol = ((Label) n).getGraphic();
                    switch (((Label) n).getText()) {
                        case "Dead":
                            symbol.setStyle("-fx-background-color: red;");
                            break;
                        case "Recovered":
                            symbol.setStyle("-fx-background-color: green;");
                            break;
                        case "Immune":
                            symbol.setStyle("-fx-background-color: ivory;");
                            break;
                        case "Sick":
                            symbol.setStyle("-fx-background-color: yellow;");
                            break;
                        case "Healthy":
                            symbol.setStyle("-fx-background-color: aqua;");
                            break;
                    }
                }
            }
        });
    }

    /**

    apply bar colors and a 3d look to the bars

     */

    private void applySeriesColor(XYChart.Series<String, Number> series,
                                  String color) {
        for (XYChart.Data<String, Number> data : series.getData()) {
            Node node = data.getNode();
            node.setStyle("-fx-bar-fill: " + color + ";"
            );
        }
    }

    /**

    create the series to set color, value, name, etc.

     */

    private XYChart.Series<String, Number> createSeries(String name,
                                                        List<Integer>
            timeIntervals, List<Integer> values) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(name);
        for (int i = 0; i < timeIntervals.size(); i++) {
            series.getData().add(new XYChart.Data<>(
                    timeIntervals.get(i).toString(), values.get(i)));
        } return series;
    }

    /**

    remove information for the plot to reset

     */


    public void clearData() {
        recoveredAgents.clear();
        deadAgents.clear();
        immuneAgents.clear();
        sickAgents.clear();
        healthyAgents.clear();
        timeInterval.clear();
        index = 0;
        this.chart.getData().clear();
        chart.getData().clear();
        createChart();
    }
}
