package com.dthielke.herochat;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.dthielke.herochat.command.CommandHandler;
import com.dthielke.herochat.command.commands.BanCommand;
import com.dthielke.herochat.command.commands.CreateCommand;
import com.dthielke.herochat.command.commands.FocusCommand;
import com.dthielke.herochat.command.commands.HelpCommand;
import com.dthielke.herochat.command.commands.JoinCommand;
import com.dthielke.herochat.command.commands.KickCommand;
import com.dthielke.herochat.command.commands.LeaveCommand;
import com.dthielke.herochat.command.commands.ListCommand;
import com.dthielke.herochat.command.commands.SetCommand;

public class HeroChat extends JavaPlugin {

    private static final Logger log = Logger.getLogger("Minecraft");
    private static final CommandHandler cmdHndlr = new CommandHandler();
    private static final ChannelManager chnnlMngr = new ChannelManager();
    private static final ChatterManager chttrMngr = new ChatterManager();

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
        cmdHndlr.addCommand(new ListCommand());
        cmdHndlr.addCommand(new CreateCommand());
        cmdHndlr.addCommand(new SetCommand());
        cmdHndlr.addCommand(new KickCommand());
        cmdHndlr.addCommand(new BanCommand());
        cmdHndlr.addCommand(new HelpCommand());
    }

    private void registerEvents() {
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvent(Type.PLAYER_CHAT, new PlayerChatListener(), Priority.High, this);
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

    public static ChannelManager getChannelManager() {
        return chnnlMngr;
    }

    public static ChatterManager getChatterManager() {
        return chttrMngr;
    }

    public static CommandHandler getCommandHandler() {
        return cmdHndlr;
    }

}
