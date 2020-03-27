package com.UI;

import com.Data.Configs;
import org.jfree.chart.ChartPanel;
import org.jfree.data.xy.XYSeries;


public interface Charts {
    ChartPanel init(XYSeries[] dataSet, Configs config);

    void update(int[][] newData, double frequencySteps, int startFrequency, Configs config);
}
