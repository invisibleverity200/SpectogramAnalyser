package com.UI;

import com.Data.Config;
import com.Data.Configs;
import com.Network.AudioClient;
import com.Network.Updater;
import org.jfree.chart.ChartPanel;
import org.jfree.data.xy.XYSeries;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;

public class GUI extends JFrame {
    public GUI() {
        Configs config = new Config();
        Charts barChart = new BarChart();
        AudioClient[] client = {null};
        Thread[] clientThread = {null};

        JButton connectButton = new JButton("Connect");
        JMenuBar menuBar = new JMenuBar();


        JMenu settingsMenu = new JMenu("Settings");
        JMenu channelMenu = new JMenu("Channel Selection");
        JMenu operations = new JMenu("Operations");
        JLabel label = new JLabel("         AVG Update time: closed");

        label.setForeground(Color.red);

        menuBar.add(settingsMenu);
        menuBar.add(channelMenu);
        menuBar.add(operations);
        menuBar.add(label);

        JMenuItem menuItem = new JMenuItem("Settings", new ImageIcon("images/settings.png"));
        menuItem.addActionListener((ActionEvent e) -> {
            JDialog settingDialog = new JDialog();
            JPanel panel = new JPanel();

            JTextField startFrequencyField = new JTextField(String.valueOf(config.getStartFrequency()));
            JTextField voltageStepWidthField = new JTextField(String.valueOf(config.getVoltageStepWidth()));
            JTextField ipAndPortField = new JTextField(config.getHostname() + ":" + config.getPort());
            JTextField frequencyStepWidthField = new JTextField(String.valueOf(config.getFrequencyStepWidth()));

            JLabel startFrequencyLabel = new JLabel("Start frequency: ");
            JLabel voltageStepWidthLabel = new JLabel("Voltage step width: ");
            JLabel ipAndPortLabel = new JLabel("IP:Port: ");
            JLabel frequencyStepWidthLabel = new JLabel("Frequency step width: ");

            JButton applyButton = new JButton("Apply");
            JButton saveButton = new JButton("Save");

            applyButton.addActionListener(e1 -> applySettings(frequencyStepWidthField, startFrequencyField, voltageStepWidthField, ipAndPortField, config));

            saveButton.addActionListener(e12 -> {
                applySettings(frequencyStepWidthField, startFrequencyField, voltageStepWidthField, ipAndPortField, config);
                config.writeConfigFile();
            });

            GridBagConstraints gbc = new GridBagConstraints();
            GridBagLayout gridLayout = new GridBagLayout();
            panel.setLayout(gridLayout);

            gbc.insets = new Insets(0, 0, 5, 0);
            gbc.weightx = 1;
            gbc.weighty = 1;
            gbc.fill = GridBagConstraints.BOTH;

            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            gridLayout.setConstraints(startFrequencyField, gbc);
            panel.add(startFrequencyField, gbc);

            gbc.gridwidth = 2;
            gbc.gridx = 1;
            gbc.gridy = 1;
            gridLayout.setConstraints(voltageStepWidthField, gbc);
            panel.add(voltageStepWidthField, gbc);

            gbc.gridwidth = 3;
            gbc.gridx = 0;
            gbc.gridy = 4;
            gridLayout.setConstraints(applyButton, gbc);
            panel.add(applyButton, gbc);

            gbc.gridwidth = 3;
            gbc.gridx = 0;
            gbc.gridy = 5;
            gridLayout.setConstraints(saveButton, gbc);
            panel.add(saveButton, gbc);

            gbc.gridwidth = 2;
            gbc.gridx = 1;
            gbc.gridy = 2;
            gridLayout.setConstraints(ipAndPortField, gbc);
            panel.add(ipAndPortField, gbc);

            gbc.gridwidth = 2;
            gbc.gridx = 1;
            gbc.gridy = 3;
            gridLayout.setConstraints(frequencyStepWidthField, gbc);
            panel.add(frequencyStepWidthField, gbc);

            gbc.gridwidth = 1;
            gbc.gridx = 0;
            gbc.gridy = 0;
            gridLayout.setConstraints(startFrequencyLabel, gbc);
            panel.add(startFrequencyLabel, gbc);

            gbc.gridwidth = 1;
            gbc.gridx = 0;
            gbc.gridy = 1;
            gridLayout.setConstraints(voltageStepWidthLabel, gbc);
            panel.add(voltageStepWidthLabel, gbc);

            gbc.gridwidth = 1;
            gbc.gridx = 0;
            gbc.gridy = 2;
            gridLayout.setConstraints(ipAndPortLabel, gbc);
            panel.add(ipAndPortLabel, gbc);

            gbc.gridwidth = 1;
            gbc.gridx = 0;
            gbc.gridy = 3;
            gridLayout.setConstraints(frequencyStepWidthLabel, gbc);
            panel.add(frequencyStepWidthLabel, gbc);

            settingDialog.setContentPane(panel);
            pack();
            settingDialog.setTitle("Settings");
            settingDialog.setSize(380, 180);
            settingDialog.setVisible(true);
        });
        menuItem.setMnemonic(KeyEvent.VK_B);

        settingsMenu.add(menuItem);

        JCheckBoxMenuItem[] channelItems = new JCheckBoxMenuItem[config.getChannelNames().size()]; //for every channel name in the config file will be created a checkbox in the Menu bar
        int channelIndex = 0;
        int selectedIndex = 0;
        for (String channelName : config.getChannelNames()) {
            channelItems[channelIndex] = new JCheckBoxMenuItem(channelName);
            if (channelIndex == config.getSelectedItems().get(selectedIndex)) {
                channelItems[channelIndex].setSelected(true);
                if (!(config.getSelectedItems().size() - 1 == selectedIndex)) {
                    selectedIndex++;
                }
            }

            channelMenu.add(channelItems[channelIndex]);
            channelIndex++;
        }

        JMenuItem stopContinueOption = new JMenuItem("Freeze Connection");
        stopContinueOption.addActionListener((
                ActionEvent e) ->

        {
            if (stopContinueOption.getText().equals("Freeze Connection") && client[0] != null) {
                stopContinueOption.setText("Continue Connection");
                client[0].freeze = true;
            } else if (stopContinueOption.getText().equals("Continue Connection") && client[0] != null) {
                stopContinueOption.setText("Freeze Connection");
                client[0].freeze = false;
            }
        });

        connectButton.addActionListener((ActionEvent e) -> {
            client[0] = new AudioClient();
            int[] selectedChannels = getSelectedChannels(channelItems, config);
            if (selectedChannels.length != 0) {
                if (client[0].connectTo(config.getHostname(), config.getPort())) {

                    XYSeries[] initArray = new XYSeries[selectedChannels.length];
                    for (int index = 0; index < initArray.length; index++) {
                        initArray[index] = new XYSeries(config.getChannelNames().get(selectedChannels[index]));
                        initArray[index].add(0, 0);
                    }
                    ChartPanel chartPanel = barChart.init(initArray);
                    setContentPane(chartPanel);
                    revalidate();
                    repaint();
                    pack();

                    client[0].selectedChannels = selectedChannels;
                    client[0].label = label;

                    System.out.println("Connection established");

                    clientThread[0] = new Updater(barChart, client[0], config);
                    clientThread[0].start();
                } else {
                    JOptionPane.showMessageDialog(null, "Client is not able to connect to the Server", "An Error occurred", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "You have to select at least one channel", "An Error occurred", JOptionPane.ERROR_MESSAGE);
            }
        });

        JMenuItem cancel = new JMenuItem("Cancel Connection");
        cancel.addActionListener((ActionEvent e) -> {
            try {
                if (client[0] != null) {
                    client[0].closeConnection();
                }
                if (clientThread[0] != null) {
                    clientThread[0].stop();
                }

                JPanel panel = new JPanel();
                panel.setLayout(new GridLayout());
                panel.add(connectButton);
                label.setText("         AVG Update time: closed");

                setContentPane(panel);

                revalidate();
                repaint();
                pack();
            } catch (NullPointerException | IOException e12) {
                System.out.println("\u001B[31m" + "ERROR" + e12.getMessage());
            }
        });

        JMenuItem reload = new JMenuItem("Reload");
        reload.addActionListener((ActionEvent e) -> {
            int[] selectedChannels = getSelectedChannels(channelItems, config);

            if ((selectedChannels.length != 0) && (client[0] != null)) {
                try {
                    client[0].reload = true;
                    client[0].selectedChannels = selectedChannels;
                    Thread.sleep(10);
                    config.setSelectedItems(new ArrayList<>());
                    for (int selectedChannel : selectedChannels) {
                        config.getSelectedItems().add(selectedChannel);
                    }
                    config.writeSelectedChannelsFile();

                    XYSeries[] initArray = new XYSeries[selectedChannels.length];


                    for (int index = 0; index < initArray.length; index++) {
                        initArray[index] = client[0].channels[selectedChannels[index]].getXYSeries(config);
                    }
                    client[0].reload = false;

                    ChartPanel chartPanel = barChart.init(initArray);
                    if (chartPanel != null) {
                        setContentPane(chartPanel);
                        revalidate();
                        repaint();
                        pack();
                    } else {
                        JOptionPane.showMessageDialog(null, "No valid data source", "An Error occurred", JOptionPane.ERROR_MESSAGE);
                    }

                } catch (NullPointerException | InterruptedException e3) {
                    System.out.println("ERROR" + e3.getMessage());
                }
            } else if (selectedChannels.length <= 0) {
                JOptionPane.showMessageDialog(null, "You have to select al least one channel", "An Error occurred", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Please connect to a Server first", "An Error occurred", JOptionPane.ERROR_MESSAGE);
            }

        });

        JMenuItem quit = new JMenuItem("Quit");
        quit.addActionListener((ActionEvent e) -> {
            try {
                if ((client[0] != null) && (clientThread[0] != null)) {
                    client[0].closeConnection();
                    clientThread[0].stop();
                }
            } catch (IOException | NullPointerException e3) {
                System.out.println("\u001B[31m" + "ERROR" + e3.getMessage());
            }
            System.exit(1);
        });

        operations.add(stopContinueOption);
        operations.add(reload);
        operations.add(cancel);
        operations.add(quit);

        setLayout(new GridLayout());

        add(connectButton);

        setJMenuBar(menuBar);

        pack();

        setTitle("SpectrumAnalyser");

        setVisible(true);

        setSize(440, 100);

        setDefaultCloseOperation(EXIT_ON_CLOSE);

    }

    private int[] getSelectedChannels(JCheckBoxMenuItem[] channelItems, Configs config) {
        ArrayList<Integer> listOfSelectedChannels = new ArrayList<>();
        for (int y = 0; y < config.getChannelNames().size(); y++) {
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

    private void applySettings(JTextField frequencyStepWidth, JTextField startFrequencyField, JTextField VoltageStepWidthField, JTextField ipAndPortField, Configs config) {
        try {
            String startFrequency = startFrequencyField.getText();
            double voltageStepWidth = Double.parseDouble(VoltageStepWidthField.getText());
            String[] hostnamePort = ipAndPortField.getText().split(":");

            if ((hostnamePort.length == 2) && (hostnamePort[0].split("\\.").length == 4)) {
                config.updateConfig(Integer.parseInt(startFrequency), voltageStepWidth, Integer.parseInt(hostnamePort[1]), hostnamePort[0], Double.parseDouble(frequencyStepWidth.getText()));
            } else {
                JOptionPane.showMessageDialog(null, "Not allowed Structure", "An Error occurred", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e2) {
            JOptionPane.showMessageDialog(null, "Strings are not allowed in Setting fields", "An Error occurred", JOptionPane.ERROR_MESSAGE);
        }
    }

}
