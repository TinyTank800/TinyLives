package com.tinytank800.tinylives.Listeners;
import com.tinytank800.tinylives.Resources.Configs;
import com.tinytank800.tinylives.Resources.Logger;
import com.tinytank800.tinylives.Resources.PlayerUtils;
import com.tinytank800.tinylives.Resources.TabUtils;
import com.tinytank800.tinylives.Tinylives;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerQuitListener implements Listener {
    final Tinylives plugin;

    public PlayerQuitListener(Tinylives plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        Configs.Get("playerdata.yml").set("players." + playerUUID, Configs.playerConfigs.get(playerUUID));
        Configs.Save("playerdata.yml");
        Configs.playerConfigs.remove(playerUUID);

        //Logger.log("Removed player from list.", 2);
    }
}
