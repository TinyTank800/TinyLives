package com.tinytank800.tinylives.Resources;

import com.tinytank800.tinylives.Tinylives;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class TabUtils {

    public static void SetTabDisplayName(Player player){
        if(Configs.Get("config.yml").getBoolean("display.player-tab.enabled")){
            player.setPlayerListName(Parser.format(player, Objects.requireNonNull(Configs.Get("config.yml").getString("display.player-tab.format"))));
        }
    }

    public static void SetTablist(Player player){
        if(Configs.Get("config.yml").getBoolean("display.tablist.enabled")){
            player.setPlayerListHeaderFooter(Parser.format(player, Objects.requireNonNull(Configs.Get("config.yml").getString("display.tablist.header"))), Parser.format(player, Objects.requireNonNull(Configs.Get("config.yml").getString("display.tablist.footer"))));
        }
    }

    private static void refreshTabList() {
        for (Player player : Tinylives.getInstance().getServer().getOnlinePlayers()) {
            player.setPlayerListHeaderFooter(Parser.format(player, Objects.requireNonNull(Configs.Get("config.yml").getString("display.tablist.header"))), Parser.format(player, Objects.requireNonNull(Configs.Get("config.yml").getString("display.tablist.footer"))));
        }
    }

    private static void refreshPlayerList() {
        for (Player player : Tinylives.getInstance().getServer().getOnlinePlayers()) {
            SetTabDisplayName(player);
        }
    }

    public static void startRefreshTabList() {
        if(Configs.Get("config.yml").getBoolean("display.tablist.enabled") && Configs.Get("config.yml").getInt("display.tablist.refresh-time") > 0){
            int interval = Configs.Get("config.yml").getInt("display.tablist.refresh-time") * 20; // seconds * ticks
            new BukkitRunnable() {
                @Override
                public void run() {
                    refreshTabList();
                    Logger.log(Logger.LogLevel.VERBOSE,"Reloaded tablist.");
                }
            }.runTaskTimer(Tinylives.getInstance(), 0, interval);
        }

        if(Configs.Get("config.yml").getBoolean("display.player-tab.enabled") && Configs.Get("config.yml").getInt("display.player-tab.refresh-time") > 0){
            int interval = Configs.Get("config.yml").getInt("display.player-tab.refresh-time") * 20; // seconds * ticks
            new BukkitRunnable() {
                @Override
                public void run() {
                    refreshPlayerList();
                    Logger.log(Logger.LogLevel.VERBOSE,"Reloaded player list.");
                }
            }.runTaskTimer(Tinylives.getInstance(), 0, interval);
        }
    }
}
