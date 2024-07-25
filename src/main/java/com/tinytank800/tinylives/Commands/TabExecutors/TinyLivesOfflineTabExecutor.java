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

public class TinyLivesOfflineTabExecutor implements TabCompleter {

    static final Map<String, String> Commands = Map.of(
            "addlife", "tinylives.addlife",
            "removelife", "tinylives.removelife",
            "respawn", "tinylives.respawn",
            "reset", "tinylives.reset",
            "lives", "tinylives.lives"
    );

    private static Boolean PlayerCommandPerm(Player player, String command){
        for (String checkCommand : Commands.keySet()) {
            if (checkCommand.equals(command)) {
                return PlayerUtils.CheckPlayerPermission(player, Commands.get(checkCommand));
            }
        }

        return (Boolean) false;
    }

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
        }

        return List;
    }
}
