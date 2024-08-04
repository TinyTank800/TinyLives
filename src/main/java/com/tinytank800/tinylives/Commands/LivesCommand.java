package com.tinytank800.tinylives.Commands;

import com.tinytank800.tinylives.Commands.Handlers.LivesHandler;
import com.tinytank800.tinylives.Resources.Parser;
import com.tinytank800.tinylives.Tinylives;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LivesCommand implements CommandExecutor {
    final Tinylives plugin;

    public LivesCommand(Tinylives plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if(sender instanceof Player){
            LivesHandler.Lives((Player)sender, command, label, args);
        } else if(sender instanceof ConsoleCommandSender) {
            sender.sendMessage(Parser.translateColor("&cOnly players can use this command."));
            //ReviveHandler.ReviveConsole((ConsoleCommandSender)sender, command, label, args);
        }

        return true;
    }
}
