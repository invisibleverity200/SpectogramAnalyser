package com.Data;

public class AudChannel {
    //int ChannelVame int[512] value
    int channelIndex;
    int[] channelSpectrum;
    AudChannel(int channelIndex, int[] channelSpectrum){
        this.channelIndex  = channelIndex;
        this.channelSpectrum = channelSpectrum;
    }
}
