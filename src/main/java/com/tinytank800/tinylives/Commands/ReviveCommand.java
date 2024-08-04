package com.tinytank800.tinylives.Commands;

import com.tinytank800.tinylives.Commands.Handlers.AddlifeHandler;
import com.tinytank800.tinylives.Commands.Handlers.RemovelifeHandler;
import com.tinytank800.tinylives.Commands.Handlers.ReviveHandler;
import com.tinytank800.tinylives.Commands.Handlers.SetlifeHandler;
import com.tinytank800.tinylives.Resources.Configs;
import com.tinytank800.tinylives.Resources.Parser;
import com.tinytank800.tinylives.Tinylives;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ReviveCommand implements CommandExecutor {
    final Tinylives plugin;

    public ReviveCommand(Tinylives plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if(args.length == 0){
            sender.sendMessage(Parser.translateColor("&cUnknown command usage of /Revive!"));
            return false;
        }

        if(sender instanceof Player){
            ReviveHandler.Revive((Player)sender, command, label, args);
        } else if(sender instanceof ConsoleCommandSender) {
            sender.sendMessage(Parser.translateColor("&cOnly players can use this command."));
            //ReviveHandler.ReviveConsole((ConsoleCommandSender)sender, command, label, args);
        }

        return true;
    }
}
