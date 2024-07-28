package com.tinytank800.tinylives.Resources;

import com.tinytank800.tinylives.Tinylives;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Configs {

    //This holds all online players to allow for quick time/respawn checks
    public static HashMap<UUID, ConfigurationSection> playerConfigs = new HashMap<>();
    public static HashMap<String, FileConfiguration> Config = new HashMap<>();
    private static final List<String> configs = List.of("config.yml", "playerdata.yml", "lives.yml", "lang.yml");

    public static void setup() {
        for(String config : configs){
            File file = new File(Tinylives.getInstance().getDataFolder(), config);
            if (!file.exists()) {
                InputStream defaultConfigStream = Tinylives.getInstance().getResource(config);
                if (defaultConfigStream != null) {
                    if(!Config.containsKey(config)){
                        Config.put(config,YamlConfiguration.loadConfiguration(new InputStreamReader(defaultConfigStream)));
                    }
                }

                save(Config.get(config), config);
            } else {
                if(!Config.containsKey(config)){
                    Config.put(config,YamlConfiguration.loadConfiguration(file));
                }
            }

            update(config);
        }
    }

    private static void update(String filename){
        FileConfiguration defaultConfig = loadResource(filename);
        FileConfiguration existingConfig = load(filename);

        if (defaultConfig != null && existingConfig != null) {
            for (String key : defaultConfig.getKeys(true)) {
                if (!existingConfig.contains(key)) {
                    Get(filename).set(key, defaultConfig.get(key));
                }
            }

            Save(filename);
            return;
        }

        Logger.log(Logger.LogLevel.SEVERE, "Failed to update config for " + filename);
    }

    private static boolean save(FileConfiguration config, String filename){
        try {
            config.save(new File(Tinylives.getInstance().getDataFolder(), filename));
            return true;
        } catch (IOException e){
            Logger.log(Logger.LogLevel.SEVERE, "Failed to save config for " + filename + ". Error: " + e);
            return false;
        }
    }

    private static FileConfiguration load(String filename){
        File file = new File(Tinylives.getInstance().getDataFolder(), filename);
        if (!file.exists()) {
            InputStream defaultConfigStream = Tinylives.getInstance().getResource(filename);
            if (defaultConfigStream != null) {
                Config.put(filename,YamlConfiguration.loadConfiguration(new InputStreamReader(defaultConfigStream)));
                return Config.get(filename);
            }
        } else {
            return Config.replace(filename,YamlConfiguration.loadConfiguration(file));
        }

        return null;
    }

    private static FileConfiguration loadResource(String filename){
        InputStream defaultConfigStream = Tinylives.getInstance().getResource(filename);
        if (defaultConfigStream != null) {
            return YamlConfiguration.loadConfiguration(new InputStreamReader(defaultConfigStream));
        }

        return null;
    }

    private static boolean reload(String filename){
        return (load(filename) != null);
    }

    private static FileConfiguration get(String filename){
        return Config.get(filename);
    }

    public static FileConfiguration Get(String filename){
        return get(filename);
    }

    public static String[] listConfigs(){
        return configs.toArray(new String[0]);
    }

    public static boolean Save(String filename){
        if(filename.equalsIgnoreCase("all")){
            for(String config : configs){
                save(Config.get(config), config);
            }

            return true;
        }

        return save(Config.get(filename), filename);
    }

    public static boolean Reload(String filename){
        if(filename.equalsIgnoreCase("all")){
            for(String config : configs){
                reload(config);
            }

            return true;
        }

        return reload(filename);
    }
}
