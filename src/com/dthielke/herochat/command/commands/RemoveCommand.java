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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RemoveCommand extends BasicCommand {
    public RemoveCommand() {
        super("Remove Channel");
        setDescription("Removes an existing channel");
        setUsage("/ch remove §8<channel>");
        setArgumentRange(1, 1);
        setIdentifiers("ch remove");
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args) {
        String name = args[0];

        ChannelManager channelMngr = HeroChat.getChannelManager();
        Channel channel = channelMngr.getChannel(name);
        if (channel == null) {
            Messaging.send(sender, "Channel not found.");
            return true;
        }

        if (sender instanceof Player) {
            Player player = (Player) sender;
            Chatter chatter = HeroChat.getChatterManager().getChatter(player);
            if (chatter.canRemove(channel) != Result.ALLOWED) {
                Messaging.send(sender, "Insufficient permission.");
                return true;
            }
        }

        for (Chatter target : channel.getMembers()) {
            channel.kickMember(target, true);

            if (target.getChannels().isEmpty()) {
                HeroChat.getChannelManager().getDefaultChannel().addMember(target, true);
            }

            if (channel.equals(target.getActiveChannel())) {
                Channel focus = target.getChannels().iterator().next();
                target.setActiveChannel(focus);
                Messaging.send(target.getPlayer(), "Now chatting in $1.", focus.getName());
            }
        }

        channelMngr.removeChannel(channel);
        Messaging.send(sender, "Channel removed.");
        return true;
    }
}
