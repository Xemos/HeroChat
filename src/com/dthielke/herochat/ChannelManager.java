package com.dthielke.herochat;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;

public class ChannelManager {

    private List<Channel> channels = new ArrayList<Channel>();
    private Map<Chatter.Permission, Permission> wildcardPermissions = new EnumMap<Chatter.Permission, Permission>(Chatter.Permission.class);

    public ChannelManager() {
        registerChannelPermissions();
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

        return true;
    }

    public Channel getChannel(String identifier) {
        for (Channel channel : channels)
            if (identifier.equalsIgnoreCase(channel.getName()) || identifier.equalsIgnoreCase(channel.getNick()))
                return channel;

        return null;
    }

    public boolean hasChannel(String identifier) {
        return getChannel(identifier) != null;
    }

    public List<Channel> getChannels() {
        return channels;
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

    public void registerChannelPermissions() {
        // setup empty wildcard permissions
        for (Chatter.Permission p : Chatter.Permission.values()) {
            Permission perm = new Permission(p.formWildcard());
            Bukkit.getServer().getPluginManager().addPermission(perm);
            wildcardPermissions.put(p, perm);
        }
    }
}
