package com.UI;

import com.Data.Configs;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.util.ArrayList;

public class BarChart implements Charts {
    private ArrayList<XYSeries> dataSet = new ArrayList<>();
    private JFreeChart chart;

    public ChartPanel init(XYSeries[] dataSet) {
        for (int i = 0; i < dataSet.length; i++) {
            if (!(dataSet[i] == null)) {
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

    public void update(int[][] newData, double frequencySteps, int startFrequency, Configs config) {
        for (int index = 0; index < newData.length; index++) {
            if ((newData[index] != null) && (index < dataSet.size())) {
                chart.setNotify(false);
                dataSet.get(index).clear();
                for (int y = 0; y < newData[index].length; y++) {
                    dataSet.get(index).addOrUpdate(startFrequency + y * frequencySteps, newData[index][y] * config.getVoltageStepWidth());
                }
            }
        }
        chart.setNotify(true);
    }
}





