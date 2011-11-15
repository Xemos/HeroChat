/**
 * Copyright (C) 2011 David Thielke <dave.thielke@gmail.com>
 **/

package com.dthielke.herochat.command.commands;

import org.bukkit.command.CommandSender;

import com.dthielke.herochat.Channel;
import com.dthielke.herochat.ChannelManager;
import com.dthielke.herochat.HeroChat;
import com.dthielke.herochat.StandardChannel;
import com.dthielke.herochat.command.BasicCommand;
import com.dthielke.herochat.util.Messaging;

public class CreateCommand extends BasicCommand {

    public CreateCommand() {
        super("Create Channel");
        setDescription("Creates a new channel");
        setUsage("/ch create §8<name>");
        setArgumentRange(1, 1);
        setIdentifiers("ch create");
        setPermission("herochat.create");
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args) {
        String name = args[0];
        ChannelManager channelMngr = HeroChat.getChannelManager();
        if (channelMngr.hasChannel(name)) {
            Messaging.send(sender, "Identifier taken.");
            return true;
        }

        String nick = name;
        for (int i = 0; i < name.length(); i++) {
            nick = name.substring(0, i + 1);
            if (!channelMngr.hasChannel(name)) {
                break;
            }
        }

        Channel channel = new StandardChannel(name, nick);
        channelMngr.addChannel(channel);

        return true;
    }

}
