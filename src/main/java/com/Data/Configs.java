package com.Data;

import java.util.ArrayList;

public interface Configs {

    void writeConfigFile();

    int getHighestValueOnY();

    void writeSelectedChannelsFile();


    void updateConfig(int startFrequency, double voltageStepWidth, int port, String hostname, double frequencyStepWidth, int highestValueOnY);


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
