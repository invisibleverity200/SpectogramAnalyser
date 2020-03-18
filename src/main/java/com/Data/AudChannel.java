package com.Data;

import org.jfree.data.xy.XYSeries;

public class AudChannel {
    //int ChannelVame int[512] value
    public int channelIndex;
    public int[] channelSpectrum;

    public AudChannel(int channelIndex, int[] channelSpectrum) {
        this.channelIndex = channelIndex;
        this.channelSpectrum = channelSpectrum;
    }

    public XYSeries getXYSeries(Config config) {
        //TODO: write FUnction
        System.out.println(config.channelNames.get(channelIndex - 1));
        XYSeries xySeries = new XYSeries(config.channelNames.get(channelIndex - 1));
        for (int i = 0; i < channelSpectrum.length; i++) {
            xySeries.add(config.startFrequency + (i * ((config.endFrequency - config.startFrequency) / config.blockSize)), channelSpectrum[i] * config.voltageStepWidth);
        }
        return xySeries;
    }
}
