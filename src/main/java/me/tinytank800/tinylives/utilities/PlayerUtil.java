package me.tinytank800.tinylives.utilities;

import me.tinytank800.tinylives.files.customConfig;
import me.tinytank800.tinylives.files.livesConfig;
import me.tinytank800.tinylives.tinylives;
import org.bukkit.*;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.List;

public class PlayerUtil {

    public static void sendSound(Player player, String sound, int Pitch, int Volume){
        try {
            player.playSound(player.getLocation(), Sound.valueOf(sound), (float) Volume, (float) Pitch);
        } catch (Exception e){
            ChatUtil.console("There was an error playing a sound:", 1);
            ChatUtil.console(e.toString(), 1);
        }
    }

    public static void kickSPlayer(Player player){
        String message = ChatUtil.format(tinylives.getInstance().getConfig().getString("death-types.ban-players.kick-message"), player);
        player.kickPlayer(message);
    }

    public static void KillPlayer(Player player){
        ChatUtil.console((player.getName() + " Is being killed."), 0);

        customConfig.get().set("players." + player.getUniqueId().toString() + ".respawning", true);
        if(tinylives.getInstance().getConfig().getBoolean("life-settings.respawn-cooldown.enabled")){
            customConfig.get().set("players." + player.getUniqueId().toString() + ".cooldown", true);
            ChatUtil.console("Player on cooldown", 0);

            Bukkit.getScheduler().runTaskLater(tinylives.getInstance(), new Runnable() {
                @Override
                public void run() {
                    customConfig.get().set("players." + player.getUniqueId().toString() + ".cooldown", false);
                    ChatUtil.console("Player off cooldown", 0);
                }
            }, tinylives.getInstance().getConfig().getInt("life-settings.respawn-cooldown.time"));
        }
        //customConfig.save();
        //customConfig.reload();

        List<String> commands = livesConfig.get().getStringList("lives." + customConfig.get().getInt("players." + player.getUniqueId().toString() + ".lives") + ".commands");

        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();

        //if(commands != null){
        for (String command : commands) {
            String commandRun = ChatUtil.format(command, player);
            Bukkit.dispatchCommand(console, commandRun);
        }
        //}

        boolean UsedExtra = false;

        if(tinylives.getInstance().enabledExtraLives){
            if (customConfig.get().contains("players." + player.getUniqueId().toString() + ".extra-lives")) {
                if (customConfig.get().getInt("players." + player.getUniqueId().toString() + ".extra-lives") >= 1) {
                    customConfig.get().set("players." + player.getUniqueId().toString() + ".extra-lives", customConfig.get().getInt("players." + player.getUniqueId().toString() + ".extra-lives") - 1);
                    //customConfig.save();
                    //customConfig.reload();

                    UsedExtra = true;

                    if(tinylives.getInstance().getConfig().getBoolean("message-settings.titles.extra-life-title.enabled")) {
                        ChatUtil.NotifyPlayerTitle(player, tinylives.getInstance().getConfig().getString("message-settings.titles.extra-life-title.title"), tinylives.getInstance().getConfig().getString("message-settings.titles.extra-life-title.subTitle"), tinylives.getInstance().getConfig().getInt("message-settings.titles.extra-life-title.fadeIn"), tinylives.getInstance().getConfig().getInt("message-settings.titles.extra-life-title.stay"), tinylives.getInstance().getConfig().getInt("message-settings.titles.extra-life-title.fadeOut"));
                    }

                    if(tinylives.getInstance().getConfig().getBoolean("message-settings.messages.global-death.enabled")) {
                        ChatUtil.NotifyAllStringPlayer(tinylives.getInstance().getConfig().getString("message-settings.messages.global-death.message"),player);
                    }
                }
            }
        }

        if(!UsedExtra) {
            if (customConfig.get().getInt("players." + player.getUniqueId().toString() + ".lives") >= 2) { //Lives left
                if(!customConfig.get().getBoolean("players." + player.getUniqueId().toString() + ".endless")) {
                    customConfig.get().set("players." + player.getUniqueId().toString() + ".lives", (customConfig.get().getInt("players." + player.getUniqueId().toString() + ".lives") - 1));
                    //customConfig.save();
                    //customConfig.reload();
                }

                if(tinylives.getInstance().getConfig().getBoolean("message-settings.titles.death-title.enabled")) {
                    ChatUtil.NotifyPlayerTitle(player, tinylives.getInstance().getConfig().getString("message-settings.titles.death-title.title"), tinylives.getInstance().getConfig().getString("message-settings.titles.death-title.subTitle"), tinylives.getInstance().getConfig().getInt("message-settings.titles.death-title.fadeIn"), tinylives.getInstance().getConfig().getInt("message-settings.titles.death-title.stay"), tinylives.getInstance().getConfig().getInt("message-settings.titles.death-title.fadeOut"));
                }

                if(tinylives.getInstance().getConfig().getBoolean("message-settings.messages.global-death.enabled")) {
                    ChatUtil.NotifyAllStringPlayer(tinylives.getInstance().getConfig().getString("message-settings.messages.global-death.message"),player);
                }
            } else { //No lives left
                customConfig.get().set("players." + player.getUniqueId().toString() + ".lives", (customConfig.get().getInt("players." + player.getUniqueId().toString() + ".lives") - 1));
                //customConfig.save();
                //customConfig.reload();

                ChatUtil.console(tinylives.getInstance().addLifes + "", 0);
                ChatUtil.console(tinylives.getInstance().allDeathReset + "", 0);
                ChatUtil.console(tinylives.getInstance().globalReset + "", 0);

                if (tinylives.getInstance().addLifes) {
                    customConfig.get().set("players." + player.getUniqueId().toString() + ".dead", true);
                    //customConfig.save();
                    //customConfig.reload();
                } else if (tinylives.getInstance().allDeathReset) {
                    customConfig.get().set("players." + player.getUniqueId().toString() + ".dead", true);
                    customConfig.get().set("players." + player.getUniqueId().toString() + ".respawn-time", tinylives.getInstance().allDeathDelay);
                    //customConfig.save();
                    //customConfig.reload();
                } else if (tinylives.getInstance().globalReset) {
                    customConfig.get().set("players." + player.getUniqueId().toString() + ".dead", true);
                    //customConfig.save();
                    //customConfig.reload();
                } else {
                    ChatUtil.console("No mode is chosen?", 0);
                    customConfig.get().set("players." + player.getUniqueId().toString() + ".dead", true);
                    //customConfig.save();
                    //customConfig.reload();
                }

                if(tinylives.getInstance().getConfig().getBoolean("message-settings.titles.death-title.enabled")) {
                    ChatUtil.NotifyPlayerTitle(player, tinylives.getInstance().getConfig().getString("message-settings.titles.death-title.title"), tinylives.getInstance().getConfig().getString("message-settings.titles.death-title.subTitle"), tinylives.getInstance().getConfig().getInt("message-settings.titles.death-title.fadeIn"), tinylives.getInstance().getConfig().getInt("message-settings.titles.death-title.stay"), tinylives.getInstance().getConfig().getInt("message-settings.titles.death-title.fadeOut"));
                }

                if(tinylives.getInstance().getConfig().getBoolean("message-settings.messages.global-final-death.enabled")) {
                    ChatUtil.NotifyAllStringPlayer(tinylives.getInstance().getConfig().getString("message-settings.messages.global-final-death.message"),player);
                }
            }
        }

        ChangeGamemode(player, 1);
        //player.setHealth(0);
        player.spigot().respawn();

        SetDisplayName(player);

        if(tinylives.getInstance().getConfig().getBoolean("death-settings.enable-death-tp")){
            if(!customConfig.get().getBoolean("players." + player.getUniqueId().toString() + ".endless") || !tinylives.getInstance().getConfig().getBoolean("life-settings.enable-endless-lives")) {
                Location location = new Location(Bukkit.getWorld(tinylives.getInstance().defaultWorld), livesConfig.get().getInt("lives." + customConfig.get().getInt("players." + player.getUniqueId().toString() + ".lives") + ".tp.x"), livesConfig.get().getInt("lives." + customConfig.get().getInt("players." + player.getUniqueId().toString() + ".lives") + ".tp.y"), livesConfig.get().getInt("lives." + customConfig.get().getInt("players." + player.getUniqueId().toString() + ".lives") + ".tp.z"));
                player.teleport(location);
                ChatUtil.console("Teleporting player", 0);
            } else {
                Location location = new Location(Bukkit.getWorld(tinylives.getInstance().defaultWorld), livesConfig.get().getInt("lives.endless.tp.x"), livesConfig.get().getInt("lives.endless.tp.y"), livesConfig.get().getInt("lives.endless.tp.z"));
                player.teleport(location);
                ChatUtil.console("Teleporting Endless player", 0);
            }
        }

        if(tinylives.getInstance().perLifeRespawn){
            ChatUtil.console("Setting players perlife", 2);
            customConfig.get().set("players." + player.getUniqueId().toString() + ".per-life-respawn", tinylives.getInstance().getConfig().getInt("life-settings.per-life-respawn.time"));
            if (tinylives.getInstance().banPlayers) {
                kickSPlayer(player);
            } else if (tinylives.getInstance().enableDeadmans) {
                if (!CheckPlayerDeadWorld(player)) {
                    ChatUtil.NotifyPlayerString(ChatUtil.format(tinylives.getInstance().getConfig().getString("death-types.deadman-world.move-message"), player), player);
                    MoveWorld(player, 2);
                }
            } else if (tinylives.getInstance().ghostPlayers) {
                ChangeGamemode(player, 2);
            }
        }

        Bukkit.getScheduler().runTaskLater(tinylives.getInstance(), new Runnable() {
            @Override
            public void run() {
                customConfig.get().set("players." + player.getUniqueId().toString() + ".respawning", false);
                //customConfig.save();
                //customConfig.reload();

                if (customConfig.get().getInt("players." + player.getUniqueId().toString() + ".lives") <= 0) {
                    if (tinylives.getInstance().banPlayers) {
                        kickSPlayer(player);
                    } else if (tinylives.getInstance().enableDeadmans) {
                        if (!CheckPlayerDeadWorld(player)) {
                            ChatUtil.NotifyPlayerString(ChatUtil.format(tinylives.getInstance().getConfig().getString("death-types.deadman-world.move-message"), player), player);
                            MoveWorld(player, 2);
                        }
                    } else if (tinylives.getInstance().ghostPlayers) {
                        ChangeGamemode(player, 2);
                    }
                }
            }
        }, 5L);

        customConfig.save();
    }

