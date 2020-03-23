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
        XYSeries xySeries = new XYSeries(config.getChannelNames().get(channelIndex - 1));

        for (int spectrumIndex = 0; spectrumIndex < channelSpectrum.length; spectrumIndex++) {
            xySeries.add(config.getStartFrequency() + config.getFrequencyStepWidth() * spectrumIndex, channelSpectrum[spectrumIndex] * config.getVoltageStepWidth());
        }
        return xySeries;
    }
}
