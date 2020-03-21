package com.UI;

import com.Data.Config;
import org.jfree.chart.ChartPanel;
import org.jfree.data.xy.XYSeries;

public interface Charts {
    ChartPanel init(XYSeries[] dataSet);

    void update(int[][] newData, double frequencySteps, int startFrequency, Config config);
}
