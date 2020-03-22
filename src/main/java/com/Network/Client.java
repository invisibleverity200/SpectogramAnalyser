package com.Network;

import com.Data.AudChannel;
import com.Data.Config;
import com.UI.BarChart;

import java.io.IOException;

public interface Client {

    boolean connectTo(String hostname, int port);

    void closeConnection() throws IOException, NullPointerException;

    void startReceiving(Config config, BarChart chart);
}

