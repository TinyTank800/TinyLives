package com.tinytank800.tinylives.Resources;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    public static String format(Player player, String input){
        if(input.contains("%color%")){
            input = input.replace("%color%", Objects.requireNonNull(Configs.Get("lives.yml").getString("lives." + Configs.Get("playerdata.yml").getInt("players." + player.getUniqueId() + ".lives") + ".color")));
        }

        if(input.contains("%life%")){
            input = input.replace("%life%", String.valueOf(Configs.Get("playerdata.yml").getInt("players." + player.getUniqueId() + ".lives")));
        }

        if(input.contains("%prefix%")){
            input = input.replace("%prefix%", Objects.requireNonNull(Configs.Get("lives.yml").getString("lives." + Configs.Get("playerdata.yml").getInt("players." + player.getUniqueId() + ".lives") + ".prefix")));
        }

        if(input.contains("%player%")){
            input = input.replace("%player%", player.getName());
        }

        if(input.contains("%respawn-timer%")){
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime futureDateTime = LocalDateTime.parse(Configs.playerConfigs.get(player.getUniqueId()).getString("respawn-date"));
            Duration duration = Duration.between(now, futureDateTime);
            long hours = duration.toHours();
            long minutes = duration.minusHours(hours).toMinutes();
            long seconds = duration.minusHours(hours).minusMinutes(minutes).getSeconds();
            input = input.replace("%respawn-timer%", (hours + "h " + minutes + "m " + seconds + "s"));
        }

        input = translateHexColorCodes("&#", "", input);
        input = ChatColor.translateAlternateColorCodes('&', input);

        if(Configs.Get("hooks.yml").getBoolean("placeholderapi.enabled")){
            input = PlaceholderAPI.setPlaceholders(player, input);
        }

        return input;
    }

    public static String translateColor(String message){
        message = translateHexColorCodes("&#", "", message);
        message = ChatColor.translateAlternateColorCodes('&', message);

        return message;
    }

    public static String translateHexColorCodes(String startTag, String endTag, String message)
    {
        final char COLOR_CHAR = ChatColor.COLOR_CHAR;
        final Pattern hexPattern = Pattern.compile(startTag + "([A-Fa-f0-9]{6})" + endTag);
        Matcher matcher = hexPattern.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
        while (matcher.find())
        {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, COLOR_CHAR + "x"
                    + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
                    + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
                    + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
            );
        }
        return matcher.appendTail(buffer).toString();
    }
}
