package com.dthielke.herochat;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class YMLChatterStorage implements ChatterStorage {
    private Map<Chatter, FileConfiguration> configs = new HashMap<Chatter, FileConfiguration>();
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
        FileConfiguration config = new YamlConfiguration();
        try {
            if (file.exists())
                config.load(file);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }

        Player player = Bukkit.getServer().getPlayer(name);
        if (player == null)
            return null;

        ChannelManager channelManager = HeroChat.getChannelManager();
        Channel defaultChannel = channelManager.getDefaultChannel();
        Channel activeChannel = channelManager.getChannel(config.getString("activeChannel", ""));
        if (activeChannel == null)
            activeChannel = defaultChannel;
        Set<Channel> channels = new HashSet<Channel>();
        config.addDefault("channels", new ArrayList<String>());
        List<String> channelNames = config.getStringList("channels");
        for (String channelName : channelNames) {
            Channel channel = channelManager.getChannel(channelName);
            if (channel != null)
                channels.add(channel);
        }
        config.addDefault("ignores", new ArrayList<String>());
        List<String> ignores = config.getStringList("ignores");
        boolean muted = config.getBoolean("muted", false);

        Chatter chatter = new StandardChatter(this, player);
        // add "auto" channels on first join
        if (channels.isEmpty()) {
            for (Channel channel : channelManager.getChannels()) {
                if (chatter.shouldAutoJoin(channel)) {
                    channels.add(channel);
                }
            }
        }
        // if it's STILL empty, add the default channel
        if (channels.isEmpty())
            channels.add(defaultChannel);
        chatter.setActiveChannel(defaultChannel, false);
        chatter.setActiveChannel(activeChannel, false); // done twice to set the last active channel
        for (Channel channel : channels)
            channel.addMember(chatter, false);
        for (String ignore : ignores)
            chatter.setIgnore(ignore, true);
        chatter.setMuted(muted);
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
        FileConfiguration config = new YamlConfiguration();
        try {
            if (file.exists())
                config.load(file);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
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
    public void update(Chatter chatter) {
        FileConfiguration config = configs.get(chatter);
        if (config != null) {
            String name = chatter.getName();
            config.set("name", name);
            if (chatter.getActiveChannel().isTransient())
                config.set("activeChannel", chatter.getLastActiveChannel().getName());
            else
                config.set("activeChannel", chatter.getActiveChannel().getName());
            List<String> channels = new ArrayList<String>();
            for (Channel channel : chatter.getChannels())
                channels.add(channel.getName());
            config.set("channels", channels);
            config.set("ignores", new ArrayList<String>(chatter.getIgnores()));
            config.set("muted", chatter.isMuted());
            File folder = new File(chatterFolder, name.substring(0, 1).toLowerCase());
            File file = new File(folder, name + ".yml");
            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        updates.remove(chatter);
    }

    @Override
    public void update() {
        for (Chatter chatter : updates.toArray(new Chatter[0]))
            update(chatter);
    }
}