    public static void ResetPlayer(Player player){
        ChatUtil.console((player.getName() + " Is being reset."), 0);

        customConfig.get().set("players." + player.getUniqueId().toString() + ".respawning", true);
        if(tinylives.getInstance().getConfig().getBoolean("life-settings.respawn-cooldown.enabled")){
            customConfig.get().set("players." + player.getUniqueId().toString() + ".cooldown", true);

            Bukkit.getScheduler().runTaskLater(tinylives.getInstance(), new Runnable() {
                @Override
                public void run() {
                    customConfig.get().set("players." + player.getUniqueId().toString() + ".cooldown", false);
                }
            }, tinylives.getInstance().getConfig().getInt("life-settings.respawn-cooldown.time"));
        }
        //customConfig.save();
        //customConfig.reload();

        ChangeGamemode(player, 1);
        //player.setHealth(0);
        player.spigot().respawn();

        if(CheckPlayerDeadWorld(player)){
            MoveWorld(player, 1);
        }

        Bukkit.getScheduler().runTaskLater(tinylives.getInstance(), new Runnable() {
            @Override
            public void run() {
                if(tinylives.getInstance().addLifes){
                    customConfig.get().set("players." + player.getUniqueId().toString() + ".respawning", false);
                    customConfig.get().set("players." + player.getUniqueId().toString() + ".dead", false);
                    //customConfig.get().set("players." + player.getUniqueId().toString() + ".lives", tinylives.getInstance().lives);
                    //customConfig.save();
                    //customConfig.reload();
                }
                else if(tinylives.getInstance().allDeathReset){
                    customConfig.get().set("players." + player.getUniqueId().toString() + ".respawning", false);
                    customConfig.get().set("players." + player.getUniqueId().toString() + ".dead", false);

                    if(tinylives.getInstance().getConfig().getBoolean("life-settings.random-lives.enabled")) {
                        int min = tinylives.getInstance().getConfig().getInt("life-settings.random-lives.min-lives");
                        int max = tinylives.getInstance().getConfig().getInt("life-settings.random-lives.max-lives");
                        int random_num = (int)Math.floor(Math.random()*(max-min+1)+min);

                        ChatUtil.console("Random lives for " + player.getName() + ": " + random_num, 0);

                        customConfig.get().set("players." + player.getUniqueId().toString() + ".lives", random_num);
                    } else {
                        customConfig.get().set("players." + player.getUniqueId().toString() + ".lives", tinylives.getInstance().getConfig().getInt("life-types.all-death.reset-lives"));
                    }

                    customConfig.get().set("players." + player.getUniqueId().toString() + ".respawn-time", tinylives.getInstance().allDeathDelay);
                    //customConfig.save();
                    //customConfig.reload();
                }
                else if(tinylives.getInstance().globalReset){
                    customConfig.get().set("players." + player.getUniqueId().toString() + ".current-reset", customConfig.get().getInt("reset-number"));
                    customConfig.get().set("players." + player.getUniqueId().toString() + ".respawning", false);
                    customConfig.get().set("players." + player.getUniqueId().toString() + ".dead", false);

                    if(tinylives.getInstance().getConfig().getBoolean("life-settings.random-lives.enabled")) {
                        int min = tinylives.getInstance().getConfig().getInt("life-settings.random-lives.min-lives");
                        int max = tinylives.getInstance().getConfig().getInt("life-settings.random-lives.max-lives");
                        int random_num = (int)Math.floor(Math.random()*(max-min+1)+min);

                        ChatUtil.console("Random lives for " + player.getName() + ": " + random_num, 0);

                        customConfig.get().set("players." + player.getUniqueId().toString() + ".lives", random_num);
                    } else {
                        customConfig.get().set("players." + player.getUniqueId().toString() + ".lives", tinylives.getInstance().getConfig().getInt("life-types.global-reset.reset-lives"));
                    }

                    customConfig.get().set("players." + player.getUniqueId().toString() + ".lives", tinylives.getInstance().getConfig().getInt("life-types.global-reset.reset-lives"));
                    //customConfig.save();
                    //customConfig.reload();
                } else {
                    customConfig.get().set("players." + player.getUniqueId().toString() + ".respawning", false);
                    customConfig.get().set("players." + player.getUniqueId().toString() + ".dead", false);

                    if(tinylives.getInstance().getConfig().getBoolean("life-settings.random-lives.enabled")) {
                        int min = tinylives.getInstance().getConfig().getInt("life-settings.random-lives.min-lives");
                        int max = tinylives.getInstance().getConfig().getInt("life-settings.random-lives.max-lives");
                        int random_num = (int)Math.floor(Math.random()*(max-min+1)+min);

                        ChatUtil.console("Random lives for " + player.getName() + ": " + random_num, 0);

                        customConfig.get().set("players." + player.getUniqueId().toString() + ".lives", random_num);
                    } else {
                        customConfig.get().set("players." + player.getUniqueId().toString() + ".lives", tinylives.getInstance().lives);
                    }

                    customConfig.get().set("players." + player.getUniqueId().toString() + ".respawn-time", tinylives.getInstance().allDeathDelay);
                    //customConfig.save();
                    //customConfig.reload();
                    ChatUtil.console("No mode is chosen?", 0);
                }

                SetDisplayName(player);

                if(tinylives.getInstance().getConfig().getBoolean("message-settings.messages.player-reset.enabled")) {
                    ChatUtil.NotifyPlayerBlock(tinylives.getInstance().getConfig().getStringList("message-settings.messages.player-reset.messages"), player);
                }

                if(tinylives.getInstance().getConfig().getBoolean("message-settings.titles.reset-title.enabled")) {
                    ChatUtil.NotifyPlayerTitle(player, tinylives.getInstance().getConfig().getString("message-settings.titles.reset-title.title"), tinylives.getInstance().getConfig().getString("message-settings.titles.reset-title.subTitle"), tinylives.getInstance().getConfig().getInt("message-settings.titles.reset-title.fadeIn"), tinylives.getInstance().getConfig().getInt("message-settings.titles.reset-title.stay"), tinylives.getInstance().getConfig().getInt("message-settings.titles.reset-title.fadeOut"));
                }
            }
        }, 5L);

        customConfig.save();
    }

