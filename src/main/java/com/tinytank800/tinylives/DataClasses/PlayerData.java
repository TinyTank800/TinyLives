package com.tinytank800.tinylives.DataClasses;

import com.tinytank800.tinylives.Resources.Configs;

import java.time.LocalDateTime;
import java.util.UUID;

public class PlayerData {
    private final String displayName;
    private final UUID player_id;
    private final int lives;
    private final String respawndate;

    public PlayerData(String displayName, UUID player_id, int lives, String respawnDate) {
        this.displayName = displayName;
        this.player_id = player_id;
        this.lives = lives;
        this.respawndate = respawnDate;
    }

    public static void save(PlayerData playerData) {
        if(!Configs.Get("playerdata.yml").contains("players." + playerData.player_id.toString())){
            Configs.Get("playerdata.yml").set("players." + playerData.player_id + ".display-name", playerData.displayName);
            Configs.Get("playerdata.yml").set("players." + playerData.player_id + ".lives", playerData.lives);
            Configs.Get("playerdata.yml").set("players." + playerData.player_id + ".respawn-date", playerData.respawndate);
        }
    }

    public static void update(PlayerData playerData) {
        if(!Configs.Get("playerdata.yml").contains("players." + playerData.player_id.toString())){
            Configs.Get("playerdata.yml").set("players." + playerData.player_id + ".display-name", playerData.displayName);
            Configs.Get("playerdata.yml").set("players." + playerData.player_id + ".lives", playerData.lives);
            Configs.Get("playerdata.yml").set("players." + playerData.player_id + ".respawn-date", playerData.respawndate);
        }
    }
}
