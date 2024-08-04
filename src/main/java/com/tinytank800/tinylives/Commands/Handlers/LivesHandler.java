package com.tinytank800.tinylives.Commands.Handlers;

import com.tinytank800.tinylives.Resources.Configs;
import com.tinytank800.tinylives.Resources.Parser;
import com.tinytank800.tinylives.Resources.PlayerUtils;
import com.tinytank800.tinylives.Tinylives;
import org.bukkit.command.Command;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LivesHandler {

    public static void Lives(@NotNull Player sender, @NotNull Command command, @NotNull String label, String[] args){
        int lives = 0;

        Player player = null;

        if(args.length >= 1){
            if(sender.hasPermission("tinylives.lives.others")){
                player = Tinylives.getInstance().getServer().getPlayer(args[0]);
                if(player == null){
                    return;
                }

                lives = Configs.Get("playerdata.yml").getInt("players." + player.getUniqueId() + ".lives");
                sender.sendMessage(Configs.Get("config.yml").getString("prefix") + "&a " + player.getName() + " currently has &f" + lives + " live/s&a.");
            }
        } else {
            lives = Configs.Get("playerdata.yml").getInt("players." + sender.getUniqueId() + ".lives");
            sender.sendMessage(Configs.Get("config.yml").getString("prefix") + "&a you currently have &f" + lives + " live/s&a.");
        }
    }
}
