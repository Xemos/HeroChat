package com.dthielke.herochat;

import org.bukkit.ChatColor;
import org.bukkit.util.config.Configuration;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class YMLChannelStorage implements ChannelStorage {
    private Map<Channel, Configuration> configs = new HashMap<Channel, Configuration>();
    private Set<Channel> updates = new HashSet<Channel>();
    private final File channelFolder;

    public YMLChannelStorage(File channelFolder) {
        this.channelFolder = channelFolder;
    }

    @Override
    public void addChannel(Channel channel) {
        File file = new File(channelFolder, channel.getName());
        Configuration config = new Configuration(file);
        config.load();
        configs.put(channel, config);
        notify(channel);
    }

    @Override
    public void notify(Channel channel) {
        updates.add(channel);
    }

    @Override
    public Channel load(String name) {
        File file = new File(channelFolder, name);
        Configuration config = new Configuration(file);
        config.load();

        String nick = config.getString("nick", name);
        String format = config.getString("format", StandardChannel.MESSAGE_FORMAT);
        ChatColor color;
        try {
            color = ChatColor.valueOf(config.getString("color", "WHITE"));
        } catch (IllegalArgumentException e) {
            color = ChatColor.WHITE;
        }
        int distance = config.getInt("distance", 0);
        boolean shortcutAllowed = config.getBoolean("shortcutAllowed", false);
        Set<String> worlds = new HashSet<String>(config.getStringList("worlds", null));
        Set<String> bans = new HashSet<String>(config.getStringList("bans", null));
        Set<String> mutes = new HashSet<String>(config.getStringList("mutes", null));
        Set<String> moderators = new HashSet<String>(config.getStringList("moderators", null));

        Channel channel = new StandardChannel(name, nick);
        channel.setFormat(format);
        channel.setColor(color);
        channel.setDistance(distance);
        channel.setShortcutAllowed(shortcutAllowed);
        channel.setWorlds(worlds);
        channel.setBans(bans);
        channel.setMutes(mutes);
        channel.setModerators(moderators);
        return channel;
    }

    @Override
    public void removeChannel(Channel channel) {
        configs.remove(channel);
        notify(channel);
    }

    @Override
    public void update() {
        for (Channel channel : updates)
            update(channel);
        updates.clear();
    }

    @Override
    public void update(Channel channel) {
        Configuration config = configs.get(channel);
        if (config == null) {
            File file = new File(channelFolder, channel.getName());
            file.delete();
        } else {
            config.setProperty("name", channel.getName());
            config.setProperty("nick", channel.getNick());
            config.setProperty("format", channel.getFormat());
            config.setProperty("color", channel.getColor().name());
            config.setProperty("distance", channel.getDistance());
            config.setProperty("shortcutAllowed", channel.isShortcutAllowed());
            config.setProperty("worlds", channel.getWorlds());
            config.setProperty("bans", channel.getBans());
            config.setProperty("mutes", channel.getMutes());
            config.setProperty("moderators", channel.getModerators());
            config.save();
        }
    }
}
