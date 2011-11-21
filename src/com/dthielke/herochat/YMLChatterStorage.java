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
    public Chatter load(String name) {
        File folder = new File(chatterFolder, name.substring(0, 1).toLowerCase());
        folder.mkdirs();
        File file = new File(folder, name + ".yml");
        Configuration config = new Configuration(file);
        config.load();

        Player player = Bukkit.getServer().getPlayer(name);
        if (player == null)
            return null;

        ChannelManager channelManager = HeroChat.getChannelManager();
        Channel activeChannel = channelManager.getChannel(config.getString("activeChannel", ""));
        if (activeChannel == null)
            activeChannel = channelManager.getDefaultChannel();
        Set<Channel> channels = new HashSet<Channel>();
        List<String> channelNames = config.getStringList("channels", null);
        for (String channelName : channelNames) {
            Channel channel = channelManager.getChannel(channelName);
            if (channel != null)
                channels.add(channel);
        }
        if (channels.isEmpty())
            channels.add(channelManager.getDefaultChannel());

        Chatter chatter = new StandardChatter(this, player);
        chatter.setActiveChannel(activeChannel);
        for (Channel channel : channels)
            channel.addMember(chatter, false);
        addChatter(chatter);
        return chatter;
    }

    @Override
    public void addChatter(Chatter chatter) {
        if (configs.containsKey(chatter))
            return;
        String name = chatter.getName();
        File folder = new File(chatterFolder, name.substring(0, 1).toLowerCase());
        folder.mkdirs();
        File file = new File(folder, name + ".yml");
        Configuration config = new Configuration(file);
        config.load();
        configs.put(chatter, config);
        flagUpdate(chatter);
    }

    @Override
    public void flagUpdate(Chatter chatter) {
        updates.add(chatter);
    }

    @Override
    public void removeChatter(Chatter chatter) {
        update(chatter);
        configs.remove(chatter);
    }

    @Override
    public void update() {
        for (Chatter chatter : updates.toArray(new Chatter[0]))
            update(chatter);
    }

    @Override
    public void update(Chatter chatter) {
        Configuration config = configs.get(chatter);
        if (config != null) {
            config.setProperty("name", chatter.getName());
            config.setProperty("activeChannel", chatter.getActiveChannel().getName());
            List<String> channels = new ArrayList<String>();
            for (Channel channel : chatter.getChannels())
                channels.add(channel.getName());
            config.setProperty("channels", channels);
            config.save();
        }
        updates.remove(chatter);
    }
}