    public static void respawnPlayer(Player player){
        if(CheckPlayerWorld(player)){
            if(customConfig.get().getBoolean("players." + player.getUniqueId().toString() + ".dead")){
                if(customConfig.get().getInt("players." + player.getUniqueId().toString() + ".lives") >= 1){
                    customConfig.get().set("players." + player.getUniqueId().toString() + ".respawning", true);
                    if(tinylives.getInstance().getConfig().getBoolean("life-settings.respawn-cooldown.enabled")){
                        customConfig.get().set("players." + player.getUniqueId().toString() + ".cooldown", true);

                        Bukkit.getScheduler().runTaskLater(tinylives.getInstance(), new Runnable() {
                            @Override
                            public void run() {
                                customConfig.get().set("players." + player.getUniqueId().toString() + ".cooldown", false);
                            }
                        }, tinylives.getInstance().getConfig().getInt("life-settings.respawn-cooldown.time"));
                    }

                    ChangeGamemode(player, 1);
                    player.spigot().respawn();

                    if(CheckPlayerDeadWorld(player)){
                        MoveWorld(player, 1);
                    }

                    customConfig.get().set("players." + player.getUniqueId().toString() + ".respawning", false);
                    customConfig.get().set("players." + player.getUniqueId().toString() + ".dead", false);
                    customConfig.save();

                    SetDisplayName(player);

                    if(tinylives.getInstance().getConfig().getBoolean("message-settings.messages.player-reset.enabled")) {
                        ChatUtil.NotifyPlayerBlock(tinylives.getInstance().getConfig().getStringList("message-settings.messages.player-reset.messages"), player);
                    }

                    if(tinylives.getInstance().getConfig().getBoolean("message-settings.titles.reset-title.enabled")) {
                        ChatUtil.NotifyPlayerTitle(player, tinylives.getInstance().getConfig().getString("message-settings.titles.reset-title.title"), tinylives.getInstance().getConfig().getString("message-settings.titles.reset-title.subTitle"), tinylives.getInstance().getConfig().getInt("message-settings.titles.reset-title.fadeIn"), tinylives.getInstance().getConfig().getInt("message-settings.titles.reset-title.stay"), tinylives.getInstance().getConfig().getInt("message-settings.titles.reset-title.fadeOut"));
                    }
                }
            }
        }
    }

