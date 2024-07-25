package com.tinytank800.tinylives.Commands.TabExecutors;

import com.tinytank800.tinylives.Resources.PlayerUtils;
import com.tinytank800.tinylives.Tinylives;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReviveTabExecutor implements TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        final List<String> List = new ArrayList<>();

        //Player player = (Player) sender;
        //UUID playerId = player.getUniqueId();

        if(args.length == 1){

            final List<String> Commands = new ArrayList<>();

            List<Player> listOfPlayers = new ArrayList<>(Tinylives.getInstance().getServer().getOnlinePlayers());
            for(Player onlinePlayer : listOfPlayers){
                if(PlayerUtils.IsDead(onlinePlayer)){
                    Commands.add(onlinePlayer.getName());
                }
            }

            StringUtil.copyPartialMatches(args[0], Commands, List);
        }

        if(args.length == 2){

            final String[] Commands = { "--confirm" };

            StringUtil.copyPartialMatches(args[1], Arrays.asList(Commands), List);
        }

        return List;
    }
}
