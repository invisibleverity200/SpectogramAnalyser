package com.Network;

import com.Data.AudChannel;
import com.Data.Config;
import com.UI.BarChart;

public interface Client {
    boolean getStatus();

    boolean connectTo(String hostname, int port);

    AudChannel[] getNextChannelMeasurements(BarChart chart);

    boolean closeConnection();

    boolean startReceiving(Config config, BarChart chart);
}

