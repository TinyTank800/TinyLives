package me.tinytank800.tinylives.files;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.tinytank800.tinylives.tinylives;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

public class SpigotExpansion extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "tinylives";
    }

    @Override
    public @NotNull String getAuthor() {
        return "TinyTank800";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @NotNull String onPlaceholderRequest(Player player, @NotNull String params) {
        if(player == null){
            return "";
        }

        if(!customConfig.get().contains("players." + player.getUniqueId().toString())){
            return "";
        }

        if(params.equals("lives")){
            if(livesConfig.get().contains("lives." + customConfig.get().getInt("players." + player.getUniqueId().toString() + ".lives"))){
                return customConfig.get().getInt("players." + player.getUniqueId().toString() + ".lives") + "";
            } else {
                return "Unknown";
            }
        }

        if(params.equals("extra_lives")){
            if(livesConfig.get().contains("lives." + customConfig.get().getInt("players." + player.getUniqueId().toString() + ".extra-lives"))){
                return customConfig.get().getInt("players." + player.getUniqueId().toString() + ".extra-lives") + "";
            } else {
                return "0";
            }
        }

        if(params.equals("player")){
            if(livesConfig.get().contains("lives." + customConfig.get().getInt("players." + player.getUniqueId().toString() + ".lives"))){
                return ChatColor.translateAlternateColorCodes('&', (livesConfig.get().getString("lives." + customConfig.get().getInt("players." + player.getUniqueId().toString() + ".lives") + ".color") + player.getName()));
            } else {
                return player.getName();
            }
        }

        if(params.equals("player_raw")){
            return player.getName();
        }

        if(params.equals("lives_prefix")){
            if(customConfig.get().getBoolean("players." + player.getUniqueId().toString() + ".endless")){
                return ChatColor.translateAlternateColorCodes('&', (livesConfig.get().getString("lives.endless.prefix")));
            } else if(livesConfig.get().contains("lives." + customConfig.get().getInt("players." + player.getUniqueId().toString() + ".lives"))){
                return ChatColor.translateAlternateColorCodes('&', (livesConfig.get().getString("lives." + customConfig.get().getInt("players." + player.getUniqueId().toString() + ".lives") + ".prefix")));
            } else {
                return "&r";
            }
        }

        if(params.equals("color")){
            if(customConfig.get().getBoolean("players." + player.getUniqueId().toString() + ".endless")) {
                return ChatColor.translateAlternateColorCodes('&', (livesConfig.get().getString("lives.endless.color")));
            } else if(livesConfig.get().contains("lives." + customConfig.get().getInt("players." + player.getUniqueId().toString() + ".lives"))){
                return ChatColor.translateAlternateColorCodes('&', (livesConfig.get().getString("lives." + customConfig.get().getInt("players." + player.getUniqueId().toString() + ".lives") + ".color")));
            } else {
                return "&r";
            }
        }

        if(params.equals("info_sec")){
            if(Bukkit.getServer().getPluginManager().getPlugin("tinylives").getConfig().getBoolean("life-types.all-death.enabled")){
                double number = ((double)customConfig.get().getInt("players."+ player.getUniqueId().toString() + ".respawn-time")/20.00);
                DecimalFormat format = new DecimalFormat("0.00");
                String output = format.format(number);
                return (output.toString());
            } else if(Bukkit.getServer().getPluginManager().getPlugin("tinylives").getConfig().getBoolean("life-types.add-lives.enabled")){
                double number = ((double)customConfig.get().getInt("addlife-current-delay") / 20.00);
                DecimalFormat format = new DecimalFormat("0.00");
                String output = format.format(number);
                return (output.toString());
            } else {
                double number = ((double)customConfig.get().getInt("current-delay")/20.00);
                DecimalFormat format = new DecimalFormat("0.00");
                String output = format.format(number);
                return (output.toString());
            }
        }

        if(params.equals("info_min")){
            if(Bukkit.getServer().getPluginManager().getPlugin("tinylives").getConfig().getBoolean("life-types.all-death.enabled")){
                double number = ((double)customConfig.get().getInt("players."+ player.getUniqueId().toString() + ".respawn-time")/1200.00);
                DecimalFormat format = new DecimalFormat("0.00");
                String output = format.format(number);
                return (output.toString());
            } else if(Bukkit.getServer().getPluginManager().getPlugin("tinylives").getConfig().getBoolean("life-types.add-lives.enabled")){
                double number =((double)customConfig.get().getInt("addlife-current-delay")/1200.00);
                DecimalFormat format = new DecimalFormat("0.00");
                String output = format.format(number);
                return (output.toString());
            } else {
                double number =((double)customConfig.get().getInt("current-delay")/1200.00);
                DecimalFormat format = new DecimalFormat("0.00");
                String output = format.format(number);
                return (output.toString());
            }
        }

        if(params.equals("info_hour")){
            if(Bukkit.getServer().getPluginManager().getPlugin("tinylives").getConfig().getBoolean("life-types.all-death.enabled")){
                double number = ((double)customConfig.get().getInt("players."+ player.getUniqueId().toString() + ".respawn-time")/72000.00);
                DecimalFormat format = new DecimalFormat("0.00");
                String output = format.format(number);
                return (output.toString());
            } else if(Bukkit.getServer().getPluginManager().getPlugin("tinylives").getConfig().getBoolean("life-types.add-lives.enabled")){
                double number = ((double)customConfig.get().getInt("addlife-current-delay")/72000.00);
                DecimalFormat format = new DecimalFormat("0.00");
                String output = format.format(number);
                return (output.toString());
            } else {
                double number = ((double)customConfig.get().getInt("current-delay")/72000.00);
                DecimalFormat format = new DecimalFormat("0.00");
                String output = format.format(number);
                return (output.toString());
            }
        }

        if(params.equals("info_day")){
            if(Bukkit.getServer().getPluginManager().getPlugin("tinylives").getConfig().getBoolean("life-types.all-death.enabled")){
                double number = ((double)customConfig.get().getInt("players."+ player.getUniqueId().toString() + ".respawn-time") / 1728000.00);
                DecimalFormat format = new DecimalFormat("0.00");
                String output = format.format(number);
                return (output.toString());
            } else if(Bukkit.getServer().getPluginManager().getPlugin("tinylives").getConfig().getBoolean("life-types.add-lives.enabled")){
                double number = ((double)customConfig.get().getInt("addlife-current-delay")/1728000.00);
                DecimalFormat format = new DecimalFormat("0.00");
                String output = format.format(number).toString();
                return (output.toString());
            } else {
                double number = ((double)customConfig.get().getInt("current-delay")/1728000.00);
                DecimalFormat format = new DecimalFormat("0.00");
                String output = format.format(number);
                return (output.toString());
            }
        }

        if(params.equals("assassin")){
            if(customConfig.get().getBoolean("players."+ player.getUniqueId().toString() + ".IsAssassin")){
                return "true";
            } else {
                return "false";
            }
        }

        if(params.equals("combat")){
            if(customConfig.get().getBoolean("players."+ player.getUniqueId().toString() + ".InCombat")){
                return "true";
            } else {
                return "false";
            }
        }

        return "";
    }
}
