/**
 * Copyright (C) 2011 David Thielke <dave.thielke@gmail.com>
 **/

package com.dthielke.herochat.command.commands;

import com.dthielke.herochat.Channel;
import com.dthielke.herochat.Chatter;
import com.dthielke.herochat.HeroChat;
import com.dthielke.herochat.command.BasicCommand;
import com.dthielke.herochat.util.Messaging;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InfoCommand extends BasicCommand {
    public InfoCommand() {
        super("Info");
        setDescription("Displays channel information");
        setUsage("/ch info §8[channel]");
        setArgumentRange(0, 1);
        setIdentifiers("ch info");
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

        if (args.length == 0) {
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

        if (chatter != null && chatter.canViewInfo(channel) != Chatter.Result.ALLOWED) {
            Messaging.send(sender, "Insufficient permission.");
            return true;
        }

        sender.sendMessage(ChatColor.RED + "------------[ " + channel.getColor() + channel.getName() + ChatColor.RED + " ]------------");
        sender.sendMessage(ChatColor.YELLOW + "Name: " + ChatColor.WHITE + channel.getName());
        sender.sendMessage(ChatColor.YELLOW + "Nick: " + ChatColor.WHITE + channel.getNick());
        sender.sendMessage(ChatColor.YELLOW + "Format: " + ChatColor.WHITE + channel.getFormat());
        if (!channel.getPassword().isEmpty())
            sender.sendMessage(ChatColor.YELLOW + "Password: " + ChatColor.WHITE + channel.getPassword());
        if (channel.getDistance() > 0)
            sender.sendMessage(ChatColor.YELLOW + "Distance: " + ChatColor.WHITE + channel.getDistance());
        sender.sendMessage(ChatColor.YELLOW + "Shortcut Allowed: " + ChatColor.WHITE + (channel.isShortcutAllowed() ? "true" : "false"));

        return true;
    }
}
