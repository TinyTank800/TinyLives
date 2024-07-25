package com.tinytank800.tinylives.Listeners;
import com.tinytank800.tinylives.Resources.Configs;
import com.tinytank800.tinylives.Resources.Logger;
import com.tinytank800.tinylives.Resources.Parser;
import com.tinytank800.tinylives.Resources.PlayerUtils;
import com.tinytank800.tinylives.Tinylives;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {
    final Tinylives plugin;

    public PlayerDeathListener(Tinylives plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = Bukkit.getPlayer(event.getEntity().getUniqueId());
        String message = event.getDeathMessage();
        String newMessage = Configs.Get("config.yml").getString("death-settings.death-message");
        if (player != null && message != null && newMessage != null) {
            message = message.replace(player.getName() + " ", "");
            newMessage = newMessage.replace("%death-message%", message);
            newMessage = Parser.format(player, newMessage);
            event.setDeathMessage(newMessage);
            Logger.fileLog(event.getDeathMessage(), player.getName());
            if(Configs.playerConfigs.get(player.getUniqueId()).getInt("lives") >= 1){
                PlayerUtils.SubtractLife(player, 1);
                PlayerUtils.CheckState(player);
            }
        }
    }
}
