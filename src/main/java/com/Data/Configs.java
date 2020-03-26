package com.Data;

import javax.json.*;
import javax.swing.*;
import java.io.*;
import java.util.ArrayList;

public interface Configs {

    void writeConfigFile();

    void writeSelectedChannelsFile();


    void updateConfig(int startFrequency, double voltageStepWidth, int port, String hostname, double frequencyStepWidth);


    ArrayList<Integer> getSelectedItems();

    void setSelectedItems(ArrayList<Integer> selectedItems);

    ArrayList<String> getChannelNames();

    String getHostname();

    double getFrequencyStepWidth();

    double getVoltageStepWidth();

    int getStartFrequency();

    int getPort();

    int getBlockSize();
}
