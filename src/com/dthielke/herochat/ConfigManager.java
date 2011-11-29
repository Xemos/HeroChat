package com.dthielke.herochat;

import org.bukkit.util.config.Configuration;

import java.io.File;

public class ConfigManager {

    public void load(File file) {
        Configuration config = new Configuration(file);
        config.load();

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

        config.save();
    }

}
