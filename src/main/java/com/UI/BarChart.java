package com.UI;

import com.Data.AudChannel;
import com.Data.Config;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.util.ArrayList;

public class BarChart implements Charts {
    private ArrayList<XYSeries> dataSet = new ArrayList<>();
    JFreeChart chart;
    XYSeriesCollection xySeriesCollection;

    public ChartPanel init(XYSeries[] dataSet) {
        for (int i = 0; i < dataSet.length; i++) {
            if (!(dataSet[i] == null)) { //FIXME possible bug
                if (i < this.dataSet.size()) {
                    this.dataSet.set(i, dataSet[i]);
                } else {
                    this.dataSet.add(dataSet[i]);
                }
            }
        }

        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        for (XYSeries xySeries : dataSet) {
            try {
                xySeriesCollection.addSeries(xySeries);
            } catch (IllegalArgumentException e) {
                System.out.println("\u001B[31m" + "this error is caused because the server sent´s to many or less channels!\nERROR(2003): " + e.getMessage());
            }
        }
        if (xySeriesCollection.getSeriesCount() != 0) {
            JFreeChart chart = ChartFactory.createScatterPlot("SpectrumAnalyser", "Frequency in Hz", "Level in V", xySeriesCollection);
            return new ChartPanel(chart);
        }
        return null;
    }

    public void update(int[][] newData, double frequencySteps, int startFrequency, Config config) {
        for (int i = 0; i < newData.length; i++) {
            if (newData[i] != null && i < dataSet.size()) {
                dataSet.get(i).clear();
                for (int y = 0; y < newData[i].length; y++) {
                    dataSet.get(i).add(startFrequency + y * frequencySteps, newData[i][y] * config.voltageStepWidth);
                }
            }
        }
    }

    public void update(AudChannel[] channels, Config config) {
        ArrayList<XYSeries> xySeriesArray = new ArrayList<>(config.selectedItems.size());
        System.out.println(channels.length);
        for (int x = 0; x < xySeriesArray.size(); x++) {
            XYSeries xySeriesTemp = channels[config.selectedItems.get(x)].getXYSeries(config);
            if (xySeriesTemp != null) {
                xySeriesArray.set(x, channels[config.selectedItems.get(x)].getXYSeries(config));
            }
        }

        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        for (XYSeries xySeries : xySeriesArray) {
            try {
                if (xySeries != null) {
                    xySeriesCollection.addSeries(xySeries);
                }
            } catch (IllegalArgumentException e) {
                System.out.println("\u001B[31m" + "this error is caused because the server sent´s to many or less channels!\nERROR(2003): " + e.getMessage());
            }
        }
        if (xySeriesCollection != null) {
            this.xySeriesCollection = xySeriesCollection;

        }
    }
}





