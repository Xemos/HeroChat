/**
 * Copyright (C) 2011 David Thielke <dave.thielke@gmail.com>
 **/

package com.dthielke.herochat.command.commands;

import com.dthielke.herochat.Channel;
import com.dthielke.herochat.Chatter;
import com.dthielke.herochat.HeroChat;
import com.dthielke.herochat.command.BasicCommand;
import com.dthielke.herochat.util.Messaging;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ModCommand extends BasicCommand {
    public ModCommand() {
        super("Mod");
        setDescription("Grants moderator status");
        setUsage("/ch mod §8[channel] <player>");
        setArgumentRange(1, 2);
        setIdentifiers("ch mod");
        setNotes("\u00a7cNote:\u00a7e If no channel is given, your active", "      channel is used.");
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args) {
        Channel channel = null;
        Chatter chatter = null;

        if (sender instanceof Player) {
            Player player = (Player) sender;
            chatter = HeroChat.getChatterManager().getChatter(player);
            channel = chatter.getActiveChannel();
        }

        if (args.length == 1) {
            if (chatter != null) {
                channel = chatter.getActiveChannel();
            }
        } else {
            channel = HeroChat.getChannelManager().getChannel(args[0]);
            if (channel == null) {
                Messaging.send(sender, "Channel not found.");
                return true;
            }
        }

        if (!(sender.hasPermission("herochat.mod") || (chatter != null && channel.isModerator(chatter.getPlayer().getName())))) {
            Messaging.send(sender, "Insufficient permission.");
            return true;
        }

        String targetName = args[args.length - 1];
        Player targetPlayer = Bukkit.getServer().getPlayer(targetName);

        if (channel.isModerator(targetName)) {
            channel.setModerator(targetName, false);
            Messaging.send(sender, "Revoked mod status.");
            if (targetPlayer != null)
                Messaging.send(targetPlayer, "You are no longer moderating $1.", channel.getName());
        } else {
            channel.setModerator(targetName, true);
            Messaging.send(sender, "Granted mod status.");
            if (targetPlayer != null)
                Messaging.send(targetPlayer, "You are now moderating $1.", channel.getName());
        }

        return true;
    }
}