    public static void MoveWorld(Player player, int PWorld){
        if (PWorld == 1) { //Move to main world
            World DefWorld = Bukkit.getWorld(tinylives.getInstance().defaultWorld);
            if(DefWorld == null){
                ChatUtil.console("DEFAULT WORLD WAS NOT FOUND!", 1);
            }
            ChatUtil.console((player.getName() + " Is being moved to the main world!"), 0);
            Location defSpawnLocation = DefWorld.getSpawnLocation();
            player.teleport(defSpawnLocation);
        } else if(PWorld == 2){ //Move to deadmans world
            World DeadMansW = Bukkit.getWorld(tinylives.getInstance().deadmansWorld);
            if(DeadMansW == null){
                ChatUtil.console("DEFAULT WORLD WAS NOT FOUND!", 1);
            }
            ChatUtil.console((player.getName() + " Is being moved to the deadmans world!"), 0);
            Location defSpawnLocation = DeadMansW.getSpawnLocation();
            player.teleport(defSpawnLocation);
        }
    }

    public static void ChangeGamemode(Player player, int PGamemode){
        if(PGamemode == 1){
            ChatUtil.console((player.getName() + " Has been set to survival"), 0);
            if(player.getGameMode() != GameMode.SURVIVAL){
                player.setGameMode(GameMode.SURVIVAL);
                return;
            }
            ChatUtil.console((player.getName() + " Is already in survival"), 0);
        }
        else if(PGamemode == 2) {
            ChatUtil.console((player.getName() + " Has been set to spectator"), 0);
            if(player.getGameMode() != GameMode.SPECTATOR){
                player.setGameMode(GameMode.SPECTATOR);
                return;
            }
            ChatUtil.console((player.getName() + " Is already in spectator"), 0);
        }
    }

