package com.tinytank800.tinylives.Commands.TabExecutors;

import com.tinytank800.tinylives.Resources.Configs;
import com.tinytank800.tinylives.Resources.PlayerUtils;
import com.tinytank800.tinylives.Tinylives;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class TinyLivesTabExecutor implements TabCompleter {

    static final Map<String, String> Commands = Map.of(
            "addlife", "tinylives.addlife",
            "removelife", "tinylives.removelife",
            "respawn", "tinylives.respawn",
            "reset", "tinylives.reset",
            "givelife", "tinylives.givelife",
            "lives", "tinylives.lives",
            "revive", "tinylives.revive",
            "save", "tinylives.admin",
            "reload", "tinylives.admin"
    );

    private static Boolean PlayerCommandPerm(Player player, String command){
        for (String checkCommand : Commands.keySet()) {
            if (checkCommand.equals(command)) {
                return PlayerUtils.CheckPlayerPermission(player, Commands.get(checkCommand));
            }
        }

        return (Boolean) false;
    }
    
    /*
        Todo - Make save and reload have a confirm. Possibly make a update file which will look for changes in disk file vs memory and updated values. This should help with not overiding memory changes or disk changes. 
        @author - TinyTank800
        @date - 7/24/2024
        @time - 8:16 PM
         */

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        final List<String> List = new ArrayList<>();

        Player player = (Player) sender;
        UUID playerId = player.getUniqueId();

        if (args.length == 1) {
            List<String> filteredCommands = new ArrayList<>();

            for (String command : Commands.keySet()) {
                // Check if the player has the required permission for the command
                if (PlayerCommandPerm(player, command)) {
                    filteredCommands.add(command);
                }
            }

            StringUtil.copyPartialMatches(args[0], filteredCommands, List);
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("addlife")) {
                final List<String> Commands = new ArrayList<>();

                List<Player> listOfPlayers = new ArrayList<>(Tinylives.getInstance().getServer().getOnlinePlayers());
                for(Player onlinePlayer : listOfPlayers){
                    Commands.add(onlinePlayer.getName());
                }

                StringUtil.copyPartialMatches(args[1], Commands, List);
            }

            if (args[0].equalsIgnoreCase("removelife")) {
                final List<String> Commands = new ArrayList<>();

                List<Player> listOfPlayers = new ArrayList<>(Tinylives.getInstance().getServer().getOnlinePlayers());
                for(Player onlinePlayer : listOfPlayers){
                    Commands.add(onlinePlayer.getName());
                }

                StringUtil.copyPartialMatches(args[1], Commands, List);
            }

            if (args[0].equalsIgnoreCase("save")) {
                final List<String> Commands = new ArrayList<>(Arrays.asList(Configs.listConfigs()));
                Commands.add("ALL");

                StringUtil.copyPartialMatches(args[1], Commands, List);
            }

            if (args[0].equalsIgnoreCase("reload")) {
                final List<String> Commands = new ArrayList<>(Arrays.asList(Configs.listConfigs()));
                Commands.add("ALL");

                StringUtil.copyPartialMatches(args[1], Commands, List);
            }
        }

        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("addlife")) {
                final String[] Commands = { "<amount>" };

                StringUtil.copyPartialMatches(args[2], Arrays.asList(Commands), List);
            }

            if (args[0].equalsIgnoreCase("removelife")) {
                final String[] Commands = { "<amount>" };

                StringUtil.copyPartialMatches(args[2], Arrays.asList(Commands), List);
            }

            if (args[0].equalsIgnoreCase("save")) {
                final String[] Commands = { "--confirm" };

                StringUtil.copyPartialMatches(args[2], Arrays.asList(Commands), List);
            }

            if (args[0].equalsIgnoreCase("reload")) {
                final String[] Commands = { "--confirm" };

                StringUtil.copyPartialMatches(args[2], Arrays.asList(Commands), List);
            }
        }

        return List;
    }
}
