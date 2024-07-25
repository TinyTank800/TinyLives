package com.tinytank800.tinylives.Listeners;
import com.tinytank800.tinylives.Resources.Configs;
import com.tinytank800.tinylives.Resources.Parser;
import com.tinytank800.tinylives.Resources.PlayerUtils;
import com.tinytank800.tinylives.Tinylives;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerRespawnListener implements Listener {
    final Tinylives plugin;

    public PlayerRespawnListener(Tinylives plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        PlayerUtils.SetDisplayName(player);
        PlayerUtils.LoadGamemode(player);
        if(PlayerUtils.IsDead(player)){
            player.sendMessage(Parser.format(player, Configs.Get("config.yml").getString("death-settings.dead.message")));
        }
    }
}
