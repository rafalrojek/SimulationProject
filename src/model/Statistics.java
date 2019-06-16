package model;

import ptolemy.plot.Plot;
import ptolemy.plot.PlotApplication;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Statistics {
    private LinkedList<TimeserieValue> washQueueResults = new LinkedList<>();
    private LinkedList<TimeserieValue> distributorQueueResults = new LinkedList<>();
    private LinkedList<TimeserieValue> rejectedCarsNumber = new LinkedList<>();

    public void addDistributorsQueueResult(Integer result, Double time){
        distributorQueueResults.add(new TimeserieValue(result,time));
    }

    public void addWashQueueResult(Integer result, Double time){
        washQueueResults.add(new TimeserieValue(result,time));
    }

    public void incrementRejectedNumber(Double time){
        rejectedCarsNumber.add(new TimeserieValue(rejectedCarsNumber.size()+1, time));
    }

    private Double printWashQueueResult(){
        int sum = 0;
        for (TimeserieValue result: washQueueResults){
            sum += result.getValue();
        }
        return ((double)sum)/washQueueResults.size();
    }

    private Double printDistributorQueueResult(){
        int sum = 0;
        for (TimeserieValue result: distributorQueueResults){
            sum += result.getValue();
        }
        return ((double)sum)/distributorQueueResults.size();
    }

    private void printPlot(String title, List<TimeserieValue> results){
        Plot plot = new Plot();
        for (TimeserieValue result : results) {
            plot.addPoint(6, result.getTime(), result.getValue(), true);
        }
        plot.setMarksStyle("dots",6);
        PlotApplication app = new PlotApplication(plot);
        app.setTitle(title);
        app.setSize(1600, 870);
        app.setLocation(-10, 0);
    }

    public void print() {
        Double washAvg = printWashQueueResult();
        Double distrAvg = printDistributorQueueResult();

        System.out.println("::::::::::::::::::::::::::::::::::::::::::::::::::::::");
        System.out.println("::::::::::::::::::::::::::::::::::::::::::::::::::::::");
        System.out.println("::::::::::::::::::::::::::::::::::::::::::::::::::::::");
        System.out.println("::::::::::::::::::::::::::::::::::::::::::::::::::::::");
        System.out.println("::::::::::::::::::::::::::::::::::::::::::::::::::::::");
        System.out.println("");
        System.out.println("Avg queue length to distributor = " + distrAvg);
        System.out.println("Avg queue length to washing = " + washAvg);
        if(rejectedCarsNumber.size() > 0)
            System.out.println("Number of rejected cars = " + rejectedCarsNumber.get(rejectedCarsNumber.size()-1).getValue());
        System.out.println("");
        System.out.println("::::::::::::::::::::::::::::::::::::::::::::::::::::::");
        System.out.println("::::::::::::::::::::::::::::::::::::::::::::::::::::::");
        System.out.println("::::::::::::::::::::::::::::::::::::::::::::::::::::::");
        System.out.println("::::::::::::::::::::::::::::::::::::::::::::::::::::::");
        System.out.println("::::::::::::::::::::::::::::::::::::::::::::::::::::::");

        printPlot("Wielkosc kolejki do dystrybutorow", distributorQueueResults);
        printPlot("Wielkosc kolejki do myjni", washQueueResults);
        if(rejectedCarsNumber.size() > 0)
            printPlot("Liczba samochodow, ktore opuscily bez obslugi", rejectedCarsNumber);
    }
}