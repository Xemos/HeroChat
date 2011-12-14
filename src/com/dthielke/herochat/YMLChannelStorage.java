package com.dthielke.herochat;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class YMLChannelStorage implements ChannelStorage {
    private Map<Channel, FileConfiguration> configs = new HashMap<Channel, FileConfiguration>();
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
        FileConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }

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
        config.addDefault("worlds", new ArrayList<String>());
        config.addDefault("bans", new ArrayList<String>());
        config.addDefault("mutes", new ArrayList<String>());
        config.addDefault("moderators", new ArrayList<String>());
        Set<String> worlds = new HashSet<String>(config.getStringList("worlds"));
        Set<String> bans = new HashSet<String>(config.getStringList("bans"));
        Set<String> mutes = new HashSet<String>(config.getStringList("mutes"));
        Set<String> moderators = new HashSet<String>(config.getStringList("moderators"));

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
        FileConfiguration config = new YamlConfiguration();
        try {
            if (file.exists())
                config.load(file);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
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
        for (Channel channel : updates.toArray(new Channel[updates.size()]))
            update(channel);
    }

    @Override
    public void update(Channel channel) {
        FileConfiguration config = configs.get(channel);
        File file = new File(channelFolder, channel.getName() + ".yml");
        if (config == null) {
            file.delete();
        } else {
            config.options().copyDefaults(true);
            config.set("name", channel.getName());
            config.set("nick", channel.getNick());
            config.set("applyFormat", channel.getFormat());
            config.set("password", channel.getPassword());
            config.set("color", channel.getColor().name());
            config.set("distance", channel.getDistance());
            config.set("shortcutAllowed", channel.isShortcutAllowed());
            config.set("worlds", new ArrayList<String>(channel.getWorlds()));
            config.set("bans", new ArrayList<String>(channel.getBans()));
            config.set("mutes", new ArrayList<String>(channel.getMutes()));
            config.set("moderators", new ArrayList<String>(channel.getModerators()));
            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        updates.remove(channel);
    }
}
