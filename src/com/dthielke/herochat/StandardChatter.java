package com.dthielke.herochat;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;

public class StandardChatter implements Chatter {

    private final Player player;
    private Set<Channel> channels = new HashSet<Channel>();
    private Channel activeChannel;

    public StandardChatter(Player player) {
        this.player = player;
    }

    @Override
    public boolean addChannel(Channel channel, boolean announce) {
        if (channels.contains(channel))
            return false;

        channels.add(channel);
        if (!channel.isMember(this)) {
            channel.addMember(this, announce);
        }

        return true;
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
    public Result canModify(Channel channel) {
        if (player.hasPermission(Permission.MODIFY.form(channel)))
            return Result.ALLOWED;

        if (channel.isModerator(player.getName()))
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
    public Channel getActiveChannel() {
        return activeChannel;
    }

    @Override
    public Set<Channel> getChannels() {
        return channels;
    }

    @Override
    public String getName() {
        return player.getName();
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public boolean hasChannel(Channel channel) {
        return channels.contains(channel);
    }

    @Override
    public int hashCode() {
        return player.hashCode();
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

        return true;
    }

    @Override
    public boolean setActiveChannel(Channel channel) {
        if (!channels.contains(channel))
            return false;

        activeChannel = channel;
        return true;
    }

}
