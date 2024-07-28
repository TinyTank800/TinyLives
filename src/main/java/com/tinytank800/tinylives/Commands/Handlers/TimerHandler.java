package com.tinytank800.tinylives.Commands.Handlers;

import com.tinytank800.tinylives.Resources.Configs;
import com.tinytank800.tinylives.Resources.Logger;
import com.tinytank800.tinylives.Resources.PlayerUtils;
import com.tinytank800.tinylives.Tinylives;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

public class TimerHandler {

    public static void StartChecks(){
        // Schedule the repeating task
        Tinylives.getInstance().getServer().getScheduler().scheduleSyncRepeatingTask(Tinylives.getInstance(), new Runnable() {
            @Override
            public void run() {

                if(Configs.Get("config.yml").getString("life-settings.reset.type").equalsIgnoreCase("all-death")) {
                    if (!Configs.playerConfigs.isEmpty()) {
                        for (UUID player : Configs.playerConfigs.keySet()) {
                            Player CPlayer = Tinylives.getInstance().getServer().getPlayer(player);
                            if(PlayerUtils.IsDead(CPlayer)) {
                                LocalDateTime now = LocalDateTime.now();
                                LocalDateTime respawnDate = LocalDateTime.parse(Configs.playerConfigs.get(player).getString("respawn-date"));
                                Duration duration = Duration.between(now, respawnDate);
                                if (duration.toSeconds() <= 0) {
                                    PlayerUtils.Respawn(CPlayer);
                                }
                            }
                        }
                    }
                }
            }
        }, 20L, 20L); // Runs every 1200 ticks (1 minute)
    }

}
