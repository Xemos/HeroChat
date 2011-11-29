package com.dthielke.herochat;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;

import java.util.*;

public class ChannelManager {
    private Map<String, Channel> channels = new HashMap<String, Channel>();
    private Channel defaultChannel;
    private Map<Chatter.Permission, Permission> wildcardPermissions = new EnumMap<Chatter.Permission, Permission>(Chatter.Permission.class);
    private Set<Chatter.Permission> modPermissions = EnumSet.noneOf(Chatter.Permission.class);
    private ChannelStorage storage;

    public ChannelManager() {
        registerChannelPermissions();
    }

    public void registerChannelPermissions() {
        // setup empty wildcard permissions
        for (Chatter.Permission p : Chatter.Permission.values()) {
            Permission perm = new Permission(p.formWildcard());
            Bukkit.getServer().getPluginManager().addPermission(perm);
            wildcardPermissions.put(p, perm);
        }
    }

    public List<Channel> getChannels() {
        List<Channel> list = new ArrayList<Channel>();
        for (Channel channel : channels.values())
            if (!list.contains(channel))
                list.add(channel);
        return list;
    }

    public Channel getDefaultChannel() {
        return defaultChannel;
    }

    public void setDefaultChannel(Channel channel) {
        defaultChannel = channel;
    }

    public Set<Chatter.Permission> getModPermissions() {
        return modPermissions;
    }

    public void setModPermissions(Set<Chatter.Permission> modPermissions) {
        this.modPermissions = modPermissions;
    }

    public ChannelStorage getStorage() {
        return storage;
    }

    public void setStorage(ChannelStorage storage) {
        this.storage = storage;
    }

    public void addModPermission(Chatter.Permission permission) {
        modPermissions.add(permission);
    }

    public boolean checkModPermission(Chatter.Permission permission) {
        return modPermissions.contains(permission);
    }

    public Channel getChannel(String identifier) {
        return channels.get(identifier.toLowerCase());
    }

    public boolean hasChannel(String identifier) {
        return channels.containsKey(identifier.toLowerCase());
    }

    public void loadChannels() {
        for (Channel channel : storage.loadChannels())
            addChannel(channel);
    }

    public void addChannel(Channel channel) {
        channels.put(channel.getName().toLowerCase(), channel);
        channels.put(channel.getNick().toLowerCase(), channel);

        // add the channel to the wildcard permissions
        for (Chatter.Permission p : Chatter.Permission.values()) {
            Permission perm = wildcardPermissions.get(p);
            perm.getChildren().put(p.form(channel), true);
            perm.recalculatePermissibles();
        }


        if (!channel.isTransient()) {
            // set the default channel if we don't have one yet
            if (defaultChannel == null)
                defaultChannel = channel;
            storage.addChannel(channel);
        }
    }

    public void removeChannel(Channel channel) {
        channels.remove(channel.getName().toLowerCase());
        channels.remove(channel.getNick().toLowerCase());

        // remove the channel from the wildcard permissions
        for (Chatter.Permission p : Chatter.Permission.values()) {
            Permission perm = wildcardPermissions.get(p);
            perm.getChildren().remove(p.form(channel));
            perm.recalculatePermissibles();
        }

        if (!channel.isTransient())
            storage.removeChannel(channel);
    }
}
