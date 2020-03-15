package com.Network;

import com.Data.Config;
import com.UI.BarChart;

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
        int[][] i = {{2, 3}};
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        chart.update(i, 1000, config.startFrequency);
    }
}
