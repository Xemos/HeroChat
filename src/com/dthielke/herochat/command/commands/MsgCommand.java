/**
 * Copyright (C) 2011 David Thielke <dave.thielke@gmail.com>
 **/

package com.dthielke.herochat.command.commands;

import com.dthielke.herochat.*;
import com.dthielke.herochat.command.BasicCommand;
import com.dthielke.herochat.util.Messaging;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MsgCommand extends BasicCommand {
    public MsgCommand() {
        super("Private Message");
        setDescription("Starts a private conversation with another player");
        setUsage("/msg §8<player> [message]");
        setArgumentRange(1, Integer.MAX_VALUE);
        setIdentifiers("msg", "tell", "ch msg");
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args) {
        if (!(sender instanceof Player))
            return true;

        Player player = (Player) sender;
        Chatter playerChatter = HeroChat.getChatterManager().getChatter(player);

        Player target = Bukkit.getServer().getPlayer(args[0]);
        if (target == null) {
            Messaging.send(sender, "Player not found.");
            return true;
        }

        if (target.equals(player)) {
            Messaging.send(sender, "This plugin does not support crazies.");
            return true;
        }

        Chatter targetChatter = HeroChat.getChatterManager().getChatter(target);
        ChannelManager channelManager = HeroChat.getChannelManager();
        String channelName = "convo" + player.getName() + target.getName();
        if (!channelManager.hasChannel(channelName)) {
            Channel convo = new ConversationChannel(playerChatter, targetChatter);
            channelManager.addChannel(convo);
        }

        Channel convo = channelManager.getChannel(channelName);
        if (args.length == 1) {
            playerChatter.setActiveChannel(convo, false);
            Messaging.send(player, "Now chatting with $1.", target.getName());
        } else {
            String msg = "";
            for (int i = 1; i < args.length; i++) {
                msg += args[i] + " ";
            }

            Channel active = playerChatter.getActiveChannel();
            playerChatter.setActiveChannel(convo, false);
            player.chat(msg.trim());
            playerChatter.setActiveChannel(active, false);
        }
        return true;
    }
}
