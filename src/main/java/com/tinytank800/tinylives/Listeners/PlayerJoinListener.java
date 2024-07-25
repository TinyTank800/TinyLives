package com.tinytank800.tinylives.Listeners;
import com.tinytank800.tinylives.Resources.Configs;
import com.tinytank800.tinylives.Resources.PlayerUtils;
import com.tinytank800.tinylives.Resources.TabUtils;
import com.tinytank800.tinylives.Tinylives;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    final Tinylives plugin;

    public PlayerJoinListener(Tinylives plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerUtils.InitPlayerData(player);
        PlayerUtils.SetDisplayName(player);
        TabUtils.SetTablist(player);
        PlayerUtils.LoadGamemode(player);

        //Init player displayname and worlds and gamemodes.
    }
}
