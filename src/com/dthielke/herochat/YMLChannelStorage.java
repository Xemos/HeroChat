package com.dthielke.herochat;

import org.bukkit.ChatColor;
import org.bukkit.util.config.Configuration;

import java.io.File;
import java.util.*;

public class YMLChannelStorage implements ChannelStorage {
    private Map<Channel, Configuration> configs = new HashMap<Channel, Configuration>();
    private Set<Channel> updates = new HashSet<Channel>();
    private final File channelFolder;

    public YMLChannelStorage(File channelFolder) {
        this.channelFolder = channelFolder;
    }

    @Override
    public Set<Channel> loadChannels() {
        Set<Channel> channels = new HashSet<Channel>();
        for (String name : channelFolder.list()) {
            name = name.substring(0, name.lastIndexOf('.'));
            Channel channel = load(name);
            addChannel(channel);
            channels.add(channel);
        }
        return channels;
    }

    @Override
    public Channel load(String name) {
        File file = new File(channelFolder, name + ".yml");
        Configuration config = new Configuration(file);
        config.load();

        String nick = config.getString("nick", name);
        String format = config.getString("applyFormat", StandardChannel.MESSAGE_FORMAT);
        String password = config.getString("password", "");
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

        Channel channel = new StandardChannel(this, name, nick);
        channel.setFormat(format);
        channel.setPassword(password);
        channel.setColor(color);
        channel.setDistance(distance);
        channel.setShortcutAllowed(shortcutAllowed);
        channel.setWorlds(worlds);
        channel.setBans(bans);
        channel.setMutes(mutes);
        channel.setModerators(moderators);
        addChannel(channel);
        return channel;
    }

    @Override
    public void addChannel(Channel channel) {
        if (configs.containsKey(channel))
            return;
        File file = new File(channelFolder, channel.getName() + ".yml");
        Configuration config = new Configuration(file);
        config.load();
        configs.put(channel, config);
        flagUpdate(channel);
    }

    @Override
    public void flagUpdate(Channel channel) {
        updates.add(channel);
    }

    @Override
    public void removeChannel(Channel channel) {
        configs.remove(channel);
        flagUpdate(channel);
    }

    @Override
    public void update() {
        for (Channel channel : updates.toArray(new Channel[0]))
            update(channel);
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
            config.setProperty("applyFormat", channel.getFormat());
            config.setProperty("password", channel.getPassword());
            config.setProperty("color", channel.getColor().name());
            config.setProperty("distance", channel.getDistance());
            config.setProperty("shortcutAllowed", channel.isShortcutAllowed());
            config.setProperty("worlds", new ArrayList<String>(channel.getWorlds()));
            config.setProperty("bans", new ArrayList<String>(channel.getBans()));
            config.setProperty("mutes", new ArrayList<String>(channel.getMutes()));
            config.setProperty("moderators", new ArrayList<String>(channel.getModerators()));
            config.save();
        }
        updates.remove(channel);
    }
}
