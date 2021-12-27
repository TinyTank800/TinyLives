package me.tinytank800.tinylives.utilities;

import font.DefualtFontInfo;
import me.clip.placeholderapi.PlaceholderAPI;
import me.tinytank800.tinylives.tinylives;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.List;

public class ChatUtil {

    public static String format(String input, Player player){
        if (tinylives.getInstance().PAPIintall) {
            return new String(ChatColor.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(player, input)));
        } else {
            return new String(ChatColor.translateAlternateColorCodes('&', input));
        }
    }

    public static void console(String input, int Level){

        if(Level == 0){ //Base level debug
            if(tinylives.getInstance().debug >= 1){
                tinylives.getInstance().getLogger().info(input);
            }
        } else if(Level == 2){ //Indepth debug
            if(tinylives.getInstance().debug == 2){
                tinylives.getInstance().getLogger().info(input);
            }
        } else { //If message is level 1 which means urgent and to post to console.
            tinylives.getInstance().getLogger().info(input);
        }
    }

    public static void NotifyAllString(String input){
        String message = input;

        if (!tinylives.getInstance().disabledPrefix) {
            message = tinylives.getInstance().PPrefix + message;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if(PlayerUtil.CheckPlayerWorld(player)) {
                player.sendMessage(ChatUtil.format(message, player));
            }
        }
    }

    public static void NotifyAllStringPlayer(String input, Player DPlayer){
        String message = input;

        if (!tinylives.getInstance().disabledPrefix) {
            message = tinylives.getInstance().PPrefix + message;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if(PlayerUtil.CheckPlayerWorld(player)) {
                player.sendMessage(ChatUtil.format(message, DPlayer));
            }
        }
    }

    public static void NotifyAllBlock(List<String> messageList){
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (PlayerUtil.CheckPlayerWorld(player)) {
                if (!tinylives.getInstance().disabledPrefix) {
                    if (tinylives.getInstance().centeredText) {
                        sendCentredMessage(player, ChatUtil.format(tinylives.getInstance().prefix, player));
                    } else {
                        player.sendMessage(ChatUtil.format(tinylives.getInstance().prefix, player));
                    }
                }

                for (String string : messageList) {
                    if (tinylives.getInstance().centeredText) {
                        sendCentredMessage(player, ChatUtil.format(string, player));
                    } else {
                        player.sendMessage(ChatUtil.format(string, player));
                    }
                }
            }
        }
    }

    public static void NotifyPlayerString(String input, Player player){
        if(PlayerUtil.CheckPlayerWorld(player)) {
            String message = input;

            if (!tinylives.getInstance().disabledPrefix) {
                message = tinylives.getInstance().PPrefix + message;
            }

            player.sendMessage(ChatUtil.format(message, player));
        }
    }

    public static void NotifyPlayerBlock(List<String> messageList, Player player){
        if(PlayerUtil.CheckPlayerWorld(player)){
            if (!tinylives.getInstance().disabledPrefix) {
                if(tinylives.getInstance().centeredText){
                    sendCentredMessage(player, ChatUtil.format(tinylives.getInstance().prefix, player));
                } else {
                    player.sendMessage(ChatUtil.format(tinylives.getInstance().prefix, player));
                }
            }

            for (String string : messageList) {
                if(tinylives.getInstance().centeredText) {
                    sendCentredMessage(player, ChatUtil.format(string, player));
                } else {
                    player.sendMessage(ChatUtil.format(string, player));
                }
            }
        }
    }

    public static void NotifyPlayerTitle(Player player, String title, String subTitle, int fadeIn, int stay, int fadeOut){
        if(PlayerUtil.CheckPlayerWorld(player)) {
            player.sendTitle(ChatUtil.format(title, player), ChatUtil.format(subTitle, player), fadeIn, stay, fadeOut);
        }
    }

    public static void sendCentredMessage(Player player, String message) {
        if(message == null || message.equals("")) {
            player.sendMessage("");
            return;
        }
        message = org.bukkit.ChatColor.translateAlternateColorCodes('&', message);

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for(char c : message.toCharArray()){
            if(c == '§'){
                previousCode = true;
            }else if(previousCode){
                previousCode = false;
                isBold = c == 'l' || c == 'L';
            }else{
                DefualtFontInfo dFI = DefualtFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }
        int CENTER_PX = 154;
        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefualtFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while(compensated < toCompensate){
            sb.append(" ");
            compensated += spaceLength;
        }
        player.sendMessage(sb.toString() + message);
    }

    public void sendChatMsg(List<String> messageList, Player player){
        if (!tinylives.getInstance().disabledPrefix) {
            sendCentredMessage(player, tinylives.getInstance().prefix);
        }
        //List<String> messagelist = tinylives.getInstance().getConfig().getStringList("all-life-reset-messages");
        for (String string : messageList) {
            sendCentredMessage(player, ChatUtil.format(string, player));
        }



        if(!tinylives.getInstance().disabledPrefix){
            messageList.add(0, tinylives.getInstance().prefix);
        }
        for (Player players : Bukkit.getOnlinePlayers()) {
            for (String string : messageList) {
                String message2Send = "";
                if (tinylives.getInstance().PAPIintall) {
                    message2Send = org.bukkit.ChatColor.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(player, string));
                } else {
                    message2Send = org.bukkit.ChatColor.translateAlternateColorCodes('&', string);
                }

                if (!tinylives.getInstance().centeredText) {
                    player.sendMessage(message2Send);
                } else {
                    sendCentredMessage(player, message2Send);
                }
            }
        }
    }

    public void sendTitleMsg(String title, String subTitle, int fadeIn, int stay, int fadeOut){
        for (Player player : Bukkit.getOnlinePlayers()) {
            String fTitle = "";
            String fSubTitle = "";
            if (tinylives.getInstance().PAPIintall) {
                fTitle = org.bukkit.ChatColor.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(player, title));
                fSubTitle = org.bukkit.ChatColor.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(player, subTitle));
            } else {
                fTitle = org.bukkit.ChatColor.translateAlternateColorCodes('&', title);
                fSubTitle = org.bukkit.ChatColor.translateAlternateColorCodes('&', subTitle);
            }
            player.sendTitle(fTitle, fSubTitle, fadeIn, stay, fadeOut);
        }
    }

    public void sendBossBarMsg(String message, String color, int style, int time){
        String fMessage = org.bukkit.ChatColor.translateAlternateColorCodes('&', message);
        BarColor bColor = BarColor.BLUE;
        BarStyle bStyle = BarStyle.SEGMENTED_6;

        if(color.equalsIgnoreCase("blue")){
            bColor = BarColor.BLUE;
        } else if(color.equalsIgnoreCase("green")){
            bColor = BarColor.GREEN;
        } else if(color.equalsIgnoreCase("pink")){
            bColor = BarColor.PINK;
        } else if(color.equalsIgnoreCase("purple")){
            bColor = BarColor.PURPLE;
        } else if(color.equalsIgnoreCase("red")){
            bColor = BarColor.RED;
        } else if(color.equalsIgnoreCase("white")){
            bColor = BarColor.WHITE;
        } else if(color.equalsIgnoreCase("yellow")){
            bColor = BarColor.YELLOW;
        }

        if(style == 6){
            bStyle = BarStyle.SEGMENTED_6;
        } else if(style == 10){
            bStyle = BarStyle.SEGMENTED_10;
        } else if(style == 12){
            bStyle = BarStyle.SEGMENTED_12;
        } else if(style == 20){
            bStyle = BarStyle.SEGMENTED_20;
        } else if(style == 0){
            bStyle = BarStyle.SOLID;
        }

        BossBar bar = null;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (tinylives.getInstance().PAPIintall) {
                bar = Bukkit.createBossBar(PlaceholderAPI.setPlaceholders(player, fMessage),bColor,bStyle);
            } else {
                bar = Bukkit.createBossBar(fMessage,bColor,bStyle);
            }

            bar.addPlayer(player);

        }

        new BarCountdown(bar, time).runTaskTimer(tinylives.getInstance(), 0L, time/8);

        tinylives.getInstance().getServer().getScheduler().runTaskLater(tinylives.getInstance(), bar::removeAll, time);
    }

    public void sendActionBarMsg(String message){
        String fMessage = org.bukkit.ChatColor.translateAlternateColorCodes('&', message);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (tinylives.getInstance().PAPIintall) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(PlaceholderAPI.setPlaceholders(player, fMessage)));
            } else {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(fMessage));
            }
        }
    }
}
