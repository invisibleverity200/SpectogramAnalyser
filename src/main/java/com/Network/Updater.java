package com.Network;

import com.Data.Config;
import com.UI.BarChart;

import javax.swing.*;

public class Updater extends Thread {
    BarChart chart;
    AudioClient client;
    int[] selectedChannels;
    Config config;

    public Updater(BarChart chart, AudioClient client, Config config) {
        this.chart = chart;
        this.client = client;
        this.config = config;
    }

    public void setSelectedChannels(int[] selectedChannels) {
        this.selectedChannels = selectedChannels;
    }

    @Override
    public void run() {
       /* int[][] i = {{2, 3}, {2, 6},{3,9,12,12,100}};
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        chart.update(i, 1000, config.startFrequency);*/
        while (true) {
            if (!client.startReceiving(config, chart)) {
                int returnVal = JOptionPane.showConfirmDialog(null, "Server timeout\n Try to reconnect?", "Information", JOptionPane.YES_NO_OPTION);
                if (returnVal == JOptionPane.YES_OPTION) {
                    client.reconnectToServer();
                }else {
                    JOptionPane.showMessageDialog(null, "closed connection", "Information", JOptionPane.INFORMATION_MESSAGE);
                    client.closeConnection();
                    this.stop();
                }

            }
        }
    }
}
