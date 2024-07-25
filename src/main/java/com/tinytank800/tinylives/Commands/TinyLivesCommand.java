package com.tinytank800.tinylives.Commands;

import com.tinytank800.tinylives.Commands.Handlers.AddlifeHandler;
import com.tinytank800.tinylives.Commands.Handlers.RemovelifeHandler;
import com.tinytank800.tinylives.Commands.Handlers.ReviveHandler;
import com.tinytank800.tinylives.Resources.Configs;
import com.tinytank800.tinylives.Resources.Logger;
import com.tinytank800.tinylives.Resources.Parser;
import com.tinytank800.tinylives.Tinylives;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TinyLivesCommand implements CommandExecutor {
    final Tinylives plugin;

    public TinyLivesCommand(Tinylives plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if(args.length == 0){
            sender.sendMessage(Parser.translateColor("&cUnknown command usage of /tinylives!"));
            return false;
        }

        if(args[0].equalsIgnoreCase("addlife")){
            AddlifeHandler.Addlife(sender, command, label, args);
            return true;
        }

        if(args[0].equalsIgnoreCase("removelife")){
            RemovelifeHandler.Removelife(sender, command, label, args);
            return true;
        }

        if(args[0].equalsIgnoreCase("revive")){
            if(sender instanceof Player){
                ReviveHandler.Revive((Player)sender, command, label, args);
            } else if(sender instanceof ConsoleCommandSender) {
                sender.sendMessage(Parser.translateColor("&cOnly players can use this command."));
                //ReviveHandler.ReviveConsole((ConsoleCommandSender)sender, command, label, args);
            }
            return true;
        }

        if(args[0].equalsIgnoreCase("save")){
            if(sender instanceof Player){
                if(sender.hasPermission("tinylives.admin")){
                    if(args[1].equalsIgnoreCase("all")){
                        Configs.Save("all");
                    } else {
                        Configs.Save(args[1]);
                    }
                    sender.sendMessage(Parser.translateColor("&aConfigs saved correctly."));
                }
            } else if(sender instanceof ConsoleCommandSender) {
                if(args[1].equalsIgnoreCase("all")){
                    Configs.Save("all");
                } else {
                    Configs.Save(args[1]);
                }
                sender.sendMessage(Parser.translateColor("&aConfigs saved correctly."));
            }
            return true;
        }

        if(args[0].equalsIgnoreCase("reload")){
            if(sender instanceof Player){
                if(sender.hasPermission("tinylives.admin")){
                    if(args[1].equalsIgnoreCase("all")){
                        Configs.Reload("all");
                    } else {
                        Configs.Reload(args[1]);
                    }
                    sender.sendMessage(Parser.translateColor("&aConfigs reloaded correctly."));
                }
            } else if(sender instanceof ConsoleCommandSender) {
                if(args[1].equalsIgnoreCase("all")){
                    Configs.Reload("all");
                } else {
                    Configs.Reload(args[1]);
                }
                sender.sendMessage(Parser.translateColor("&aConfigs reloaded correctly."));
            }
            return true;
        }


        return true;
    }
}
