package com.Data;

import org.jfree.data.xy.XYSeries;

public class AudChannel {
    private int channelIndex;
    public int[] channelSpectrum;

    public AudChannel(int channelIndex, int[] channelSpectrum) {
        this.channelIndex = channelIndex;
        this.channelSpectrum = channelSpectrum;
    }

    public XYSeries getXYSeries(Config config) {
        XYSeries xySeries = new XYSeries(config.channelNames.get(channelIndex - 1));
        for (int i = 0; i < channelSpectrum.length; i++) {
            xySeries.add(config.startFrequency + config.frequencyStepWidth * i, channelSpectrum[i] * config.voltageStepWidth);
        }
        return xySeries;
    }
}
