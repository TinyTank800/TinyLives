package com.tinytank800.tinylives.Resources;

import com.tinytank800.tinylives.DataClasses.PlayerData;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class PlayerUtils {

    public static void InitPlayerData(Player player){
        String displayName = player.getDisplayName();
        UUID playerid = player.getUniqueId();
        int lives = Configs.Get("config.yml").getInt("life-settings.lives.default");
        if(Configs.Get("config.yml").getBoolean("life-settings.lives.random-lives.enabled")){
            int min = Configs.Get("config.yml").getInt("life-settings.lives.random-lives.min-lives");
            int max = Configs.Get("config.yml").getInt("life-settings.lives.random-lives.max-lives");
            lives = InitRandomLives(min, max);
        }
        LocalDateTime now = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        PlayerData playerData = new PlayerData(displayName, playerid, lives, now.format(formatter));
        PlayerData.save(playerData);

        Configs.playerConfigs.put(playerid,Configs.Get("playerdata.yml").getConfigurationSection("players." + playerid));
    }

    public static Boolean CheckPlayerPermission(Player player, String permission){
        return player.hasPermission(permission);
    }

    public static void SetDisplayName(Player player){
        if(Configs.Get("config.yml").getBoolean("display.chat.enabled")){
            player.setDisplayName(Parser.format(player, Objects.requireNonNull(Configs.Get("config.yml").getString("display.chat.format"))));
        }
    }

    public static void SubtractLife(Player player, Integer amount){
        int currentLives = Configs.Get("playerdata.yml").getInt("players." + player.getUniqueId() + ".lives");
        int newLives = currentLives - amount;

        if (newLives <= 0) {
            newLives = 0;
        }

        Configs.playerConfigs.get(player.getUniqueId()).set("lives", newLives);
        //Configs.playerData.set("players." + player.getUniqueId() + ".lives", newLives);
    }

    public static void AddLife(Player player, Integer amount){
        int currentLives = Configs.Get("playerdata.yml").getInt("players." + player.getUniqueId() + ".lives");
        int newLives = currentLives + amount;

        if (newLives > CheckMaxLives(player)) {
            newLives = CheckMaxLives(player);
        }

        Configs.playerConfigs.get(player.getUniqueId()).set("lives", newLives);
        //Configs.playerData.set("players." + player.getUniqueId() + ".lives", newLives);
    }

    public static void SetLives(Player player, Integer amount){
        if (amount > CheckMaxLives(player)) {
            amount = CheckMaxLives(player);
        }

        Configs.playerConfigs.get(player.getUniqueId()).set("lives", amount);
        //Configs.playerData.set("players." + player.getUniqueId() + ".lives", amount);
    }

    public static void Revive(Player player){
        if(IsDead(player)){
            PlayerUtils.SetLives(player, 1);
            PlayerUtils.SetDisplayName(player);
            TabUtils.SetTablist(player);
            PlayerUtils.LoadGamemode(player);
        }
    }

    public static void Respawn(Player player){
        if(IsDead(player)){
            PlayerUtils.SetLives(player, 3);
            PlayerUtils.SetDisplayName(player);
            TabUtils.SetTablist(player);
            PlayerUtils.LoadGamemode(player);
        }
    }

    public static Integer InitRandomLives(Integer min, Integer max){
        Random random = new Random();

        return random.nextInt(max - min + 1) + min;
    }

    public static int CheckMaxLives(Player player){
        int maxlives = Configs.Get("config.yml").getInt("life-settings.lives.max-lives");

        String permissionPrefix = "tinylives.maxlives.";

        for (PermissionAttachmentInfo attachmentInfo : player.getEffectivePermissions()) {
            if (attachmentInfo.getPermission().startsWith(permissionPrefix)) {
                String permission = attachmentInfo.getPermission();
                int permLives = Integer.parseInt(permission.substring(permission.lastIndexOf(".") + 1));
                if( permLives > maxlives){
                    maxlives = permLives;
                }
            }
        }

        return maxlives;
    }

    public static void SetTimer(Player player){
        if(Configs.Get("config.yml").getString("life-settings.reset.type").equalsIgnoreCase("all-death")){
            LocalDateTime respawnTime = LocalDateTime.now().plusSeconds(Configs.Get("config.yml").getInt("life-settings.reset.delay"));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            Configs.playerConfigs.get(player.getUniqueId()).set("respawn-date", respawnTime.format(formatter));
        }
    }

    public static void CheckState(Player player){
        SetDisplayName(player);

        if(IsDead(player)){
            SetTimer(player);

            if(Configs.Get("config.yml").getString("death-settings.dead.type").equalsIgnoreCase("ghost-players")){
                LoadGamemode(player);
                return;
            }

            if(Configs.Get("config.yml").getString("death-settings.dead.type").equalsIgnoreCase("deadman-world")){
                LoadGamemode(player);
                return;
            }

            if(Configs.Get("config.yml").getString("death-settings.dead.type").equalsIgnoreCase("kick-players")){
                player.kickPlayer(Parser.format(player,Configs.Get("config.yml").getString("death-settings.dead.message")));
                return;
            }
        } else {
            LoadGamemode(player);
        }
    }

    public static void SetGamemode(Player player, GameMode gamemode){
        player.setGameMode(gamemode);
    }

    public static void LoadGamemode(Player player){
        if(PlayerUtils.IsDead(player)){
            PlayerUtils.SetGamemode(player, GameMode.SPECTATOR);
        } else {
            if(!PlayerUtils.CheckPlayerPermission(player, "tinylives.gamemode.bypass") || !Configs.Get("config.yml").getBoolean("life-settings.respawns.gamemode-bypass.enabled")){
                PlayerUtils.SetGamemode(player, GameMode.SURVIVAL);
            }
        }
    }

    public static boolean IsDead(Player player){
        return Configs.Get("playerdata.yml").getInt("players." + player.getUniqueId() + ".lives") <= 0;
    }
}