    public static void SetDisplayName(Player player){
        if (!livesConfig.get().getBoolean("disable-life-prefixs")) {
            if(customConfig.get().getBoolean("players." + player.getUniqueId().toString() + ".endless")) {
                player.setDisplayName(ChatUtil.format(livesConfig.get().getString("lives.endless.prefix") + livesConfig.get().getString("lives.endless.color") + player.getName() + "&r", player));
            } else if (livesConfig.get().contains("lives." + customConfig.get().getInt("players." + player.getUniqueId().toString() + ".lives"))) {
                player.setDisplayName(ChatUtil.format(livesConfig.get().getString("lives." + customConfig.get().getInt("players." + player.getUniqueId().toString() + ".lives") + ".prefix") + livesConfig.get().getString("lives." + customConfig.get().getInt("players." + player.getUniqueId().toString() + ".lives") + ".color") + player.getName() + "&r", player));
            }
        }
    }

    public static Boolean CheckPlayerWorld(Player player){
        boolean correctWorld = false;
        String world = player.getWorld().getName().toLowerCase().toString();

        for(String w: tinylives.getInstance().enabledWorlds){
            if(w.toLowerCase().equals(world)){
                correctWorld = true;
            }
        }

        if(tinylives.getInstance().getConfig().getString("death-types.deadman-world.deadman-world-name").toLowerCase().equals(world)){
            correctWorld = true;
        }

        return correctWorld;
    }

