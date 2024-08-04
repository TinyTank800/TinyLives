package com.tinytank800.tinylives.Commands.Handlers;

import com.tinytank800.tinylives.Resources.Configs;
import com.tinytank800.tinylives.Resources.Parser;
import com.tinytank800.tinylives.Resources.PlayerUtils;
import com.tinytank800.tinylives.Tinylives;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ReviveHandler {

    public static void Revive(@NotNull Player sender, @NotNull Command command, @NotNull String label, String[] args){
        if(args.length == 1){
            if(Configs.Get("playerdata.yml").getInt("players." + sender.getUniqueId() + ".lives") <= Configs.Get("config.yml").getInt("death-settings.revive.cost")){
                sender.sendMessage(Parser.translateColor("&cYou cannot revive this player as you do not have enough lives to give."));
                return;
            }

            Player selPlayer = Tinylives.getInstance().getServer().getPlayer(args[0]);
            if(selPlayer != null){
                if(!PlayerUtils.IsDead(selPlayer)){
                    sender.sendMessage(Parser.translateColor("&cYou cannot revive this player as they are currently alive."));
                    return;
                }

                sender.sendMessage(Parser.translateColor("&cAre you sure you would like to revive " + selPlayer.getName() + " for " + Configs.Get("config.yml").getInt("death-settings.revive.cost") + " of your lives? /revive (playername) --confirm"));
                return;
            } else {
                //Get offline player through yaml
            }
        }

        if(args.length >= 2){
            Player selPlayer = Tinylives.getInstance().getServer().getPlayer(args[0]);
            if(selPlayer != null && args[1].equalsIgnoreCase("--confirm")){
                PlayerUtils.SubtractLife(sender, Configs.Get("config.yml").getInt("death-settings.revive.cost"));
                sender.sendMessage(Parser.translateColor("&aYou have revived " + selPlayer.getName() + " at the cost of " + Configs.Get("config.yml").getInt("death-settings.revive.cost") + " of your own lives."));
                PlayerUtils.Revive(selPlayer);
                selPlayer.sendMessage(Parser.translateColor("&aYou have been revived by " + sender.getName() +"."));
                return;
            } else {
                //Get offline player through yaml
            }
        }
    }
}
