/**
 * Copyright (C) 2011 David Thielke <dave.thielke@gmail.com>
 **/

package com.dthielke.herochat.command.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dthielke.herochat.Channel;
import com.dthielke.herochat.Chatter;
import com.dthielke.herochat.Chatter.Result;
import com.dthielke.herochat.HeroChat;
import com.dthielke.herochat.command.BasicCommand;
import com.dthielke.herochat.util.Messaging;

public class KickCommand extends BasicCommand {

    public KickCommand() {
        super("Kick");
        setDescription("Kicks a user from a channel");
        setUsage("/ch kick §8[channel] <player>");
        setArgumentRange(1, 2);
        setIdentifiers("ch kick");
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

        if (chatter != null && chatter.canKick(channel) != Result.ALLOWED) {
            Messaging.send(sender, "Insufficient permission.");
            return true;
        }

        Player targetPlayer = Bukkit.getServer().getPlayer(args[args.length - 1]);
        if (targetPlayer == null) {
            Messaging.send(sender, "Player not found.");
            return true;
        }

        Chatter target = HeroChat.getChatterManager().getChatter(targetPlayer);
        if (!target.hasChannel(channel)) {
            Messaging.send(sender, "Player not in channel.");
            return true;
        }

        channel.kickMember(target, true);
        Messaging.send(sender, "Player kicked.");

        if (target.getChannels().isEmpty()) {
            HeroChat.getChannelManager().getDefaultChannel().addMember(chatter, true);
        }

        if (channel.equals(target.getActiveChannel())) {
            Channel focus = target.getChannels().iterator().next();
            target.setActiveChannel(focus);
            Messaging.send(targetPlayer, "Now chatting in $1.", focus.getName());
        }

        return true;
    }

}
