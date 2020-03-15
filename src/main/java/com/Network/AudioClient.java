package com.Network;

import com.Data.AudChannel;

public class AudioClient implements Client {
    public AudChannel[] channels;

    @Override
    public boolean getStatus() {
        return false;
    }

    @Override
    public boolean connectTo(String hostname, int port) {
        return false;
    }

   @Override
   public AudChannel[] getNextChannelMeasurements() {
      return new AudChannel[0];
   }
}
