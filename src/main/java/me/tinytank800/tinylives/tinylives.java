package me.tinytank800.tinylives;

import me.tinytank800.tinylives.files.SpigotExpansion;
import me.tinytank800.tinylives.files.customConfig;
import me.tinytank800.tinylives.files.livesConfig;
import me.tinytank800.tinylives.utilities.*;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public final class tinylives extends JavaPlugin implements CommandExecutor, Listener {

    //NOTES
    // Try to add countdown for choosing.
    // Add reminder timer for assassins.
    // Log player deaths/life losses.
    // Limited pvp kills per player
    // Clean up configs a bit.
    // REWORK time left placeholders

    // Add sounds for each announcement and thing like assassins - Done
    // Make kill message for when someone dies to an assassin. - Done
    // Add per life death cooldowns. - Done
    // Add player based assassin - done

    //tinylives plugin;

    public int debug = 0;
    public long ScheduleDelay = 1200L; //Set to 1200 after testing
    public boolean disableDelay = false;
    public int resetNumber = 0;
    public String prefix = "";
    public String PPrefix = "&e[&aTiny Lives&e] ";
    public List<String> enabledWorlds = null;
    public int lives = 3;
    public boolean centeredText = true;
    public boolean disabledPrefix = false;
    public boolean banPlayers = false;
    public boolean enableDeadmans = false;
    public String deadmansWorld = "";
    public String defaultWorld = "";
    public boolean enabledExtraLives = false;
    public int extraLives = 0;
    public boolean ghostPlayers = true;

    public boolean pvpOnlyDeaths = false;
    public boolean disablePvpDeaths = false;

    public boolean addLifes = false;
    public int addLifeDelay = 1728000;
    public int addlifeCurrentDelay = 0;

    public boolean assassin = false;
    public boolean assassinGlobal = false;
    public int assassinGlobalTimer = 576000;
    public int assassinGlobalCooldown = 1152000;
    public int assassinGlobalCount = 1;
    public boolean assassinPlayer = false;
    public int assassinPlayerCooldown = 1152000;
    public int assassinPlayerTime = 288000;
    public int assassinPlayerChance = 2;

    public boolean allDeathReset = false;
    public int allDeathDelay = 17280000;

    public boolean globalReset= false;
    public int delay = 51840000;
    public int currentDelay = 0;

    public boolean perLifeRespawn = false;
    public int perLifeRespawnTime = 72000;

    private static tinylives instance;

    public boolean PAPIintall = false;

    public MySQL SQL;
    public SQLGetter data;
    public boolean enableSQL = false;

    @Override
    public void onEnable() {

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            /*
             * We register the EventListener here, when PlaceholderAPI is installed.
             * Since all events are in the main class (this class), we simply use "this"
             */
            new SpigotExpansion().register();
            Bukkit.getPluginManager().registerEvents(this, this);
            PAPIintall = true;
        } else {
            /*
             * We inform about the fact that PlaceholderAPI isn't installed and then
             * disable this plugin to prevent issues.
             */
            getLogger().warning("Could not find PlaceholderAPI! This plugin is required for placeholder and message usage! The plugin will still function without it.");
            //Bukkit.getPluginManager().disablePlugin(this);
            Bukkit.getPluginManager().registerEvents(this, this);
        }

        new UpdateChecker(this, 92276).getVersion(version -> {
            if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                getLogger().info("You are running the latest version.");
            } else {
                getLogger().info("There is a new update available! MAKE SURE TO READ CHANGES WHEN UPDATING! Update at https://www.spigotmc.org/resources/tiny-lives.92276/");
            }
        });

        this.getCommand("tl").setExecutor(this);
        this.getCommand("tinylives").setExecutor(this);
        this.getCommand("lives").setExecutor(this);
        this.getCommand("givelife").setExecutor(this);

        // Plugin startup logic
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        loadConfig();

        customConfig.setup();
        customConfig.get().addDefault("current-delay", delay);
        customConfig.get().addDefault("reset-number", 0);
        customConfig.get().addDefault("addlife-current-delay", addLifeDelay);
        customConfig.get().addDefault("assassin-cooldown", assassinGlobalCooldown);
        customConfig.get().addDefault("players", null);
        customConfig.get().options().copyDefaults(true);
        customConfig.save();

        //Part 1 of section 1
        boolean newFile = livesConfig.setup();
        if (newFile) {
            //Disabled Prefixs
            livesConfig.get().addDefault("disable-life-prefixs", false);
            //Endless lives
            livesConfig.get().addDefault("lives.endless.prefix", "&8*Endless*");
            livesConfig.get().addDefault("lives.endless.color", "&8");
            livesConfig.get().addDefault("lives.endless.tp.x", 100);
            livesConfig.get().addDefault("lives.endless.tp.y", 100);
            livesConfig.get().addDefault("lives.endless.tp.z", 100);
            //Life dead
            livesConfig.get().addDefault("lives.0.prefix", "&7*DEAD*");
            livesConfig.get().addDefault("lives.0.color", "&7");
            livesConfig.get().addDefault("lives.0.tp.x", 100);
            livesConfig.get().addDefault("lives.0.tp.y", 100);
            livesConfig.get().addDefault("lives.0.tp.z", 100);
            //Life 1
            livesConfig.get().addDefault("lives.1.prefix", "&7{&c1&7}");
            livesConfig.get().addDefault("lives.1.color", "&c");
            livesConfig.get().addDefault("lives.1.startpvp", true);
            livesConfig.get().addDefault("lives.1.fightback", true);
            livesConfig.get().addDefault("lives.1.tp.x", 200);
            livesConfig.get().addDefault("lives.1.tp.y", 200);
            livesConfig.get().addDefault("lives.1.tp.z", 200);
            //Life 2
            livesConfig.get().addDefault("lives.2.prefix", "&7{&62&7}");
            livesConfig.get().addDefault("lives.2.color", "&6");
            livesConfig.get().addDefault("lives.2.startpvp", true);
            livesConfig.get().addDefault("lives.2.fightback", true);
            livesConfig.get().addDefault("lives.2.tp.x", 300);
            livesConfig.get().addDefault("lives.2.tp.y", 300);
            livesConfig.get().addDefault("lives.2.tp.z", 300);
            //Life 3
            livesConfig.get().addDefault("lives.3.prefix", "&7{&a3&7}");
            livesConfig.get().addDefault("lives.3.color", "&a");
            livesConfig.get().addDefault("lives.3.startpvp", true);
            livesConfig.get().addDefault("lives.3.fightback", true);
            livesConfig.get().addDefault("lives.3.tp.x", 400);
            livesConfig.get().addDefault("lives.3.tp.y", 400);
            livesConfig.get().addDefault("lives.3.tp.z", 400);
            //Life 4
            livesConfig.get().addDefault("lives.4.prefix", "&7{&24&7}");
            livesConfig.get().addDefault("lives.4.color", "&a");
            livesConfig.get().addDefault("lives.4.startpvp", true);
            livesConfig.get().addDefault("lives.4.fightback", true);
            livesConfig.get().addDefault("lives.4.tp.x", 400);
            livesConfig.get().addDefault("lives.4.tp.y", 400);
            livesConfig.get().addDefault("lives.4.tp.z", 400);
            //Life 5
            livesConfig.get().addDefault("lives.5.prefix", "&7{&25&7}");
            livesConfig.get().addDefault("lives.5.color", "&a");
            livesConfig.get().addDefault("lives.5.startpvp", true);
            livesConfig.get().addDefault("lives.5.fightback", true);
            livesConfig.get().addDefault("lives.5.tp.x", 400);
            livesConfig.get().addDefault("lives.5.tp.y", 400);
            livesConfig.get().addDefault("lives.5.tp.z", 400);
            //Life 6
            livesConfig.get().addDefault("lives.6.prefix", "&7{&26&7}");
            livesConfig.get().addDefault("lives.6.color", "&a");
            livesConfig.get().addDefault("lives.6.startpvp", true);
            livesConfig.get().addDefault("lives.6.fightback", true);
            livesConfig.get().addDefault("lives.6.tp.x", 400);
            livesConfig.get().addDefault("lives.6.tp.y", 400);
            livesConfig.get().addDefault("lives.6.tp.z", 400);
            livesConfig.get().options().copyDefaults(true);
        }
        livesConfig.save();
        livesConfig.reload();

        //plugin = this;

        setInstance(this);

        this.SQL = new MySQL();
        this.data = new SQLGetter();

        SQL.setup();

        if(enableSQL) {
            try {
                SQL.connect();
            } catch (ClassNotFoundException | SQLException e) {
                //e.printStackTrace();
                ChatUtil.console("Database not connected.", 0);
            }

            if (SQL.isConnected()) {
                ChatUtil.console("Database is connected.", 0);
                data.createTable();
            }
        }

        if(enableDeadmans) {
            new WorldCreator(deadmansWorld).createWorld();
        }

        if(addLifes){
            Bukkit.getScheduler().runTaskTimer(tinylives.getInstance(), new Runnable() {
                @Override
                public void run() {
                    ChatUtil.console("Triggered Add Life Check", 2);

                    if(customConfig.get().getConfigurationSection("players") == null){
                        return;
                    }

                    //customConfig.save();
                    //customConfig.reload();

                    addlifeCurrentDelay = customConfig.get().getInt("addlife-current-delay");
                    addlifeCurrentDelay = addlifeCurrentDelay - (int)ScheduleDelay;
                    customConfig.get().set("addlife-current-delay", addlifeCurrentDelay);
                    //customConfig.save();
                    //customConfig.reload();
                    if (addlifeCurrentDelay <= 0) {
                        ChatUtil.console("Add life was triggered", 0);
                        customConfig.get().set("addlife-current-delay", addLifeDelay);
                        //customConfig.save();
                        //customConfig.reload();

                        Object[] playerKeys = customConfig.get().getConfigurationSection("players").getKeys(false).toArray();

                        for (Object key : playerKeys) {
                            int pLives = customConfig.get().getInt("players." + key + ".lives");
                            if (customConfig.get().contains("players." + key + ".max-lives")) {
                                if (pLives < customConfig.get().getInt("players." + key + ".max-lives")) {
                                    customConfig.get().set("players." + key + ".lives", pLives + 1);
                                }
                            } else if (pLives < lives) {
                                customConfig.get().set("players." + key + ".lives", pLives + 1);
                            }
                        }
                        //customConfig.save();
                        //customConfig.reload();

                        for (Player player : Bukkit.getOnlinePlayers()) {
                            PlayerUtil.SetDisplayName(player);
                            if(!getConfig().getBoolean("life-types.add-lives.disable-messages")) {
                                ChatUtil.NotifyPlayerBlock(getConfig().getStringList("life-types.add-lives.messages"), player);
                            }

                            if(PlayerUtil.CheckPlayerDeadWorld(player)){
                                PlayerUtil.MoveWorld(player,1);
                            }
                        }
                    }

                    customConfig.save();
                    //customConfig.reload();
                }
            }, 20L, ScheduleDelay);
        }
        else if(allDeathReset){
            Bukkit.getScheduler().runTaskTimer(tinylives.getInstance(), new Runnable() {
                @Override
                public void run() {
                    ChatUtil.console("Triggered All Death Check", 2);

                    if(customConfig.get().getConfigurationSection("players") == null){
                        return;
                    }

                    //customConfig.save();
                    //customConfig.reload();

                    ConfigurationSection playerKeys = customConfig.get().getConfigurationSection("players");

                    //Object[] playerKeys = customConfig.get().getConfigurationSection("players").getKeys(false).toArray();

                    for (Object key : playerKeys.getKeys(false)){
                        if(customConfig.get().getBoolean("players."+key+".dead")){
                            int pTime = customConfig.get().getInt("players."+key+".respawn-time");
                            if(pTime - ScheduleDelay <= 0){
                                UUID id = UUID.fromString(key.toString());
                                if(pTime != 0) {
                                    customConfig.get().set("players." + key + ".respawn-time", 0);
                                }
                                Player player = Bukkit.getPlayer(id);

                                if(player != null) {
                                    customConfig.get().set("players."+key+".respawn-time", allDeathDelay);
                                    //customConfig.save();
                                    //customConfig.reload();

                                    PlayerUtil.ResetPlayer(player);
                                    if(PlayerUtil.CheckPlayerDeadWorld(player)){
                                        PlayerUtil.MoveWorld(player,1);
                                    }
                                }
                            } else {
                                customConfig.get().set("players."+key+".respawn-time", pTime - ScheduleDelay);
                            }
                        }
                    }
                    customConfig.save();
                    //customConfig.reload();
                }
            }, 20L, ScheduleDelay);
        }
        else if(globalReset){
            Bukkit.getScheduler().runTaskTimer(tinylives.getInstance(), new Runnable() {
                @Override
                public void run() {
                    ChatUtil.console("Triggered Global Check", 2);

                    if(customConfig.get().getConfigurationSection("players") == null){
                        return;
                    }

                    //customConfig.save();
                    //customConfig.reload();

                    currentDelay = customConfig.get().getInt("current-delay");
                    resetNumber = customConfig.get().getInt("reset-number");

                    currentDelay = currentDelay - (int)ScheduleDelay;

                    if (currentDelay <= 0) {
                        ChatUtil.console("Reset was triggered", 0);
                        customConfig.get().set("reset-number", (resetNumber + 1));
                        customConfig.get().set("current-delay", delay);
                        //customConfig.save();
                        //customConfig.reload();

                        Object[] playerKeys = customConfig.get().getConfigurationSection("players").getKeys(false).toArray();

                        for (Object key : playerKeys){
                            int pLives = customConfig.get().getInt("players."+key+".lives");
                            if(pLives < lives && customConfig.get().getInt("players."+key+".current-reset") != customConfig.get().getInt("reset-number")){
                                customConfig.get().set("players."+key+".lives", lives);
                                customConfig.get().set("players."+key+".current-reset", customConfig.get().getInt("reset-number"));
                            }
                        }
                        //customConfig.save();
                        //customConfig.reload();

                        for (Player player : Bukkit.getOnlinePlayers()) {
                            PlayerUtil.SetDisplayName(player);
                            if(!getConfig().getBoolean("life-types.global-reset.disable-messages")) {
                                ChatUtil.NotifyPlayerBlock(getConfig().getStringList("life-types.global-reset.messages"), player);
                            }

                            if(PlayerUtil.CheckPlayerDeadWorld(player)){
                                PlayerUtil.MoveWorld(player,1);
                            }
                        }
                    } else {
                        customConfig.get().set("current-delay", currentDelay);
                        //customConfig.save();
                        //customConfig.reload();
                    }

                    customConfig.save();
                    //customConfig.reload();
                }
            }, 20L, ScheduleDelay);
        }
        else {
            ChatUtil.console("NO MODE SELECTED.", 0);
        }

        if(customConfig.get().getBoolean("assassin.enabled") && customConfig.get().getBoolean("assassin.global.enabled")){
            Bukkit.getScheduler().runTaskTimer(tinylives.getInstance(), new Runnable() {
                @Override
                public void run() {
                    customConfig.get().set("assassin-cooldown", customConfig.get().getInt("assassin-cooldown") - ScheduleDelay);
                    customConfig.get().set("assassin-time", customConfig.get().getInt("assassin-time") - ScheduleDelay);

                    ChatUtil.console("Trigger Assassin loop", 2);

                    if (customConfig.get().getInt("assassin-time") <= 0){
                        ChatUtil.console("Trigger Assassin kill timer", 2);
                        customConfig.get().set("assassin-time", tinylives.getInstance().getConfig().getInt("assassin.global.time"));

                        if(customConfig.get().getConfigurationSection("players") == null){
                            return;
                        }
                        Object[] playerKeys = customConfig.get().getConfigurationSection("players").getKeys(false).toArray();
                        for (Object key : playerKeys){
                            if(customConfig.get().contains("players."+key+".IsAssassin")){
                                if(customConfig.get().getBoolean("players."+key+".IsAssassin")) {
                                    UUID id = UUID.fromString(key.toString());
                                    Player player = Bukkit.getPlayer(id);
                                    assert player != null;

                                    ChatUtil.console("Taking assassin " + player.getName() + "'s life.", 0);
                                    customConfig.get().set("players." + key + ".IsAssassin", false);
                                    if (customConfig.get().getInt("players." + key + ".lives") <= 1) {
                                        PlayerUtil.KillPlayer(player);
                                    } else {
                                        customConfig.get().set("players." + key + ".lives", customConfig.get().getInt("players." + key + ".lives") - 1);
                                    }
                                    if(tinylives.getInstance().getConfig().getBoolean("assassin.titles.failed.sound.enabled")) {
                                        PlayerUtil.sendSound(player, tinylives.getInstance().getConfig().getString("assassin.titles.failed.sound.sound"), tinylives.getInstance().getConfig().getInt("assassin.titles.failed.sound.pitch"), tinylives.getInstance().getConfig().getInt("assassin.titles.failed.sound.volume"));
                                    }

                                    ChatUtil.NotifyAllStringPlayer(tinylives.getInstance().getConfig().getString("assassin.titles.failed.message"), player);

                                    if (tinylives.getInstance().getConfig().getBoolean("assassin.titles.failed.title.enabled")) {
                                        ChatUtil.NotifyPlayerTitle(player, tinylives.getInstance().getConfig().getString("assassin.titles.failed.title.title"), tinylives.getInstance().getConfig().getString("assassin.titles.failed.title.subTitle"), tinylives.getInstance().getConfig().getInt("assassin.titles.failed.title.fadeIn"), tinylives.getInstance().getConfig().getInt("assassin.titles.failed.title.stay"), tinylives.getInstance().getConfig().getInt("assassin.titles.failed.title.fadeOut"));
                                    }
                                }
                            }
                        }
                    }

                    if (customConfig.get().getInt("assassin-cooldown") <= 0){
                        ChatUtil.console("Trigger Assassin", 2);
                        customConfig.get().set("assassin-cooldown", tinylives.getInstance().getConfig().getInt("assassin.global.cooldown"));
                        customConfig.get().set("assassin-time", tinylives.getInstance().getConfig().getInt("assassin.global.time"));

                        List<Player> onlinePlayers = new ArrayList<Player>(Bukkit.getOnlinePlayers());
                        int upper = onlinePlayers.size();
                        if (upper >= 1) {
                            Random rand = new Random();
                            int lastRnd = -1;
                            for (int i = 0; i < tinylives.getInstance().getConfig().getInt("assassin.global.count"); i++) {
                                int rnd = rand.nextInt(upper);
                                while (rnd == lastRnd && upper >= tinylives.getInstance().getConfig().getInt("assassin.global.count") && !customConfig.get().getBoolean("players." + onlinePlayers.get(rnd).getUniqueId() + ".dead")) {
                                    rnd = rand.nextInt(upper);
                                    ChatUtil.console(rnd+"", 2);
                                    ChatUtil.console("Random player randomized again.", 2);
                                }
                                ChatUtil.console(rnd+"", 2);
                                ChatUtil.console("Selected " + onlinePlayers.get(rnd).getName() + " as an assassin", 0);
                                ChatUtil.NotifyPlayerString(tinylives.getInstance().getConfig().getString("assassin.titles.chosen.message"), onlinePlayers.get(rnd));
                                if(tinylives.getInstance().getConfig().getBoolean("assassin.titles.chosen.sound.enabled")) {
                                    PlayerUtil.sendSound(onlinePlayers.get(rnd), tinylives.getInstance().getConfig().getString("assassin.titles.chosen.sound.sound"), tinylives.getInstance().getConfig().getInt("assassin.titles.chosen.sound.pitch"), tinylives.getInstance().getConfig().getInt("assassin.titles.chosen.sound.volume"));
                                }
                                if (tinylives.getInstance().getConfig().getBoolean("assassin.titles.chosen.title.enabled")) {
                                    ChatUtil.console("Notifying Chosen", 2);
                                    ChatUtil.NotifyPlayerTitle(onlinePlayers.get(rnd), tinylives.getInstance().getConfig().getString("assassin.titles.chosen.title.title"), tinylives.getInstance().getConfig().getString("assassin.titles.chosen.title.subTitle"), tinylives.getInstance().getConfig().getInt("assassin.titles.chosen.title.fadeIn"), tinylives.getInstance().getConfig().getInt("assassin.titles.chosen.title.stay"), tinylives.getInstance().getConfig().getInt("assassin.titles.chosen.title.fadeOut"));
                                }
                                customConfig.get().set("players." + onlinePlayers.get(rnd).getUniqueId() + ".IsAssassin", true);
                                lastRnd = rnd;
                            }

                            for (Player player : onlinePlayers) {
                                if (!customConfig.get().getBoolean("players." + player.getUniqueId() + ".IsAssassin")) {
                                    ChatUtil.console("Notifying Not Chosen", 2);
                                    ChatUtil.NotifyPlayerString(tinylives.getInstance().getConfig().getString("assassin.titles.not-chosen.message"), player);
                                    if(tinylives.getInstance().getConfig().getBoolean("assassin.titles.not-chosen.sound.enabled")) {
                                        PlayerUtil.sendSound(player, tinylives.getInstance().getConfig().getString("assassin.titles.not-chosen.sound.sound"), tinylives.getInstance().getConfig().getInt("assassin.titles.not-chosen.sound.pitch"), tinylives.getInstance().getConfig().getInt("assassin.titles.not-chosen.sound.volume"));
                                    }

                                    if (tinylives.getInstance().getConfig().getBoolean("assassin.titles.not-chosen.title.enabled")) {
                                        ChatUtil.NotifyPlayerTitle(player, tinylives.getInstance().getConfig().getString("assassin.titles.not-chosen.title.title"), tinylives.getInstance().getConfig().getString("assassin.titles.not-chosen.title.subTitle"), tinylives.getInstance().getConfig().getInt("assassin.titles.not-chosen.title.fadeIn"), tinylives.getInstance().getConfig().getInt("assassin.titles.not-chosen.title.stay"), tinylives.getInstance().getConfig().getInt("assassin.titles.not-chosen.title.fadeOut"));
                                    }
                                }
                            }
                        }
                    }

                    customConfig.save();
                }
            }, 20L, ScheduleDelay);
        } else if(customConfig.get().getBoolean("assassin.enabled") && customConfig.get().getBoolean("assassin.player.enabled")){
            Bukkit.getScheduler().runTaskTimer(tinylives.getInstance(), new Runnable() {
                @Override
                public void run() {
                    ChatUtil.console("Trigger Player Assassin loop", 2);

                    if(customConfig.get().getConfigurationSection("players") == null){
                        return;
                    }
                    Object[] playerKeys = customConfig.get().getConfigurationSection("players").getKeys(false).toArray();
                    for (Object key : playerKeys){
                        if(customConfig.get().contains("players."+key+".IsAssassin")){
                            customConfig.get().set("players."+key+".AssassinTime", customConfig.get().getInt("players."+key+".AssassinTime") - ScheduleDelay);
                            customConfig.get().set("players."+key+".AssassinCooldown", customConfig.get().getInt("players."+key+".AssassinCooldown") - ScheduleDelay);

                            if(customConfig.get().getInt("players."+key+".AssassinTime") <= 0){
                                UUID id = UUID.fromString(key.toString());
                                Player player = Bukkit.getPlayer(id);
                                assert player != null;

                                ChatUtil.console("Taking assassin " + player.getName() + "'s life.", 0);
                                customConfig.get().set("players." + key + ".IsAssassin", false);
                                if (customConfig.get().getInt("players." + key + ".lives") <= 1) {
                                    PlayerUtil.KillPlayer(player);
                                } else {
                                    customConfig.get().set("players." + key + ".lives", customConfig.get().getInt("players." + key + ".lives") - 1);
                                }
                                if(tinylives.getInstance().getConfig().getBoolean("assassin.titles.failed.sound.enabled")) {
                                    PlayerUtil.sendSound(player, tinylives.getInstance().getConfig().getString("assassin.titles.failed.sound.sound"), tinylives.getInstance().getConfig().getInt("assassin.titles.failed.sound.pitch"), tinylives.getInstance().getConfig().getInt("assassin.titles.failed.sound.volume"));
                                }

                                ChatUtil.NotifyAllStringPlayer(tinylives.getInstance().getConfig().getString("assassin.titles.failed.message"), player);

                                if (tinylives.getInstance().getConfig().getBoolean("assassin.titles.failed.title.enabled")) {
                                    ChatUtil.NotifyPlayerTitle(player, tinylives.getInstance().getConfig().getString("assassin.titles.failed.title.title"), tinylives.getInstance().getConfig().getString("assassin.titles.failed.title.subTitle"), tinylives.getInstance().getConfig().getInt("assassin.titles.failed.title.fadeIn"), tinylives.getInstance().getConfig().getInt("assassin.titles.failed.title.stay"), tinylives.getInstance().getConfig().getInt("assassin.titles.failed.title.fadeOut"));
                                }
                            }
                        }
                    }

                    customConfig.save();
                }
            }, 20L, ScheduleDelay);
        }

        if(perLifeRespawn){
            Bukkit.getScheduler().runTaskTimer(tinylives.getInstance(), new Runnable() {
                @Override
                public void run() {
                    ChatUtil.console("Triggered per life check", 2);

                    if(customConfig.get().getConfigurationSection("players") == null){
                        return;
                    }

                    //customConfig.save();
                    //customConfig.reload();

                    ConfigurationSection playerKeys = customConfig.get().getConfigurationSection("players");

                    //Object[] playerKeys = customConfig.get().getConfigurationSection("players").getKeys(false).toArray();

                    for (Object key : playerKeys.getKeys(false)){
                        if(customConfig.get().contains("players."+key+".per-life-respawn")){
                            int pTime = customConfig.get().getInt("players."+key+".per-life-respawn");
                            if(pTime - ScheduleDelay <= 0){
                                customConfig.get().set("players."+key+".per-life-respawn", 0);
                            } else {
                                customConfig.get().set("players."+key+".per-life-respawn", pTime - ScheduleDelay);
                            }
                        }
                    }
                    customConfig.save();
                    //customConfig.reload();
                }
            }, 20L, ScheduleDelay);
        }
    }

    @Override
    public void onDisable() {
        SQL.disconnect();

        getLogger().info("Tiny Lives is now disabled.");
    }

    public static tinylives getInstance() {
        return instance;
    }

    private static void setInstance(tinylives instance) {
        tinylives.instance = instance;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent ev) {
        if(PlayerUtil.CheckPlayerWorld(ev.getPlayer())) {
            Player player = ev.getPlayer();

            PlayerUtil.PlayerJoin(player);
        } else {
            ChatUtil.console((ev.getPlayer().getName() + " Is not in the correct world. Not running join logic."), 2);
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent ev) {
        if(tinylives.getInstance().getConfig().getBoolean("pvp-settings.punish-combat-log")){
            Player player = ev.getPlayer();

            if(customConfig.get().getBoolean("players." + player.getUniqueId().toString() + ".InCombat")){
                customConfig.get().set("players." + player.getUniqueId().toString() + ".lives",customConfig.get().getInt("players." + player.getUniqueId().toString() + ".lives") - 1);
                customConfig.save();
                //customConfig.reload();
                ChatUtil.console("Player has been punished for combat logging.", 0);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e) {
        //ChatUtil.console("Check", 1);
        if (!tinylives.getInstance().getConfig().getBoolean("pvp-settings.disable-combat-state")) {
            //ChatUtil.console("Running Combat", 1);
            if(e.getEntity() instanceof Player) {
                Player player = (Player) e.getEntity();
                if (customConfig.get().getBoolean("players." + player.getUniqueId().toString() + ".cooldown")) {
                    if (tinylives.getInstance().getConfig().getBoolean("set-fight-cancel-true")) {
                        e.setCancelled(true);
                    }
                    return;
                }
            }


            if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
                Player player = (Player) e.getEntity();

                if (customConfig.get().getBoolean("players." + player.getUniqueId().toString() + ".startedpvp")) {
                    if (!livesConfig.get().getBoolean("lives." + customConfig.get().getInt("players." + e.getDamager().getUniqueId().toString() + ".lives") + ".fightback")) {
                        if (tinylives.getInstance().getConfig().getBoolean("pvp-settings.messages.cantfightback.enabled")) {
                            ChatUtil.NotifyPlayerString(tinylives.getInstance().getConfig().getString("pvp-settings.messages.cantfightback"), (Player) e.getDamager());
                        }

                        if (tinylives.getInstance().getConfig().getBoolean("set-fight-cancel-true")) {
                            e.setCancelled(true);
                        }
                        return;
                    }
                } else {
                    if (!livesConfig.get().getBoolean("lives." + customConfig.get().getInt("players." + e.getDamager().getUniqueId().toString() + ".lives") + ".startpvp")) {
                        if (tinylives.getInstance().getConfig().getBoolean("pvp-settings.messages.cantstart.enabled")) {
                            ChatUtil.NotifyPlayerString(tinylives.getInstance().getConfig().getString("pvp-settings.messages.cantstart"), (Player) e.getDamager());
                        }

                        if (tinylives.getInstance().getConfig().getBoolean("set-fight-cancel-true")) {
                            e.setCancelled(true);
                        }

                        return;
                    } else {
                        if (!customConfig.get().getBoolean("players." + e.getDamager().getUniqueId().toString() + ".startedpvp")) {
                            customConfig.get().set("players." + e.getDamager().getUniqueId().toString() + ".startedpvp", true);
                            customConfig.get().set("players." + e.getDamager().getUniqueId().toString() + ".InCombat", true);
                            customConfig.get().set("players." + player.getUniqueId().toString() + ".InCombat", true);
                            //customConfig.save();
                            //customConfig.reload();
                            if (tinylives.getInstance().getConfig().getBoolean("pvp-settings.messages.entered.enabled")) {
                                ChatUtil.NotifyPlayerString(tinylives.getInstance().getConfig().getString("pvp-settings.messages.entered.message"), (Player) e.getDamager());
                            }

                            new BukkitRunnable() {
                                public void run() {
                                    customConfig.get().set("players." + e.getDamager().getUniqueId().toString() + ".startedpvp", false);
                                    customConfig.get().set("players." + e.getDamager().getUniqueId().toString() + ".InCombat", false);
                                    customConfig.get().set("players." + player.getUniqueId().toString() + ".InCombat", false);
                                    //customConfig.save();
                                    //customConfig.reload();
                                    if (tinylives.getInstance().getConfig().getBoolean("pvp-settings.messages.exited.enabled")) {
                                        ChatUtil.NotifyPlayerString(tinylives.getInstance().getConfig().getString("pvp-settings.messages.exited.message"), (Player) e.getDamager());
                                    }
                                }
                            }.runTaskLater(tinylives.getInstance(), tinylives.getInstance().getConfig().getInt("pvp-settings.combat-timer"));

                            customConfig.save();
                        }
                    }
                }
            }
            if (tinylives.getInstance().getConfig().getBoolean("set-fight-cancel-false")) {
                e.setCancelled(false);
            }
        }
    }

    @EventHandler
    public void onKill(PlayerDeathEvent e) {
        Player player = e.getEntity();
        Player killer = player.getKiller();

        if(killer != null) {
            ChatUtil.console("Found killer", 0);
            if (customConfig.get().getBoolean("players." + killer.getUniqueId() + ".IsAssassin")) {
                ChatUtil.console("Killer was assassin", 0);
                customConfig.get().set("players." + killer.getUniqueId() + ".IsAssassin", false);
                ChatUtil.NotifyAllStringPlayer(tinylives.getInstance().getConfig().getString("assassin.titles.success.message"), killer);
                if (tinylives.getInstance().getConfig().getBoolean("assassin.gain-lives")) {
                    ChatUtil.console("Gave killer life", 0);
                    customConfig.get().set("players." + killer.getUniqueId() + ".lives", customConfig.get().getInt("players." + killer.getUniqueId() + ".lives") + 1);
                }

                if(tinylives.getInstance().getConfig().getBoolean("assassin.titles.success.sound.enabled")) {
                    PlayerUtil.sendSound(player, tinylives.getInstance().getConfig().getString("assassin.titles.success.sound.sound"), tinylives.getInstance().getConfig().getInt("assassin.titles.success.sound.pitch"), tinylives.getInstance().getConfig().getInt("assassin.titles.success.sound.volume"));
                }

                if (tinylives.getInstance().getConfig().getBoolean("assassin.titles.success.title.enabled")) {
                    ChatUtil.NotifyPlayerTitle(killer, tinylives.getInstance().getConfig().getString("assassin.titles.success.title.title"), tinylives.getInstance().getConfig().getString("assassin.titles.success.title.subTitle"), tinylives.getInstance().getConfig().getInt("assassin.titles.success.title.fadeIn"), tinylives.getInstance().getConfig().getInt("assassin.titles.success.title.stay"), tinylives.getInstance().getConfig().getInt("assassin.titles.success.title.fadeOut"));
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e)
    {

        Player player = Bukkit.getPlayer(e.getEntity().getUniqueId());
        if(player != null) {
            if (!customConfig.get().getBoolean("players." + player.getUniqueId().toString() + ".respawning")){

                if (PlayerUtil.CheckPlayerWorld(player)) {

                    if (enableDeadmans) {
                        if (PlayerUtil.CheckPlayerDeadWorld(player)) {
                            if(customConfig.get().getInt("players." + player.getUniqueId().toString() + ".lives") >= 1){
                                PlayerUtil.MoveWorld(player, 1);
                                return;
                            }

                            ChatUtil.console((player.getName() + " Died in the deadmans world."), 0);
                            World DeadWorld = Bukkit.getWorld(deadmansWorld);
                            if(DeadWorld == null){
                                DeadWorld = new WorldCreator(deadmansWorld).createWorld();
                                Location deadSpawnLocation = DeadWorld.getSpawnLocation();
                                player.spigot().respawn();
                                player.teleport(deadSpawnLocation);
                                player.setGameMode(GameMode.SURVIVAL);
                                //player.teleport(DeadWorld, spawnLocation.getX(), spawnLocation.getY(), spawnLocation.getZ());
                            }
                            Location deadSpawnLocation = DeadWorld.getSpawnLocation();
                            player.spigot().respawn();
                            player.teleport(deadSpawnLocation);
                            player.setGameMode(GameMode.SURVIVAL);

                            return;
                        }
                    }

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (disablePvpDeaths) {
                                if (e.getEntity().getKiller() instanceof Player) {
                                    ChatUtil.console("Player was killed by something other than a player", 2);
                                    return;
                                }
                            }

                            if (pvpOnlyDeaths) {
                                if (e.getEntity().getKiller() == null) {
                                    ChatUtil.console("Player was killed by something other than a player", 2);
                                    return;
                                }
                            }

                            if (customConfig.get().getConfigurationSection("players." + player.getUniqueId().toString()) == null) {
                                if(tinylives.getInstance().getConfig().getBoolean("life-settings.random-lives.enabled")) {
                                    int min = tinylives.getInstance().getConfig().getInt("life-settings.random-lives.min-lives");
                                    int max = tinylives.getInstance().getConfig().getInt("life-settings.random-lives.max-lives");
                                    int random_num = (int)Math.floor(Math.random()*(max-min+1)+min);

                                    ChatUtil.console("Random lives for " + player.getName() + ": " + random_num, 0);

                                    customConfig.get().set("players." + player.getUniqueId().toString() + ".lives", random_num);
                                } else {
                                    customConfig.get().set("players." + player.getUniqueId().toString() + ".lives", lives);
                                }

                                customConfig.get().set("players." + player.getUniqueId().toString() + ".current-reset", resetNumber);
                                customConfig.get().set("players." + player.getUniqueId().toString() + ".dead", false);
                                customConfig.get().set("players." + player.getUniqueId().toString() + ".respawning", false);
                                //customConfig.save();
                                //customConfig.reload();

                                PlayerUtil.KillPlayer(player);
                            } else {
                                PlayerUtil.KillPlayer(player);
                            }
                        }
                    }.runTaskLater(instance, 5);
                } else {
                    ChatUtil.console((player.getName() + " Is not in the correct world. Not running death logic."), 0);
                }
            }
        }
        else {
            ChatUtil.console("PLAYER WHO DIED WAS NOT FOUND!!!", 1);
        }
    }

    public void loadConfig(){
        prefix = getConfig().getString("message-settings.prefixs.prefix");
        PPrefix = getConfig().getString("message-settings.prefixs.command-prefix");
        disabledPrefix = getConfig().getBoolean("message-settings.prefixs.disable-prefix");
        centeredText = getConfig().getBoolean("message-settings.centered-text");

        globalReset = getConfig().getBoolean("life-types.global-reset.enabled");
        delay = getConfig().getInt("life-types.global-reset.delay");
        addLifes = getConfig().getBoolean("life-types.add-lives.enabled");
        addLifeDelay = getConfig().getInt("life-types.add-lives.delay");
        allDeathReset = getConfig().getBoolean("life-types.all-death.enabled");
        allDeathDelay = getConfig().getInt("life-types.all-death.delay");

        banPlayers = getConfig().getBoolean("death-types.ban-players.enabled");
        enableDeadmans = getConfig().getBoolean("death-types.deadman-world.enabled");
        deadmansWorld = getConfig().getString("death-types.deadman-world.deadman-world-name");
        ghostPlayers = getConfig().getBoolean("death-types.ghost-players.enabled");

        assassin = getConfig().getBoolean("assassin.enabled");
        assassinGlobal = getConfig().getBoolean("assassin.global.enabled");
        assassinGlobalCooldown = getConfig().getInt("assassin.global.cooldown");
        assassinGlobalTimer = getConfig().getInt("assassin.global.time");
        assassinGlobalCount = getConfig().getInt("assassin.global.count");
        assassinPlayer = getConfig().getBoolean("assassin.player.enabled");
        assassinPlayerChance = getConfig().getInt("assassin.player.chance");
        assassinPlayerCooldown = getConfig().getInt("assassin.player.cooldown");
        assassinPlayerTime = getConfig().getInt("assassin.player.time");

        lives = getConfig().getInt("life-settings.lives");
        extraLives = getConfig().getInt("life-settings.extra-lives.max-extra-lives");
        enabledExtraLives = getConfig().getBoolean("life-settings.extra-lives.enabled");

        debug = getConfig().getInt("debug");

        enabledWorlds = getConfig().getStringList("world-settings.enabled-worlds");
        defaultWorld = getConfig().getString("world-settings.main-world");

        pvpOnlyDeaths = getConfig().getBoolean("death-settings.pvp-only-deaths");
        disablePvpDeaths = getConfig().getBoolean("death-settings.disable-pvp-deaths");

        perLifeRespawn = getConfig().getBoolean("life-settings.per-life-respawn.enabled");
        perLifeRespawnTime = getConfig().getInt("life-settings.per-life-respawn.time");

        enableSQL = getConfig().getBoolean("MySQL.enabled");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("tl") || command.getName().equalsIgnoreCase("tinylives")) {
            if (args.length > 0){
                if(sender instanceof Player) {
                    Player player = (Player) sender;
                    String playerid = player.getUniqueId().toString();

                    //-----------------------------------HELP-----------------------------------//
                    if (args[0].equalsIgnoreCase("help")) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.BOLD + "List of commands:");
                        if (player.hasPermission("tinylives.reload")) {
                            player.sendMessage(ChatColor.GREEN + "/TinyLives reload " + ChatColor.GRAY + "-" + ChatColor.BLUE + " Reloads player data.");
                        }
                        if (player.hasPermission("tinylives.lives")) {
                            player.sendMessage(ChatColor.GREEN + "/TinyLives lives " + ChatColor.GRAY + "-" + ChatColor.BLUE + " Tells you how many lives you have left.");
                        }
                        if (player.hasPermission("tinylives.info")) {
                            player.sendMessage(ChatColor.GREEN + "/TinyLives info " + ChatColor.GRAY + "-" + ChatColor.BLUE + " Lists the your lives and how long till reset.");
                        }
                        if (player.hasPermission("tinylives.respawn")) {
                            player.sendMessage(ChatColor.GREEN + "/TinyLives respawn (player) " + ChatColor.GRAY + "-" + ChatColor.BLUE + " Respawn a player to full lives if they have died.");
                        }
                        if(player.hasPermission("tinylives.resetall")) {
                            player.sendMessage(ChatColor.GREEN + "/TinyLives resetall " + ChatColor.GRAY + "-" + ChatColor.BLUE + " Reset all players lives. This forces all players to new life amounts.");
                        }
                        if(player.hasPermission("tinylives.resetallextra")) {
                            player.sendMessage(ChatColor.GREEN + "/TinyLives resetallextra " + ChatColor.GRAY + "-" + ChatColor.BLUE + " Reset all players extra lives.");
                        }
                        if(player.hasPermission("tinylives.reset")) {
                            player.sendMessage(ChatColor.GREEN + "/TinyLives reset (player) " + ChatColor.GRAY + "-" + ChatColor.BLUE + " Resets a specific players lives.");
                        }
                        if(player.hasPermission("tinylives.debug")) {
                            player.sendMessage(ChatColor.GREEN + "/TinyLives debug " + ChatColor.GRAY + "-" + ChatColor.BLUE + " Enables/Disables debug mode.");
                        }
                        if(player.hasPermission("tinylives.addlife")) {
                            player.sendMessage(ChatColor.GREEN + "/TinyLives addlife (player) (amount) " + ChatColor.GRAY + "-" + ChatColor.BLUE + " Adds 1 life to specific player.");
                        }
                        if(player.hasPermission("tinylives.removelife")) {
                            player.sendMessage(ChatColor.GREEN + "/TinyLives removelife (player) (amount) " + ChatColor.GRAY + "-" + ChatColor.BLUE + " Removes 1 life from specific player.");
                        }
                        if(player.hasPermission("tinylives.addextralife")) {
                            player.sendMessage(ChatColor.GREEN + "/TinyLives addextralife (player) (amount) " + ChatColor.GRAY + "-" + ChatColor.BLUE + " Adds 1 extra life to specific player.");
                        }
                        if(player.hasPermission("tinylives.removeextralife")) {
                            player.sendMessage(ChatColor.GREEN + "/TinyLives removeextralife (player) (amount) " + ChatColor.GRAY + "-" + ChatColor.BLUE + " Removes 1 extra life from specific player.");
                        }
                        if(player.hasPermission("tinylives.givelife")) {
                            player.sendMessage(ChatColor.GREEN + "/TinyLives givelife (player) " + ChatColor.GRAY + "-" + ChatColor.BLUE + "  Gives 1 of your lives to specified player.");
                        }
                        if(player.hasPermission("tinylives.lives")) {
                            player.sendMessage(ChatColor.GREEN + "/Lives " + ChatColor.GRAY + "-" + ChatColor.BLUE + " Tells you how many lives you have left.");
                        }

                        return true;
                    }

                    //-----------------------------------RELOAD-----------------------------------//
                    else if (args[0].equalsIgnoreCase("reload")) {
                        if(!player.hasPermission("tinylives.reload")){
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "You do not have permission!");
                            return true;
                        }

                        customConfig.reload();
                        livesConfig.reload();
                        reloadConfig();
                        loadConfig();
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.BOLD + " Reloaded");
                        return true;
                    }

                    //-----------------------------------LIVES-----------------------------------//
                    else if (args[0].equalsIgnoreCase("lives")) {
                        if(!player.hasPermission("tinylives.lives")){
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "You do not have permission!");
                            return true;
                        }

                        if(enabledExtraLives){
                            if(customConfig.get().contains("players." + playerid + ".extra-lives")) {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix + "&aYou have &e" + customConfig.get().getInt("players." + playerid + ".extra-lives") + " &aextra lives left"));
                            } else {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix + "&aYou have &e0 &aextra lives left"));
                            }
                        }

                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',PPrefix + "&aYou have &e" + customConfig.get().getInt("players." + playerid + ".lives") + " &alives left"));
                        return true;
                    }

                    //-----------------------------------INFO-----------------------------------//
                    else if (args[0].equalsIgnoreCase("info")) {
                        if(!player.hasPermission("tinylives.info")){
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "You do not have permission!");
                            return true;
                        }

                        String message = getConfig().getString("message-settings.messages.info.message");

                        ChatUtil.NotifyPlayerString(message, player);
                        return true;
                    }

                    //-----------------------------------RESPAWN-----------------------------------//
                    else if (args[0].equalsIgnoreCase("respawn")) {
                        if(!player.hasPermission("tinylives.respawn")){
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "You do not have permission!");
                            return true;
                        }

                        if(args.length >= 2) {
                            if (Bukkit.getPlayer(args[1]) != null){
                                Player TargetPlayer = Bukkit.getPlayer(args[1]);

                                customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".lives", tinylives.getInstance().lives);
                                customConfig.save();
                                customConfig.reload();

                                PlayerUtil.ResetPlayer(TargetPlayer);

                                ChatUtil.NotifyPlayerString(PPrefix + "You have respawned " + TargetPlayer.getName(), player);

                                return true;
                            } else {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Not a valid player!");
                                return true;
                            }
                        } else {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Not the correct usage! /TinyLives respawn (Player)");
                            return true;
                        }
                    }

                    //-----------------------------------RESET(PLAYER)-----------------------------------//
                    else if (args[0].equalsIgnoreCase("reset")) {
                        if(!player.hasPermission("tinylives.reset")){
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "You do not have permission!");
                            return true;
                        }

                        if(args.length >= 2) {
                            if (Bukkit.getPlayer(args[1]) != null){
                                Player TargetPlayer = Bukkit.getPlayer(args[1]);

                                customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".lives", tinylives.getInstance().lives);
                                customConfig.save();
                                customConfig.reload();

                                PlayerUtil.ResetPlayer(TargetPlayer);

                                ChatUtil.NotifyPlayerString(PPrefix + "You have reset " + TargetPlayer.getName(), player);

                                return true;
                            } else {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Not a valid player!");
                                return true;
                            }
                        } else {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Not the correct usage! /TinyLives respawn (Player)");
                            return true;
                        }
                    }

                    //-----------------------------------RESET(ALL)-----------------------------------//
                    else if (args[0].equalsIgnoreCase("resetall")) {
                        if(!player.hasPermission("tinylives.resetall")){
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "You do not have permission!");
                            return true;
                        }

                        Object[] playerKeys = customConfig.get().getConfigurationSection("players").getKeys(false).toArray();

                        for (Object key : playerKeys){
                            if(tinylives.getInstance().getConfig().getBoolean("life-settings.random-lives.enabled")) {
                                int min = tinylives.getInstance().getConfig().getInt("life-settings.random-lives.min-lives");
                                int max = tinylives.getInstance().getConfig().getInt("life-settings.random-lives.max-lives");
                                int random_num = (int)Math.floor(Math.random()*(max-min+1)+min);

                                customConfig.get().set("players." + key + ".lives", random_num);
                            } else {
                                if(customConfig.get().contains("players." + key + ".max-lives")){
                                    customConfig.get().set("players." + key + ".lives", customConfig.get().getInt("players." + key + ".max-lives"));
                                } else {
                                    customConfig.get().set("players." + key + ".lives", tinylives.getInstance().lives);
                                }
                            }
                        }
                        customConfig.save();

                        for (Player Aplayer : Bukkit.getOnlinePlayers()) {
                            PlayerUtil.respawnPlayer(Aplayer);
                        }

                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',PPrefix + "&eYou have reset everyones lives."));
                        return true;
                    }

                    //-----------------------------------RESET(ALLEXTRA)-----------------------------------//
                    else if (args[0].equalsIgnoreCase("resetallextra")) {
                        if(!player.hasPermission("tinylives.resetallextra")){
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "You do not have permission!");
                            return true;
                        }

                        Object[] playerKeys = customConfig.get().getConfigurationSection("players").getKeys(false).toArray();

                        for (Object key : playerKeys){
                            if(customConfig.get().contains("players." + key + ".extra-lives")){
                                customConfig.get().set("players." + key + ".extra-lives", 0);
                            }
                        }
                        customConfig.save();

                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',PPrefix + "&eYou have reset everyones extra lives."));
                        return true;
                    }

                    //-----------------------------------DEBUG-----------------------------------//
                    else if (args[0].equalsIgnoreCase("debug")) {
                        if(!player.hasPermission("tinylives.debug")){
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "You do not have permission!");
                            return true;
                        }

                        if(debug == 1){
                            debug = 0;
                            getConfig().set("debug", 0);
                            saveConfig();
                            reloadConfig();
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.GREEN + "Debug mode is now off!");
                            return true;
                        } else {
                            debug = 1;
                            getConfig().set("debug", 1);
                            saveConfig();
                            reloadConfig();
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.GREEN + "Debug mode is now on!");
                            return true;
                        }
                    }

                    //-----------------------------------SET-MAX-LIVES-----------------------------------//
                    else if (args[0].equalsIgnoreCase("setmaxlives")) {
                        if(args.length >= 3){
                            if (Bukkit.getPlayer(args[1]) != null) {
                                Player TargetPlayer = Bukkit.getPlayer(args[1]);
                                int lifeAmount = 0;
                                try {
                                    lifeAmount = Integer.parseInt(args[2]);
                                } catch (NumberFormatException ignored){
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Not a valid number!");
                                    return true;
                                }
                                ChatUtil.console("Player found", 2);
                                if(customConfig.get().contains("players." + TargetPlayer.getUniqueId().toString())){
                                    ChatUtil.console("Player config found", 2);
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".max-lives", lifeAmount);
                                    customConfig.save();
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.GREEN + "You have given " + TargetPlayer.getName() + " " + lifeAmount + " more max lives!");
                                } else {
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".respawning", false);
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".dead", false);
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".lives", lives);
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".current-reset", resetNumber);
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + "max-lives", lifeAmount);
                                    customConfig.save();

                                    PlayerUtil.SetDisplayName(TargetPlayer);

                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Player is already on max lives or number given will push them past max!");
                                    return true;
                                }
                            } else {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Not a valid player!");
                                return true;
                            }
                        } else {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Not the correct usage! /TinyLives setmaxlives (Player) (Amount)");
                            return true;
                        }
                    }

                    //-----------------------------------SET-MAX-EXTRA-LIVES-----------------------------------//
                    else if (args[0].equalsIgnoreCase("setmaxextralives")) {
                        if(args.length >= 3){
                            if (Bukkit.getPlayer(args[1]) != null) {
                                Player TargetPlayer = Bukkit.getPlayer(args[1]);
                                int lifeAmount = 0;
                                try {
                                    lifeAmount = Integer.parseInt(args[2]);
                                } catch (NumberFormatException ignored){
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Not a valid number!");
                                    return true;
                                }
                                ChatUtil.console("Player found", 2);
                                if(customConfig.get().contains("players." + TargetPlayer.getUniqueId().toString())){
                                    ChatUtil.console("Player config found", 2);
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".max-extra-lives", lifeAmount);
                                    customConfig.save();
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.GREEN + "You have given " + TargetPlayer.getName() + " " + lifeAmount + " more max extra lives!");
                                } else {
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".respawning", false);
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".dead", false);
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".lives", lives);
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".current-reset", resetNumber);
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + "max-extra-lives", lifeAmount);
                                    customConfig.save();

                                    PlayerUtil.SetDisplayName(TargetPlayer);

                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Player is already on max lives or number given will push them past max!");
                                    return true;
                                }
                            } else {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Not a valid player!");
                                return true;
                            }
                        } else {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Not the correct usage! /TinyLives setmaxextralives (Player) (Amount)");
                            return true;
                        }
                    }

                    //-----------------------------------ADDLIFE-----------------------------------//
                    else if (args[0].equalsIgnoreCase("addlife")) {
                        if(!player.hasPermission("tinylives.addlife")){
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "You do not have permission!");
                            return true;
                        }

                        if(args.length >= 2) {
                            if (Bukkit.getPlayer(args[1]) != null) {
                                Player TargetPlayer = Bukkit.getPlayer(args[1]);
                                int lifeAmount = 0;
                                try {
                                    if(args.length >= 3) {
                                        lifeAmount = Integer.parseInt(args[2]);
                                    } else {
                                        lifeAmount = 1;
                                    }
                                } catch (NumberFormatException ignored) {
                                    lifeAmount = 1;
                                }
                                if(customConfig.get().contains("players." + TargetPlayer.getUniqueId().toString())){
                                    if(customConfig.get().getInt("players." + TargetPlayer.getUniqueId().toString() + ".lives") + lifeAmount <= customConfig.get().getInt("players." + TargetPlayer.getUniqueId().toString() + ".max-lives")){
                                        if(customConfig.get().getInt("players." + TargetPlayer.getUniqueId().toString() + ".lives") > 0) {
                                            //Add life
                                            customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".lives", customConfig.get().getInt("players." + TargetPlayer.getUniqueId().toString() + ".lives") + lifeAmount);
                                            customConfig.save();
                                            if(livesConfig.get().contains("lives." + customConfig.get().getInt("players." + TargetPlayer.getUniqueId().toString() + ".lives"))){
                                                if(!livesConfig.get().getBoolean("disable-life-prefixs")) {
                                                    TargetPlayer.setDisplayName(ChatColor.translateAlternateColorCodes('&', (livesConfig.get().getString("lives." + customConfig.get().getInt("players." + TargetPlayer.getUniqueId().toString() + ".lives") + ".prefix") + livesConfig.get().getString("lives." + customConfig.get().getInt("players." + TargetPlayer.getUniqueId().toString() + ".lives") + ".color") + TargetPlayer.getName() + "&r")));
                                                }
                                            }
                                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.GREEN + "Added " + lifeAmount + " lives to " + TargetPlayer.getName() + "!");
                                            return true;
                                        } else {
                                            //Respawn with 1 life
                                            PlayerUtil.ResetPlayer(TargetPlayer);

                                            int finalLifeAmount = lifeAmount;
                                            new BukkitRunnable() {
                                                public void run() {
                                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".respawning", false);
                                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".dead", false);
                                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".lives", finalLifeAmount);
                                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".current-reset", resetNumber);
                                                    customConfig.save();

                                                    PlayerUtil.SetDisplayName(TargetPlayer);

                                                    TargetPlayer.sendTitle((ChatColor.translateAlternateColorCodes('&', ("&aYou have been given another life..."))), (ChatColor.translateAlternateColorCodes('&', ("&eYou have " + finalLifeAmount + " lives left..."))), 20, 100, 20);
                                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.GREEN + "Added " + finalLifeAmount + " lives to " + TargetPlayer.getName() + "!");
                                                }
                                            }.runTaskLater(this, 10);

                                            return true;
                                        }
                                    } else {
                                        //Already at max lives.
                                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Player is already on max lives or number given will push them above max!");
                                        return true;
                                    }
                                } else {
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".respawning", false);
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".dead", false);
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".lives", lives);
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".current-reset", resetNumber);
                                    customConfig.save();

                                    PlayerUtil.SetDisplayName(TargetPlayer);

                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Player is already on max lives!");
                                    return true;
                                }
                            } else {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Not a valid player!");
                                return true;
                            }
                        } else {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Not the correct usage! /TinyLives addlife (Player)");
                            return true;
                        }
                    }

                    //-----------------------------------REMOVELIFE-----------------------------------//
                    else if (args[0].equalsIgnoreCase("removelife")) {
                        if(!player.hasPermission("tinylives.removelife")){
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "You do not have permission!");
                            return true;
                        }

                        if(args.length >= 2) {
                            if (Bukkit.getPlayer(args[1]) != null) {
                                Player TargetPlayer = Bukkit.getPlayer(args[1]);
                                int lifeAmount = 0;
                                try {
                                    if(args.length >= 3) {
                                        lifeAmount = Integer.parseInt(args[2]);
                                    } else {
                                        lifeAmount = 1;
                                    }
                                } catch (NumberFormatException ignored){
                                    lifeAmount = 1;
                                }
                                if(customConfig.get().contains("players." + TargetPlayer.getUniqueId().toString())){
                                    if(customConfig.get().getInt("players." + TargetPlayer.getUniqueId().toString() + ".lives") > 0){
                                        if(customConfig.get().getInt("players." + TargetPlayer.getUniqueId().toString() + ".lives") > lifeAmount) {
                                            //Remove life
                                            customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".lives", customConfig.get().getInt("players." + TargetPlayer.getUniqueId().toString() + ".lives") - lifeAmount);
                                            customConfig.save();

                                            PlayerUtil.SetDisplayName(TargetPlayer);

                                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.GREEN + "Removed " + lifeAmount + " lives from " + TargetPlayer.getName() + "!");
                                            return true;
                                        } else {
                                            //Kill player
                                            customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".lives", 1);
                                            customConfig.save();
                                            PlayerUtil.KillPlayer(TargetPlayer);

                                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.GREEN + "Removed " + lifeAmount + " lives from " + TargetPlayer.getName() + "!");
                                            return true;
                                        }
                                    } else {
                                        //Already at max lives.
                                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Player is already out of lives!");
                                        return true;
                                    }
                                } else {
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".respawning", false);
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".dead", false);
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".lives", lives);
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".current-reset", resetNumber);
                                    customConfig.save();

                                    PlayerUtil.SetDisplayName(TargetPlayer);

                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Player is already on max lives!");
                                    return true;
                                }
                            } else {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Not a valid player!");
                                return true;
                            }
                        } else {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Not the correct usage! /TinyLives removelife (Player)");
                            return true;
                        }
                    }

                    //-----------------------------------ADDEXTRALIFE-----------------------------------//
                    else if (args[0].equalsIgnoreCase("addextralife")) {
                        if (!player.hasPermission("tinylives.addextralife")) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "You do not have permission!");
                            return true;
                        }

                        if(args.length >= 2) {
                            if (Bukkit.getPlayer(args[1]) != null) {
                                Player TargetPlayer = Bukkit.getPlayer(args[1]);
                                int lifeAmount = 0;
                                try {
                                    if(args.length >= 3) {
                                        lifeAmount = Integer.parseInt(args[2]);
                                    } else {
                                        lifeAmount = 1;
                                    }
                                } catch (NumberFormatException ignored){
                                    lifeAmount = 1;
                                }
                                if (customConfig.get().contains("players." + TargetPlayer.getUniqueId().toString())) {
                                    if (customConfig.get().contains("players." + TargetPlayer.getUniqueId().toString() + ".extra-lives") && customConfig.get().contains("players." + TargetPlayer.getUniqueId().toString() + ".max-extra-lives")) {
                                        if(customConfig.get().getInt("players." + TargetPlayer.getUniqueId().toString() + ".extra-lives") + lifeAmount <= customConfig.get().getInt("players." + TargetPlayer.getUniqueId().toString() + ".max-extra-lives")){
                                            customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".extra-lives", customConfig.get().getInt("players." + TargetPlayer.getUniqueId().toString() + ".extra-lives") + lifeAmount);
                                            customConfig.save();
                                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.GREEN + "You have given " + lifeAmount +" extra lives to " + TargetPlayer.getName().toString() + "!");
                                            return true;
                                        } else {
                                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Player is already on max extra lives or the amount of lives given will push them past max!");
                                            return true;
                                        }
                                    } else {
                                        customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".extra-lives", lifeAmount);
                                        PlayerUtil.checkMaxExtraLivesAmount(TargetPlayer);
                                        customConfig.save();
                                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.GREEN + "You have given " + lifeAmount +" extra lives to " + TargetPlayer.getName().toString() + "!");
                                        return true;
                                    }
                                } else {
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".respawning", false);
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".dead", false);
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".lives", lives);
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".current-reset", resetNumber);
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".extra-lives", 1);
                                    customConfig.save();

                                    PlayerUtil.SetDisplayName(TargetPlayer);

                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.GREEN + "You have given " + lifeAmount +" extra lives to " + TargetPlayer.getName().toString() + "!");
                                    return true;
                                }
                            } else {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Invalid player! /TinyLives addextralife (player)");
                                return true;
                            }
                        } else {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Invalid usage! /TinyLives addextralife (player)");
                            return true;
                        }
                    }

                    //-----------------------------------REMOVEEXTRALIFE-----------------------------------//
                    else if (args[0].equalsIgnoreCase("removeextralife")) {
                        if (!player.hasPermission("tinylives.removeextralife")) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "You do not have permission!");
                            return true;
                        }

                        if(args.length >= 2) {
                            if (Bukkit.getPlayer(args[1]) != null) {
                                Player TargetPlayer = Bukkit.getPlayer(args[1]);
                                int lifeAmount = 0;
                                try {
                                    if(args.length >= 3) {
                                        lifeAmount = Integer.parseInt(args[2]);
                                    } else {
                                        lifeAmount = 1;
                                    }
                                } catch (NumberFormatException ignored){
                                    lifeAmount = 1;
                                }
                                if (customConfig.get().contains("players." + TargetPlayer.getUniqueId().toString())) {
                                    if (customConfig.get().contains("players." + TargetPlayer.getUniqueId().toString() + ".extra-lives")) {
                                        if(customConfig.get().getInt("players." + TargetPlayer.getUniqueId().toString() + ".extra-lives") - lifeAmount >= 0){
                                            customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".extra-lives", customConfig.get().getInt("players." + TargetPlayer.getUniqueId().toString() + ".extra-lives") - lifeAmount);
                                            customConfig.save();
                                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.GREEN + "You have removed " + lifeAmount + " extra lives from " + TargetPlayer.getName().toString() + "!");
                                            return true;
                                        } else {
                                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Player is already out of extra lives or number given will put them below 0!");
                                            return true;
                                        }
                                    } else {
                                        customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".extra-lives", 0);
                                        customConfig.save();
                                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.GREEN + "You have removed " + lifeAmount + " extra lives from " + TargetPlayer.getName().toString() + "!");
                                        return true;
                                    }
                                } else {
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".respawning", false);
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".dead", false);
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".lives", lives);
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".current-reset", resetNumber);
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".extra-lives", 0);
                                    customConfig.save();

                                    PlayerUtil.SetDisplayName(TargetPlayer);

                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.GREEN + "You have removed " + lifeAmount + " extra lives from " + TargetPlayer.getName().toString() + "!");
                                    return true;
                                }
                            } else {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Invalid player! /TinyLives removeextralife (player)");
                                return true;
                            }
                        } else {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Invalid usage! /TinyLives removeextralife (player)");
                            return true;
                        }
                    }

                    //-----------------------------------GIVELIFE-----------------------------------//
                    else if (args[0].equalsIgnoreCase("givelife")) {
                        if (!player.hasPermission("tinylives.givelife")) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "You do not have permission!");
                            return true;
                        }

                        if(args.length >= 2) {
                            if (Bukkit.getPlayer(args[1]) != null) {
                                Player TargetPlayer = Bukkit.getPlayer(args[1]);
                                if (customConfig.get().contains("players." + TargetPlayer.getUniqueId().toString())) {
                                    if (customConfig.get().contains("players." + TargetPlayer.getUniqueId().toString() + ".lives")) {
                                        if(customConfig.get().getInt("players." + TargetPlayer.getUniqueId().toString() + ".lives") >= 1){
                                            if(!(customConfig.get().getInt("players." + TargetPlayer.getUniqueId().toString() + ".lives") >= lives)) {
                                                if(customConfig.get().getInt("players." + player.getUniqueId().toString() + ".lives") >= 1) {
                                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".lives", customConfig.get().getInt("players." + TargetPlayer.getUniqueId().toString() + ".lives") + 1);
                                                    customConfig.get().set("players." + player.getUniqueId().toString() + ".lives", customConfig.get().getInt("players." + player.getUniqueId().toString() + ".lives") - 1);
                                                    customConfig.save();
                                                    customConfig.reload();
                                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.GREEN + "You have given a life to " + TargetPlayer.getName().toString() + "!");
                                                    TargetPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.GREEN + player.getName() + " Has given you a life!");

                                                    PlayerUtil.sendSound(player, "ITEM_TOTEM_USE", 2, 2);
                                                    PlayerUtil.SetDisplayName(player);
                                                    PlayerUtil.SetDisplayName(TargetPlayer);

                                                    return true;
                                                } else {
                                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "You only have 1 life left! You cannot give this one away!");
                                                    return true;
                                                }
                                            } else {
                                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "This player has max lives!");
                                                return true;
                                            }
                                        } else {
                                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "This player is dead.");
                                            return true;
                                        }
                                    }
                                } else {
                                    ChatUtil.console("PLAYER WHO WAS GIVEN A LIFE HAS NO CONFIG VALUES", 1);
                                }
                            } else {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Invalid player! /TinyLives givelife (player)");
                                return true;
                            }
                        } else {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Invalid usage! /TinyLives givelife (player)");
                            return true;
                        }
                    }
                } else {
                    //CONSOLE SENT COMMAND
                    //-----------------------------------HELP-----------------------------------//
                    if (args[0].equalsIgnoreCase("help")) {
                        getLogger().info(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.BOLD + "List of commands:");
                        getLogger().info(ChatColor.GREEN + "/TinyLives reload " + ChatColor.GRAY + "-" + ChatColor.BLUE + " Reloads player data.");
                        getLogger().info(ChatColor.GREEN + "/TinyLives lives " + ChatColor.GRAY + "-" + ChatColor.BLUE + " Tells you how many lives you have left.");
                        getLogger().info(ChatColor.GREEN + "/TinyLives info " + ChatColor.GRAY + "-" + ChatColor.BLUE + " Lists the your lives and how long till reset.");
                        getLogger().info(ChatColor.GREEN + "/TinyLives respawn (player) " + ChatColor.GRAY + "-" + ChatColor.BLUE + " Respawn a player to full lives if they have died.");
                        getLogger().info(ChatColor.GREEN + "/TinyLives resetall " + ChatColor.GRAY + "-" + ChatColor.BLUE + " Reset all players lives. This forces all players to new life amounts.");
                        getLogger().info(ChatColor.GREEN + "/TinyLives resetallextra " + ChatColor.GRAY + "-" + ChatColor.BLUE + " Reset all players extra lives.");
                        getLogger().info(ChatColor.GREEN + "/TinyLives reset (player) " + ChatColor.GRAY + "-" + ChatColor.BLUE + " Resets a specific players lives.");
                        getLogger().info(ChatColor.GREEN + "/TinyLives debug " + ChatColor.GRAY + "-" + ChatColor.BLUE + " Enables/Disables debug mode.");
                        getLogger().info(ChatColor.GREEN + "/TinyLives addlife (player) (amount) " + ChatColor.GRAY + "-" + ChatColor.BLUE + " Adds 1 life to specific player.");
                        getLogger().info(ChatColor.GREEN + "/TinyLives removelife (player) (amount) " + ChatColor.GRAY + "-" + ChatColor.BLUE + " Removes 1 life from specific player.");
                        getLogger().info(ChatColor.GREEN + "/TinyLives addextralife (player) (amount) " + ChatColor.GRAY + "-" + ChatColor.BLUE + " Adds 1 extra life to specific player.");
                        getLogger().info(ChatColor.GREEN + "/TinyLives removeextralife (player) (amount) " + ChatColor.GRAY + "-" + ChatColor.BLUE + " Removes 1 extra life from specific player.");
                        getLogger().info(ChatColor.GREEN + "/TinyLives givelife (player) " + ChatColor.GRAY + "-" + ChatColor.BLUE + "  Gives 1 of your lives to specified player.");
                        getLogger().info(ChatColor.GREEN + "/Lives " + ChatColor.GRAY + "-" + ChatColor.BLUE + " Tells you how many lives you have left.");
                        return true;
                    }
                    //-----------------------------------RELOAD-----------------------------------//
                    else if (args[0].equalsIgnoreCase("reload")) {
                        customConfig.reload();
                        livesConfig.reload();
                        reloadConfig();
                        loadConfig();
                        getLogger().info(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.BOLD + " Reloaded");
                        return true;
                    }
                    //-----------------------------------LIVES-----------------------------------//
                    else if (args[0].equalsIgnoreCase("lives")) {
                        getLogger().info("This command must be used by a player.");
                        return true;
                    }
                    //-----------------------------------GIVELIFE-----------------------------------//
                    else if (args[0].equalsIgnoreCase("givelife")) {
                        getLogger().info("This command must be used by a player.");
                        return true;
                    }
                    //-----------------------------------INFO-----------------------------------//
                    else if (args[0].equalsIgnoreCase("info")) {
                        getLogger().info("This command must be used by a player.");

                        return true;
                    }
                    //-----------------------------------RESPAWN-----------------------------------//
                    else if (args[0].equalsIgnoreCase("respawn")) {
                        if(args.length >= 2) {
                            if (Bukkit.getPlayer(args[1]) != null){
                                Player TargetPlayer = Bukkit.getPlayer(args[1]);

                                customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".lives", tinylives.getInstance().lives);
                                customConfig.save();
                                customConfig.reload();

                                PlayerUtil.ResetPlayer(TargetPlayer);
                                TargetPlayer.sendTitle((ChatColor.translateAlternateColorCodes('&', ("&a&lYour lives were reset..."))), (ChatColor.translateAlternateColorCodes('&', ("&eYou have " + lives + " lives left..."))), 20, 100, 20);
                                getLogger().info(ChatColor.translateAlternateColorCodes('&', PPrefix) + "You have respawned " + TargetPlayer.getName());
                                return true;
                            } else {
                                getLogger().info(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Not a valid player!");
                                return true;
                            }
                        } else {
                            getLogger().info(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Not the correct usage! /TinyLives respawn (Player)");
                            return true;
                        }
                    }
                    //-----------------------------------RESET(PLAYER)-----------------------------------//
                    else if (args[0].equalsIgnoreCase("reset")) {
                        if(args.length >= 2) {
                            if (Bukkit.getPlayer(args[1]) != null){
                                Player TargetPlayer = Bukkit.getPlayer(args[1]);

                                customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".lives", tinylives.getInstance().lives);
                                customConfig.save();
                                customConfig.reload();

                                PlayerUtil.ResetPlayer(TargetPlayer);
                                return true;
                            } else {
                                getLogger().info(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Not a valid player!");
                                return true;
                            }
                        } else {
                            getLogger().info(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Not the correct usage! /TinyLives respawn (Player)");
                            return true;
                        }
                    }

                    //-----------------------------------RESET(ALL)-----------------------------------//
                    else if (args[0].equalsIgnoreCase("resetall")) {
                        Object[] playerKeys = customConfig.get().getConfigurationSection("players").getKeys(false).toArray();

                        for (Object key : playerKeys){
                            if(tinylives.getInstance().getConfig().getBoolean("life-settings.random-lives.enabled")) {
                                int min = tinylives.getInstance().getConfig().getInt("life-settings.random-lives.min-lives");
                                int max = tinylives.getInstance().getConfig().getInt("life-settings.random-lives.max-lives");
                                int random_num = (int)Math.floor(Math.random()*(max-min+1)+min);

                                customConfig.get().set("players." + key + ".lives", random_num);
                            } else {
                                if(customConfig.get().contains("players." + key + ".max-lives")){
                                    customConfig.get().set("players." + key + ".lives", customConfig.get().getInt("players." + key + ".max-lives"));
                                } else {
                                    customConfig.get().set("players." + key + ".lives", tinylives.getInstance().lives);
                                }
                            }
                        }
                        customConfig.save();

                        for (Player Aplayer : Bukkit.getOnlinePlayers()) {
                            PlayerUtil.respawnPlayer(Aplayer);
                        }

                        getLogger().info(ChatColor.translateAlternateColorCodes('&',PPrefix + "&eYou have reset everyones lives."));
                        return true;
                    }

                    //-----------------------------------RESET(ALLEXTRA)-----------------------------------//
                    else if (args[0].equalsIgnoreCase("resetallextra")) {
                        Object[] playerKeys = customConfig.get().getConfigurationSection("players").getKeys(false).toArray();

                        for (Object key : playerKeys){
                            if(customConfig.get().contains("players." + key + ".extra-lives")){
                                customConfig.get().set("players." + key + ".extra-lives", 0);
                            }
                        }
                        customConfig.save();

                        getLogger().info(ChatColor.translateAlternateColorCodes('&',PPrefix + "&eYou have reset everyones extra lives."));
                        return true;
                    }

                    //-----------------------------------DEBUG-----------------------------------//
                    else if (args[0].equalsIgnoreCase("debug")) {
                        if(debug == 1){
                            debug = 0;
                            getConfig().set("debug", 0);
                            saveConfig();
                            reloadConfig();
                            getLogger().info(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.GREEN + "Debug mode is now off!");
                            return true;
                        } else {
                            debug = 1;
                            getConfig().set("debug", 1);
                            saveConfig();
                            reloadConfig();
                            getLogger().info(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.GREEN + "Debug mode is now on!");
                            return true;
                        }
                    }

                    //-----------------------------------SET-MAX-LIVES-----------------------------------//
                    else if (args[0].equalsIgnoreCase("setmaxlives")) {
                        if(args.length >= 3){
                            if (Bukkit.getPlayer(args[1]) != null) {
                                Player TargetPlayer = Bukkit.getPlayer(args[1]);
                                int lifeAmount = 0;
                                try {
                                    lifeAmount = Integer.parseInt(args[2]);
                                } catch (NumberFormatException ignored){
                                    getLogger().info(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Not a valid number!");
                                    return true;
                                }
                                ChatUtil.console("Player found", 2);
                                if(customConfig.get().contains("players." + TargetPlayer.getUniqueId().toString())){
                                    ChatUtil.console("Player config found", 2);
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".max-lives", lifeAmount);
                                    customConfig.save();
                                    getLogger().info(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.GREEN + "You have given " + TargetPlayer.getName() + " " + lifeAmount + " more max lives!");
                                } else {
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".respawning", false);
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".dead", false);
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".lives", lives);
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".current-reset", resetNumber);
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + "max-lives", lifeAmount);
                                    customConfig.save();

                                    PlayerUtil.SetDisplayName(TargetPlayer);

                                    getLogger().info(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Player is already on max lives or number given will push them past max!");
                                    return true;
                                }
                            } else {
                                getLogger().info(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Not a valid player!");
                                return true;
                            }
                        } else {
                            getLogger().info(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Not the correct usage! /TinyLives setmaxlives (Player) (Amount)");
                            return true;
                        }
                    }

                    //-----------------------------------SET-MAX-EXTRA-LIVES-----------------------------------//
                    else if (args[0].equalsIgnoreCase("setmaxextralives")) {
                        if(args.length >= 3){
                            if (Bukkit.getPlayer(args[1]) != null) {
                                Player TargetPlayer = Bukkit.getPlayer(args[1]);
                                int lifeAmount = 0;
                                try {
                                    lifeAmount = Integer.parseInt(args[2]);
                                } catch (NumberFormatException ignored){
                                    getLogger().info(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Not a valid number!");
                                    return true;
                                }
                                ChatUtil.console("Player found", 2);
                                if(customConfig.get().contains("players." + TargetPlayer.getUniqueId().toString())){
                                    ChatUtil.console("Player config found", 2);
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".max-extra-lives", lifeAmount);
                                    customConfig.save();
                                    getLogger().info(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.GREEN + "You have given " + TargetPlayer.getName() + " " + lifeAmount + " more max extra lives!");
                                } else {
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".respawning", false);
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".dead", false);
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".lives", lives);
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".current-reset", resetNumber);
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + "max-extra-lives", lifeAmount);
                                    customConfig.save();

                                    PlayerUtil.SetDisplayName(TargetPlayer);

                                    getLogger().info(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Player is already on max lives or number given will push them past max!");
                                    return true;
                                }
                            } else {
                                getLogger().info(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Not a valid player!");
                                return true;
                            }
                        } else {
                            getLogger().info(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Not the correct usage! /TinyLives setmaxextralives (Player) (Amount)");
                            return true;
                        }
                    }

                    //-----------------------------------ADDLIFE-----------------------------------//
                    else if (args[0].equalsIgnoreCase("addlife")) {
                        if(args.length >= 2) {
                            if (Bukkit.getPlayer(args[1]) != null) {
                                Player TargetPlayer = Bukkit.getPlayer(args[1]);
                                int lifeAmount = 0;
                                try {
                                    if(args.length >= 3) {
                                        lifeAmount = Integer.parseInt(args[2]);
                                    } else {
                                        lifeAmount = 1;
                                    }
                                } catch (NumberFormatException ignored){
                                    lifeAmount = 1;
                                }
                                ChatUtil.console("Player found", 2);
                                if(customConfig.get().contains("players." + TargetPlayer.getUniqueId().toString())){
                                    ChatUtil.console("Player config found", 2);
                                    if(customConfig.get().getInt("players." + TargetPlayer.getUniqueId().toString() + ".lives") + lifeAmount <= customConfig.get().getInt("players." + TargetPlayer.getUniqueId().toString() + ".max-lives")){
                                        ChatUtil.console("Player config found check lives", 2);
                                        if(customConfig.get().getInt("players." + TargetPlayer.getUniqueId().toString() + ".lives") > 0) {
                                            //Add life
                                            ChatUtil.console("Adding lives", 2);
                                            customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".lives", customConfig.get().getInt("players." + TargetPlayer.getUniqueId().toString() + ".lives") + lifeAmount);
                                            customConfig.save();
                                            if(livesConfig.get().contains("lives." + customConfig.get().getInt("players." + TargetPlayer.getUniqueId().toString() + ".lives"))){
                                                if(!livesConfig.get().getBoolean("disable-life-prefixs")) {
                                                    PlayerUtil.SetDisplayName(TargetPlayer);
                                                    //TargetPlayer.setDisplayName(ChatColor.translateAlternateColorCodes('&', (livesConfig.get().getString("lives." + customConfig.get().getInt("players." + TargetPlayer.getUniqueId().toString() + ".lives") + ".prefix") + livesConfig.get().getString("lives." + customConfig.get().getInt("players." + TargetPlayer.getUniqueId().toString() + ".lives") + ".color") + TargetPlayer.getName() + "&r")));
                                                }
                                            }
                                            getLogger().info(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.GREEN + "Added " + lifeAmount + " lives to " + TargetPlayer.getName() + "!");
                                            return true;
                                        } else {
                                            //Respawn with 1 life

                                            PlayerUtil.ResetPlayer(TargetPlayer);

                                            int finalLifeAmount = lifeAmount;
                                            new BukkitRunnable() {
                                                public void run() {
                                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".respawning", false);
                                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".dead", false);
                                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".lives", finalLifeAmount);
                                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".current-reset", resetNumber);
                                                    customConfig.save();
                                                    if(livesConfig.get().contains("lives." + customConfig.get().getInt("players." + TargetPlayer.getUniqueId().toString() + ".lives"))){
                                                        if(!livesConfig.get().getBoolean("disable-life-prefixs")) {
                                                            TargetPlayer.setDisplayName(ChatColor.translateAlternateColorCodes('&', (livesConfig.get().getString("lives." + customConfig.get().getInt("players." + TargetPlayer.getUniqueId().toString() + ".lives") + ".prefix") + livesConfig.get().getString("lives." + customConfig.get().getInt("players." + TargetPlayer.getUniqueId().toString() + ".lives") + ".color") + TargetPlayer.getName() + "&r")));
                                                        }
                                                    }
                                                    TargetPlayer.sendTitle((ChatColor.translateAlternateColorCodes('&', ("&aYou have been given another life..."))), (ChatColor.translateAlternateColorCodes('&', ("&eYou have " + 1 + " lives left..."))), 20, 100, 20);
                                                    getLogger().info(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.GREEN + "Added " + finalLifeAmount + " lives to " + TargetPlayer.getName() + "!");
                                                }
                                            }.runTaskLater(this, 10);

                                            return true;
                                        }
                                    } else {
                                        //Already at max lives.
                                        getLogger().info(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Player is already on max lives or number given will push them past max!");
                                        return true;
                                    }
                                } else {
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".respawning", false);
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".dead", false);
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".lives", lives);
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".current-reset", resetNumber);
                                    customConfig.save();

                                    PlayerUtil.SetDisplayName(TargetPlayer);

                                    getLogger().info(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Player is already on max lives or number given will push them past max!");
                                    return true;
                                }
                            } else {
                                getLogger().info(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Not a valid player!");
                                return true;
                            }
                        } else {
                            getLogger().info(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Not the correct usage! /TinyLives addlife (Player)");
                            return true;
                        }
                    }

                    //-----------------------------------REMOVELIFE-----------------------------------//
                    else if (args[0].equalsIgnoreCase("removelife")) {
                        if(args.length >= 2) {
                            if (Bukkit.getPlayer(args[1]) != null) {
                                Player TargetPlayer = Bukkit.getPlayer(args[1]);
                                int lifeAmount = 0;
                                try {
                                    if(args.length >= 3) {
                                        lifeAmount = Integer.parseInt(args[2]);
                                    } else {
                                        lifeAmount = 1;
                                    }
                                } catch (NumberFormatException ignored){
                                    lifeAmount = 1;
                                }
                                if(customConfig.get().contains("players." + TargetPlayer.getUniqueId().toString())){
                                    if(customConfig.get().getInt("players." + TargetPlayer.getUniqueId().toString() + ".lives") > 0){
                                        if(customConfig.get().getInt("players." + TargetPlayer.getUniqueId().toString() + ".lives") > lifeAmount) {
                                            //Remove life
                                            customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".lives", customConfig.get().getInt("players." + TargetPlayer.getUniqueId().toString() + ".lives") - lifeAmount);
                                            customConfig.save();

                                            PlayerUtil.SetDisplayName(TargetPlayer);

                                            getLogger().info(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.GREEN + "Removed " + lifeAmount + " lives from " + TargetPlayer.getName() + "!");
                                            return true;
                                        } else {
                                            //Kill player
                                            customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".lives", 1);
                                            customConfig.save();
                                            PlayerUtil.KillPlayer(TargetPlayer);

                                            getLogger().info(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.GREEN + "Removed " + lifeAmount + " lives from " + TargetPlayer.getName() + "!");
                                            return true;
                                        }
                                    } else {
                                        //Already at max lives.
                                        getLogger().info(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Player is already out of lives!");
                                        return true;
                                    }
                                } else {
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".respawning", false);
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".dead", false);
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".lives", lives);
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".current-reset", resetNumber);
                                    customConfig.save();

                                    PlayerUtil.SetDisplayName(TargetPlayer);

                                    getLogger().info(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Player is already on max lives!");
                                    return true;
                                }
                            } else {
                                getLogger().info(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Not a valid player!");
                                return true;
                            }
                        } else {
                            getLogger().info(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Not the correct usage! /TinyLives removelife (Player)");
                            return true;
                        }
                    }

                    //-----------------------------------ADDEXTRALIFE-----------------------------------//
                    else if (args[0].equalsIgnoreCase("addextralife")) {
                        if(args.length >= 2) {
                            if (Bukkit.getPlayer(args[1]) != null) {
                                Player TargetPlayer = Bukkit.getPlayer(args[1]);
                                int lifeAmount = 0;
                                try {
                                    if(args.length >= 3) {
                                        lifeAmount = Integer.parseInt(args[2]);
                                    } else {
                                        lifeAmount = 1;
                                    }
                                } catch (NumberFormatException ignored){
                                    lifeAmount = 1;
                                }
                                if (customConfig.get().contains("players." + TargetPlayer.getUniqueId().toString())) {
                                    if (customConfig.get().contains("players." + TargetPlayer.getUniqueId().toString() + ".extra-lives")) {
                                        if(customConfig.get().getInt("players." + TargetPlayer.getUniqueId().toString() + ".extra-lives") + lifeAmount <= customConfig.get().getInt("players." + TargetPlayer.getUniqueId().toString() + ".max-extra-lives")){
                                            customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".extra-lives", customConfig.get().getInt("players." + TargetPlayer.getUniqueId().toString() + ".extra-lives") + lifeAmount);
                                            customConfig.save();
                                            getLogger().info(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.GREEN + "You have given " + lifeAmount + " extra lives to " + TargetPlayer.getName().toString() + "!");
                                            return true;
                                        } else {
                                            getLogger().info(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Player is already on max extra lives or number will push them past max!");
                                            return true;
                                        }
                                    } else {
                                        customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".extra-lives", lifeAmount);
                                        customConfig.save();
                                        customConfig.reload();
                                        getLogger().info(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.GREEN + "You have given " + lifeAmount + " extra lives to " + TargetPlayer.getName().toString() + "!");
                                        return true;
                                    }
                                } else {
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".respawning", false);
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".dead", false);
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".lives", lives);
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".current-reset", resetNumber);
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".extra-lives", lifeAmount);
                                    customConfig.save();

                                    PlayerUtil.SetDisplayName(TargetPlayer);

                                    getLogger().info(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.GREEN + "You have given " + lifeAmount + " extra lives to " + TargetPlayer.getName().toString() + "!");
                                    return true;
                                }
                            } else {
                                getLogger().info(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Invalid player! /TinyLives addextralife (player)");
                                return true;
                            }
                        } else {
                            getLogger().info(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Invalid usage! /TinyLives addextralife (player)");
                            return true;
                        }
                    }

                    //-----------------------------------REMOVEEXTRALIFE-----------------------------------//
                    else if (args[0].equalsIgnoreCase("removeextralife")) {
                        if(args.length >= 2) {
                            if (Bukkit.getPlayer(args[1]) != null) {
                                Player TargetPlayer = Bukkit.getPlayer(args[1]);
                                int lifeAmount = 0;
                                try {
                                    if(args.length >= 3) {
                                        lifeAmount = Integer.parseInt(args[2]);
                                    } else {
                                        lifeAmount = 1;
                                    }
                                } catch (NumberFormatException ignored){
                                    lifeAmount = 1;
                                }
                                if (customConfig.get().contains("players." + TargetPlayer.getUniqueId().toString())) {
                                    if (customConfig.get().contains("players." + TargetPlayer.getUniqueId().toString() + ".extra-lives")) {
                                        if(customConfig.get().getInt("players." + TargetPlayer.getUniqueId().toString() + ".extra-lives") - lifeAmount >= 0){
                                            customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".extra-lives", customConfig.get().getInt("players." + TargetPlayer.getUniqueId().toString() + ".extra-lives") - lifeAmount);
                                            customConfig.save();
                                            getLogger().info(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.GREEN + "You have removed " + lifeAmount + " extra lives from " + TargetPlayer.getName().toString() + "!");
                                            return true;
                                        } else {
                                            getLogger().info(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Player is already out of extra lives!");
                                            return true;
                                        }
                                    } else {
                                        customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".extra-lives", 0);
                                        customConfig.save();
                                        getLogger().info(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.GREEN + "You have removed " + lifeAmount + " extra lives from " + TargetPlayer.getName().toString() + "!");
                                        return true;
                                    }
                                } else {
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".respawning", false);
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".dead", false);
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".lives", lives);
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".current-reset", resetNumber);
                                    customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".extra-lives", 0);
                                    customConfig.save();

                                    PlayerUtil.SetDisplayName(TargetPlayer);

                                    getLogger().info(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.GREEN + "You have removed " + lifeAmount + " extra lives from " + TargetPlayer.getName().toString() + "!");
                                    return true;
                                }
                            } else {
                                getLogger().info(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Invalid player! /TinyLives removeextralife (player)");
                                return true;
                            }
                        } else {
                            getLogger().info(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Invalid usage! /TinyLives removeextralife (player)");
                            return true;
                        }
                    }
                }
            }

            // --------------------------- LIVES ---------------------- //
        } else if (command.getName().equalsIgnoreCase("lives")){
            if(sender instanceof Player) {
                Player player = (Player) sender;
                String playerid = player.getUniqueId().toString();

                if(!player.hasPermission("tinylives.lives")){
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "You do not have permission!");
                    return true;
                }

                if(enabledExtraLives){
                    if(customConfig.get().contains("players." + playerid + ".extra-lives")) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix + "&aYou have &e" + customConfig.get().getInt("players." + playerid + ".extra-lives") + " &aextra lives left"));
                    } else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix + "&aYou have &e0 &aextra lives left"));
                    }
                }

                player.sendMessage(ChatColor.translateAlternateColorCodes('&',PPrefix + "&aYou have &e" + customConfig.get().getInt("players." + playerid + ".lives") + " &alives left"));
                return true;
            } else {
                getLogger().info("This command must be used by a player.");
            }

            // --------------------------- LIVES ---------------------- //

            // --------------------------- GIVELIFE ---------------------- //
        } else if (command.getName().equalsIgnoreCase("givelife")){
            if(sender instanceof Player) {
                Player player = (Player) sender;
                String playerid = player.getUniqueId().toString();

                if (!player.hasPermission("tinylives.givelife")) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "You do not have permission!");
                    return true;
                }

                if(args.length >= 1) {
                    if (Bukkit.getPlayer(args[0]) != null) {
                        Player TargetPlayer = Bukkit.getPlayer(args[0]);
                        if (customConfig.get().contains("players." + TargetPlayer.getUniqueId().toString())) {
                            if (customConfig.get().contains("players." + TargetPlayer.getUniqueId().toString() + ".lives")) {
                                if(customConfig.get().getInt("players." + TargetPlayer.getUniqueId().toString() + ".lives") >= 1){
                                    if(!(customConfig.get().getInt("players." + TargetPlayer.getUniqueId().toString() + ".lives") >= lives)) {
                                        if(customConfig.get().getInt("players." + player.getUniqueId().toString() + ".lives") >= 1) {
                                            customConfig.get().set("players." + TargetPlayer.getUniqueId().toString() + ".lives", customConfig.get().getInt("players." + TargetPlayer.getUniqueId().toString() + ".lives") + 1);
                                            customConfig.get().set("players." + player.getUniqueId().toString() + ".lives", customConfig.get().getInt("players." + player.getUniqueId().toString() + ".lives") - 1);
                                            customConfig.save();
                                            customConfig.reload();
                                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.GREEN + "You have given a life to " + TargetPlayer.getName().toString() + "!");
                                            TargetPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.GREEN + player.getName() + " Has given you a life!");

                                            PlayerUtil.sendSound(player, "ITEM_TOTEM_USE", 2, 2);
                                            PlayerUtil.SetDisplayName(player);
                                            PlayerUtil.SetDisplayName(TargetPlayer);

                                            return true;
                                        } else {
                                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "You only have 1 life left! You cannot give this one away!");
                                            return true;
                                        }
                                    } else {
                                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "This player has max lives!");
                                        return true;
                                    }
                                } else {
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "This player is dead.");
                                    return true;
                                }
                            }
                        } else {
                            ChatUtil.console("PLAYER WHO WAS GIVEN A LIFE HAS NO CONFIG VALUES", 1);
                        }
                    } else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Invalid player! /TinyLives givelife (player)");
                        return true;
                    }
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', PPrefix) + ChatColor.RED + "Invalid usage! /TinyLives givelife (player)");
                    return true;
                }
            } else {
                getLogger().info("This command must be used by a player.");
            }

            // --------------------------- GIVELIFE ---------------------- //
        }
        return true;
    }
}
