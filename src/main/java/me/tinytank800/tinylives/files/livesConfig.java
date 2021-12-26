package me.tinytank800.tinylives.files;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class livesConfig {

    private static File file;
    private static FileConfiguration customFile;

    public static boolean setup(){
        boolean newFile = false;
        file = new File(Bukkit.getServer().getPluginManager().getPlugin("tinylives").getDataFolder(), "lives.yml");
        if(!file.exists()){
            Bukkit.getLogger().info("Creating new example file!");
            try{
                file.createNewFile();
                newFile = true;
            } catch (IOException e){
                Bukkit.getLogger().warning("Failed to create file!");
                Bukkit.getLogger().warning(e.toString());
            }
        }

        customFile = YamlConfiguration.loadConfiguration(file);
        return newFile;
    }

    public static FileConfiguration get(){
        return customFile;
    }

    public static void save(){
        try{
            customFile.save(file);
        } catch (IOException e){
            Bukkit.getLogger().warning("Failed to save file!");
            Bukkit.getLogger().warning(e.toString());
        }
    }

    public static void reload(){
        customFile = YamlConfiguration.loadConfiguration(file);
    }

}
