package com.Network;

import com.Data.AudChannel;
import com.Data.Config;
import com.UI.BarChart;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class AudioClient implements Client {
    public AudChannel[] channels;
    public boolean reload = false;
    private Socket s;
    private DataOutputStream outputStream;
    private DataInputStream dataInputStream;
    private String hostname;
    private int port;
    public int[] selectedChannels;

    @Override
    public boolean connectTo(String hostname, int port) {
        try {
            System.out.println("Hostname: " + hostname);
            this.hostname = hostname;
            System.out.println("Port:" + port);
            this.port = port;
            s = new Socket(hostname, port);
            outputStream = new DataOutputStream(s.getOutputStream());
            dataInputStream = new DataInputStream(s.getInputStream());

        } catch (IOException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean closeConnection() {
        try {
            outputStream.write(0);
            s.close();
            outputStream.close();
            dataInputStream.close();
        } catch (IOException | NullPointerException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean startReceiving(Config config, BarChart chart) {
        try {
            boolean correctNumberOfPackages = true;
            int[][] updateDataSet = new int[selectedChannels.length][config.blockSize];
            long temp = System.currentTimeMillis() - 20;
            outputStream.write(1);
            while (true) {
                if (dataInputStream.available() >= ((config.blockSize + 1) * config.channelNames.size() * Integer.BYTES)) {
                    temp = System.currentTimeMillis();
                    channels = new AudChannel[config.channelNames.size()];
                    for (int i = 0; i < config.channelNames.size(); i++) {
                        int channelIndex = dataInputStream.readInt();
                        if (channelIndex != i + 1) { //FIXME POSSIBLE BUG
                            correctNumberOfPackages = false;
                        }
                        int[] channelSpectrum = new int[config.blockSize];
                        for (int y = 0; y < channelSpectrum.length; y++) {
                            channelSpectrum[y] = dataInputStream.readInt();
                        }
                        channels[i] = new AudChannel(channelIndex, channelSpectrum);
                    }
                    if (!correctNumberOfPackages) {
                        JOptionPane.showMessageDialog(null, "Server sent´s less channel packages than you have\n Fix the config file otherwise the shown data will be incorrect!!!", "An Error occurred", JOptionPane.WARNING_MESSAGE);
                    }
                    while (reload) {
                    }

                    for (int x = 0; x < updateDataSet.length; x++) {
                        updateDataSet[x] = channels[selectedChannels[x]].channelSpectrum;
                    }
                    chart.update(updateDataSet, (config.endFrequency - config.startFrequency) / config.blockSize, config.startFrequency, config);
                }

                if ((System.currentTimeMillis() - temp) > 200) {
                    return false;
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    void reconnectToServer() {
        try {
            System.out.println("Hostname: " + hostname);
            System.out.println("Port:" + port);
            s = new Socket(hostname, port);
            outputStream = new DataOutputStream(s.getOutputStream());
            dataInputStream = new DataInputStream(s.getInputStream());

        } catch (IOException ignored) {
        }
    }
}
