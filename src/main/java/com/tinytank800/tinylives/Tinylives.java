package com.tinytank800.tinylives;

import com.tinytank800.tinylives.Commands.Handlers.TimerHandler;
import com.tinytank800.tinylives.Commands.TabExecutors.TinyLivesTabExecutor;
import com.tinytank800.tinylives.Commands.TinyLivesCommand;
import com.tinytank800.tinylives.Listeners.PlayerQuitListener;
import com.tinytank800.tinylives.Listeners.PlayerRespawnListener;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import com.tinytank800.tinylives.Listeners.PlayerDeathListener;
import com.tinytank800.tinylives.Listeners.PlayerJoinListener;
import com.tinytank800.tinylives.Resources.*;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class Tinylives extends JavaPlugin implements Listener {

    private static Tinylives instance;
    private static Economy econ = null;

    public static HashMap<String, Boolean> hooks = new HashMap<>();

    @Override
    public void onEnable() {

        setInstance(this);

        try {
            Configs.setup();
        } catch (Exception e) {
            Logger.startLog("FAILED TO LOAD/SETUP FILES! ERROR:" + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // I DONT THINK THIS IS NEEDED. JUST CHECK ON EVERY LOAD.
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new SpigotExpansion().register();
            hooks.put("placeholderapi", true);
            Logger.log(Logger.LogLevel.DEBUG,"PlaceholderAPI hook found!");
        } else {
            hooks.put("placeholderapi", false);
            Logger.log(Logger.LogLevel.DEBUG,"PlaceholderAPI hook was not found.");
        }

        if (setupEconomy()) {
            hooks.put("vault", true);
            Logger.log(Logger.LogLevel.DEBUG,"Vault hook found!");
        } else {
            hooks.put("vault", false);
            Logger.log(Logger.LogLevel.DEBUG,"Vault hook was not found.");
        }

        new UpdateChecker(this, 92276).getVersion(version -> {
            if (this.getDescription().getVersion().equalsIgnoreCase(version) || this.getDescription().getVersion().contains("-DEV")) {
                getLogger().info("You are running the latest version.");
            } else {
                getLogger().info("There is a new update available! MAKE SURE TO READ CHANGES WHEN UPDATING! Update at https://www.spigotmc.org/resources/tiny-lives.92276/");
            }
        });

        //int pluginId = 18125; // <-- Replace with the id of your plugin!
        //Metrics metrics = new Metrics(this, pluginId);

        Objects.requireNonNull(getCommand("tinylives")).setExecutor(new TinyLivesCommand(this));
        Objects.requireNonNull(getCommand("tinylives")).setTabCompleter(new TinyLivesTabExecutor());

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerRespawnListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);

        TabUtils.startRefreshTabList();

        TimerHandler.StartChecks();

        Logger.log(Logger.LogLevel.NONE,"Tiny Lives has been enabled!");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }

    public static Economy getEconomy() {
        return econ;
    }

    public static Tinylives getInstance() {
        return instance;
    }

    private static void setInstance(Tinylives instance) {
        Tinylives.instance = instance;
    }

    public static Boolean getHook(String hook){
        return hooks.getOrDefault(hook, false);
    }

    @Override
    public void onDisable() {
        //Save any data inside the currently online players
        for (UUID playerUUID : Configs.playerConfigs.keySet()) {
            Configs.Get("playerdata.yml").set("players." + playerUUID.toString(), Configs.playerConfigs.get(playerUUID));
        }

        Configs.Save("all");
        Logger.log(Logger.LogLevel.DEBUG,"Tiny Lives has been shut down correctly.");
    }
}
