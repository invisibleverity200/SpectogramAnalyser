package com.UI;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.util.ArrayList;

public class BarChart implements Charts {
    JFreeChart chart;
    ArrayList<XYSeries> dataSet = new ArrayList<>();

    public ChartPanel init(XYSeries[] dataSet) {
        for (int i = 0; i < dataSet.length; i++) {
            if (i < this.dataSet.size()) {
                this.dataSet.set(i, dataSet[i]);
            } else {
                this.dataSet.add(dataSet[i]);
            }
        }

        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        for (int i = 0; i < dataSet.length; i++) {
            if (dataSet[i] != null) {
                xySeriesCollection.addSeries(dataSet[i]);
            }
        }
        if (xySeriesCollection.getSeriesCount() != 0) {
            chart = ChartFactory.createScatterPlot("SpectrumAnalyser", "Frequency in Hz", "Level in V", xySeriesCollection);
            ChartPanel panel = new ChartPanel(chart);
            return panel;
        }

        return null;
    }

    public void update(int[][] newData, int frequencySteps, int startFrequency) {
        for (int i = 0; i < newData.length; i++) {
            if (newData[i] != null && i < dataSet.size()) {
                dataSet.get(i).clear();
                for (int y = 0; y < newData[i].length; y++) {
                    dataSet.get(i).add(startFrequency + y * frequencySteps, newData[i][y]);
                }
            }
        }
    }

}
