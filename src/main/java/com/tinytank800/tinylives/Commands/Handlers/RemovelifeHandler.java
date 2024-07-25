package com.tinytank800.tinylives.Commands.Handlers;

import com.tinytank800.tinylives.Resources.Parser;
import com.tinytank800.tinylives.Resources.PlayerUtils;
import com.tinytank800.tinylives.Tinylives;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RemovelifeHandler {

    public static void Removelife(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args){
        if(args.length == 1){
            sender.sendMessage(Parser.translateColor("&cInvalid usage of /tinylives removelife! EX: /tinylives removelife (playername) <amount>."));
            return;
        }

        if(args.length == 2){
            Player player = Tinylives.getInstance().getServer().getPlayer(args[1]);
            if(player != null){
                PlayerUtils.SubtractLife(player, 1);
                return;
            } else {
                //Get offline player through yaml
            }
        }

        if(args.length >= 3){
            Player player = Tinylives.getInstance().getServer().getPlayer(args[1]);
            Integer amount = Integer.valueOf(args[2]);
            if(player != null){
                PlayerUtils.SubtractLife(player, amount);
                return;
            } else {
                //Get offline player through yaml
            }
        }
    }
}
