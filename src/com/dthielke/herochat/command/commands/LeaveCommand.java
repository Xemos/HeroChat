/**
 * Copyright (C) 2011 David Thielke <dave.thielke@gmail.com>
 **/

package com.dthielke.herochat.command.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

import com.dthielke.herochat.Channel;
import com.dthielke.herochat.ChannelManager;
import com.dthielke.herochat.Chatter;
import com.dthielke.herochat.Chatter.Result;
import com.dthielke.herochat.HeroChat;
import com.dthielke.herochat.command.BasicCommand;
import com.dthielke.herochat.util.Messaging;

public class LeaveCommand extends BasicCommand {

    public LeaveCommand() {
        super("Leave");
        setDescription("Leaves a channel");
        setUsage("/ch leave §8<channel>");
        setArgumentRange(1, 1);
        setIdentifiers("ch leave");
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args) {
        if (!(sender instanceof Player))
            return true;
        Player player = (Player) sender;

        ChannelManager channelMngr = HeroChat.getChannelManager();
        Channel channel = channelMngr.getChannel(args[0]);
        if (channel == null) {
            Messaging.send(sender, "Channel not found.");
            return true;
        }

        Chatter chatter = HeroChat.getChatterManager().getChatter(player);
        Result result = chatter.canLeave(channel);
        switch (result) {
            case INVALID:
                Messaging.send(sender, "You are not in this channel.");
                return true;
            case NO_PERMISSION:
                Messaging.send(sender, "Insufficient permission.");
                return true;
        }

        int channelCount = chatter.getChannels().size();
        if (channelCount == 1) {
            Messaging.send(sender, "You must stay in at least one channel.");
            return true;
        }

        chatter.removeChannel(channel);
        PlayerChatEvent event = new PlayerChatEvent(player, "left the channel.");
        Bukkit.getServer().getPluginManager().callEvent(event);

        return true;
    }

}
