/**
 * AudioClient
 * <p>
 * Version: 1.0
 * <p>
 * Author: Tomek Steenbock
 */
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
    public boolean freeze = false;
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
            long temp = System.currentTimeMillis();
            outputStream.write(1);

            while (true) {
                int[][] updateDataSet = new int[selectedChannels.length][config.blockSize];
                if (dataInputStream.available() >= ((config.blockSize + 1) * config.channelNames.size() * Integer.BYTES)) {
                    temp = System.currentTimeMillis();
                    if (!freeze && !reload) {
                        channels = new AudChannel[config.channelNames.size()];
                    }
                    for (int i = 0; i < config.channelNames.size(); i++) {
                        int channelIndex = dataInputStream.readInt();
                        System.out.println("ChannelIndex: " + channelIndex);
                        if (channelIndex != i + 1) {
                            correctNumberOfPackages = false;
                        }
                        int[] channelSpectrum = new int[config.blockSize];
                        for (int y = 0; y < channelSpectrum.length; y++) {
                            channelSpectrum[y] = dataInputStream.readInt();
                            System.out.println("ChannelSpec: " + channelSpectrum[y]);
                        }
                        if (!freeze && !reload) {
                            channels[i] = new AudChannel(channelIndex, channelSpectrum);
                        }
                    }
                    if (!correctNumberOfPackages) {
                        JOptionPane.showMessageDialog(null,
                                "Server sent´s less channel packages than you have\n Fix the config file otherwise the shown data will be incorrect!!!", "An Error occurred",
                                JOptionPane.WARNING_MESSAGE);
                    }
                    if (!freeze && !reload) {
                        for (int x = 0; x < updateDataSet.length; x++) {
                            try {
                                updateDataSet[x] = channels[selectedChannels[x]].channelSpectrum;
                            } catch (IndexOutOfBoundsException ignored) {

                            }
                        }
                        chart.update(updateDataSet, config.frequencyStepWidth, config.startFrequency, config);
                    }

                }

                if ((System.currentTimeMillis() - temp) > 20000) {
                    return false;
                }
            }
        } catch (IOException e) {
            System.out.println("ERROR: " + e.getMessage());
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
