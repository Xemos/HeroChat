/**
 * Copyright (C) 2011 David Thielke <dave.thielke@gmail.com>
 **/

package com.dthielke.herochat.command.commands;

import com.dthielke.herochat.Channel;
import com.dthielke.herochat.Chatter;
import com.dthielke.herochat.HeroChat;
import com.dthielke.herochat.command.BasicCommand;
import com.dthielke.herochat.util.Messaging;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class QuickMsgCommand extends BasicCommand {
    public QuickMsgCommand() {
        super("Quick Message");
        setDescription("Sends a message without changing your active channel");
        setUsage("/ch qm §8<channel> <message>");
        setArgumentRange(2, Integer.MAX_VALUE);
        setIdentifiers("ch qm");
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args) {
        if (!(sender instanceof Player))
            return true;

        Player player = (Player) sender;
        Chatter chatter = HeroChat.getChatterManager().getChatter(player);
        Channel channel = HeroChat.getChannelManager().getChannel(args[0]);
        if (channel == null) {
            Messaging.send(sender, "Channel not found.");
            return true;
        }

        String msg = "";
        for (int i = 1; i < args.length; i++) {
            msg += args[i] + " ";
        }

        Channel active = chatter.getActiveChannel();
        chatter.setActiveChannel(channel);
        player.chat(msg.trim());
        chatter.setActiveChannel(active);
        return true;
    }
}