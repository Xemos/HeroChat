package com.dthielke.herochat;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class StandardChatter implements Chatter {
    private final Player player;
    private Set<Channel> channels = new HashSet<Channel>();
    private Channel activeChannel;
    private ChatterStorage storage;

    public StandardChatter(ChatterStorage storage, Player player) {
        this.storage = storage;
        this.player = player;
    }

    @Override
    public Channel getActiveChannel() {
        return activeChannel;
    }

    @Override
    public Set<Channel> getChannels() {
        return channels;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public ChatterStorage getStorage() {
        return storage;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this)
            return true;

        if (other == null)
            return false;

        if (!(other instanceof Chatter))
            return false;

        return player.equals(((Chatter) other).getPlayer());
    }

    @Override
    public int hashCode() {
        return player.hashCode();
    }

    @Override
    public boolean addChannel(Channel channel, boolean announce) {
        if (channels.contains(channel))
            return false;

        channels.add(channel);
        if (!channel.isMember(this)) {
            channel.addMember(this, announce);
        }

        storage.flagUpdate(this);

        return true;
    }

    @Override
    public void attachStorage(ChatterStorage storage) {
        this.storage = storage;
    }

    @Override
    public Result canBan(Channel channel) {
        if (player.hasPermission(Permission.BAN.form(channel)))
            return Result.ALLOWED;

        if (channel.isModerator(player.getName()))
            return Result.ALLOWED;

        return Result.NO_PERMISSION;
    }

    @Override
    public Result canJoin(Channel channel) {
        if (channel.isMember(this))
            return Result.INVALID;

        if (!player.hasPermission(Permission.JOIN.form(channel)))
            return Result.NO_PERMISSION;

        if (channel.isBanned(player.getName()))
            return Result.BANNED;

        return Result.ALLOWED;
    }

    @Override
    public Result canKick(Channel channel) {
        if (player.hasPermission(Permission.KICK.form(channel)))
            return Result.ALLOWED;

        if (channel.isModerator(player.getName()))
            return Result.ALLOWED;

        return Result.NO_PERMISSION;
    }

    @Override
    public Result canLeave(Channel channel) {
        if (!channel.isMember(this))
            return Result.INVALID;

        if (!player.hasPermission(Permission.LEAVE.form(channel)))
            return Result.NO_PERMISSION;

        return Result.ALLOWED;
    }

    @Override
    public Result canModify(String setting, Channel channel) {
        setting = setting.toLowerCase();
        Permission permission;

        if (setting.equals("name")) {
            permission = Permission.MODIFY_NAME;
        } else if (setting.equals("nick")) {
            permission = Permission.MODIFY_NICK;
        } else if (setting.equals("format")) {
            permission = Permission.MODIFY_FORMAT;
        } else if (setting.equals("distance")) {
            permission = Permission.MODIFY_DISTANCE;
        } else if (setting.equals("color")) {
            permission = Permission.MODIFY_COLOR;
        } else if (setting.equals("shortcut")) {
            permission = Permission.MODIFY_SHORTCUT;
        } else {
            return Result.INVALID;
        }

        if (player.hasPermission(permission.form(channel)))
            return Result.ALLOWED;

        if (channel.isModerator(player.getName()) && HeroChat.getChannelManager().checkModPermission(permission))
            return Result.ALLOWED;

        return Result.NO_PERMISSION;
    }

    @Override
    public Result canMute(Channel channel) {
        if (player.hasPermission(Permission.MUTE.form(channel)))
            return Result.ALLOWED;

        if (channel.isModerator(player.getName()))
            return Result.ALLOWED;

        return Result.NO_PERMISSION;
    }

    @Override
    public Result canRemove(Channel channel) {
        if (player.hasPermission(Permission.REMOVE.form(channel)))
            return Result.ALLOWED;

        if (channel.isModerator(player.getName()) && HeroChat.getChannelManager().checkModPermission(Permission.REMOVE))
            return Result.ALLOWED;

        return Result.NO_PERMISSION;
    }

    @Override
    public Result canSpeak(Channel channel) {
        if (!channel.isMember(this))
            return Result.INVALID;

        if (!player.hasPermission(Permission.SPEAK.form(channel)))
            return Result.NO_PERMISSION;

        if (channel.isMuted(player.getName()))
            return Result.MUTED;

        if (!channel.hasWorld(player.getWorld()))
            return Result.BAD_WORLD;

        return Result.ALLOWED;
    }

    @Override
    public String getName() {
        return player.getName();
    }

    @Override
    public boolean hasChannel(Channel channel) {
        return channels.contains(channel);
    }

    @Override
    public boolean isInRange(Chatter other, int distance) {
        return player.getLocation().distanceSquared(other.getPlayer().getLocation()) <= distance;
    }

    @Override
    public boolean removeChannel(Channel channel, boolean announce) {
        if (!channels.contains(channel))
            return false;

        channels.remove(channel);
        if (channel.isMember(this)) {
            channel.removeMember(this, announce);
        }

        storage.flagUpdate(this);

        return true;
    }

    @Override
    public void setActiveChannel(Channel channel) {
        activeChannel = channel;
        storage.flagUpdate(this);
    }
}
