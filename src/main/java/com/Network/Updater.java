package com.Network;

import com.Data.Config;
import com.UI.BarChart;

import javax.swing.*;
import java.io.IOException;

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
            client.startReceiving(config, chart);
            int returnVal = JOptionPane.showConfirmDialog(null, "Server timeout\n/Cant reach Server\nTry to reconnect?", "Information", JOptionPane.YES_NO_OPTION);
            if (returnVal == JOptionPane.YES_OPTION) {
                client.reconnectToServer();

            } else {
                try {
                    client.closeConnection();
                } catch (IOException e) {
                    System.out.println("ERROR: " + e.getMessage());
                }
                JOptionPane.showMessageDialog(null, "closed connection", "Information", JOptionPane.INFORMATION_MESSAGE);
                this.stop();
            }


        }
    }
}
