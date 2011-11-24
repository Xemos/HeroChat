/**
 * Copyright (C) 2011 David Thielke <dave.thielke@gmail.com>
 **/

package com.dthielke.herochat.command.commands;

import com.dthielke.herochat.Channel;
import com.dthielke.herochat.ChannelManager;
import com.dthielke.herochat.Chatter;
import com.dthielke.herochat.Chatter.Result;
import com.dthielke.herochat.HeroChat;
import com.dthielke.herochat.command.BasicCommand;
import com.dthielke.herochat.util.Messaging;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetCommand extends BasicCommand {
    public SetCommand() {
        super("Set Channel Setting");
        setDescription("Modifies a setting of an existing channel");
        setUsage("/ch set §8<channel> <setting> <value>");
        setArgumentRange(3, 3);
        setIdentifiers("ch set");
        setNotes("\u00a7cSettings:\u00a7e nick, applyFormat, password, distance, color, qm");
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args) {
        String name = args[0];
        String setting = args[1].toLowerCase();
        String value = args[2];

        ChannelManager channelMngr = HeroChat.getChannelManager();
        Channel channel = channelMngr.getChannel(name);
        if (channel == null) {
            Messaging.send(sender, "Channel not found.");
            return true;
        }

        if (sender instanceof Player) {
            Player player = (Player) sender;
            Chatter chatter = HeroChat.getChatterManager().getChatter(player);
            if (chatter.canModify(setting, channel) != Result.ALLOWED) {
                Messaging.send(sender, "Insufficient permission.");
                return true;
            }
        }

        if (setting.equals("nick")) {
            if (channelMngr.hasChannel(value)) {
                Messaging.send(sender, "Identifier taken.");
            } else {
                channel.setNick(value);
                Messaging.send(sender, "Nick changed.");
            }
        } else if (setting.equals("applyFormat")) {
            channel.setFormat(value);
            Messaging.send(sender, "Format changed.");
        } else if (setting.equals("password")) {
            channel.setPassword(value);
            Messaging.send(sender, "Password changed.");
        } else if (setting.equals("distance")) {
            try {
                int distance = Integer.parseInt(value);
                channel.setDistance(distance);
                Messaging.send(sender, "Distance changed.");
            } catch (NumberFormatException e) {
                Messaging.send(sender, "Invalid distance.");
            }
        } else if (setting.equals("color")) {
            ChatColor color = Messaging.parseColor(value);
            if (color == null) {
                Messaging.send(sender, "Invalid color.");
            } else {
                channel.setColor(color);
                Messaging.send(sender, "Color changed.");
            }
        } else if (setting.equals("shortcut")) {
            if (value.equals("0")) {
                channel.setShortcutAllowed(false);
                Messaging.send(sender, "Quick messaging disabled.");
            } else {
                channel.setShortcutAllowed(true);
                Messaging.send(sender, "Quick messaging enabled.");
            }
        }

        return true;
    }
}
