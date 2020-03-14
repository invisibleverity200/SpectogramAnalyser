package com.UI;

import org.jfree.chart.ChartPanel;
import org.jfree.data.xy.XYSeries;

public interface Charts {
    public ChartPanel init(XYSeries[] dataSet);

    public void update(int[][] newData, int frequencySteps, int startFrequency);
}
