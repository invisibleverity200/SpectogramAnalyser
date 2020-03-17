package com.Network;

import com.Data.AudChannel;
import com.UI.BarChart;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class AudioClient implements Client {
    public AudChannel[] channels;
    Socket s;
    DataOutputStream outputStream;
    DataInputStream dataInputStream;

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

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public AudChannel[] getNextChannelMeasurements(BarChart chart) {
        return new AudChannel[0];
    }
}
