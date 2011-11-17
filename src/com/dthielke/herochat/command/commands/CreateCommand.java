/**
 * Copyright (C) 2011 David Thielke <dave.thielke@gmail.com>
 **/

package com.dthielke.herochat.command.commands;

import com.dthielke.herochat.Channel;
import com.dthielke.herochat.ChannelManager;
import com.dthielke.herochat.HeroChat;
import com.dthielke.herochat.StandardChannel;
import com.dthielke.herochat.command.BasicCommand;
import com.dthielke.herochat.util.Messaging;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateCommand extends BasicCommand {
    public CreateCommand() {
        super("Create Channel");
        setDescription("Creates a new channel");
        setUsage("/ch create §8<name> [nick]");
        setArgumentRange(1, 2);
        setIdentifiers("ch create");
        setPermission("herochat.create");
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args) {
        String name = args[0];
        ChannelManager channelMngr = HeroChat.getChannelManager();
        if (channelMngr.hasChannel(name)) {
            Messaging.send(sender, "Name taken.");
            return true;
        }

        String nick;
        if (args.length == 2) {
            nick = args[1];
            if (channelMngr.hasChannel(name)) {
                Messaging.send(sender, "Nick taken.");
                return true;
            }
        } else {
            nick = name;
            for (int i = 0; i < name.length(); i++) {
                nick = name.substring(0, i + 1);
                if (!channelMngr.hasChannel(name)) {
                    break;
                }
            }
        }

        Channel channel = new StandardChannel(name, nick);
        if (sender instanceof Player) {
            channel.setModerator(sender.getName(), true);
        }
        channelMngr.addChannel(channel);
        Messaging.send(sender, "Channel created.");

        return true;
    }
}
