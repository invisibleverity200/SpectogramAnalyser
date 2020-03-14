package com.UI;

import com.Data.BarChartDataSetGenerator;
import com.Data.Config;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

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
        BarChartDataSetGenerator dataSetGenerator = new BarChartDataSetGenerator();
        config.readConfigFile();

        JMenuBar menuBar = new JMenuBar();
        JMenu settingsMenu = new JMenu("Settings");
        JMenu channelMenu = new JMenu("Channel Selection");

        menuBar.add(settingsMenu);
        menuBar.add(channelMenu);

        JMenuItem menuItem = new JMenuItem("Settings", new ImageIcon("images/settings.png"));
        menuItem.addActionListener((ActionEvent e) -> {
            JDialog settingDialog = new JDialog();
            JPanel panel = new JPanel();

            JTextField startEndFrequencyField = new JTextField("Start-End Frequency");
            JTextField VoltageStepWidthField = new JTextField("Voltage step width");
            JTextField ipAndPortField = new JTextField("Hostname:Port");

            JButton applyButton = new JButton("Apply");
            JButton saveButton = new JButton("Save");

            applyButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    config.updateConfig(Integer.valueOf(startEndFrequencyField.getText().split("-")[0]), Integer.valueOf(startEndFrequencyField.getText().split("-")[1]), Double.valueOf(VoltageStepWidthField.getText()), Integer.valueOf(ipAndPortField.getText().split(":")[1]), ipAndPortField.getText().split(":")[0]);
                }
            });

            saveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    config.updateConfig(Integer.valueOf(startEndFrequencyField.getText().split("-")[0]), Integer.valueOf(startEndFrequencyField.getText().split("-")[1]), Double.valueOf(VoltageStepWidthField.getText()), Integer.valueOf(ipAndPortField.getText().split(":")[1]), ipAndPortField.getText().split(":")[0]);
                    config.writeConfigFile();
                }
            });


            GridBagConstraints gbc = new GridBagConstraints();
            panel.setLayout(new GridBagLayout());

            gbc.fill = GridBagConstraints.BASELINE;
            gbc.gridx = 10;
            gbc.gridy = 0;
            panel.add(startEndFrequencyField, gbc);
            gbc.gridx = 1;
            gbc.gridy = 0;
            panel.add(VoltageStepWidthField, gbc);
            gbc.gridheight = 0;
            panel.add(applyButton, gbc);
            panel.add(saveButton, gbc);
            panel.add(ipAndPortField,gbc);

            settingDialog.setContentPane(panel);
            settingDialog.setTitle("Settings");
            settingDialog.setVisible(true);
            settingDialog.setSize(400, 400);
        });
        menuItem.setMnemonic(KeyEvent.VK_B);

        settingsMenu.add(menuItem);

        JCheckBoxMenuItem[] channelItems = new JCheckBoxMenuItem[config.channelNames.length];
        int i = 0;
        for (String channelName : config.channelNames) {
            channelItems[i] = new JCheckBoxMenuItem(channelName);
            channelMenu.add(channelItems[i]);
            i++;
        }

        JButton plotButton = new JButton("Connect");

        plotButton.addActionListener((ActionEvent e) -> {
            boolean[] selectedItems = new boolean[config.channelNames.length];
            for (int y = 0; y < config.channelNames.length - 1; y++) {
                selectedItems[y] = channelItems[y].isSelected();
            }
            /*dataSetGenerator.createDataSet(null,null,config.startFrequency,config.endFrequency)
            barChart.init();*/
        });

        GridBagConstraints gbc = new GridBagConstraints();
        setLayout(new GridBagLayout());
        add(plotButton, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        setJMenuBar(menuBar);

        setVisible(true);
        setSize(400, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

    }
}
