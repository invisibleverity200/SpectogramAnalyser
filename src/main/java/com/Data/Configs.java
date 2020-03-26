package com.Data;

import javax.json.*;
import javax.swing.*;
import java.io.*;
import java.util.ArrayList;

public interface Configs {

    public void writeConfigFile();

    public void writeSelectedChannelsFile();


    public void updateConfig(int startFrequency, double voltageStepWidth, int port, String hostname, double frequencyStepWidth);


    public ArrayList<Integer> getSelectedItems();

    public void setSelectedItems(ArrayList<Integer> selectedItems);

    public ArrayList<String> getChannelNames();

    public String getHostname();

    public double getFrequencyStepWidth();

    public double getVoltageStepWidth();

    public int getStartFrequency();

    public int getPort();

    public int getBlockSize();
}
