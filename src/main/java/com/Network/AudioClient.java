package com.Network;

import com.Data.AudChannel;
import com.Data.Config;
import com.UI.BarChart;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

public class AudioClient implements Client {
    public AudChannel[] channels;
    Socket s;
    DataOutputStream outputStream;
    DataInputStream dataInputStream;
    String hostname;
    int port;
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
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean closeConnection() {
        try {
            outputStream.write(0); //tells server to stop sending data
            s.close();
            outputStream.close();
            dataInputStream.close();
        } catch (IOException | NullPointerException e) {
            System.out.println("\u001B[31m" + "ERROR: " + e.getMessage());
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
            channels = new AudChannel[config.channelNames.size()];
            outputStream.write(1);
            Thread.sleep(100);
            while (true) {
                while (dataInputStream.available() > 0) {
                    temp = System.currentTimeMillis();
                    if (dataInputStream.available() > (config.blockSize * config.channelNames.size() * Integer.BYTES)) { // in byte um wandeln
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
                        for (int x = 0; x < updateDataSet.length; x++) {
                            updateDataSet[x] = channels[selectedChannels[x]].channelSpectrum;
                        }
                        chart.update(updateDataSet, (config.endFrequency - config.startFrequency) / config.blockSize, config.startFrequency, config);
                    }
                }
                if ((System.currentTimeMillis() - temp) > 200) {
                    return false;
                }
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("\u001B[31m" + "ERROR: " + e.getMessage());
            return false;
        }
    }

    public boolean reconnectToServer() {
        try {
            System.out.println("Hostname: " + hostname);
            System.out.println("Port:" + port);
            s = new Socket(hostname, port);
            outputStream = new DataOutputStream(s.getOutputStream());
            dataInputStream = new DataInputStream(s.getInputStream());

        } catch (IOException e) {
            System.out.println("\u001B[31m" + "ERROR: " + e.getMessage());
            return false;
        }
        return true;
    }
}
