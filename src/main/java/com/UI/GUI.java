package com.UI;

import com.Data.AudChannel;
import com.Data.BarChartDataSetGenerator;
import com.Data.Config;
import com.Network.AudioClient;
import com.Network.Updater;
import org.jfree.chart.ChartPanel;
import org.jfree.data.xy.XYSeries;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class GUI extends JFrame {
    public GUI() {
       /* int[] i = {1, 2, 3};
        int[] y = {4, 5, 6};
        int[] temp = {7,8,9};
        int[][] x = {i,y,temp};
        BarChartDataSet barChartDataSet = new BarChartDataSet();
        XYSeries[] xySeries = {barChartDataSet.createDataSet(i, "test", 1000, 1000),barChartDataSet.createDataSet(y, "test2", 1000, 1000)};

        XYSeries[] xySeriess = {barChartDataSet.createDataSet(i, "test1", 1000, 1000),barChartDataSet.createDataSet(y, "test2", 1000, 1000),barChartDataSet.createDataSet(temp,"test3",1000,1000)};
        JFrame frame = new JFrame();
        BarChart barChart = new BarChart();
        frame.setContentPane(barChart.init(xySeries));
        frame.setVisible(true);
        frame.setSize(600,600);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        frame.getContentPane().removeAll();
        frame.setContentPane(barChart.init(xySeriess));
        frame.repaint();
        frame.revalidate();
        frame.pack();*/

        Config config = new Config();
        BarChart barChart = new BarChart();
        final AudioClient[] client = {null};
        final Thread[] clientThread = new Thread[1];

        JButton connectButton = new JButton("Connect");

        JMenuBar menuBar = new JMenuBar();
        JMenu settingsMenu = new JMenu("Settings");
        JMenu channelMenu = new JMenu("Channel Selection");
        JMenu operations = new JMenu("Operations");

        menuBar.add(settingsMenu);
        menuBar.add(channelMenu);
        menuBar.add(operations);

        JMenuItem menuItem = new JMenuItem("Settings", new ImageIcon("images/settings.png"));
        menuItem.addActionListener((ActionEvent e) -> {
            JDialog settingDialog = new JDialog();
            JPanel panel = new JPanel();

            JTextField startEndFrequencyField = new JTextField(config.startFrequency + "-" + config.endFrequency);
            JTextField VoltageStepWidthField = new JTextField(String.valueOf(config.voltageStepWidth));
            JTextField ipAndPortField = new JTextField(config.hostname + ":" + config.port);

            JButton applyButton = new JButton("Apply");
            JButton saveButton = new JButton("Save");

            applyButton.addActionListener(e1 -> {
                try {
                    String[] startEndFrequency = startEndFrequencyField.getText().split("-");
                    double voltageStepWidth = Double.valueOf(VoltageStepWidthField.getText());
                    String[] hostnamePort = ipAndPortField.getText().split(":");
                    //192.168.1.1
                    if (startEndFrequency.length == 2 && hostnamePort.length == 2 && hostnamePort[0].split("\\.").length == 4) {
                        config.updateConfig(Integer.valueOf(startEndFrequency[0]), Integer.valueOf(startEndFrequency[1]), voltageStepWidth, Integer.valueOf(hostnamePort[1]), hostnamePort[0]);
                    } else {
                        JOptionPane.showMessageDialog(null, "Not allowed Structure", "An Error occurred", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException e2) {
                    JOptionPane.showMessageDialog(null, "Strings are not allowed in Setting fields", "An Error occurred", JOptionPane.ERROR_MESSAGE);
                }
            });

            saveButton.addActionListener(e12 -> {
                try {
                    String[] startEndFrequency = startEndFrequencyField.getText().split("-");
                    double voltageStepWidth = Double.valueOf(VoltageStepWidthField.getText());
                    String[] hostnamePort = ipAndPortField.getText().split(":");

                    if (startEndFrequency.length == 2 && hostnamePort.length == 2 && hostnamePort[0].split("\\.").length == 4) {
                        config.updateConfig(Integer.valueOf(startEndFrequency[0]), Integer.valueOf(startEndFrequency[1]), voltageStepWidth, Integer.valueOf(hostnamePort[1]), hostnamePort[0]);
                        config.writeConfigFile();
                    } else {
                        JOptionPane.showMessageDialog(null, "Not allowed Structure", "An Error occurred", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException e2) {
                    JOptionPane.showMessageDialog(null, "Strings are not allowed in Setting fields", "An Error occurred", JOptionPane.ERROR_MESSAGE);
                }
            });


            GridBagConstraints gbc = new GridBagConstraints();
            panel.setLayout(new GridBagLayout());

            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0;
            gbc.gridy = 0;
            panel.add(startEndFrequencyField, gbc);
            gbc.gridx = 0;
            gbc.gridy = 1;
            panel.add(VoltageStepWidthField, gbc);
            gbc.gridwidth = 3;
            gbc.gridx = 0;
            gbc.gridy = 5;
            panel.add(applyButton, gbc);
            gbc.gridwidth = 3;
            gbc.gridx = 0;
            gbc.gridy = 4;
            panel.add(saveButton, gbc);
            gbc.gridwidth = 3;
            gbc.gridx = 0;
            gbc.gridy = 3;
            panel.add(ipAndPortField, gbc);

            settingDialog.setContentPane(panel);
            settingDialog.setTitle("Settings");
            settingDialog.setVisible(true);
            settingDialog.setSize(150, 180);
        });
        menuItem.setMnemonic(KeyEvent.VK_B);

        settingsMenu.add(menuItem);

        JCheckBoxMenuItem[] channelItems = new JCheckBoxMenuItem[config.channelNames.size()];
        int i = 0;
        for (String channelName : config.channelNames) {
            channelItems[i] = new JCheckBoxMenuItem(channelName);
            channelMenu.add(channelItems[i]);
            i++;
        }

        JMenuItem stopContinueOption = new JMenuItem("freeze Connection");
        stopContinueOption.addActionListener((ActionEvent e) -> {
            if (stopContinueOption.getText().equals("freeze Connection")) {
                stopContinueOption.setText("continue Connection");
            } else {
                stopContinueOption.setText("freeze Connection");
            }
        });

        connectButton.addActionListener((ActionEvent e) -> {
            int[] selectedChannels = getSelectedChannels(channelItems, config);
            if (selectedChannels.length != 0) {
                XYSeries[] initArray = new XYSeries[selectedChannels.length];//TODO init richtig machen
                for (int x = 0; x < initArray.length; x++) {
                    initArray[x] = new XYSeries(config.channelNames.get(selectedChannels[x]));
                    initArray[x].add(0, 0);
                }
                ChartPanel chartPanel = barChart.init(initArray);
                setContentPane(chartPanel); //TODO bug
                revalidate();
                repaint();
                pack();

                client[0] = new AudioClient();
                client[0].selectedChannels = selectedChannels;
                if (client[0].connectTo(config.hostname, config.port)) {
                    System.out.println("Connection established");
                } else {
                    JOptionPane.showMessageDialog(null, "Client is not able to connect to the Server", "An Error occurred", JOptionPane.ERROR_MESSAGE);
                }
                clientThread[0] = new Updater(barChart, client[0], config); //TODO make this correct
                clientThread[0].start();
            } else {
                JOptionPane.showMessageDialog(null, "You have to select at least one channel", "An Error occurred", JOptionPane.ERROR_MESSAGE);
            }
        });

        JMenuItem cancel = new JMenuItem("cancel Connection");
        cancel.addActionListener((ActionEvent e) -> {
            if (!client[0].stop()) {
                JOptionPane.showMessageDialog(null, "No Connection to Close", "An Error occurred", JOptionPane.ERROR_MESSAGE);
            }
            clientThread[0].stop();
            //cancel Connection
            setContentPane(new JPanel());
            setLayout(new GridLayout());
            setSize(400, 400);
            add(connectButton);
            repaint();
            revalidate();
            pack();
        });

        JMenuItem reload = new JMenuItem("reload");
        reload.addActionListener((ActionEvent e) -> { // FINISHED I GUESS
            int[] selectedChannels = getSelectedChannels(channelItems, config);
            if (selectedChannels.length != 0) {
                XYSeries[] initArray = new XYSeries[selectedChannels.length];
                client[0].selectedChannels = selectedChannels;

                for (int x = 0; x < initArray.length; x++) {
                    initArray[x] = new XYSeries(config.channelNames.get(selectedChannels[x]));
                    initArray[x].add(0, 0);
                    // initArray[x] = client[0].channels[selectedChannels[x]].getXYSeries(config);
                }

                ChartPanel chartPanel = barChart.init(initArray);
                if (chartPanel != null) {
                    setContentPane(chartPanel); //TODO bug
                    revalidate();
                    repaint();
                    pack();
                } else {
                    JOptionPane.showMessageDialog(null, "No valid data source", "An Error occurred", JOptionPane.ERROR_MESSAGE);
                }

                revalidate();
                repaint();
                pack();
            } else {
                JOptionPane.showMessageDialog(null, "You have to select al least one channel", "An Error occurred", JOptionPane.ERROR_MESSAGE);
            }
        });

        operations.add(reload);
        operations.add(stopContinueOption);
        operations.add(cancel);


        setLayout(new GridLayout());
        add(connectButton);

        setJMenuBar(menuBar);

        setVisible(true);
        setSize(280, 100);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

    }

    private int[] getSelectedChannels(JCheckBoxMenuItem[] channelItems, Config config) {
        ArrayList<Integer> listOfSelectedChannels = new ArrayList<>();
        for (int y = 0; y < config.channelNames.size(); y++) {
            if (channelItems[y].isSelected()) {
                listOfSelectedChannels.add(y);
            }
        }
        int[] selectedChannels = new int[listOfSelectedChannels.size()];
        for (int i = 0; i < listOfSelectedChannels.size(); i++) {
            selectedChannels[i] = listOfSelectedChannels.get(i);
        }
        return selectedChannels;
    }

    private void fillArray(XYSeries[] array) { //TODO hier brauche ich noch die Config um die Channel richtig zubennenen.
        for (int i = 0; i < array.length; i++) {
            array[i] = new XYSeries(String.valueOf(i));
            array[i].add(0, 0);
        }
    }
}
