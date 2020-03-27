package com.UI;

import com.Data.Configs;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

public class MyChartPanel extends ChartPanel {
    Configs config;
    JFreeChart chart;

    public MyChartPanel(JFreeChart chart, Configs config) {
        super(chart);
        this.config = config;
        this.chart = chart;
    }

    @Override
    public void restoreAutoRangeBounds() {
        super.restoreAutoRangeBounds();

        chart.getXYPlot().getRangeAxis().setRange(0, config.getHighestValueOnY());
        System.out.println("Chart Axix Range: " + chart.getXYPlot().getRangeAxis().getRange());
    }
}
