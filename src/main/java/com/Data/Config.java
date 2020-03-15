package com.Data;

import javax.json.*;
import java.io.*;
import java.util.ArrayList;

public class Config {
    public int startFrequency = 1000;
    public int endFrequency = 1000;
    public ArrayList<String> channelNames = new ArrayList<>();
    public double voltageStepWidth = 0.01;
    int port = 1337;
    String hostname = "192.168.0.1";
    int blockSize = 512;

    //Start-End Frequency
    //Channel-Names
    //Delta-[V]
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateConfig(int startFrequency, int endFrequency, double voltageStepWidth, int port, String hostname) {
        this.startFrequency = startFrequency;
        this.endFrequency = endFrequency;
        this.voltageStepWidth = voltageStepWidth;
    }

    public void readConfigFile() {
        try {
            InputStream inputStream = new FileInputStream("config.json");
            JsonReader reader = Json.createReader(inputStream);

            JsonObject config = reader.readObject();

            this.startFrequency = config.getInt("StartFrequency");
            this.endFrequency = config.getInt("EndFrequency");
            this.voltageStepWidth = Double.valueOf(config.getString("VoltageStepWidth"));
            this.port = config.getInt("Port");
            this.hostname = config.getString("Hostname");
            this.blockSize = config.getInt("BlockSize");

            JsonArray channelNames = config.getJsonArray("channelNames");
            System.out.println(channelNames);
            for (JsonValue channelName : channelNames) {
                this.channelNames.add(channelName.toString());
                System.out.println(channelName.toString());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private JsonObject createJsonObject() {
       /* JsonArrayBuilder channelNames = Json.createArrayBuilder();
        for (String channelName : this.channelNames) {
            channelNames.add(channelName);
            System.out.println(channelName);
        }*/
        JsonObject config = Json.createObjectBuilder()
                .add("StartFrequency", startFrequency)
                .add("EndFrequency", endFrequency)
                .add("VoltageStepWidth", String.valueOf(voltageStepWidth))
                // .add("channelNames", channelNames)
                .add("Hostname", hostname)
                .add("Port", port)
                .add("BlockSize", blockSize)
                .build();
        return config;
    }

    private void flushFile() throws IOException {
        FileWriter fwOb = null;
        fwOb = new FileWriter("config.json", false);
        PrintWriter pwOb = new PrintWriter(fwOb, false);
        pwOb.flush();
        pwOb.close();
        fwOb.close();
    }
}
