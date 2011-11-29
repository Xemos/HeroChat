/**
 * Copyright (C) 2011 David Thielke <dave.thielke@gmail.com>
 **/

package com.dthielke.herochat.command.commands;

import com.dthielke.herochat.Channel;
import com.dthielke.herochat.Chatter;
import com.dthielke.herochat.Chatter.Result;
import com.dthielke.herochat.HeroChat;
import com.dthielke.herochat.command.BasicCommand;
import com.dthielke.herochat.util.Messaging;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BanCommand extends BasicCommand {
    public BanCommand() {
        super("Ban");
        setDescription("Bans a user from a channel");
        setUsage("/ch ban §8[channel] <player>");
        setArgumentRange(1, 2);
        setIdentifiers("ch ban");
        setNotes("\u00a7cNote:\u00a7e If no channel is given, your active", "      channel is used.");
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args) {
        Channel channel;
        Chatter chatter = null;

        if (sender instanceof Player) {
            Player player = (Player) sender;
            chatter = HeroChat.getChatterManager().getChatter(player);
        }

        if (args.length == 1) {
            if (chatter != null) {
                channel = chatter.getActiveChannel();
            } else {
                channel = HeroChat.getChannelManager().getDefaultChannel();
            }
        } else {
            channel = HeroChat.getChannelManager().getChannel(args[0]);
            if (channel == null) {
                Messaging.send(sender, "Channel not found.");
                return true;
            }
        }

        if (chatter != null && chatter.canBan(channel) != Result.ALLOWED) {
            Messaging.send(sender, "Insufficient permission.");
            return true;
        }

        String targetName = args[args.length - 1];
        Player targetPlayer = Bukkit.getServer().getPlayer(targetName);
        if (targetPlayer != null)
            targetName = targetPlayer.getName();

        if (channel.isBanned(targetName)) {
            channel.setBanned(targetName, false);
            Messaging.send(sender, "Unbanned $1 from $2.", targetName, channel.getColor() + channel.getName());
            if (targetPlayer != null)
                Messaging.send(targetPlayer, "Unbanned from $1.", channel.getColor() + channel.getName());
        } else {
            if (targetPlayer != null) {
                Chatter target = HeroChat.getChatterManager().getChatter(targetPlayer);
                channel.banMember(target, true);

                if (target.getChannels().isEmpty()) {
                    HeroChat.getChannelManager().getDefaultChannel().addMember(target, true);
                }

                if (channel.equals(target.getActiveChannel())) {
                    Channel focus = target.getChannels().iterator().next();
                    target.setActiveChannel(focus, true);
                }
            } else {
                channel.setBanned(targetName, true);
            }
            Messaging.send(sender, "Banned $1 from $2.", targetName, channel.getColor() + channel.getName());
        }

        return true;
    }
}