package com.tinytank800.tinylives.Resources;

import com.tinytank800.tinylives.Tinylives;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

    private static File file;

    public static void startLog(String input){
        Tinylives.getInstance().getLogger().info(input);
    }

    public enum LogLevel {
        DEBUG, // Base level debug
        VERBOSE, // In-depth debug
        SEVERE // URGENT
    }

    public static void log(LogLevel level, String input){
        switch (level) {
            case DEBUG:
                if (Configs.Get("config.yml").getString("logging.debug").equalsIgnoreCase("debug")) {
                    Tinylives.getInstance().getLogger().info(input);
                }
                break;
            case VERBOSE:
                if (Configs.Get("config.yml").getString("logging.debug").equalsIgnoreCase("verbose")) {
                    Tinylives.getInstance().getLogger().info(input);
                }
                break;
            case SEVERE:
                Tinylives.getInstance().getLogger().warning(input);
                break;
        }
    }

    public static void fileLog(String deathType, String name){

        if(Configs.Get("config.yml").getBoolean("logging.deaths")){
            setup();

            formatMessage(deathType, name);
        }

    }

    private static void addMessage(String message){
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
            bw.append(message);
            bw.close();
        } catch (IOException e) {
            log(LogLevel.SEVERE ,"FAILED TO LOG DEATH! ERROR: " + e);
            e.printStackTrace();
        }
    }

    private static void setup(){
        file = new File(Tinylives.getInstance().getDataFolder() + "/deathLog.txt");

        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                log(LogLevel.SEVERE ,"FAILED TO CREATE DEATH LOG FILE! ERROR: " + e);
                e.printStackTrace();
            }
        }
    }

    private static void formatMessage(String deathType, String name){
        DateFormat dateFormat = new SimpleDateFormat("yy/dd/MM HH:mm:ss");
        String date = dateFormat.format(new Date());
        String finalMessage = date + " - "+ name + ": " + deathType + "\n";
        addMessage(finalMessage);
    }

}
