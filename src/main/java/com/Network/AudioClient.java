/*
 * AudioClient
 *
 * Version: 1
 *
 * Author: Tomek Steenbock
 */

package com.Network;

import com.Data.AudChannel;
import com.Data.Configs;
import com.UI.Charts;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class AudioClient {
    public JLabel label = null;
    public AudChannel[] channels;
    public int[] selectedChannels;
    public boolean freeze = false;
    public boolean reload = false;

    private Socket s;
    private DataOutputStream outputStream;
    private DataInputStream dataInputStream;
    private String hostname;
    private int port;


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


    public void closeConnection() throws IOException, NullPointerException {
        if (!(s.isClosed())) {
            if (s != null) s.close();
            if (outputStream != null) outputStream.close();
            if (dataInputStream != null) dataInputStream.close();
        }
    }


    void startReceiving(Configs config, Charts chart) {
        try {
            boolean correctNumberOfPackages = true;
            long avgUpdateTime = 0;
            long temp = System.currentTimeMillis();
            outputStream.write(1);

            while (true) {
                int[][] updateDataSet = new int[selectedChannels.length][config.getBlockSize()];
                if (dataInputStream.available() >= ((config.getBlockSize() + 1) * config.getChannelNames().size() * Integer.BYTES)) {
                    avgUpdateTime = System.currentTimeMillis() - temp;
                    temp = System.currentTimeMillis();
                    if (!(freeze) && !(reload)) {
                        channels = new AudChannel[config.getChannelNames().size()];
                    }
                    for (int index = 0; index < config.getChannelNames().size(); index++) {
                        int channelIndex = dataInputStream.readInt();
                        if (channelIndex != index + 1) {
                            correctNumberOfPackages = false;
                        }
                        int[] channelSpectrum = new int[config.getBlockSize()];
                        for (int spectrumIndex = 0; spectrumIndex < channelSpectrum.length; spectrumIndex++) {
                            channelSpectrum[spectrumIndex] = dataInputStream.readInt();
                        }
                        if (!(freeze) && !(reload)) {
                            channels[index] = new AudChannel(channelIndex, channelSpectrum);
                        }
                    }
                    if (!correctNumberOfPackages) {
                        JOptionPane.showMessageDialog(null,
                                "Server sent´s less channel packages than you have\n Fix the config file otherwise the shown data will be incorrect!!!", "An Error occurred",
                                JOptionPane.WARNING_MESSAGE);
                        System.exit(1);
                    }
                    if (!(freeze) && !(reload)) {
                        for (int selectedIndex = 0; selectedIndex < updateDataSet.length; selectedIndex++) {
                            try {
                                updateDataSet[selectedIndex] = channels[selectedChannels[selectedIndex]].channelSpectrum;
                            } catch (IndexOutOfBoundsException ignored) {

                            }
                        }
                        chart.update(updateDataSet, config.getFrequencyStepWidth(), config.getStartFrequency(), config);
                    }

                }
                if (avgUpdateTime != 0) label.setText("         AVG Update time: " + avgUpdateTime + "ms");
                if (System.currentTimeMillis() - temp >= 1300) {
                    label.setText("         AVG Update time: Timeout");
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("\u001B[31m" + "ERROR at Line 99: " + e.getMessage());
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
