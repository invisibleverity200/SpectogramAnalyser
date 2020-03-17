package com.Network;

import com.Data.AudChannel;
import com.Data.Config;
import com.UI.BarChart;

import java.awt.event.ItemEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class AudioClient implements Client {
    public AudChannel[] channels;
    Socket s;
    DataOutputStream outputStream;
    DataInputStream dataInputStream;
    public int[] selectedChannels;

    @Override
    public boolean getStatus() {
        return false;
    }

    @Override
    public boolean connectTo(String hostname, int port) {
        try {
            System.out.println("Hostname: " + hostname);
            System.out.println("Port:" + port);
            s = new Socket(hostname, port);
            outputStream = new DataOutputStream(s.getOutputStream());
            dataInputStream = new DataInputStream(s.getInputStream());
            //
            outputStream.write(1);
            Thread.sleep(100);
            while (dataInputStream.available() > 0) {
                char charByte = (char) dataInputStream.readByte();
                System.out.println(charByte);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public AudChannel[] getNextChannelMeasurements(BarChart chart) {
        return new AudChannel[0];
    }

    @Override
    public boolean stop() {
        try {
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
            int[][] updateDataSet = new int[selectedChannels.length][config.blockSize];
            long temp = 0;
            channels = new AudChannel[config.channelNames.size()];
            outputStream.write(1);
            Thread.sleep(100);
            while (true) {
                while (dataInputStream.available() > 0) {
                    temp = System.currentTimeMillis();
                    if (dataInputStream.available() > config.blockSize) {
                        for (int i = 0; i < config.channelNames.size(); i++) {
                            int channelIndex = dataInputStream.readInt();
                            int[] channelSpectrum = new int[config.blockSize];
                            for (int y = 0; y < channelSpectrum.length; y++) {
                                channelSpectrum[y] = dataInputStream.readInt();
                            }
                            channels[i] = new AudChannel(channelIndex, channelSpectrum);
                        }

                        for (int x = 0; x < updateDataSet.length; x++) {
                            updateDataSet[x] = channels[selectedChannels[x]].channelSpectrum;
                        }
                        chart.update(updateDataSet, (config.endFrequency - config.startFrequency) / config.blockSize, config.startFrequency);
                    }
                }
                if ((System.currentTimeMillis() - temp) > 200) {
                    return false;
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

}
