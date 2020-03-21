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
            chart = ChartFactory.createScatterPlot("SpectrumAnalyser", "Frequency in Hz", "Level in V", xySeriesCollection);
            return new ChartPanel(chart);
        }
        return null;
    }

    public void update(int[][] newData, double frequencySteps, int startFrequency, Config config) {
        for (int i = 0; i < newData.length; i++) {
            if (newData[i] != null && i < dataSet.size()) {
                chart.setNotify(false);
                dataSet.get(i).clear();
                for (int y = 0; y < newData[i].length; y++) {
                    dataSet.get(i).addOrUpdate(startFrequency + y * frequencySteps, newData[i][y] * config.voltageStepWidth);
                }
            }
        }
        chart.setNotify(true);
    }
}





