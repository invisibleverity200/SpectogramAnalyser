package com.Network;

import com.Data.Config;
import com.UI.BarChart;

import javax.swing.*;

public class Updater extends Thread {
    private BarChart chart;
    private AudioClient client;
    private Config config;

    public Updater(BarChart chart, AudioClient client, Config config) {
        this.chart = chart;
        this.client = client;
        this.config = config;
    }

    @Override
    public void run() {
        while (true) {
            if (!client.startReceiving(config, chart)) {
                int returnVal = JOptionPane.showConfirmDialog(null, "Server timeout\n/Cant reach Server\nTry to reconnect?", "Information", JOptionPane.YES_NO_OPTION);
                if (returnVal == JOptionPane.YES_OPTION) {
                    client.reconnectToServer();

                } else {
                    JOptionPane.showMessageDialog(null, "closed connection", "Information", JOptionPane.INFORMATION_MESSAGE);
                    client.closeConnection();
                    this.stop();
                }

            }
        }
    }
}
