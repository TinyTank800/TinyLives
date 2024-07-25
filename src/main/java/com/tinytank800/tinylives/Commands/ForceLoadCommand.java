package com.tinytank800.tinylives.Commands;

import com.tinytank800.tinylives.Resources.Configs;
import com.tinytank800.tinylives.Resources.Parser;
import com.tinytank800.tinylives.Tinylives;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ForceLoadCommand implements CommandExecutor {
    final Tinylives plugin;

    public ForceLoadCommand(Tinylives plugin) {
        this.plugin = plugin;
    }
    
    /*
        Todo - Remove force load and force save commands as tinylives save and reload are a replacement for them
        @author - TinyTank800
        @date - 7/24/2024
        @time - 8:14 PM
         */

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            //Console sent the command
            if(args.length >= 2){
                if(args[1].equals("--confirm")){
                    Configs.Reload(args[0]);
                    sender.sendMessage(Parser.translateColor("&aSuccessfully Loaded " + args[0] + "."));
                    return true;
                }
            } else {
                sender.sendMessage(Parser.translateColor("&4MAKE SURE YOU KNOW WHAT YOU ARE DOING! This operation is not recommended as it WILL overwrite ALL data saved on the server side. To accept this warning use /forceload (FILE) --confirm"));
                return true;
            }
            return true;
        }

        //Player sent the command
        Player player = (Player) sender;

        if(!player.isOp()){
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cYou do not have access to this command!"));
            return true;
        }

        if(args.length >= 2){
            if(args[1].equals("--confirm")){
                Configs.Reload(args[0]);
                sender.sendMessage(Parser.translateColor("&aSuccessfully Loaded " + args[0] + "."));
                return true;
            }
        } else {
            sender.sendMessage(Parser.translateColor("&4MAKE SURE YOU KNOW WHAT YOU ARE DOING! This operation is not recommended as it WILL overwrite ALL data saved on the server side. To accept this warning use /forceload (FILE) --confirm"));
            return true;
        }
        return true;
    }
}
