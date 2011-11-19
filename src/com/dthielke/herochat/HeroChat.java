package com.dthielke.herochat;

import com.dthielke.herochat.command.CommandHandler;
import com.dthielke.herochat.command.commands.*;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class HeroChat extends JavaPlugin {
    private static final Logger log = Logger.getLogger("Minecraft");
    private static final CommandHandler cmdHndlr = new CommandHandler();
    private static final ChannelManager chnnlMngr = new ChannelManager();
    private static final ChatterManager chttrMngr = new ChatterManager();

    public static ChannelManager getChannelManager() {
        return chnnlMngr;
    }

    public static ChatterManager getChatterManager() {
        return chttrMngr;
    }

    public static CommandHandler getCommandHandler() {
        return cmdHndlr;
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        return cmdHndlr.dispatch(sender, label, args);
    }

    @Override
    public void onDisable() {
        log.info(getDescription().getName() + " version " + getDescription().getVersion() + " is disabled.");
    }

    @Override
    public void onEnable() {
        log.info(getDescription().getName() + " version " + getDescription().getVersion() + " is enabled.");

        registerCommands();
        registerEvents();

        setupDummyEnvironment();
    }

    private void registerCommands() {
        cmdHndlr.addCommand(new FocusCommand());
        cmdHndlr.addCommand(new JoinCommand());
        cmdHndlr.addCommand(new LeaveCommand());
        cmdHndlr.addCommand(new QuickMsgCommand());
        cmdHndlr.addCommand(new ListCommand());
        cmdHndlr.addCommand(new WhoCommand());
        cmdHndlr.addCommand(new CreateCommand());
        cmdHndlr.addCommand(new RemoveCommand());
        cmdHndlr.addCommand(new SetCommand());
        cmdHndlr.addCommand(new MuteCommand());
        cmdHndlr.addCommand(new KickCommand());
        cmdHndlr.addCommand(new BanCommand());
        cmdHndlr.addCommand(new ModCommand());
        cmdHndlr.addCommand(new HelpCommand());
    }

    private void registerEvents() {
        PluginManager pm = this.getServer().getPluginManager();
        PlayerChatListener pcl = new PlayerChatListener();
        pm.registerEvent(Type.PLAYER_CHAT, pcl, Priority.High, this);
        pm.registerEvent(Type.PLAYER_COMMAND_PREPROCESS, pcl, Priority.Normal, this);
    }

    private void setupDummyEnvironment() {
        Channel channel = new StandardChannel("Dummy", "D");
        channel.setColor(ChatColor.GREEN);
        chnnlMngr.addChannel(channel);

        for (Player player : getServer().getOnlinePlayers()) {
            Chatter chatter = new StandardChatter(player);
            chttrMngr.addChatter(chatter);
            chatter.addChannel(channel, false);
            chatter.setActiveChannel(channel);
        }
    }
}
