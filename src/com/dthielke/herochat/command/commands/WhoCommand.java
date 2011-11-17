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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WhoCommand extends BasicCommand {
    public WhoCommand() {
        super("Who");
        setDescription("Lists players in a channel");
        setUsage("/ch who §8[channel]");
        setArgumentRange(0, 1);
        setIdentifiers("ch who");
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

        List<String> names = new ArrayList<String>();
        for (Chatter member : channel.getMembers()) {
            names.add(member.getPlayer().getName());
        }
        Collections.sort(names);

        sender.sendMessage(ChatColor.RED + "------------[ " + channel.getColor() + channel.getName() + ChatColor.RED + " ]------------");
        int count = names.size();
        int lines = (int) Math.ceil(count / 4.0);
        for (int i = 0; i < lines; i++) {
            String line = "";
            for (int j = 0; j < 4; j++) {
                if (i * 3 + j < count) {
                    String name = names.get(i * 3 + j);
                    String formatted = String.format("%-15s", name);
                    if (channel.isMuted(name))
                        formatted = ChatColor.RED + formatted + ChatColor.WHITE;
                    else if (channel.isModerator(name))
                        formatted = ChatColor.GREEN + formatted + ChatColor.WHITE;
                    line += formatted + " ";
                }
            }
            sender.sendMessage(line.trim());
        }

        return true;
    }
}
