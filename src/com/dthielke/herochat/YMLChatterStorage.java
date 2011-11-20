package com.dthielke.herochat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

import java.io.File;
import java.util.*;

public class YMLChatterStorage implements ChatterStorage {
    private Map<Chatter, Configuration> configs = new HashMap<Chatter, Configuration>();
    private Set<Chatter> updates = new HashSet<Chatter>();
    private final File chatterFolder;

    public YMLChatterStorage(File chatterFolder) {
        this.chatterFolder = chatterFolder;
    }

    @Override
    public void addChatter(Chatter chatter) {
        File file = new File(chatterFolder, chatter.getName());
        Configuration config = new Configuration(file);
        config.load();
        configs.put(chatter, config);
        notify(chatter);
    }

    @Override
    public void notify(Chatter chatter) {
        updates.add(chatter);
    }

    @Override
    public Chatter load(String name) {
        File file = new File(chatterFolder, name);
        Configuration config = new Configuration(file);
        config.load();

        Player player = Bukkit.getServer().getPlayer(name);
        if (player == null)
            return null;

        ChannelManager channelManager = HeroChat.getChannelManager();
        Channel activeChannel = channelManager.getChannel(config.getString("activeChannel"));
        if (activeChannel == null)
            activeChannel = channelManager.getDefaultChannel();
        Set<Channel> channels = new HashSet<Channel>();
        List<String> channelNames = config.getStringList("channels", null);
        for (String channelName : channelNames) {
            Channel channel = channelManager.getChannel(channelName);
            if (channel != null)
                channels.add(channel);
        }

        Chatter chatter = new StandardChatter(player);
        chatter.setActiveChannel(activeChannel);
        chatter.setChannels(channels);
        return chatter;
    }

    @Override
    public void removeChatter(Chatter chatter) {
        configs.remove(chatter);
        notify(chatter);
    }

    @Override
    public void update() {
        for (Chatter chatter : updates)
            update(chatter);
        updates.clear();
    }

    @Override
    public void update(Chatter chatter) {
        Configuration config = configs.get(chatter);
        if (config == null) {
            File file = new File(chatterFolder, chatter.getName());
            file.delete();
        } else {
            config.setProperty("name", chatter.getName());
            config.setProperty("activeChannel", chatter.getActiveChannel().getName());
            Set<String> channels = new HashSet<String>();
            for (Channel channel : chatter.getChannels())
                channels.add(channel.getName());
            config.setProperty("channels", channels);
            config.save();
        }
    }
}
