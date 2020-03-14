package com.Network;

import com.UI.BarChart;

public class Updater extends Thread {
    BarChart chart;
    AudioClient client;

    public Updater(BarChart chart, AudioClient client) {
        this.chart = chart;
        this.client = client;
    }

    @Override
    public void run() {
        //chart.update();
    }
}
