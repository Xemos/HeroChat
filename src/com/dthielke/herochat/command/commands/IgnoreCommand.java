/**
 * Copyright (C) 2011 David Thielke <dave.thielke@gmail.com>
 **/

package com.dthielke.herochat.command.commands;

import com.dthielke.herochat.Chatter;
import com.dthielke.herochat.HeroChat;
import com.dthielke.herochat.command.BasicCommand;
import com.dthielke.herochat.util.Messaging;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class IgnoreCommand extends BasicCommand {
    public IgnoreCommand() {
        super("Ignore");
        setDescription("Ignores a user");
        setUsage("/ch ignore §8[player]");
        setArgumentRange(0, 1);
        setIdentifiers("ch ignore");
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args) {
        if (!(sender instanceof Player))
            return true;

        Player player = (Player) sender;
        Chatter chatter = HeroChat.getChatterManager().getChatter(player);

        if (args.length == 0) {
            String msg = "Ignoring";
            for (String name : chatter.getIgnores())
                msg += " " + name;
            Messaging.send(sender, msg);
        } else {
            String targetName = args[args.length - 1];
            Player targetPlayer = Bukkit.getServer().getPlayer(targetName);
            if (targetPlayer != null)
                targetName = targetPlayer.getName();

            if (chatter.isIgnoring(targetName)) {
                chatter.setIgnore(targetName, false);
                Messaging.send(sender, "Player unignored.");
            } else {
                chatter.setIgnore(targetName, true);
                Messaging.send(sender, "Player ignored.");
            }
        }

        return true;
    }
}
