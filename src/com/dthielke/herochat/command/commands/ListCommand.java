/**
 * Copyright (C) 2011 David Thielke <dave.thielke@gmail.com>
 **/

package com.dthielke.herochat.command.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.dthielke.herochat.Channel;
import com.dthielke.herochat.Chatter.Permission;
import com.dthielke.herochat.HeroChat;
import com.dthielke.herochat.command.BasicCommand;

public class ListCommand extends BasicCommand {

    private static final int CHANNELS_PER_PAGE = 8;

    public ListCommand() {
        super("List");
        setDescription("Lists available channels");
        setUsage("/ch list §8[page#]");
        setArgumentRange(0, 1);
        setIdentifiers("ch list");
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args) {
        int page = 0;
        if (args.length != 0) {
            try {
                page = Integer.parseInt(args[0]) - 1;
            } catch (NumberFormatException e) {}
        }

        List<Channel> channels = new ArrayList<Channel>();

        // Filter out Skills from the command list.
        for (Channel channel : HeroChat.getChannelManager().getChannels()) {
            if (sender.hasPermission(Permission.JOIN.form(channel))) {
                channels.add(channel);
            }
        }

        int numPages = channels.size() / CHANNELS_PER_PAGE;
        if (channels.size() % CHANNELS_PER_PAGE != 0) {
            numPages++;
        }

        if (page >= numPages || page < 0) {
            page = 0;
        }
        sender.sendMessage("§c-----[ " + "§fHeroChat Channels <" + (page + 1) + "/" + numPages + ">§c ]-----");
        int start = page * CHANNELS_PER_PAGE;
        int end = start + CHANNELS_PER_PAGE;
        if (end > channels.size()) {
            end = channels.size();
        }
        for (int c = start; c < end; c++) {
            Channel channel = channels.get(c);
            sender.sendMessage(channel.getColor() + "  [" + channel.getNick() + "] " + channel.getName());
        }
        return true;
    }

}
