package com.Data;

import org.jfree.data.xy.XYSeries;

public class AudChannel {
    //int ChannelVame int[512] value
    public int channelIndex;
    public int[] channelSpectrum;

    AudChannel(int channelIndex, int[] channelSpectrum) {
        this.channelIndex = channelIndex;
        this.channelSpectrum = channelSpectrum;
    }

    public XYSeries getXYSeries(Config config) {
        //TODO: write FUnction
        XYSeries xySeries = new XYSeries(config.channelNames.get(channelIndex));
        for (int i = 0; i < channelSpectrum.length; i++) {
            xySeries.add(config.startFrequency + i * (config.endFrequency - config.startFrequency) / config.blockSize, channelSpectrum[i]);
        }
        return xySeries;
    }
}
