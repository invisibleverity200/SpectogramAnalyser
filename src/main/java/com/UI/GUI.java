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
import java.util.concurrent.atomic.AtomicReference;

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
        final Thread[] clientThread = new Thread[1];
        BarChartDataSetGenerator dataSetGenerator = new BarChartDataSetGenerator();
        config.readConfigFile();

        JMenuBar menuBar = new JMenuBar();
        JMenu settingsMenu = new JMenu("Settings");
        JMenu channelMenu = new JMenu("Channel Selection");
        JMenu operations = new JMenu("operations");

        menuBar.add(settingsMenu);
        menuBar.add(channelMenu);
        menuBar.add(operations);

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

        JCheckBoxMenuItem[] channelItems = new JCheckBoxMenuItem[config.channelNames.length];
        int i = 0;
        for (String channelName : config.channelNames) {
            channelItems[i] = new JCheckBoxMenuItem(channelName);
            channelMenu.add(channelItems[i]);
            i++;
        }

        JMenuItem stopContinueOption = new JMenuItem("frezze Connection");
        stopContinueOption.addActionListener((ActionEvent e) -> {
            if (stopContinueOption.getText().equals("frezz Connection")) {
                stopContinueOption.setText("continue Connection");
            } else {
                stopContinueOption.setText("frezz Connection");
            }
        });

        JMenuItem cancel = new JMenuItem("cancel Connection");
        cancel.addActionListener((ActionEvent e) -> {
            getContentPane().remove(getContentPane());
            repaint();
            revalidate();
            pack();
        });

        JMenuItem reload = new JMenuItem("reload");
        reload.addActionListener((ActionEvent e) -> {
            int[] selectedChannels = getSelectedChannels(channelItems, config);
            XYSeries[] emptyInitArray = new XYSeries[selectedChannels.length];
            fillArray(emptyInitArray);

            setContentPane(barChart.init(emptyInitArray)); //TODO bug //FIXME its not working after a second init you can update anymore

            revalidate();
            repaint();
            pack();
            //
        });

        operations.add(reload);
        operations.add(stopContinueOption);
        operations.add(cancel);

        JButton plotButton = new JButton("Connect");

        plotButton.addActionListener((ActionEvent e) -> {
            AudChannel[] channels = new AudChannel[16];
            /*dataSetGenerator.createDataSet(null,null,config.startFrequency,config.endFrequency)
            barChart.init();*/
            int[] selectedChannels = getSelectedChannels(channelItems, config);
            XYSeries[] s = new XYSeries[1];
            XYSeries series = new XYSeries("test");
            series.add(1, 2);
            s[0] = series; //
            ChartPanel chartPanel = barChart.init(s);
            if (chartPanel != null) {
                setContentPane(chartPanel); //TODO bug
                revalidate();
                repaint();
                pack();
            } else {
                JOptionPane.showMessageDialog(null, "No valid data source", "An Error occurred", JOptionPane.ERROR_MESSAGE);
            }

            clientThread[0] = new Updater(barChart, new AudioClient(), config); //TODO make this correct
            clientThread[0].start();
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

    private int[] getSelectedChannels(JCheckBoxMenuItem[] channelItems, Config config) {
        ArrayList<Integer> listOfSelectedChannels = new ArrayList<>();
        for (int y = 0; y < config.channelNames.length - 1; y++) {
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
    private void fillArray(XYSeries[] array){
        for(int i = 0; i < array.length; i++){
            array[i] = new XYSeries(String.valueOf(i));
            array[i].add(0,0);
        }
    }
}
