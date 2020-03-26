package com.Network;

import com.Data.Configs;
import com.UI.Charts;

import javax.swing.*;
import java.io.IOException;

public class Updater extends Thread {
    private Charts chart;
    private AudioClient client;
    private Configs config;

    public Updater(Charts chart, AudioClient client, Configs config) {
        this.chart = chart;
        this.client = client;
        this.config = config;
    }

    @SuppressWarnings("InfiniteLoopStatement")
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
