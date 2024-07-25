package com.tinytank800.tinylives.Commands.TabExecutors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ForceTabExecutor implements TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        final List<String> List = new ArrayList<>();

        //Player player = (Player) sender;
        //UUID playerId = player.getUniqueId();

        if(args.length == 1){

            final String[] Commands = { "config.yml", "hooks.yml", "lives.yml", "playerdata.yml" };

            StringUtil.copyPartialMatches(args[0], Arrays.asList(Commands), List);
        }

        if(args.length == 2){

            final String[] Commands = { "--confirm" };

            StringUtil.copyPartialMatches(args[1], Arrays.asList(Commands), List);
        }

        return List;
    }
}
