package com.Network;

import com.Data.AudChannel;
import com.Data.Config;
import com.UI.BarChart;

public interface Client {

    boolean connectTo(String hostname, int port);

    boolean closeConnection();

    boolean startReceiving(Config config, BarChart chart);
}

