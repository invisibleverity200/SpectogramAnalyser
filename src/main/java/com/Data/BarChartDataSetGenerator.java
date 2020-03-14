package com.Data;

import org.jfree.data.xy.XYSeries;

public class BarChartDataSetGenerator {
    public XYSeries createDataSet(int[] spectrum, String channelName, int frequencySteps, int startFrequency) {
        XYSeries xySeries = new XYSeries(channelName);
        for (int i = 0; i < spectrum.length; i++) {
            xySeries.add(i + startFrequency + frequencySteps, spectrum[i]);
        }

        return xySeries;
    }

}
