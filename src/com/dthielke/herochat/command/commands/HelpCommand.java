/**
 * Copyright (C) 2011 David Thielke <dave.thielke@gmail.com>
 **/

package com.dthielke.herochat.command.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.dthielke.herochat.HeroChat;
import com.dthielke.herochat.command.BasicCommand;
import com.dthielke.herochat.command.Command;
import com.dthielke.herochat.command.CommandHandler;

public class HelpCommand extends BasicCommand {

    private static final int CMDS_PER_PAGE = 8;

    public HelpCommand() {
        super("Help");
        setDescription("Displays the help menu");
        setUsage("/ch help §8[page#]");
        setArgumentRange(0, 1);
        setIdentifiers("ch help");
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args) {
        int page = 0;
        if (args.length != 0) {
            try {
                page = Integer.parseInt(args[0]) - 1;
            } catch (NumberFormatException e) {}
        }

        List<Command> sortCommands = HeroChat.getCommandHandler().getCommands();
        List<Command> commands = new ArrayList<Command>();

        // Filter out Skills from the command list.
        for (Command command : sortCommands) {
            if (command.isShownOnHelpMenu()) {
                if (CommandHandler.hasPermission(sender, command.getPermission()))
                    commands.add(command);
            }
        }

        int numPages = commands.size() / CMDS_PER_PAGE;
        if (commands.size() % CMDS_PER_PAGE != 0) {
            numPages++;
        }

        if (page >= numPages || page < 0) {
            page = 0;
        }
        sender.sendMessage("§c-----[ " + "§fHeroChat Help <" + (page + 1) + "/" + numPages + ">§c ]-----");
        int start = page * CMDS_PER_PAGE;
        int end = start + CMDS_PER_PAGE;
        if (end > commands.size()) {
            end = commands.size();
        }
        for (int c = start; c < end; c++) {
            Command cmd = commands.get(c);
            sender.sendMessage("  §a" + cmd.getUsage());
        }

        sender.sendMessage("§cFor more info on a particular command, type §f/<command> ?");
        return true;
    }

}
