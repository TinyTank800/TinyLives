package me.tinytank800.tinylives.files;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class customConfig {

    private static File file;
    private static FileConfiguration customFile;

    public static void setup(){
        file = new File(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin("tinylives")).getDataFolder(), "players.yml");
        if(!file.exists()){
            try{
                file.createNewFile();
            } catch (IOException e){
                Bukkit.getLogger().warning("Failed to create file!");
                Bukkit.getLogger().warning(e.toString());
            }
        }

        customFile = YamlConfiguration.loadConfiguration(file);
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
