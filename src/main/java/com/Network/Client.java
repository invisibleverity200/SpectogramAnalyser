package com.Network;

import com.Data.AudChannel;

public interface Client {
     boolean getStatus();
     boolean connectTo(String hostname,int port);
     AudChannel[] getNextChannelMeasurements();
}
