package com.dthielke.herochat;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;

import java.util.*;

public class ChannelManager {
    private List<Channel> channels = new ArrayList<Channel>();
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
        return channels;
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

    public boolean hasChannel(String identifier) {
        return getChannel(identifier) != null;
    }

    public Channel getChannel(String identifier) {
        for (Channel channel : channels)
            if (identifier.equalsIgnoreCase(channel.getName()) || identifier.equalsIgnoreCase(channel.getNick()))
                return channel;

        return null;
    }

    public void loadChannels() {
        for (Channel channel : storage.loadChannels())
            addChannel(channel);
    }

    public boolean addChannel(Channel channel) {
        if (channels.contains(channel))
            return false;

        channels.add(channel);

        // add the channel to the wildcard permissions
        for (Chatter.Permission p : Chatter.Permission.values()) {
            Permission perm = wildcardPermissions.get(p);
            perm.getChildren().put(p.form(channel), true);
            perm.recalculatePermissibles();
        }

        // set the default channel if we don't have one yet
        if (defaultChannel == null) {
            defaultChannel = channel;
        }

        storage.addChannel(channel);

        return true;
    }

    public boolean removeChannel(Channel channel) {
        if (!channels.contains(channel))
            return false;

        channels.remove(channel);

        // remove the channel from the wildcard permissions
        for (Chatter.Permission p : Chatter.Permission.values()) {
            Permission perm = wildcardPermissions.get(p);
            perm.getChildren().remove(p.form(channel));
            perm.recalculatePermissibles();
        }

        return true;
    }
}