    public static Boolean CheckPlayerDeadWorld(Player player){
        boolean correctWorld = false;
        String world = player.getWorld().getName().toLowerCase().toString();

        if(tinylives.getInstance().getConfig().getString("death-types.deadman-world.deadman-world-name").toLowerCase().equals(world)){
            correctWorld = true;
        }

        return correctWorld;
    }

    public static int getMaxLivesPermAmount(Player player) {
        String permissionPrefix = "tinylives.maxlives.";

        int highestMax = 0;
        for (PermissionAttachmentInfo attachmentInfo : player.getEffectivePermissions()) {
            if (attachmentInfo.getPermission().startsWith(permissionPrefix)) {
                String permission = attachmentInfo.getPermission();
                int permLives = Integer.parseInt(permission.substring(permission.lastIndexOf(".") + 1));
                if( permLives > highestMax){
                    highestMax = permLives;
                }
            }
        }

        if(highestMax != 0){
            return highestMax;
        }

        return tinylives.getInstance().lives;
    }

    public static void checkMaxLivesAmount(Player player){
        int permAmount = getMaxLivesPermAmount(player);
        int currentAmount = 0;
        if(customConfig.get().contains("players." + player.getUniqueId().toString() + ".max-lives")){
            currentAmount = customConfig.get().getInt("players." + player.getUniqueId().toString() + ".max-lives");
        }

        if(currentAmount < permAmount){
            customConfig.get().set("players." + player.getUniqueId().toString() + ".max-lives", permAmount);
        }
    }

    public static void checkMaxExtraLivesAmount(Player player){
        int permAmount = getMaxExtraLivesPermAmount(player);
        int currentAmount = 0;
        if(customConfig.get().contains("players." + player.getUniqueId().toString() + ".max-extra-lives")){
            currentAmount = customConfig.get().getInt("players." + player.getUniqueId().toString() + ".max-extra-lives");
        }

        if(currentAmount < permAmount){
            customConfig.get().set("players." + player.getUniqueId().toString() + ".max-extra-lives", permAmount);
        }
    }

    public static boolean getEndless(Player player) {
        String permissionPrefix = "tinylives.endless";
        if(!tinylives.getInstance().getConfig().contains("life-settings.enable-endless-lives")){
            tinylives.getInstance().getConfig().set("life-settings.enable-endless-lives", false);
        }

        if(tinylives.getInstance().getConfig().getBoolean("life-settings.enable-endless-lives")){
            return player.hasPermission(permissionPrefix);
        } else {
            return false;
        }
    }

    public static int getMaxExtraLivesPermAmount(Player player) {
        String permissionPrefix = "tinylives.maxextralives.";

        int highestMax = 0;
        for (PermissionAttachmentInfo attachmentInfo : player.getEffectivePermissions()) {
            if (attachmentInfo.getPermission().startsWith(permissionPrefix)) {
                String permission = attachmentInfo.getPermission();
                int permLives = Integer.parseInt(permission.substring(permission.lastIndexOf(".") + 1));
                if( permLives > highestMax){
                    highestMax = permLives;
                }
            }
        }

        if(highestMax != 0){
            return highestMax;
        }

        return 0;
    }

