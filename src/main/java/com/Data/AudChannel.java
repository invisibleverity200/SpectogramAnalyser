package com.Data;

import org.jfree.data.xy.XYSeries;

public class AudChannel {
    public int[] channelSpectrum;

    private int channelIndex;

    public AudChannel(int channelIndex, int[] channelSpectrum) {
        this.channelIndex = channelIndex;
        this.channelSpectrum = channelSpectrum;
    }

    public XYSeries getXYSeries(Config config) {
        XYSeries xySeries = new XYSeries(config.channelNames.get(channelIndex - 1));
        for (int spectrumIndex = 0; spectrumIndex < channelSpectrum.length; spectrumIndex++) {
            xySeries.add(config.startFrequency + config.frequencyStepWidth * spectrumIndex, channelSpectrum[spectrumIndex] * config.voltageStepWidth);
        }
        return xySeries;
    }
}
