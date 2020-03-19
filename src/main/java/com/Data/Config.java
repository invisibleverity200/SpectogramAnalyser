package com.Data;

import javax.json.*;
import javax.swing.*;
import java.io.*;
import java.util.ArrayList;

public class Config {
    public int startFrequency = 1000;
    public int endFrequency = 1000;
    public ArrayList<String> channelNames = new ArrayList<>(); //FIXME BUG: if server sents less packages than in config file some channels get the same name WTF
    public double voltageStepWidth = 0.01;
    public int port = 1337;
    public String hostname = "192.168.0.1";
    public int blockSize = 512;

    public Config() {
        readConfigFile();
    }

    public void writeConfigFile() {
        try {
            flushFile();
            OutputStream fileWriter = new FileOutputStream("config.json");
            JsonWriter jsonWriter = Json.createWriter(fileWriter);
            jsonWriter.writeObject(createJsonObject());
            jsonWriter.close();
        } catch (IOException e) {
            System.out.println("\u001B[31m" + "ERROR: " + e.getMessage());
        }
    }

    public void updateConfig(int startFrequency, int endFrequency, double voltageStepWidth, int port, String hostname) {
        this.startFrequency = startFrequency;
        this.endFrequency = endFrequency;
        this.voltageStepWidth = voltageStepWidth;
        this.port = port;
        this.hostname = hostname;
    }

    private void readConfigFile() {
        try {
            InputStream inputStream = new FileInputStream("config.json");
            JsonReader reader = Json.createReader(inputStream);

            JsonObject config = reader.readObject();

            this.startFrequency = config.getInt("StartFrequency");
            this.endFrequency = config.getInt("EndFrequency");
            this.voltageStepWidth = Double.parseDouble(config.getString("VoltageStepWidth"));
            this.port = config.getInt("Port");
            this.hostname = config.getString("Hostname");
            this.blockSize = config.getInt("BlockSize");

            JsonArray channelNames = config.getJsonArray("channelNames");

            for (JsonValue channelName : channelNames) {
                this.channelNames.add(channelName.toString());
                System.out.println(channelName.toString());
            }
            if (checkForSimilarNames()) {
                JOptionPane.showMessageDialog(null, "You have similar Channel names please correct that and restart the program", "An Error occurred", JOptionPane.ERROR_MESSAGE);
            }
        } catch (FileNotFoundException e) {
            System.out.println("\u001B[31m" + "ERROR: " + e.getMessage());
        }
    }

    private boolean checkForSimilarNames() {
        for (int i = 0; i < channelNames.size(); i++) {
            for (int y = i + 1; y < channelNames.size(); y++) {
                if (channelNames.get(i).equals(channelNames.get(y))) {
                    return true;
                }
            }
        }
        return false;
    }

    private JsonObject createJsonObject() {
        JsonArrayBuilder channelNames = Json.createArrayBuilder();
        int index = 0;
        for (String channelName : this.channelNames) {
            channelNames.add(index, channelName);
            index++;
        }
        return Json.createObjectBuilder()
                .add("StartFrequency", startFrequency)
                .add("EndFrequency", endFrequency)
                .add("VoltageStepWidth", String.valueOf(voltageStepWidth))
                .add("channelNames", channelNames)
                .add("Hostname", hostname)
                .add("Port", port)
                .add("BlockSize", blockSize)
                .build();
    }

    private void flushFile() throws IOException {
        FileWriter fwOb;
        fwOb = new FileWriter("config.json", false);
        PrintWriter pwOb = new PrintWriter(fwOb, false);
        pwOb.flush();
        pwOb.close();
        fwOb.close();
    }
}