    public static void PlayerAssassinCheck(Player player) {
        if (customConfig.get().getBoolean("assassin.enabled") && customConfig.get().getBoolean("assassin.player.enabled")) {
            if (!customConfig.get().getBoolean("players." + player.getUniqueId().toString() + ".IsAssassin") && customConfig.get().getInt("players." + player.getUniqueId().toString() + ".AssassinCooldown") > 0) {
                int min = 0;
                int max = 100;

                //Generate random int value from 50 to 100
                int random_int = (int) Math.floor(Math.random() * (max - min + 1) + min);

                if (random_int <= tinylives.getInstance().assassinPlayerChance) {
                    ChatUtil.console(player.getName() + " was chosen as an assassin.", 0);

                    customConfig.get().set("players." + player.getUniqueId().toString() + ".IsAssassin", true);
                    customConfig.get().set("players." + player.getUniqueId().toString() + ".AssassinTime", tinylives.getInstance().assassinPlayerTime);
                    customConfig.get().set("players." + player.getUniqueId().toString() + ".AssassinCooldown", tinylives.getInstance().assassinPlayerCooldown);

                    ChatUtil.NotifyPlayerString(tinylives.getInstance().getConfig().getString("assassin.titles.chosen.message"), player);
                    if (tinylives.getInstance().getConfig().getBoolean("assassin.titles.chosen.sound.enabled")) {
                        PlayerUtil.sendSound(player, tinylives.getInstance().getConfig().getString("assassin.titles.chosen.sound.sound"), tinylives.getInstance().getConfig().getInt("assassin.titles.chosen.sound.pitch"), tinylives.getInstance().getConfig().getInt("assassin.titles.chosen.sound.volume"));
                    }
                    if (tinylives.getInstance().getConfig().getBoolean("assassin.titles.chosen.title.enabled")) {
                        ChatUtil.NotifyPlayerTitle(player, tinylives.getInstance().getConfig().getString("assassin.titles.chosen.title.title"), tinylives.getInstance().getConfig().getString("assassin.titles.chosen.title.subTitle"), tinylives.getInstance().getConfig().getInt("assassin.titles.chosen.title.fadeIn"), tinylives.getInstance().getConfig().getInt("assassin.titles.chosen.title.stay"), tinylives.getInstance().getConfig().getInt("assassin.titles.chosen.title.fadeOut"));
                    }
                } else {
                    if (tinylives.getInstance().getConfig().getBoolean("assassin.titles.not-chosen.title.enabled")) {
                        ChatUtil.NotifyPlayerString(tinylives.getInstance().getConfig().getString("assassin.titles.not-chosen.message"), player);
                        if (tinylives.getInstance().getConfig().getBoolean("assassin.titles.not-chosen.sound.enabled")) {
                            PlayerUtil.sendSound(player, tinylives.getInstance().getConfig().getString("assassin.titles.not-chosen.sound.sound"), tinylives.getInstance().getConfig().getInt("assassin.titles.not-chosen.sound.pitch"), tinylives.getInstance().getConfig().getInt("assassin.titles.not-chosen.sound.volume"));
                        }
                        ChatUtil.NotifyPlayerTitle(player, tinylives.getInstance().getConfig().getString("assassin.titles.not-chosen.title.title"), tinylives.getInstance().getConfig().getString("assassin.titles.not-chosen.title.subTitle"), tinylives.getInstance().getConfig().getInt("assassin.titles.not-chosen.title.fadeIn"), tinylives.getInstance().getConfig().getInt("assassin.titles.not-chosen.title.stay"), tinylives.getInstance().getConfig().getInt("assassin.titles.not-chosen.title.fadeOut"));
                    }
                }
            }
        }
    }

