package com.dthielke.herochat;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ConfigManager {

    public void load(File file) {
        FileConfiguration config = new YamlConfiguration();
        try {
            if (file.exists())
                config.load(file);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
        config.setDefaults(getDefaults());

        ChannelManager channelManager = HeroChat.getChannelManager();

        // load moderator permissions
        if (config.getBoolean("moderator-permissions.can-kick", true))
            channelManager.addModPermission(Chatter.Permission.KICK);
        if (config.getBoolean("moderator-permissions.can-ban", true))
            channelManager.addModPermission(Chatter.Permission.BAN);
        if (config.getBoolean("moderator-permissions.can-mute", true))
            channelManager.addModPermission(Chatter.Permission.MUTE);
        if (config.getBoolean("moderator-permissions.can-remove-channel", true))
            channelManager.addModPermission(Chatter.Permission.REMOVE);
        if (config.getBoolean("moderator-permissions.can-modify-nick", true))
            channelManager.addModPermission(Chatter.Permission.MODIFY_NICK);
        if (config.getBoolean("moderator-permissions.can-modify-color", true))
            channelManager.addModPermission(Chatter.Permission.MODIFY_COLOR);
        if (config.getBoolean("moderator-permissions.can-modify-distance", true))
            channelManager.addModPermission(Chatter.Permission.MODIFY_DISTANCE);
        if (config.getBoolean("moderator-permissions.can-modify-password", true))
            channelManager.addModPermission(Chatter.Permission.MODIFY_PASSWORD);
        if (config.getBoolean("moderator-permissions.can-modify-format", false))
            channelManager.addModPermission(Chatter.Permission.MODIFY_FORMAT);
        if (config.getBoolean("moderator-permissions.can-modify-shortcut", false))
            channelManager.addModPermission(Chatter.Permission.MODIFY_SHORTCUT);
        if (config.getBoolean("moderator-permissions.can-color-messages", true))
            channelManager.addModPermission(Chatter.Permission.COLOR);

        // make sure we have at least one channel (create one if we don't)
        if (channelManager.getChannels().isEmpty()) {
            Channel defaultChannel = new StandardChannel(channelManager.getStorage(), "Global", "G");
            defaultChannel.setColor(ChatColor.DARK_GREEN);
            channelManager.addChannel(defaultChannel);
        }

        // load default channel
        String defaultChannel = config.getString("default-channel");
        if (defaultChannel != null && channelManager.hasChannel(defaultChannel))
            channelManager.setDefaultChannel(channelManager.getDefaultChannel());

        // load censors
        HeroChat.getMessageHandler().setCensors(config.getStringList("censors"));

        try {
            config.options().copyDefaults(true);
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private MemoryConfiguration getDefaults() {
        MemoryConfiguration config = new MemoryConfiguration();
        config.set("moderator-permissions.can-kick", true);
        config.set("moderator-permissions.can-ban", true);
        config.set("moderator-permissions.can-mute", true);
        config.set("moderator-permissions.can-remove-channel", true);
        config.set("moderator-permissions.can-modify-nick", true);
        config.set("moderator-permissions.can-modify-color", true);
        config.set("moderator-permissions.can-modify-distance", true);
        config.set("moderator-permissions.can-modify-password", true);
        config.set("moderator-permissions.can-modify-format", false);
        config.set("moderator-permissions.can-modify-shortcut", false);
        config.set("moderator-permissions.can-color-messages", true);
        config.set("default-channel", "Global");
        config.set("censors", new ArrayList<String>());
        return config;
    }

}
