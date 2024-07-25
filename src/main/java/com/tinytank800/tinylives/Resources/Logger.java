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
        NONE, // Base level debug
        DEBUG, // Minor debug
        VERBOSE, // In-depth debug
        SEVERE // URGENT
    }

    public static void log(LogLevel level, String input){
        switch (level) {
            case NONE:
                // This is basic plugin info like start and stop and is always shown.
                Tinylives.getInstance().getLogger().info(input);
                break;
            case DEBUG:
                // If debug is set to debug or verbose show this info.
                if (Configs.Get("config.yml").getString("logging.debug").equalsIgnoreCase("debug") || Configs.Get("config.yml").getString("logging.debug").equalsIgnoreCase("verbose")) {
                    Tinylives.getInstance().getLogger().info(input);
                }
                break;
            case VERBOSE:
                // If debug is verbose show everything.
                if (Configs.Get("config.yml").getString("logging.debug").equalsIgnoreCase("verbose")) {
                    Tinylives.getInstance().getLogger().info(input);
                }
                break;
            case SEVERE:
                // Errors or needed info goes here and is shown as a warning.
                Tinylives.getInstance().getLogger().warning(input);
                break;
        }
    }
    
    /*
        Todo - Possibly change the file saving for text to be closer to the new system.
        @author - TinyTank800
        @date - 7/24/2024
        @time - 8:13 PM
         */

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