    public static void PlayerJoin(Player player){
        //customConfig.reload();

        //SQL player entry
        if(tinylives.getInstance().enableSQL && tinylives.getInstance().SQL.isConnected()) {
            tinylives.getInstance().data.createPlayer(player);
        }

        if (customConfig.get().getConfigurationSection("players." + player.getUniqueId().toString()) == null) {
            if(tinylives.getInstance().getConfig().getBoolean("life-settings.random-lives.enabled")) {
                int min = tinylives.getInstance().getConfig().getInt("life-settings.random-lives.min-lives");
                int max = tinylives.getInstance().getConfig().getInt("life-settings.random-lives.max-lives");
                int random_num = (int)Math.floor(Math.random()*(max-min+1)+min);

                ChatUtil.console("Random lives for " + player.getName() + ": " + random_num, 0);

                customConfig.get().set("players." + player.getUniqueId().toString() + ".lives", random_num);
            } else {
                customConfig.get().set("players." + player.getUniqueId().toString() + ".lives", tinylives.getInstance().lives);
            }

            customConfig.get().set("players." + player.getUniqueId().toString() + ".playername", player.getName());
            customConfig.get().set("players." + player.getUniqueId().toString() + ".current-reset", tinylives.getInstance().resetNumber);
            customConfig.get().set("players." + player.getUniqueId().toString() + ".dead", false);
            customConfig.get().set("players." + player.getUniqueId().toString() + ".respawning", false);
            customConfig.get().set("players." + player.getUniqueId().toString() + ".startedpvp", false);
            customConfig.get().set("players." + player.getUniqueId().toString() + ".respawn-time", 1200);
            customConfig.get().set("players." + player.getUniqueId().toString() + ".endless", getEndless(player));
            checkMaxExtraLivesAmount(player);
            checkMaxLivesAmount(player);
            customConfig.get().set("players." + player.getUniqueId().toString() + ".cooldown", false);
            customConfig.get().set("players." + player.getUniqueId().toString() + ".InCombat", false);
            customConfig.get().set("players." + player.getUniqueId().toString() + ".IsAssassin", false);
            customConfig.get().set("players." + player.getUniqueId().toString() + ".AssassinTime", tinylives.getInstance().assassinPlayerTime);
            customConfig.get().set("players." + player.getUniqueId().toString() + ".AssassinCooldown", tinylives.getInstance().assassinPlayerCooldown);
            customConfig.get().set("players." + player.getUniqueId().toString() + ".per-life-respawn", 7200);
            //customConfig.save();
            //customConfig.reload();

            SetDisplayName(player);

            if(tinylives.getInstance().getConfig().getBoolean("message-settings.messages.player-first-join.enabled")) {
                ChatUtil.NotifyPlayerBlock(tinylives.getInstance().getConfig().getStringList("message-settings.messages.player-first-join.messages"), player);
            }

            if(tinylives.getInstance().getConfig().getBoolean("message-settings.titles.welcome-title.enabled")) {
                ChatUtil.NotifyPlayerTitle(player, tinylives.getInstance().getConfig().getString("message-settings.titles.welcome-title.title"), tinylives.getInstance().getConfig().getString("message-settings.titles.welcome-title.subTitle"), tinylives.getInstance().getConfig().getInt("message-settings.titles.welcome-title.fadeIn"), tinylives.getInstance().getConfig().getInt("message-settings.titles.welcome-title.stay"), tinylives.getInstance().getConfig().getInt("message-settings.titles.welcome-title.fadeOut"));
            }
        } else { // Player exists
            customConfig.get().set("players." + player.getUniqueId().toString() + ".playername", player.getName());
            checkMaxLivesAmount(player);
            checkMaxExtraLivesAmount(player);
            customConfig.get().set("players." + player.getUniqueId().toString() + ".endless", getEndless(player));
            customConfig.get().set("players." + player.getUniqueId().toString() + ".cooldown", false);
            //customConfig.save();
            //customConfig.reload();

            SetDisplayName(player);

            if (customConfig.get().getBoolean("players." + player.getUniqueId().toString() + ".dead")) {
                if (customConfig.get().getInt("players." + player.getUniqueId().toString() + ".lives") >= 1) {
                    ResetPlayer(player);
                } else {
                    if(tinylives.getInstance().allDeathReset){
                        if(customConfig.get().getInt("players." + player.getUniqueId().toString() + ".respawn-time") <= 0){
                            ResetPlayer(player);
                            return;
                        }
                    }

                    if (tinylives.getInstance().banPlayers) {
                        kickSPlayer(player);
                    } else if (tinylives.getInstance().enableDeadmans) {
                        if (!CheckPlayerDeadWorld(player)) {
                            MoveWorld(player, 2);
                        }
                    } else if (tinylives.getInstance().ghostPlayers) {
                        ChangeGamemode(player, 2);
                    }
                }
            }

            if(tinylives.getInstance().perLifeRespawn){
                if(customConfig.get().getInt("players." + player.getUniqueId().toString() + ".per-life-respawn") <= 0){
                    ChatUtil.console("Respawning per life player", 2);
                    ChangeGamemode(player, 1);
                    player.spigot().respawn();
                    return;
                } else {
                    ChatUtil.console("Player with perlife joined", 2);
                    if (tinylives.getInstance().banPlayers) {
                        kickSPlayer(player);
                    } else if (tinylives.getInstance().enableDeadmans) {
                        if (!CheckPlayerDeadWorld(player)) {
                            MoveWorld(player, 2);
                        }
                    } else if (tinylives.getInstance().ghostPlayers) {
                        ChangeGamemode(player, 2);
                    }
                }
            }
        }

        PlayerAssassinCheck(player);

        customConfig.save();
        customConfig.reload();
    }
}
